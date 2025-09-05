import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";

// Import translation files
import translationEN from "./locales/en/translation.json";
import translationBN from "./locales/bn/translation.json";

// Translation resources
const resources = {
	en: {
		translation: translationEN,
	},
	bn: {
		translation: translationBN,
	},
};

// i18next configuration
i18n
	.use(LanguageDetector) // Detect user language
	.use(initReactI18next) // Pass i18n down to react-i18next
	.init({
		resources,

		// Default language (Bengali as requested for semi-literate users)
		lng: "bn",
		fallbackLng: "en", // Fallback to English if translation missing

		// Language detection options
		detection: {
			order: ["localStorage", "navigator", "htmlTag"],
			caches: ["localStorage"], // Cache language preference
			lookupLocalStorage: "preferredLanguage", // Key for localStorage
		},

		interpolation: {
			escapeValue: false, // React already does escaping
		},

		// Development options
		debug: process.env.NODE_ENV === "development",

		// Namespace and key separator
		keySeparator: ".",
		nsSeparator: false, // We don't use namespaces in this simple setup

		// React-specific options
		react: {
			useSuspense: false, // Disable suspense for better compatibility
		},
	});

export default i18n;
