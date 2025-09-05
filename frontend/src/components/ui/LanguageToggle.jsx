import React, { useState } from "react";
import { Globe, ChevronDown } from "lucide-react";
import { useTranslation } from "react-i18next";

const LanguageToggle = () => {
	const [isOpen, setIsOpen] = useState(false);
	const { i18n } = useTranslation();

	const languages = [
		{ code: "bn", name: "বাংলা", flag: "🇧🇩" },
		{ code: "en", name: "English", flag: "🇺🇸" },
	];

	const currentLang = languages.find((lang) => lang.code === i18n.language);

	const handleLanguageChange = (langCode) => {
		i18n.changeLanguage(langCode);
		setIsOpen(false);
	};

	return (
		<div className="relative">
			<button
				onClick={() => setIsOpen(!isOpen)}
				className="flex items-center space-x-2 px-3 py-2 rounded-lg hover:bg-gray-100 transition-colors text-sm font-medium text-gray-700"
				aria-label="Change language"
			>
				<Globe className="w-4 h-4" />
				<span className="hidden sm:inline">{currentLang?.name}</span>
				<span className="sm:hidden">{currentLang?.flag}</span>
				<ChevronDown className="w-3 h-3" />
			</button>

			{/* Dropdown Menu */}
			{isOpen && (
				<div className="absolute right-0 mt-2 w-40 bg-white rounded-xl shadow-lg border border-gray-200 py-2 z-50">
					{languages.map((language) => (
						<button
							key={language.code}
							onClick={() => handleLanguageChange(language.code)}
							className={`w-full flex items-center px-4 py-2 text-sm hover:bg-gray-50 transition-colors ${
								i18n.language === language.code
									? "bg-primary-50 text-primary-700 font-medium"
									: "text-gray-700"
							}`}
						>
							<span className="mr-3 text-lg">{language.flag}</span>
							<span>{language.name}</span>
							{i18n.language === language.code && (
								<span className="ml-auto text-primary-600">✓</span>
							)}
						</button>
					))}
				</div>
			)}

			{/* Backdrop */}
			{isOpen && (
				<div className="fixed inset-0 z-40" onClick={() => setIsOpen(false)} />
			)}
		</div>
	);
};

export default LanguageToggle;
