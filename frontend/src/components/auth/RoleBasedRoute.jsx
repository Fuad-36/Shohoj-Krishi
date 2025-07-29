import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

const RoleBasedRoute = ({
	children,
	allowedRoles = [],
	redirectTo = "/unauthorized",
}) => {
	const { user, isAuthenticated, isLoading } = useAuth();

	// Show loading while checking authentication
	if (isLoading) {
		return (
			<div className="min-h-screen flex items-center justify-center bg-primary-50">
				<div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
			</div>
		);
	}

	// Redirect to sign in if not authenticated
	if (!isAuthenticated) {
		return <Navigate to="/auth/signin" replace />;
	}

	// Check if user has required role
	const hasRequiredRole =
		allowedRoles.length === 0 || allowedRoles.includes(user?.userType);

	if (!hasRequiredRole) {
		return <Navigate to={redirectTo} replace />;
	}

	return children;
};

export default RoleBasedRoute;
