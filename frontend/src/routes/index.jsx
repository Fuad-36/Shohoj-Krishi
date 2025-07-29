import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import { USER_ROLES, hasRouteAccess } from "../constants/roles";

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

const AppRoutes = () => {
	const { user, isAuthenticated } = useAuth();

	return (
		<Routes>
			{/* Public Routes */}
			<Route path="/" element={<MainLayout />}>
				<Route index element={<HomePage />} />
				<Route path="about" element={<AboutPage />} />
				<Route path="features" element={<FeaturesPage />} />
				<Route path="contact" element={<ContactPage />} />
			</Route>

			{/* Auth Routes */}
			<Route path="/auth" element={<AuthLayout />}>
				<Route path="signin" element={<SignIn />} />
				<Route path="signup" element={<SignUp />} />
				<Route path="forgot-password" element={<ForgotPassword />} />
			</Route>

			{/* Legacy auth routes (for compatibility) */}
			<Route path="/signin" element={<Navigate to="/auth/signin" replace />} />
			<Route path="/signup" element={<Navigate to="/auth/signup" replace />} />

			{/* Protected Dashboard Routes */}
			<Route
				path="/dashboard"
				element={
					<ProtectedRoute>
						<DashboardLayout />
					</ProtectedRoute>
				}
			>
				{/* Main Dashboard */}
				<Route index element={<DashboardOverview />} />

				{/* Farmer Routes */}
				<Route
					path="crops"
					element={
						<RoleBasedRoute allowedRoles={[USER_ROLES.FARMER]}>
							<FarmerCrops />
						</RoleBasedRoute>
					}
				/>
				<Route
					path="market"
					element={
						<RoleBasedRoute allowedRoles={[USER_ROLES.FARMER]}>
							<FarmerMarket />
						</RoleBasedRoute>
					}
				/>
				<Route
					path="ai-tools"
					element={
						<RoleBasedRoute allowedRoles={[USER_ROLES.FARMER]}>
							<FarmerAITools />
						</RoleBasedRoute>
					}
				/>
				<Route
					path="weather"
					element={
						<RoleBasedRoute allowedRoles={[USER_ROLES.FARMER]}>
							<FarmerWeather />
						</RoleBasedRoute>
					}
				/>
				<Route
					path="education"
					element={
						<RoleBasedRoute allowedRoles={[USER_ROLES.FARMER]}>
							<FarmerEducation />
						</RoleBasedRoute>
					}
				/>

				{/* Buyer Routes */}
				<Route
					path="marketplace"
					element={
						<RoleBasedRoute allowedRoles={[USER_ROLES.BUYER]}>
							<BuyerMarketplace />
						</RoleBasedRoute>
					}
				/>
				<Route
					path="requirements"
					element={
						<RoleBasedRoute allowedRoles={[USER_ROLES.BUYER]}>
							<BuyerRequirements />
						</RoleBasedRoute>
					}
				/>
				<Route
					path="contacts"
					element={
						<RoleBasedRoute allowedRoles={[USER_ROLES.BUYER]}>
							<BuyerContacts />
						</RoleBasedRoute>
					}
				/>
				<Route
					path="analytics"
					element={
						<RoleBasedRoute
							allowedRoles={[
								USER_ROLES.BUYER,
								USER_ROLES.GOVERNMENT,
								USER_ROLES.ADMIN,
							]}
						>
							{user?.userType === USER_ROLES.BUYER && <BuyerAnalytics />}
							{user?.userType === USER_ROLES.GOVERNMENT && (
								<GovernmentAnalytics />
							)}
							{user?.userType === USER_ROLES.ADMIN && <AdminAnalytics />}
						</RoleBasedRoute>
					}
				/>

				{/* Government Routes */}
				<Route
					path="farmers"
					element={
						<RoleBasedRoute
							allowedRoles={[USER_ROLES.GOVERNMENT, USER_ROLES.ADMIN]}
						>
							<GovernmentFarmers />
						</RoleBasedRoute>
					}
				/>
				<Route
					path="monitoring"
					element={
						<RoleBasedRoute
							allowedRoles={[USER_ROLES.GOVERNMENT, USER_ROLES.ADMIN]}
						>
							<GovernmentMonitoring />
						</RoleBasedRoute>
					}
				/>
				<Route
					path="content"
					element={
						<RoleBasedRoute
							allowedRoles={[USER_ROLES.GOVERNMENT, USER_ROLES.ADMIN]}
						>
							{user?.userType === USER_ROLES.GOVERNMENT && (
								<GovernmentContent />
							)}
							{user?.userType === USER_ROLES.ADMIN && <AdminContent />}
						</RoleBasedRoute>
					}
				/>
				<Route
					path="schemes"
					element={
						<RoleBasedRoute
							allowedRoles={[USER_ROLES.GOVERNMENT, USER_ROLES.ADMIN]}
						>
							<GovernmentSchemes />
						</RoleBasedRoute>
					}
				/>

				{/* Admin Routes */}
				<Route
					path="users"
					element={
						<RoleBasedRoute allowedRoles={[USER_ROLES.ADMIN]}>
							<AdminUsers />
						</RoleBasedRoute>
					}
				/>
				<Route
					path="system"
					element={
						<RoleBasedRoute allowedRoles={[USER_ROLES.ADMIN]}>
							<AdminSystem />
						</RoleBasedRoute>
					}
				/>
				<Route
					path="settings"
					element={
						<RoleBasedRoute allowedRoles={[USER_ROLES.ADMIN]}>
							<AdminSettings />
						</RoleBasedRoute>
					}
				/>
			</Route>

			{/* Legacy routes for backward compatibility */}
			<Route path="/farmer" element={<Navigate to="/dashboard" replace />} />
			<Route path="/buyer" element={<Navigate to="/dashboard" replace />} />
			<Route path="/admin" element={<Navigate to="/dashboard" replace />} />

			{/* Error Routes */}
			<Route path="/unauthorized" element={<Unauthorized />} />
			<Route path="/404" element={<NotFound />} />

			{/* Catch all route */}
			<Route path="*" element={<Navigate to="/404" replace />} />
		</Routes>
	);
};

export default AppRoutes;
