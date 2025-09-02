import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import LoadingSpinner from "../ui/LoadingSpinner";

const ProtectedRoute = ({ children, requiredRole = null }) => {
	const { isAuthenticated, isLoading, user } = useAuth();
	const location = useLocation();

	// Show loading spinner while checking authentication
	if (isLoading) {
		return <LoadingSpinner />;
	}

	// Redirect to sign in if not authenticated
	if (!isAuthenticated) {
		return <Navigate to="/signin" state={{ from: location }} replace />;
	}

	// Check role-based access if required
	if (requiredRole && user?.userType !== requiredRole) {
		return (
			<div className="min-h-screen flex items-center justify-center bg-primary-50">
				<div className="bg-white rounded-xl shadow-lg p-8 max-w-md text-center">
					<div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
						<svg
							className="w-8 h-8 text-red-600"
							fill="none"
							stroke="currentColor"
							viewBox="0 0 24 24"
						>
							<path
								strokeLinecap="round"
								strokeLinejoin="round"
								strokeWidth={2}
								d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"
							/>
						</svg>
					</div>
					<h2 className="text-xl font-bold text-gray-900 mb-2">
						Access Denied
					</h2>
					<p className="text-gray-600 mb-4">
						You don't have permission to access this page. Required role:{" "}
						{requiredRole}
					</p>
					<p className="text-sm text-gray-500">
						Your current role: {user?.userType || "Unknown"}
					</p>
				</div>
			</div>
		);
	}

	return children;
};

export default ProtectedRoute;
