import React from "react";
import { useAuth } from "../../contexts/AuthContext";
import { Navigate, useLocation } from "react-router-dom";

const RoleBasedRoute = ({ children, allowedRoles = [] }) => {
	const { isAuthenticated, role, user } = useAuth();
	const location = useLocation();

	// If not authenticated, redirect to sign in
	if (!isAuthenticated) {
		return <Navigate to="/auth/signin" state={{ from: location }} replace />;
	}

	// Get current user role (prefer role from auth context, fallback to user.userType)
	const currentRole = role || user?.userType;

	// If no role specified, allow access
	if (allowedRoles.length === 0) {
		return children;
	}

	// Check if user's role is in allowed roles
	if (!allowedRoles.includes(currentRole)) {
		// Redirect to unauthorized page or main dashboard
		return <Navigate to="/dashboard" replace />;
	}

	return children;
};

export default RoleBasedRoute;
