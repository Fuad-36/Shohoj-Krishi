import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { Shield, ArrowLeft, LogOut } from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";
import Button from "../../components/ui/Button";

const Unauthorized = () => {
	const { user, logout } = useAuth();
	const navigate = useNavigate();

	const handleLogout = async () => {
		await logout();
		navigate("/");
	};

	return (
		<div className="min-h-screen bg-linear-to-br from-red-50 via-white to-orange-50 flex items-center justify-center p-4">
			<div className="text-center max-w-lg">
				{/* Unauthorized Illustration */}
				<div className="mb-8">
					<div className="inline-flex items-center justify-center w-20 h-20 bg-red-100 rounded-full mb-6">
						<Shield className="w-10 h-10 text-red-600" />
					</div>
					<h1 className="text-3xl font-bold text-gray-900 mb-2">
						Access Denied
					</h1>
					<p className="text-gray-600">
						You don't have permission to access this page. This area is
						restricted to specific user roles.
					</p>
				</div>

				{/* User Info */}
				{user && (
					<div className="bg-white rounded-xl p-6 shadow-sm border mb-6">
						<h3 className="font-semibold text-gray-900 mb-3">
							Your Account Information
						</h3>
						<div className="text-left space-y-2">
							<div className="flex justify-between">
								<span className="text-gray-600">Name:</span>
								<span className="font-medium">
									{user.firstName} {user.lastName}
								</span>
							</div>
							<div className="flex justify-between">
								<span className="text-gray-600">Role:</span>
								<span className="font-medium capitalize">{user.userType}</span>
							</div>
							<div className="flex justify-between">
								<span className="text-gray-600">Location:</span>
								<span className="font-medium">{user.location}</span>
							</div>
						</div>
					</div>
				)}

				{/* Role-specific messaging */}
				<div className="bg-amber-50 rounded-xl p-6 border border-amber-200 mb-6">
					<h3 className="font-semibold text-amber-800 mb-2">Need Access?</h3>
					<p className="text-amber-700 text-sm">
						{user?.userType === "farmer" &&
							"This page is for buyers or administrators. If you need to access buyer features, please contact support to upgrade your account."}
						{user?.userType === "buyer" &&
							"This page is for farmers or administrators. If you need different access levels, please contact our support team."}
						{user?.userType === "admin" &&
							"This page requires super admin privileges. Contact the platform administrator for access."}
						{!user?.userType &&
							"Please sign in with an account that has the required permissions to access this page."}
					</p>
				</div>

				{/* Action Buttons */}
				<div className="flex flex-col sm:flex-row gap-3 justify-center mb-6">
					<Button
						variant="outline"
						size="lg"
						onClick={() => window.history.back()}
					>
						<ArrowLeft className="w-4 h-4 mr-2" />
						Go Back
					</Button>
					<Link to="/dashboard">
						<Button variant="primary" size="lg">
							Go to Dashboard
						</Button>
					</Link>
				</div>

				{/* Additional Options */}
				<div className="space-y-3">
					<div className="text-sm text-gray-500">
						<p>Switch to a different account with proper permissions</p>
					</div>
					<Button
						variant="ghost"
						size="sm"
						onClick={handleLogout}
						className="text-red-600 hover:text-red-700"
					>
						<LogOut className="w-4 h-4 mr-2" />
						Sign Out & Login as Different User
					</Button>
				</div>

				{/* Contact Support */}
				<div className="mt-8 pt-6 border-t border-gray-200">
					<p className="text-sm text-gray-500 mb-2">
						Need help or believe this is an error?
					</p>
					<Link
						to="/contact"
						className="text-primary-600 hover:text-primary-700 underline text-sm"
					>
						Contact Support Team
					</Link>
				</div>
			</div>
		</div>
	);
};

export default Unauthorized;
