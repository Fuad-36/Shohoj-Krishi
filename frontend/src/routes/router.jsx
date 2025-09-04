import React from "react";
import { createBrowserRouter } from "react-router-dom";

// Layout Components
import MainLayout from "../components/layouts/MainLayout";
import DashboardLayout from "../components/layouts/DashboardLayout";
import AuthLayout from "../components/layouts/AuthLayout";

// Protection Components
import ProtectedRoute from "../components/auth/ProtectedRoute";
import RoleBasedRoute from "../components/auth/RoleBasedRoute";

// Public Pages
import HomePage from "../pages/public/HomePage";
import AboutPage from "../pages/public/AboutPage";
import FeaturesPage from "../pages/public/FeaturesPage";
import ContactPage from "../pages/public/ContactPage";

// Auth Pages
import SignIn from "../pages/auth/SignIn";
import SignUp from "../pages/auth/SignUp";
import VerifyOTP from "../pages/auth/VerifyOTP";
import ForgotPassword from "../pages/auth/ForgotPassword";

// Dashboard Pages
import DashboardOverview from "../pages/dashboard/DashboardOverview";

// Farmer Pages
import FarmerCrops from "../pages/dashboard/farmer/FarmerCrops";
import {
	FarmerMarket,
	FarmerAITools,
	FarmerWeather,
	FarmerEducation,
	BuyerMarketplace,
	BuyerRequirements,
	BuyerContacts,
	BuyerAnalytics,
	GovernmentFarmers,
	GovernmentMonitoring,
	GovernmentContent,
	GovernmentSchemes,
	GovernmentAnalytics,
	AdminUsers,
	AdminContent,
	AdminSystem,
	AdminAnalytics,
	AdminSettings,
} from "../placeholder-components.jsx";

// Error Pages
import NotFound from "../pages/error/NotFound";
import Unauthorized from "../pages/error/Unauthorized";

const router = createBrowserRouter([
	{
		path: "/",
		element: <MainLayout />,
		children: [
			{
				index: true,
				element: <HomePage />,
			},
			{
				path: "about",
				element: <AboutPage />,
			},
			{
				path: "features",
				element: <FeaturesPage />,
			},
			{
				path: "contact",
				element: <ContactPage />,
			},
		],
	},
	{
		path: "auth",
		element: <AuthLayout />,
		children: [
			{
				path: "signin",
				element: <SignIn />,
			},
			{
				path: "signup",
				element: <SignUp />,
			},
			{
				path: "verify-otp",
				element: <VerifyOTP />,
			},
			{
				path: "forgot-password",
				element: <ForgotPassword />,
			},
		],
	},
	// {
	// 	path: "dashboard",
	// 	element: <DashboardLayout />,
	// 	children: [
	// 		{
	// 			index: true,
	// 			element: <DashboardOverview />,
	// 		},
	// 		{
	// 			path: "farmer",
	// 			element: <DashboardLayout />,
	// 			children: [
	// 				{
	// 					path: "crops",
	// 					element: <FarmerCrops />,
	// 				},
	// 				{
	// 					path: "market",
	// 					element: <FarmerMarket />,
	// 				},
	// 				{
	// 					path: "ai-tools",
	// 					element: <FarmerAITools />,
	// 				},
	// 				{
	// 					path: "weather",
	// 					element: <FarmerWeather />,
	// 				},
	// 				{
	// 					path: "education",
	// 					element: <FarmerEducation />,
	// 				},
	// 			],
	// 		},
	// 		{
	// 			path: "buyer",
	// 			element: <DashboardLayout />,
	// 			children: [
	// 				{
	// 					path: "/dashboard/buyer/marketplace",
	// 					element: <BuyerMarketplace />,
	// 				},
	// 				{
	// 					path: "/dashboard/buyer/requirements",
	// 					element: <BuyerRequirements />,
	// 				},
	// 				{
	// 					path: "/dashboard/buyer/contacts",
	// 					element: <BuyerContacts />,
	// 				},
	// 			],
	// 		},
	// 		{
	// 			path: "/dashboard/government",
	// 			element: <DashboardLayout />,
	// 			children: [
	// 				{
	// 					path: "/dashboard/government/farmers",
	// 					element: <GovernmentFarmers />,
	// 				},
	// 				{
	// 					path: "/dashboard/government/monitoring",
	// 					element: <GovernmentMonitoring />,
	// 				},
	// 				{
	// 					path: "/dashboard/government/content",
	// 					element: <GovernmentContent />,
	// 				},
	// 				{
	// 					path: "/dashboard/government/schemes",
	// 					element: <GovernmentSchemes />,
	// 				},
	// 				{
	// 					path: "/dashboard/government/analytics",
	// 					element: <GovernmentAnalytics />,
	// 				},
	// 			],
	// 		},
	// 		{
	// 			path: "/dashboard/admin",
	// 			element: <DashboardLayout />,
	// 			children: [
	// 				{
	// 					path: "/dashboard/admin/users",
	// 					element: <AdminUsers />,
	// 				},
	// 				{
	// 					path: "/dashboard/admin/system",
	// 					element: <AdminSystem />,
	// 				},
	// 				{
	// 					path: "/dashboard/admin/settings",
	// 					element: <AdminSettings />,
	// 				},
	// 			],
	// 		},
	// 	],
	// },
	{
		path: "*",
		element: <NotFound />,
	},
	{
		path: "/unauthorized",
		element: <Unauthorized />,
	},
]);

export default router;
