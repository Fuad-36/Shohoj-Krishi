package com.example.backend.service.weather;

import com.example.backend.config.WeatherConfig;
import com.example.backend.dto.weather.request.WeatherRequest;
import com.example.backend.dto.weather.response.*;
import com.example.backend.service.chatbot.FarmerDataService;
import com.example.backend.service.chatbot.GeminiChatService;
import com.example.backend.service.chatbot.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final WeatherConfig config;
    private final RestTemplate weatherRestTemplate;

     private final TranslationService translationService;
     private final FarmerDataService farmerDataService;
     private final GeminiChatService geminiChatService;

    @Cacheable(value = "weather", key = "#location + '_current'")
    public WeatherResponse getCurrentWeather(WeatherRequest request) {
        log.info("Fetching current weather for: {}", request.getLocation());

        try {
            CurrentWeather currentWeather = fetchCurrentWeatherData(request.getLocation());

            WeatherResponse.WeatherResponseBuilder responseBuilder = WeatherResponse.builder()
                    .currentWeather(currentWeather)
                    .location(request.getLocation())
                    .language(request.getLanguage())
                    .lastUpdated(LocalDateTime.now())
                    .dataSource("Multiple Free APIs");

            // Add farming advice if requested
            if (request.isIncludeFarmingAdvice() && request.getUserId() != null) {
                FarmingWeatherAdvice farmingAdvice = generateFarmingAdvice(currentWeather, request);
                responseBuilder.farmingAdvice(farmingAdvice);
            }

            return responseBuilder.build();

        } catch (Exception e) {
            log.error("Error fetching current weather: {}", e.getMessage());
            return createErrorWeatherResponse(request);
        }
    }

    @Cacheable(value = "weather", key = "#location + '_forecast'")
    public WeatherResponse getWeatherForecast(WeatherRequest request) {
        log.info("Fetching weather forecast for: {}", request.getLocation());

        try {
            List<WeatherForecast> forecast = fetchWeatherForecastData(request.getLocation());
            List<WeatherAlert> alerts = generateWeatherAlerts(forecast);

            WeatherResponse.WeatherResponseBuilder responseBuilder = WeatherResponse.builder()
                    .forecast(forecast)
                    .alerts(alerts)
                    .location(request.getLocation())
                    .language(request.getLanguage())
                    .lastUpdated(LocalDateTime.now())
                    .dataSource("Multiple Free APIs");

            // Add farming advice based on forecast
            if (request.isIncludeFarmingAdvice() && request.getUserId() != null) {
                FarmingWeatherAdvice farmingAdvice = generateForecastBasedFarmingAdvice(forecast, alerts, request);
                responseBuilder.farmingAdvice(farmingAdvice);
            }

            return responseBuilder.build();

        } catch (Exception e) {
            log.error("Error fetching weather forecast: {}", e.getMessage());
            return createErrorWeatherResponse(request);
        }
    }

    public WeatherResponse getComprehensiveWeather(WeatherRequest request) {
        log.info("Fetching comprehensive weather for: {}", request.getLocation());

        try {
            CurrentWeather currentWeather = fetchCurrentWeatherData(request.getLocation());
            List<WeatherForecast> forecast = fetchWeatherForecastData(request.getLocation());
            List<WeatherAlert> alerts = generateWeatherAlerts(forecast);

            WeatherResponse.WeatherResponseBuilder responseBuilder = WeatherResponse.builder()
                    .currentWeather(currentWeather)
                    .forecast(forecast)
                    .alerts(alerts)
                    .location(request.getLocation())
                    .language(request.getLanguage())
                    .lastUpdated(LocalDateTime.now())
                    .dataSource("Multiple Free Sources");

            // Always include farming advice for comprehensive requests
            if (request.getUserId() != null) {
                FarmingWeatherAdvice farmingAdvice = generateComprehensiveFarmingAdvice(
                        currentWeather, forecast, alerts, request);
                responseBuilder.farmingAdvice(farmingAdvice);
            }

            return responseBuilder.build();

        } catch (Exception e) {
            log.error("Error fetching comprehensive weather: {}", e.getMessage());
            return createErrorWeatherResponse(request);
        }
    }

    // Data fetching methods
    private CurrentWeather fetchCurrentWeatherData(String location) {
        // Try multiple free APIs in order of reliability
        try {
            return fetchFromOpenWeatherMap(location);
        } catch (Exception e) {
            log.warn("OpenWeatherMap failed: {}", e.getMessage());
            try {
                return fetchFromWeatherAPI(location);
            } catch (Exception e2) {
                log.warn("WeatherAPI failed: {}", e2.getMessage());
                try {
                    return fetchFromOpenMeteo(location);
                } catch (Exception e3) {
                    log.warn("OpenMeteo failed: {}", e3.getMessage());
                    return getDefaultWeatherForBangladesh();
                }
            }
        }
    }

    private CurrentWeather fetchFromOpenWeatherMap(String location) {
        if (config.getOpenWeatherApiKey() == null || config.getOpenWeatherApiKey().isEmpty()) {
            throw new RuntimeException("OpenWeatherMap API key not configured");
        }

        String url = String.format("%s/weather?q=%s,BD&appid=%s&units=metric&lang=en",
                config.getOpenWeatherBaseUrl(),
                URLEncoder.encode(location, StandardCharsets.UTF_8),
                config.getOpenWeatherApiKey());

        ResponseEntity<Map> response = weatherRestTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return parseOpenWeatherResponse(response.getBody());
        }

        throw new RuntimeException("Failed to fetch from OpenWeatherMap");
    }

    private CurrentWeather fetchFromWeatherAPI(String location) {
        if (config.getWeatherApiKey() == null || config.getWeatherApiKey().isEmpty()) {
            throw new RuntimeException("WeatherAPI key not configured");
        }

        String url = String.format("%s/current.json?key=%s&q=%s,Bangladesh&aqi=no",
                config.getWeatherApiBaseUrl(),
                config.getWeatherApiKey(),
                URLEncoder.encode(location, StandardCharsets.UTF_8));

        ResponseEntity<Map> response = weatherRestTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return parseWeatherAPIResponse(response.getBody());
        }

        throw new RuntimeException("Failed to fetch from WeatherAPI");
    }

    private CurrentWeather fetchFromOpenMeteo(String location) {
        // OpenMeteo is completely free but requires coordinates
        // First get coordinates for the location (using a simple lookup)
        Map<String, double[]> bangladeshCities = getBangladeshCityCoordinates();
        double[] coords = bangladeshCities.getOrDefault(location.toLowerCase(),
                bangladeshCities.get("dhaka")); // Default to Dhaka

        String url = String.format("%s/forecast?latitude=%.4f&longitude=%.4f&current_weather=true&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code&timezone=Asia/Dhaka",
                config.getOpenMeteoBaseUrl(),
                coords[0], coords[1]);

        ResponseEntity<Map> response = weatherRestTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return parseOpenMeteoResponse(response.getBody());
        }

        throw new RuntimeException("Failed to fetch from OpenMeteo");
    }

    private List<WeatherForecast> fetchWeatherForecastData(String location) {
        try {
            return fetchForecastFromWeatherAPI(location);
        } catch (Exception e) {
            log.warn("WeatherAPI forecast failed: {}", e.getMessage());
            try {
                return fetchForecastFromOpenWeatherMap(location);
            } catch (Exception e2) {
                log.warn("OpenWeatherMap forecast failed: {}", e2.getMessage());
                try {
                    return fetchForecastFromOpenMeteo(location);
                } catch (Exception e3) {
                    log.warn("OpenMeteo forecast failed: {}", e3.getMessage());
                    return generateDefaultForecast();
                }
            }
        }
    }

    private List<WeatherForecast> fetchForecastFromOpenMeteo(String location) {
        Map<String, double[]> bangladeshCities = getBangladeshCityCoordinates();
        double[] coords = bangladeshCities.getOrDefault(location.toLowerCase(),
                bangladeshCities.get("dhaka"));

        String url = String.format("%s/forecast?latitude=%.4f&longitude=%.4f&daily=temperature_2m_max,temperature_2m_min,weather_code,precipitation_sum,wind_speed_10m_max&timezone=Asia/Dhaka&forecast_days=%d",
                config.getOpenMeteoBaseUrl(),
                coords[0], coords[1],
                config.getForecastDays());

        ResponseEntity<Map> response = weatherRestTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return parseOpenMeteoForecast(response.getBody());
        }

        throw new RuntimeException("Failed to fetch forecast from OpenMeteo");
    }

    // Parsing methods
    private CurrentWeather parseOpenWeatherResponse(Map<String, Object> data) {
        Map<String, Object> main = (Map<String, Object>) data.get("main");
        Map<String, Object> wind = (Map<String, Object>) data.get("wind");
        Map<String, Object> sys = (Map<String, Object>) data.get("sys");
        List<Map<String, Object>> weather = (List<Map<String, Object>>) data.get("weather");

        String condition = weather.get(0).get("main").toString();

        return CurrentWeather.builder()
                .temperature(((Number) main.get("temp")).doubleValue())
                .feelsLike(((Number) main.get("feels_like")).doubleValue())
                .humidity(((Number) main.get("humidity")).doubleValue())
                .pressure(((Number) main.get("pressure")).doubleValue())
                .windSpeed(wind != null ? ((Number) wind.get("speed")).doubleValue() * 3.6 : 0)
                .condition(condition)
                .conditionBangla(translateWeatherCondition(condition))
                .sunrise(LocalDateTime.ofEpochSecond(((Number) sys.get("sunrise")).longValue(), 0, ZoneOffset.ofHours(6)))
                .sunset(LocalDateTime.ofEpochSecond(((Number) sys.get("sunset")).longValue(), 0, ZoneOffset.ofHours(6)))
                .build();
    }

    private CurrentWeather parseWeatherAPIResponse(Map<String, Object> data) {
        Map<String, Object> current = (Map<String, Object>) data.get("current");
        Map<String, Object> condition = (Map<String, Object>) current.get("condition");

        String conditionText = condition.get("text").toString();

        return CurrentWeather.builder()
                .temperature(((Number) current.get("temp_c")).doubleValue())
                .feelsLike(((Number) current.get("feelslike_c")).doubleValue())
                .humidity(((Number) current.get("humidity")).doubleValue())
                .pressure(((Number) current.get("pressure_mb")).doubleValue())
                .windSpeed(((Number) current.get("wind_kph")).doubleValue())
                .windDirection((String) current.get("wind_dir"))
                .condition(conditionText)
                .conditionBangla(translateWeatherCondition(conditionText))
                .iconUrl((String) condition.get("icon"))
                .uvIndex(((Number) current.get("uv")).doubleValue())
                .visibility(((Number) current.get("vis_km")).doubleValue())
                .build();
    }

    private CurrentWeather parseOpenMeteoResponse(Map<String, Object> data) {
        Map<String, Object> current = (Map<String, Object>) data.get("current_weather");
        Map<String, Object> hourly = (Map<String, Object>) data.get("hourly");

        double temperature = ((Number) current.get("temperature")).doubleValue();
        double windSpeed = ((Number) current.get("windspeed")).doubleValue();
        int weatherCode = ((Number) current.get("weathercode")).intValue();

        // Get humidity from hourly data (first hour)
        List<Number> humidityList = (List<Number>) hourly.get("relative_humidity_2m");
        double humidity = humidityList != null && !humidityList.isEmpty() ?
                humidityList.get(0).doubleValue() : 70.0;

        String condition = interpretWeatherCode(weatherCode);

        return CurrentWeather.builder()
                .temperature(temperature)
                .humidity(humidity)
                .windSpeed(windSpeed)
                .condition(condition)
                .conditionBangla(translateWeatherCondition(condition))
                .build();
    }

    private List<WeatherForecast> parseOpenMeteoForecast(Map<String, Object> data) {
        Map<String, Object> daily = (Map<String, Object>) data.get("daily");

        List<String> dates = (List<String>) daily.get("time");
        List<Number> maxTemps = (List<Number>) daily.get("temperature_2m_max");
        List<Number> minTemps = (List<Number>) daily.get("temperature_2m_min");
        List<Number> weatherCodes = (List<Number>) daily.get("weather_code");
        List<Number> precipitation = (List<Number>) daily.get("precipitation_sum");
        List<Number> windSpeeds = (List<Number>) daily.get("wind_speed_10m_max");

        List<WeatherForecast> forecasts = new ArrayList<>();

        for (int i = 0; i < dates.size() && i < config.getForecastDays(); i++) {
            String condition = interpretWeatherCode(weatherCodes.get(i).intValue());

            forecasts.add(WeatherForecast.builder()
                    .date(LocalDate.parse(dates.get(i)))
                    .maxTemp(maxTemps.get(i).doubleValue())
                    .minTemp(minTemps.get(i).doubleValue())
                    .condition(condition)
                    .conditionBangla(translateWeatherCondition(condition))
                    .rainfall(precipitation.get(i).doubleValue())
                    .windSpeed(windSpeeds.get(i).doubleValue())
                    .farmingTips(generateDailyFarmingTips(condition,
                            maxTemps.get(i).doubleValue(),
                            precipitation.get(i).doubleValue()))
                    .build());
        }

        return forecasts;
    }

    // Helper methods
    private Map<String, double[]> getBangladeshCityCoordinates() {
        Map<String, double[]> cities = new HashMap<>();


        cities.put("dhaka", new double[]{23.8103, 90.4125});
        cities.put("chittagong", new double[]{22.3569, 91.7832});
        cities.put("sylhet", new double[]{24.8949, 91.8687});
        cities.put("rajshahi", new double[]{24.3745, 88.6042});
        cities.put("khulna", new double[]{22.8456, 89.5403});
        cities.put("barishal", new double[]{22.7010, 90.3535});
        cities.put("rangpur", new double[]{25.7439, 89.2752});
        cities.put("mymensingh", new double[]{24.7471, 90.4203});
        cities.put("comilla", new double[]{23.4607, 91.1809});
        cities.put("bogura", new double[]{24.8465, 89.3775});
        cities.put("bandarban", new double[]{22.1980, 92.2200});
        cities.put("chandpur", new double[]{23.2321, 90.6631});
        cities.put("chuadanga", new double[]{23.6440, 88.8556});
        cities.put("coxs_bazar", new double[]{21.4272, 92.0050});
        cities.put("dinajpur", new double[]{25.6366, 88.6363});
        cities.put("feni", new double[]{23.0159, 91.3976});
        cities.put("fatulla", new double[]{23.6376, 90.4833});
        cities.put("gazipur", new double[]{23.9999, 90.4203});
        cities.put("jagannathpur", new double[]{24.7713, 91.5456});
        cities.put("jamalpur", new double[]{24.9230, 89.9501});
        cities.put("jashore", new double[]{23.1707, 89.2124});
        cities.put("joydebpur", new double[]{23.9890, 90.4182});
        cities.put("kishoreganj", new double[]{24.4331, 90.7866});
        cities.put("kurigram", new double[]{25.8103, 89.6487});
        cities.put("meherpur", new double[]{23.7721, 88.6314});
        cities.put("narayanganj", new double[]{23.6226, 90.4998});
        cities.put("narsingdi", new double[]{23.9207, 90.7188});
        cities.put("palash", new double[]{23.9880, 90.6488});
        cities.put("pabna", new double[]{24.0064, 89.2493});
        cities.put("panchagarh", new double[]{26.3354, 88.5517});
        cities.put("savar", new double[]{23.8583, 90.2667});
        cities.put("satkhira", new double[]{22.7234, 89.0751});
        cities.put("saidpur", new double[]{25.7785, 88.8974});
        cities.put("sherpur", new double[]{25.0194, 90.0137});
        cities.put("shyamnagar", new double[]{22.3373, 89.1087});
        cities.put("sonargaon", new double[]{23.6445, 90.5984});

        return cities;
    }


    private String interpretWeatherCode(int code) {
        // OpenMeteo weather codes interpretation
        if (code == 0) return "Clear";
        if (code <= 3) return "Partly Cloudy";
        if (code <= 48) return "Foggy";
        if (code <= 57) return "Drizzle";
        if (code <= 67) return "Rainy";
        if (code <= 77) return "Snowy";
        if (code <= 82) return "Heavy Rain";
        if (code <= 86) return "Snow Showers";
        if (code <= 99) return "Thunderstorm";
        return "Unknown";
    }

    private String translateWeatherCondition(String condition) {
        if (condition == null || condition.isBlank()) {
            return "";
        }

        Map<String, String> translations = new HashMap<>();

        // Clear/Sunny
        translations.put("Clear", "পরিষ্কার");
        translations.put("Sunny", "রৌদ্রোজ্জ্বল");
        translations.put("Bright", "রৌদ্রোজ্জ্বল");

        // Clouds
        translations.put("Clouds", "মেঘলা");
        translations.put("Cloudy", "মেঘলা");
        translations.put("Overcast", "ঘন মেঘলা");
        translations.put("Partly Cloudy", "আংশিক মেঘলা");
        translations.put("Mostly Cloudy", "প্রধানত মেঘলা");

        // Rain
        translations.put("Rain", "বৃষ্টি");
        translations.put("Rainy", "বৃষ্টি");
        translations.put("Light Rain", "হালকা বৃষ্টি");
        translations.put("Moderate Rain", "মাঝারি বৃষ্টি");
        translations.put("Heavy Rain", "ভারী বৃষ্টি");
        translations.put("Showers", "ঝিরিঝিরি বৃষ্টি");
        translations.put("Drizzle", "গুঁড়ি গুঁড়ি বৃষ্টি");

        // Thunderstorm
        translations.put("Thunderstorm", "বজ্রপাত");
        translations.put("Storm", "ঝড়");
        translations.put("Severe Thunderstorm", "তীব্র বজ্রঝড়");

        // Snow (for completeness, though Bangladesh rarely gets snow)
        translations.put("Snow", "তুষারপাত");
        translations.put("Light Snow", "হালকা তুষারপাত");
        translations.put("Heavy Snow", "ভারী তুষারপাত");
        translations.put("Sleet", "শিলাবৃষ্টি");

        // Fog/Mist/Haze
        translations.put("Fog", "কুয়াশা");
        translations.put("Foggy", "কুয়াশাচ্ছন্ন");
        translations.put("Mist", "হালকা কুয়াশা");
        translations.put("Haze", "ধোঁয়াশা");

        // Wind
        translations.put("Wind", "ঝড়ো হাওয়া");
        translations.put("Windy", "ঝড়ো হাওয়া");
        translations.put("Strong Winds", "প্রবল বাতাস");

        // Heat/Cold
        translations.put("Hot", "গরম");
        translations.put("Cold", "ঠান্ডা");
        translations.put("Freezing", "বরফ জমা ঠান্ডা");

        // Try exact match
        String normalized = condition.trim();
        if (translations.containsKey(normalized)) {
            return translations.get(normalized);
        }

        // Try case-insensitive match
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(normalized)) {
                return entry.getValue();
            }
        }

        // Fallback → translate dynamically
        return translationService.translateToBengali(condition);
    }


    private CurrentWeather getDefaultWeatherForBangladesh() {
        // Return typical Bangladesh weather as fallback
        return CurrentWeather.builder()
                .temperature(28.0)
                .feelsLike(32.0)
                .humidity(75.0)
                .condition("Partly Cloudy")
                .conditionBangla("আংশিক মেঘলা")
                .windSpeed(10.0)
                .build();
    }

    private List<WeatherForecast> generateDefaultForecast() {
        List<WeatherForecast> forecast = new ArrayList<>();

        for (int i = 1; i <= config.getForecastDays(); i++) {
            forecast.add(WeatherForecast.builder()
                    .date(LocalDate.now().plusDays(i))
                    .maxTemp(30.0 + (Math.random() * 4 - 2)) // 28-32°C range
                    .minTemp(22.0 + (Math.random() * 4 - 2)) // 20-24°C range
                    .condition("Partly Cloudy")
                    .conditionBangla("আংশিক মেঘলা")
                    .humidity(70.0 + (Math.random() * 20)) // 70-90%
                    .rainfall(Math.random() * 10) // 0-10mm
                    .farmingTips("আবহাওয়া অনুযায়ী কৃষিকাজ করুন।")
                    .build());
        }

        return forecast;
    }

    private List<WeatherAlert> generateWeatherAlerts(List<WeatherForecast> forecast) {
        List<WeatherAlert> alerts = new ArrayList<>();

        // Check for heavy rain
        for (WeatherForecast day : forecast) {
            if (day.getRainfall() > 25.0) {
                alerts.add(WeatherAlert.builder()
                        .alertType("HEAVY_RAIN")
                        .alertTypeBangla("ভারী বৃষ্টি")
                        .severity("MEDIUM")
                        .title("Heavy Rain Expected")
                        .titleBangla("ভারী বৃষ্টির সম্ভাবনা")
                        .description("Heavy rainfall expected on " + day.getDate())
                        .descriptionBangla(day.getDate() + " তারিখে ভারী বৃষ্টির সম্ভাবনা")
                        .startTime(day.getDate().atStartOfDay())
                        .endTime(day.getDate().atTime(23, 59))
                        .farmingRecommendation("ফসল রক্ষার ব্যবস্থা নিন এবং নিষ্কাশনের ব্যবস্থা করুন।")
                        .build());
            }

            // Check for extreme temperature
            if (day.getMaxTemp() > 38.0) {
                alerts.add(WeatherAlert.builder()
                        .alertType("HEAT_WAVE")
                        .alertTypeBangla("তাপপ্রবাহ")
                        .severity("HIGH")
                        .title("Heat Wave Warning")
                        .titleBangla("তাপপ্রবাহের সতর্কতা")
                        .description("Extreme heat expected on " + day.getDate())
                        .descriptionBangla(day.getDate() + " তারিখে প্রচণ্ড গরম পড়বে")
                        .startTime(day.getDate().atStartOfDay())
                        .endTime(day.getDate().atTime(23, 59))
                        .farmingRecommendation("ফসলে বেশি পানি দিন এবং ছায়ার ব্যবস্থা করুন।")
                        .build());
            }
        }

        return alerts;
    }

    // Farming advice generation methods

    private FarmingWeatherAdvice generateFarmingAdvice(CurrentWeather weather, WeatherRequest request) {
        return FarmingWeatherAdvice.builder()
                .generalAdvice(generateGeneralAdvice(weather))
                .immediateActions(generateImmediateActions(weather))
                .irrigationAdvice(generateIrrigationAdvice(weather))
                .pestDiseaseRisk(generatePestDiseaseRisk(weather))
                .build();
    }

    private FarmingWeatherAdvice generateForecastBasedFarmingAdvice(
            List<WeatherForecast> forecast, List<WeatherAlert> alerts, WeatherRequest request) {

        return FarmingWeatherAdvice.builder()
                .generalAdvice("আগামী ৫ দিনের আবহাওয়া অনুযায়ী কৃষিকাজের পরিকল্পনা করুন।")
                .weeklyPlan(generateWeeklyPlan(forecast))
                .harvestRecommendation(generateHarvestRecommendation(forecast))
                .cropAdvice(generateDefaultCropAdvice())
                .build();
    }

    private FarmingWeatherAdvice generateComprehensiveFarmingAdvice(
            CurrentWeather current, List<WeatherForecast> forecast,
            List<WeatherAlert> alerts, WeatherRequest request) {

        return FarmingWeatherAdvice.builder()
                .generalAdvice(generateGeneralAdvice(current))
                .immediateActions(generateImmediateActions(current))
                .weeklyPlan(generateWeeklyPlan(forecast))
                .irrigationAdvice(generateIrrigationAdvice(current))
                .pestDiseaseRisk(generatePestDiseaseRisk(current))
                .harvestRecommendation(generateHarvestRecommendation(forecast))
                .cropAdvice(generateDefaultCropAdvice())
                .build();
    }

    private String generateGeneralAdvice(CurrentWeather weather) {
        StringBuilder advice = new StringBuilder();

        if (weather.getTemperature() > 35) {
            advice.append("আজ খুব গরম পড়বে। ফসলে বেশি পানি দিন এবং দুপুরের রোদ থেকে রক্ষা করুন। ");
        } else if (weather.getTemperature() < 15) {
            advice.append("আজ ঠান্ডা আছে। ফসল ঠান্ডা থেকে রক্ষা করুন এবং পানি কম দিন। ");
        }

        if (weather.getHumidity() > 80) {
            advice.append("আর্দ্রতা বেশি, ছত্রাক রোগের সম্ভাবনা আছে। ");
        }

        if (weather.getRainfall() != null && weather.getRainfall() > 10) {
            advice.append("আজ বৃষ্টি হয়েছে। জমিতে পানি জমতে দিবেন না। ");
        }

        return advice.length() > 0 ? advice.toString() : "আজকের আবহাওয়া কৃষিকাজের জন্য উপযুক্ত।";
    }

    private List<String> generateImmediateActions(CurrentWeather weather) {
        List<String> actions = new ArrayList<>();

        if (weather.getTemperature() > 35) {
            actions.add("সকাল বা বিকালে সেচ দিন");
            actions.add("ফসলে ছায়ার ব্যবস্থা করুন");
        }

        if (weather.getHumidity() > 80) {
            actions.add("ছত্রাকনাশক স্প্রে করুন");
            actions.add("জমিতে বাতাস চলাচলের ব্যবস্থা করুন");
        }

        if (weather.getRainfall() != null && weather.getRainfall() > 20) {
            actions.add("জমি থেকে অতিরিক্ত পানি বের করুন");
            actions.add("নালা পরিষ্কার করুন");
        }

        if (actions.isEmpty()) {
            actions.add("নিয়মিত ফসল পরিদর্শন করুন");
        }

        return actions;
    }

    private String generateIrrigationAdvice(CurrentWeather weather) {
        if (weather.getRainfall() != null && weather.getRainfall() > 15) {
            return "আজ বৃষ্টি হয়েছে, তাই সেচের প্রয়োজন নেই। আগামীকাল মাটির অবস্থা দেখে সেচ দিন।";
        }

        if (weather.getTemperature() > 32 && weather.getHumidity() < 60) {
            return "গরম এবং শুষ্ক আবহাওয়া। সকাল বা সন্ধ্যায় পর্যাপ্ত সেচ দিন।";
        }

        if (weather.getHumidity() > 85) {
            return "আর্দ্রতা বেশি। সেচ কম দিন এবং মাটিতে জল জমতে দিবেন না।";
        }

        return "মাটির অবস্থা দেখে প্রয়োজন অনুযায়ী সেচ দিন।";
    }

    private String generatePestDiseaseRisk(CurrentWeather weather) {
        StringBuilder risk = new StringBuilder();

        if (weather.getHumidity() > 80 && weather.getTemperature() > 25) {
            risk.append("ছত্রাক রোগের ঝুঁকি বেশি। ");
        }

        if (weather.getTemperature() > 30 && weather.getHumidity() < 70) {
            risk.append("পোকামাকড়ের আক্রমণের সম্ভাবনা আছে। ");
        }

        if (weather.getRainfall() != null && weather.getRainfall() > 25) {
            risk.append("অতিরিক্ত বৃষ্টির কারণে রোগের ঝুঁকি বেড়েছে। ");
        }

        return risk.length() > 0 ? risk.toString() + "নিয়মিত ফসল পরিদর্শন করুন।"
                : "রোগ ও পোকার ঝুঁকি কম। তবে নিয়মিত পরিদর্শন করুন।";
    }

    private List<String> generateWeeklyPlan(List<WeatherForecast> forecast) {
        List<String> plan = new ArrayList<>();

        for (int i = 0; i < forecast.size(); i++) {
            WeatherForecast day = forecast.get(i);
            StringBuilder dayPlan = new StringBuilder();

            dayPlan.append(day.getDate().toString()).append(": ");

            if (day.getRainfall() > 10) {
                dayPlan.append("বৃষ্টির দিন - জমি পরিদর্শন করুন");
            } else if (day.getMaxTemp() > 35) {
                dayPlan.append("গরমের দিন - সকালে সেচ দিন");
            } else if (day.getMaxTemp() < 20) {
                dayPlan.append("ঠান্ডার দিন - ফসল রক্ষা করুন");
            } else {
                dayPlan.append("স্বাভাবিক কৃষিকাজ করুন");
            }

            plan.add(dayPlan.toString());
        }

        return plan;
    }

    private String generateHarvestRecommendation(List<WeatherForecast> forecast) {
        boolean heavyRainExpected = forecast.stream()
                .anyMatch(day -> day.getRainfall() > 20);

        boolean heatWaveExpected = forecast.stream()
                .anyMatch(day -> day.getMaxTemp() > 38);

        if (heavyRainExpected) {
            return "আগামী দিনগুলোতে ভারী বৃষ্টির সম্ভাবনা। পাকা ফসল তাড়াতাড়ি কেটে নিন।";
        }

        if (heatWaveExpected) {
            return "তাপপ্রবাহের আগে পাকা ফসল কাটার চেষ্টা করুন।";
        }

        return "আবহাওয়া ফসল কাটার জন্য উপযুক্ত। উপযুক্ত সময়ে ফসল কাটুন।";
    }

    private List<CropSpecificAdvice> generateDefaultCropAdvice() {
        List<CropSpecificAdvice> advice = new ArrayList<>();

        advice.add(CropSpecificAdvice.builder()
                .cropName("Rice")
                .cropNameBangla("ধান")
                .advice("নিয়মিত পানির ব্যবস্থা রাখুন এবং আগাছা পরিষ্কার করুন")
                .riskLevel("LOW")
                .actions(Arrays.asList("সেচ দিন", "সার প্রয়োগ করুন"))
                .build());

        advice.add(CropSpecificAdvice.builder()
                .cropName("Wheat")
                .cropNameBangla("গম")
                .advice("মাটির আর্দ্রতা ঠিক রাখুন এবং রোগ দমনের ব্যবস্থা করুন")
                .riskLevel("MEDIUM")
                .actions(Arrays.asList("পরিমিত সেচ", "ছত্রাকনাশক প্রয়োগ"))
                .build());

        advice.add(CropSpecificAdvice.builder()
                .cropName("Vegetables")
                .cropNameBangla("সবজি")
                .advice("নিয়মিত পরিচর্যা করুন এবং পোকামাকড়ের আক্রমণ থেকে রক্ষা করুন")
                .riskLevel("MEDIUM")
                .actions(Arrays.asList("নিয়মিত সেচ", "জৈব সার প্রয়োগ"))
                .build());

        return advice;
    }

    private String generateDailyFarmingTips(String condition, double maxTemp, double rainfall) {
        StringBuilder tips = new StringBuilder();

        if (condition.toLowerCase().contains("rain") || rainfall > 5) {
            tips.append("বৃষ্টির দিন - জমিতে পানি জমতে দিবেন না। ");
        } else if (maxTemp > 35) {
            tips.append("গরমের দিন - সকাল বা বিকালে সেচ দিন। ");
        } else if (maxTemp < 20) {
            tips.append("ঠান্ডার দিন - ফসল ঠান্ডা থেকে রক্ষা করুন। ");
        }

        tips.append("নিয়মিত ফসল পরিদর্শন করুন।");

        return tips.toString();
    }

    private WeatherResponse createErrorWeatherResponse(WeatherRequest request) {
        return WeatherResponse.builder()
                .location(request.getLocation())
                .language(request.getLanguage())
                .lastUpdated(LocalDateTime.now())
                .dataSource("Error - Using Default Data")
                .currentWeather(getDefaultWeatherForBangladesh())
                .forecast(generateDefaultForecast())
                .alerts(new ArrayList<>())
                .metadata(Map.of("error", "Unable to fetch live weather data"))
                .build();
    }

    // Additional fetch methods for completeness
    private List<WeatherForecast> fetchForecastFromWeatherAPI(String location) {
        if (config.getWeatherApiKey() == null || config.getWeatherApiKey().isEmpty()) {
            throw new RuntimeException("WeatherAPI key not configured");
        }

        String url = String.format("%s/forecast.json?key=%s&q=%s,Bangladesh&days=%d&aqi=no&alerts=no",
                config.getWeatherApiBaseUrl(),
                config.getWeatherApiKey(),
                URLEncoder.encode(location, StandardCharsets.UTF_8),
                config.getForecastDays());

        ResponseEntity<Map> response = weatherRestTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return parseWeatherAPIForecast(response.getBody());
        }

        throw new RuntimeException("Failed to fetch forecast from WeatherAPI");
    }

    private List<WeatherForecast> fetchForecastFromOpenWeatherMap(String location) {
        if (config.getOpenWeatherApiKey() == null || config.getOpenWeatherApiKey().isEmpty()) {
            throw new RuntimeException("OpenWeatherMap API key not configured");
        }

        String url = String.format("%s/forecast?q=%s,BD&appid=%s&units=metric&cnt=40",
                config.getOpenWeatherBaseUrl(),
                URLEncoder.encode(location, StandardCharsets.UTF_8),
                config.getOpenWeatherApiKey());

        ResponseEntity<Map> response = weatherRestTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return parseOpenWeatherForecast(response.getBody());
        }

        throw new RuntimeException("Failed to fetch forecast from OpenWeatherMap");
    }

    private List<WeatherForecast> parseWeatherAPIForecast(Map<String, Object> data) {
        Map<String, Object> forecast = (Map<String, Object>) data.get("forecast");
        List<Map<String, Object>> forecastdays = (List<Map<String, Object>>) forecast.get("forecastday");

        return forecastdays.stream().map(day -> {
            Map<String, Object> dayData = (Map<String, Object>) day.get("day");
            Map<String, Object> condition = (Map<String, Object>) dayData.get("condition");
            String conditionText = condition.get("text").toString();

            return WeatherForecast.builder()
                    .date(LocalDate.parse((String) day.get("date")))
                    .maxTemp(((Number) dayData.get("maxtemp_c")).doubleValue())
                    .minTemp(((Number) dayData.get("mintemp_c")).doubleValue())
                    .condition(conditionText)
                    .conditionBangla(translateWeatherCondition(conditionText))
                    .humidity(((Number) dayData.get("avghumidity")).doubleValue())
                    .rainfall(((Number) dayData.get("totalprecip_mm")).doubleValue())
                    .uvIndex(((Number) dayData.get("uv")).doubleValue())
                    .iconUrl((String) condition.get("icon"))
                    .farmingTips(generateDailyFarmingTips(conditionText,
                            ((Number) dayData.get("maxtemp_c")).doubleValue(),
                            ((Number) dayData.get("totalprecip_mm")).doubleValue()))
                    .build();
        }).collect(Collectors.toList());
    }

    private List<WeatherForecast> parseOpenWeatherForecast(Map<String, Object> data) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");

        // Group by date and create daily forecasts
        Map<LocalDate, List<Map<String, Object>>> groupedByDate = list.stream()
                .collect(Collectors.groupingBy(item -> {
                    long timestamp = ((Number) item.get("dt")).longValue();
                    return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofHours(6)).toLocalDate();
                }));

        return groupedByDate.entrySet().stream()
                .limit(config.getForecastDays())
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Map<String, Object>> dayData = entry.getValue();

                    // Calculate daily values
                    double maxTemp = dayData.stream()
                            .mapToDouble(item -> ((Number) ((Map) item.get("main")).get("temp_max")).doubleValue())
                            .max().orElse(30.0);

                    double minTemp = dayData.stream()
                            .mapToDouble(item -> ((Number) ((Map) item.get("main")).get("temp_min")).doubleValue())
                            .min().orElse(20.0);

                    String condition = ((List<Map>) dayData.get(0).get("weather")).get(0).get("main").toString();

                    return WeatherForecast.builder()
                            .date(date)
                            .maxTemp(maxTemp)
                            .minTemp(minTemp)
                            .condition(condition)
                            .conditionBangla(translateWeatherCondition(condition))
                            .farmingTips(generateDailyFarmingTips(condition, maxTemp, 0.0))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
