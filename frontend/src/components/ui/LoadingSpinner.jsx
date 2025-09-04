import React from "react";
import { Leaf } from "lucide-react";

const LoadingSpinner = ({ size = "default", message = "Loading..." }) => {
	const sizeClasses = {
		small: "w-6 h-6",
		default: "w-12 h-12",
		large: "w-16 h-16",
	};

	return (
		<div className="min-h-screen flex items-center justify-center bg-linear-to-br from-primary-50 via-white to-secondary-50">
			<div className="text-center">
				{/* Animated Logo */}
				<div className={`relative ${sizeClasses[size]} mx-auto mb-4`}>
					<div className="absolute inset-0 rounded-full border-4 border-primary-200"></div>
					<div className="absolute inset-0 rounded-full border-4 border-transparent border-t-primary-600 animate-spin"></div>
					<div className="absolute inset-0 flex items-center justify-center">
						<Leaf className="w-6 h-6 text-primary-600 animate-pulse" />
					</div>
				</div>

				{/* Loading Text */}
				<p className="text-gray-600 font-medium">{message}</p>

				{/* Animated Dots */}
				<div className="flex justify-center items-center space-x-1 mt-2">
					<div className="w-2 h-2 bg-primary-400 rounded-full animate-bounce"></div>
					<div
						className="w-2 h-2 bg-primary-400 rounded-full animate-bounce"
						style={{ animationDelay: "0.1s" }}
					></div>
					<div
						className="w-2 h-2 bg-primary-400 rounded-full animate-bounce"
						style={{ animationDelay: "0.2s" }}
					></div>
				</div>
			</div>
		</div>
	);
};

export default LoadingSpinner;
