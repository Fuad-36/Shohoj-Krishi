import axios from "axios";

// Base URL for your Spring Boot backend
const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

// Create axios instance
const apiClient = axios.create({
	baseURL: BASE_URL,
	timeout: 10000,
	headers: {
		"Content-Type": "application/json",
	},
});

// Request interceptor to add auth token
apiClient.interceptors.request.use(
	(config) => {
		const token = localStorage.getItem("authToken");
		if (token) {
			config.headers.Authorization = `Bearer ${token}`;
		}
		return config;
	},
	(error) => {
		return Promise.reject(error);
	}
);

// Response interceptor to handle errors
apiClient.interceptors.response.use(
	(response) => {
		return response;
	},
	(error) => {
		if (error.response?.status === 401) {
			// Token expired or invalid
			localStorage.removeItem("authToken");
			window.location.href = "/signin";
		}
		return Promise.reject(error);
	}
);

// Authentication API endpoints
export const authAPI = {
	// Register user - matches API documentation
	register: (userData) => apiClient.post("/auth/register", userData),

	// Verify OTP sent during registration
	verifyOTP: (otpData) => apiClient.post("/auth/verify-otp", otpData),

	// Resend OTP for email verification
	resendOTP: (email) =>
		apiClient.post(`/auth/resend-otp?email=${encodeURIComponent(email)}`),

	// Login user (to be implemented later when backend provides login endpoint)
	login: (credentials) => apiClient.post("/auth/login", credentials),

	// Verify token (to be implemented later)
	verifyToken: () => apiClient.get("/auth/verify"),

	// Logout user (to be implemented later)
	logout: () => apiClient.post("/auth/logout"),

	// Refresh token (to be implemented later)
	refreshToken: () => apiClient.post("/auth/refresh"),

	// Update user profile (to be implemented later)
	updateProfile: (updateData) => apiClient.put("/auth/profile", updateData),

	// Change password (to be implemented later)
	changePassword: (passwordData) =>
		apiClient.put("/auth/change-password", passwordData),

	// Forgot password (to be implemented later)
	forgotPassword: (email) => apiClient.post("/auth/forgot-password", { email }),

	// Reset password (to be implemented later)
	resetPassword: (resetData) =>
		apiClient.post("/auth/reset-password", resetData),
};

// General API endpoints (for other features)
export const farmAPI = {
	// Farm management endpoints
	getFarms: () => apiClient.get("/farms"),
	createFarm: (farmData) => apiClient.post("/farms", farmData),
	updateFarm: (farmId, farmData) => apiClient.put(`/farms/${farmId}`, farmData),
	deleteFarm: (farmId) => apiClient.delete(`/farms/${farmId}`),

	// Crop management
	getCrops: (farmId) => apiClient.get(`/farms/${farmId}/crops`),
	addCrop: (farmId, cropData) =>
		apiClient.post(`/farms/${farmId}/crops`, cropData),

	// Market endpoints
	getMarketPrices: () => apiClient.get("/market/prices"),
	createListing: (listingData) =>
		apiClient.post("/market/listings", listingData),
};

// Weather API
export const weatherAPI = {
	getCurrentWeather: (location) =>
		apiClient.get(`/weather/current?location=${location}`),
	getWeatherForecast: (location) =>
		apiClient.get(`/weather/forecast?location=${location}`),
	getWeatherAlerts: () => apiClient.get("/weather/alerts"),
};

// AI/ML API endpoints
export const aiAPI = {
	// Pest detection
	detectPest: (imageData) => {
		const formData = new FormData();
		formData.append("image", imageData);
		return apiClient.post("/ai/pest-detection", formData, {
			headers: {
				"Content-Type": "multipart/form-data",
			},
		});
	},

	// Disease detection
	detectDisease: (imageData) => {
		const formData = new FormData();
		formData.append("image", imageData);
		return apiClient.post("/ai/disease-detection", formData, {
			headers: {
				"Content-Type": "multipart/form-data",
			},
		});
	},

	// Chatbot
	chatbot: (message, language = "bn") =>
		apiClient.post("/ai/chatbot", { message, language }),

	// Voice processing
	processVoice: (audioData) => {
		const formData = new FormData();
		formData.append("audio", audioData);
		return apiClient.post("/ai/voice-processing", formData, {
			headers: {
				"Content-Type": "multipart/form-data",
			},
		});
	},
};

export default apiClient;
