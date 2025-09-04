// User role constants
export const USER_ROLES = {
	FARMER: "farmer",
	BUYER: "buyer",
	GOVERNMENT: "admin", // Krishi Odhidoptor officials
	ADMIN: "super_admin", // Platform administrators
};

// Role permissions and access levels
export const ROLE_PERMISSIONS = {
	[USER_ROLES.FARMER]: {
		dashboard: [
			"overview",
			"crops",
			"market",
			"ai-tools",
			"weather",
			"education",
		],
		features: [
			"post_crops",
			"view_buyers",
			"use_chatbot",
			"pest_detection",
			"voice_interaction",
			"weather_alerts",
			"educational_content",
			"government_schemes",
		],
		routes: [
			"/dashboard",
			"/farmer",
			"/crops",
			"/market",
			"/ai-assistant",
			"/weather",
			"/education",
		],
	},
	[USER_ROLES.BUYER]: {
		dashboard: [
			"overview",
			"marketplace",
			"requirements",
			"contacts",
			"analytics",
		],
		features: [
			"view_farmer_posts",
			"post_requirements",
			"contact_farmers",
			"view_analytics",
			"manage_orders",
		],
		routes: [
			"/dashboard",
			"/buyer",
			"/marketplace",
			"/requirements",
			"/contacts",
		],
	},
	[USER_ROLES.GOVERNMENT]: {
		dashboard: [
			"overview",
			"farmers",
			"monitoring",
			"content",
			"schemes",
			"analytics",
		],
		features: [
			"respond_to_queries",
			"publish_notices",
			"monitor_trends",
			"upload_content",
			"manage_schemes",
			"view_system_analytics",
		],
		routes: [
			"/dashboard",
			"/admin",
			"/farmers",
			"/monitoring",
			"/content-management",
			"/schemes",
		],
	},
	[USER_ROLES.ADMIN]: {
		dashboard: [
			"overview",
			"users",
			"content",
			"system",
			"analytics",
			"settings",
		],
		features: [
			"manage_all_users",
			"moderate_content",
			"system_administration",
			"view_all_analytics",
			"manage_platform_settings",
			"ai_model_management",
		],
		routes: [
			"/dashboard",
			"/admin",
			"/users",
			"/content",
			"/system",
			"/analytics",
			"/settings",
		],
	},
};

// Dashboard tab configurations for each role
export const DASHBOARD_TABS = {
	[USER_ROLES.FARMER]: [
		{
			id: "overview",
			label: "Overview",
			icon: "BarChart3",
			path: "/dashboard",
			description: "Farm overview and quick stats",
		},
		{
			id: "crops",
			label: "My Crops",
			icon: "Sprout",
			path: "/dashboard/crops",
			description: "Manage your crop listings",
		},
		{
			id: "market",
			label: "Marketplace",
			icon: "ShoppingCart",
			path: "/dashboard/market",
			description: "Browse buyer requirements",
		},
		{
			id: "ai-tools",
			label: "AI Assistant",
			icon: "Bot",
			path: "/dashboard/ai-tools",
			description: "Chatbot, pest detection, voice features",
		},
		{
			id: "weather",
			label: "Weather",
			icon: "Cloud",
			path: "/dashboard/weather",
			description: "Weather alerts and forecasts",
		},
		{
			id: "education",
			label: "Education",
			icon: "BookOpen",
			path: "/dashboard/education",
			description: "Courses and agricultural news",
		},
	],
	[USER_ROLES.BUYER]: [
		{
			id: "overview",
			label: "Overview",
			icon: "BarChart3",
			path: "/dashboard",
			description: "Purchase overview and analytics",
		},
		{
			id: "marketplace",
			label: "Marketplace",
			icon: "Store",
			path: "/dashboard/marketplace",
			description: "Browse available crops",
		},
		{
			id: "requirements",
			label: "My Requirements",
			icon: "ClipboardList",
			path: "/dashboard/requirements",
			description: "Post and manage crop requirements",
		},
		{
			id: "contacts",
			label: "Farmer Contacts",
			icon: "Users",
			path: "/dashboard/contacts",
			description: "Manage farmer relationships",
		},
		{
			id: "analytics",
			label: "Analytics",
			icon: "TrendingUp",
			path: "/dashboard/analytics",
			description: "Purchase analytics and trends",
		},
	],
	[USER_ROLES.GOVERNMENT]: [
		{
			id: "overview",
			label: "Overview",
			icon: "BarChart3",
			path: "/dashboard",
			description: "System overview and farmer statistics",
		},
		{
			id: "farmers",
			label: "Farmer Support",
			icon: "Users",
			path: "/dashboard/farmers",
			description: "Respond to farmer queries",
		},
		{
			id: "monitoring",
			label: "Monitoring",
			icon: "Activity",
			path: "/dashboard/monitoring",
			description: "Monitor pest/disease trends",
		},
		{
			id: "content",
			label: "Content Management",
			icon: "FileText",
			path: "/dashboard/content",
			description: "Manage educational content",
		},
		{
			id: "schemes",
			label: "Government Schemes",
			icon: "Banknote",
			path: "/dashboard/schemes",
			description: "Manage subsidy and loan information",
		},
		{
			id: "analytics",
			label: "Analytics",
			icon: "PieChart",
			path: "/dashboard/analytics",
			description: "Regional farming analytics",
		},
	],
	[USER_ROLES.ADMIN]: [
		{
			id: "overview",
			label: "Overview",
			icon: "BarChart3",
			path: "/dashboard",
			description: "Platform overview and statistics",
		},
		{
			id: "users",
			label: "User Management",
			icon: "UserCog",
			path: "/dashboard/users",
			description: "Manage all platform users",
		},
		{
			id: "content",
			label: "Content Moderation",
			icon: "Shield",
			path: "/dashboard/content",
			description: "Moderate platform content",
		},
		{
			id: "system",
			label: "System Health",
			icon: "Server",
			path: "/dashboard/system",
			description: "Monitor system performance",
		},
		{
			id: "analytics",
			label: "Analytics",
			icon: "TrendingUp",
			path: "/dashboard/analytics",
			description: "Platform-wide analytics",
		},
		{
			id: "settings",
			label: "Settings",
			icon: "Settings",
			path: "/dashboard/settings",
			description: "Platform configuration",
		},
	],
};

// Route access helper functions
export const hasRouteAccess = (userRole, route) => {
	return ROLE_PERMISSIONS[userRole]?.routes.includes(route) || false;
};

export const getUserDashboardTabs = (userRole) => {
	return DASHBOARD_TABS[userRole] || [];
};

export const hasFeatureAccess = (userRole, feature) => {
	return ROLE_PERMISSIONS[userRole]?.features.includes(feature) || false;
};
