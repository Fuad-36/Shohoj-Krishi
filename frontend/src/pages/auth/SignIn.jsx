import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { Link, useNavigate, useLocation } from "react-router-dom";
import {
	Eye,
	EyeOff,
	LogIn,
	Mail,
	Lock,
	Loader2,
	AlertCircle,
	CheckCircle,
	Leaf,
	X,
} from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";
import { signInSchema } from "../../utils/validationSchemas";
import Button from "../../components/ui/Button";
import { useTranslation } from "react-i18next";

const SignIn = () => {
	const [showPassword, setShowPassword] = useState(false);
	const { login, isLoading, error, clearError } = useAuth();
	const { t } = useTranslation();
	const navigate = useNavigate();
	const location = useLocation();

	const from = location.state?.from?.pathname || "/dashboard";

	const {
		register,
		handleSubmit,
		formState: { errors, isSubmitting },
		setError,
	} = useForm({
		resolver: yupResolver(signInSchema),
		mode: "onChange",
	});

	// Check for messages from state (e.g., from OTP verification)
	const [stateMessage, setStateMessage] = useState("");
	const [messageType, setMessageType] = useState("");

	// Clear errors when component mounts and handle state messages
	useEffect(() => {
		clearError();

		// Check for message from location state
		if (location.state?.message) {
			setStateMessage(location.state.message);
			setMessageType(location.state.type || "info");

			// Clear the state to prevent showing the message on refresh
			window.history.replaceState(null, "");

			// Auto-clear message after 10 seconds
			const timer = setTimeout(() => {
				setStateMessage("");
				setMessageType("");
			}, 10000);

			return () => clearTimeout(timer);
		}
	}, [clearError, location.state]);

	const onSubmit = async (data) => {
		try {
			const result = await login(data);
			if (result.success) {
				navigate(from, { replace: true });
			} else {
				setError("root", { message: result.error });
			}
		} catch (error) {
			setError("root", { message: "An unexpected error occurred" });
		}
	};

	return (
		<div className="min-h-screen bg-linear-to-br from-primary-50 via-white to-secondary-50 flex items-center justify-center p-4">
			{/* Background Pattern */}
			<div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGRlZnM+CjxwYXR0ZXJuIGlkPSJncmlkIiB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHBhdHRlcm5Vbml0cz0idXNlclNwYWNlT25Vc2UiPgo8cGF0aCBkPSJNIDEwIDAgTCAwIDAgMCAxMCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjZGNmY2U3IiBzdHJva2Utd2lkdGg9IjEiLz4KPC9wYXR0ZXJuPgo8L2RlZnM+CjxyZWN0IHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9InVybCgjZ3JpZCkiLz4KPHN2Zz4=')] opacity-20"></div>

			<div className="relative w-full max-w-md">
				{/* Decorative Elements */}
				<div className="absolute -top-10 -left-10 w-20 h-20 bg-primary-200 rounded-full opacity-60 animate-pulse-gentle"></div>
				<div
					className="absolute -bottom-10 -right-10 w-16 h-16 bg-secondary-200 rounded-full opacity-60 animate-pulse-gentle"
					style={{ animationDelay: "1s" }}
				></div>

				{/* Main Card */}
				<div className="bg-white/80 backdrop-blur-md rounded-3xl shadow-2xl border border-white/20 p-8">
					{/* Header */}
					<div className="text-center mb-8">
						<Link
							to="/"
							className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-2xl mb-4"
						>
							<Leaf className="w-8 h-8 text-primary-600" />
						</Link>
						<h1 className="text-2xl font-bold text-gray-900 mb-2">
							{t("auth.signIn.title")}
						</h1>
						<p className="text-gray-600">{t("auth.signIn.subtitle")}</p>
					</div>

					{/* State Message (from OTP verification, etc.) */}
					{stateMessage && (
						<div
							className={`mb-6 p-4 border rounded-xl flex items-center gap-3 ${
								messageType === "success"
									? "bg-green-50 border-green-200"
									: messageType === "error"
									? "bg-red-50 border-red-200"
									: "bg-blue-50 border-blue-200"
							}`}
						>
							{messageType === "success" ? (
								<CheckCircle className="w-5 h-5 text-green-500 flex-shrink-0" />
							) : messageType === "error" ? (
								<AlertCircle className="w-5 h-5 text-red-500 flex-shrink-0" />
							) : (
								<AlertCircle className="w-5 h-5 text-blue-500 flex-shrink-0" />
							)}
							<span
								className={`text-sm ${
									messageType === "success"
										? "text-green-700"
										: messageType === "error"
										? "text-red-700"
										: "text-blue-700"
								}`}
							>
								{stateMessage}
							</span>
							<button
								onClick={() => {
									setStateMessage("");
									setMessageType("");
								}}
								className="ml-auto text-gray-400 hover:text-gray-600"
							>
								<X className="w-4 h-4" />
							</button>
						</div>
					)}

					{/* Error Message */}
					{(error || errors.root) && (
						<div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-center gap-3">
							<AlertCircle className="w-5 h-5 text-red-500 flex-shrink-0" />
							<span className="text-sm text-red-700">
								{error || errors.root?.message}
							</span>
						</div>
					)}

					{/* Form */}
					<form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
						{/* Email Field */}
						<div>
							<label
								htmlFor="email"
								className="block text-sm font-medium text-gray-700 mb-2"
							>
								Email Address
							</label>
							<div className="relative">
								<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
									<Mail className="w-5 h-5 text-gray-400" />
								</div>
								<input
									{...register("email")}
									type="email"
									id="email"
									className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
										errors.email
											? "border-red-300 bg-red-50"
											: "border-gray-300"
									}`}
									placeholder="Enter your email"
								/>
							</div>
							{errors.email && (
								<p className="mt-1 text-sm text-red-600">
									{errors.email.message}
								</p>
							)}
						</div>

						{/* Password Field */}
						<div>
							<label
								htmlFor="password"
								className="block text-sm font-medium text-gray-700 mb-2"
							>
								Password
							</label>
							<div className="relative">
								<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
									<Lock className="w-5 h-5 text-gray-400" />
								</div>
								<input
									{...register("password")}
									type={showPassword ? "text" : "password"}
									id="password"
									className={`w-full pl-10 pr-12 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
										errors.password
											? "border-red-300 bg-red-50"
											: "border-gray-300"
									}`}
									placeholder="Enter your password"
								/>
								<button
									type="button"
									className="absolute inset-y-0 right-0 pr-3 flex items-center"
									onClick={() => setShowPassword(!showPassword)}
								>
									{showPassword ? (
										<EyeOff className="w-5 h-5 text-gray-400 hover:text-gray-600" />
									) : (
										<Eye className="w-5 h-5 text-gray-400 hover:text-gray-600" />
									)}
								</button>
							</div>
							{errors.password && (
								<p className="mt-1 text-sm text-red-600">
									{errors.password.message}
								</p>
							)}
						</div>

						{/* Forgot Password Link */}
						<div className="flex justify-end">
							<Link
								to="/auth/forgot-password"
								className="text-sm text-primary-600 hover:text-primary-700 transition-colors"
							>
								Forgot your password?
							</Link>
						</div>

						{/* Submit Button */}
						<Button
							type="submit"
							variant="primary"
							size="lg"
							className="w-full"
							disabled={isSubmitting || isLoading}
						>
							{isSubmitting || isLoading ? (
								<>
									<Loader2 className="w-5 h-5 mr-2 animate-spin" />
									Signing in...
								</>
							) : (
								<>
									<LogIn className="w-5 h-5 mr-2" />
									Sign In
								</>
							)}
						</Button>
					</form>

					{/* Sign Up Link */}
					<div className="mt-8 text-center">
						<p className="text-gray-600">
							Don't have an account?{" "}
							<Link
								to="/auth/signup"
								className="font-medium text-primary-600 hover:text-primary-700 transition-colors"
							>
								Create one here
							</Link>
						</p>
					</div>

					{/* Divider */}
					<div className="mt-8 relative">
						<div className="absolute inset-0 flex items-center">
							<div className="w-full border-t border-gray-200"></div>
						</div>
						<div className="relative flex justify-center text-sm">
							<span className="px-4 bg-white text-gray-500">
								Join thousands of farmers
							</span>
						</div>
					</div>

					{/* Benefits */}
					<div className="mt-6 grid grid-cols-3 gap-4 text-center">
						<div className="p-3">
							<div className="text-2xl mb-1">🌱</div>
							<p className="text-xs text-gray-600">Smart Farming</p>
						</div>
						<div className="p-3">
							<div className="text-2xl mb-1">🤖</div>
							<p className="text-xs text-gray-600">AI Assistance</p>
						</div>
						<div className="p-3">
							<div className="text-2xl mb-1">🏪</div>
							<p className="text-xs text-gray-600">Direct Market</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
};

export default SignIn;
