import React from "react";
import { Link } from "react-router-dom";
import { Home, ArrowLeft, Search } from "lucide-react";
import Button from "../../components/ui/Button";

const NotFound = () => {
	return (
		<div className="min-h-screen bg-linear-to-br from-primary-50 via-white to-secondary-50 flex items-center justify-center p-4">
			<div className="text-center max-w-lg">
				{/* 404 Illustration */}
				<div className="mb-8">
					<div className="text-8xl mb-4">🌾</div>
					<div className="text-6xl font-bold text-primary-600 mb-2">404</div>
					<h1 className="text-2xl font-bold text-gray-900 mb-2">
						Page Not Found
					</h1>
					<p className="text-gray-600">
						Oops! It looks like this page wandered off to the fields. Let's help
						you get back to where you need to be.
					</p>
				</div>

				{/* Suggestions */}
				<div className="bg-white rounded-xl p-6 shadow-sm border mb-6">
					<h3 className="font-semibold text-gray-900 mb-4">
						What would you like to do?
					</h3>
					<div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
						<Link
							to="/"
							className="flex flex-col items-center p-3 rounded-lg hover:bg-primary-50 transition-colors"
						>
							<Home className="w-6 h-6 text-primary-600 mb-2" />
							<span className="text-sm font-medium text-gray-900">Go Home</span>
						</Link>
						<Link
							to="/dashboard"
							className="flex flex-col items-center p-3 rounded-lg hover:bg-secondary-50 transition-colors"
						>
							<Search className="w-6 h-6 text-secondary-600 mb-2" />
							<span className="text-sm font-medium text-gray-900">
								Dashboard
							</span>
						</Link>
						<button
							onClick={() => window.history.back()}
							className="flex flex-col items-center p-3 rounded-lg hover:bg-accent-50 transition-colors"
						>
							<ArrowLeft className="w-6 h-6 text-accent-600 mb-2" />
							<span className="text-sm font-medium text-gray-900">Go Back</span>
						</button>
					</div>
				</div>

				{/* Action Buttons */}
				<div className="flex flex-col sm:flex-row gap-3 justify-center">
					<Link to="/">
						<Button variant="primary" size="lg">
							<Home className="w-4 h-4 mr-2" />
							Back to Home
						</Button>
					</Link>
					<Link to="/auth/signin">
						<Button variant="outline" size="lg">
							Sign In
						</Button>
					</Link>
				</div>

				{/* Help Text */}
				<div className="mt-8 text-sm text-gray-500">
					<p>
						If you believe this is an error, please contact our support team.
					</p>
					<Link
						to="/contact"
						className="text-primary-600 hover:text-primary-700 underline"
					>
						Contact Support
					</Link>
				</div>
			</div>
		</div>
	);
};

export default NotFound;
