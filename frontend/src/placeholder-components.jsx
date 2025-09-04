import React from "react";

// Placeholder component factory
const createPlaceholder = (name, title, description) => {
	const Component = () => (
		<div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
			<div className="text-center max-w-md">
				<div className="text-6xl mb-4">🚧</div>
				<h1 className="text-2xl font-bold text-gray-900 mb-2">{title}</h1>
				<p className="text-gray-600 mb-4">{description}</p>
				<div className="bg-white rounded-xl p-6 shadow-sm border">
					<p className="text-sm text-gray-500">
						This {name} page is under development and will be available soon.
					</p>
				</div>
			</div>
		</div>
	);
	Component.displayName = name;
	return Component;
};

// Export all placeholder components
export const FarmerMarket = createPlaceholder(
	"FarmerMarket",
	"Farmer Market",
	"Browse buyer requirements and market opportunities"
);
export const FarmerAITools = createPlaceholder(
	"FarmerAITools",
	"AI Tools",
	"Chatbot, pest detection, and voice features"
);
export const FarmerWeather = createPlaceholder(
	"FarmerWeather",
	"Weather Dashboard",
	"Real-time weather alerts and forecasts"
);
export const FarmerEducation = createPlaceholder(
	"FarmerEducation",
	"Educational Content",
	"Bengali courses and agricultural news"
);

export const BuyerMarketplace = createPlaceholder(
	"BuyerMarketplace",
	"Buyer Marketplace",
	"Browse available crops from farmers"
);
export const BuyerRequirements = createPlaceholder(
	"BuyerRequirements",
	"Post Requirements",
	"Let farmers know what you need"
);
export const BuyerContacts = createPlaceholder(
	"BuyerContacts",
	"Farmer Contacts",
	"Manage your farmer relationships"
);
export const BuyerAnalytics = createPlaceholder(
	"BuyerAnalytics",
	"Purchase Analytics",
	"Track your buying patterns and costs"
);

export const GovernmentFarmers = createPlaceholder(
	"GovernmentFarmers",
	"Farmer Support",
	"Respond to farmer queries and provide assistance"
);
export const GovernmentMonitoring = createPlaceholder(
	"GovernmentMonitoring",
	"Agricultural Monitoring",
	"Monitor pest/disease trends and farming patterns"
);
export const GovernmentContent = createPlaceholder(
	"GovernmentContent",
	"Content Management",
	"Manage educational content and announcements"
);
export const GovernmentSchemes = createPlaceholder(
	"GovernmentSchemes",
	"Government Schemes",
	"Manage subsidy and loan programs"
);
export const GovernmentAnalytics = createPlaceholder(
	"GovernmentAnalytics",
	"Regional Analytics",
	"View regional farming statistics and trends"
);

export const AdminUsers = createPlaceholder(
	"AdminUsers",
	"User Management",
	"Manage all platform users and permissions"
);
export const AdminContent = createPlaceholder(
	"AdminContent",
	"Content Moderation",
	"Moderate platform content and posts"
);
export const AdminSystem = createPlaceholder(
	"AdminSystem",
	"System Health",
	"Monitor platform performance and issues"
);
export const AdminAnalytics = createPlaceholder(
	"AdminAnalytics",
	"Platform Analytics",
	"View platform-wide usage statistics"
);
export const AdminSettings = createPlaceholder(
	"AdminSettings",
	"Platform Settings",
	"Configure platform-wide settings"
);
