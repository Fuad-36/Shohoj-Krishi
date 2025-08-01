import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { Link, useNavigate } from "react-router-dom";
import {
	Eye,
	EyeOff,
	UserPlus,
	Mail,
	Lock,
	User,
	Phone,
	MapPin,
	Loader2,
	AlertCircle,
	CheckCircle,
	Leaf,
	Tractor,
	ShoppingCart,
	Shield,
} from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";
import { signUpSchema } from "../../utils/validationSchemas";
import Button from "../../components/ui/Button";

const SignUp = () => {
	const [showPassword, setShowPassword] = useState(false);
	const [showConfirmPassword, setShowConfirmPassword] = useState(false);
	const { register: registerUser, isLoading, error, clearError } = useAuth();
	const navigate = useNavigate();

	const {
		register,
		handleSubmit,
		formState: { errors, isSubmitting },
		setError,
		watch,
	} = useForm({
		resolver: yupResolver(signUpSchema),
		mode: "onChange",
	});

	const watchUserType = watch("userType");

	// Clear errors when component mounts
	useEffect(() => {
		clearError();
	}, []); // eslint-disable-line react-hooks/exhaustive-deps

	const onSubmit = async (data) => {
		try {
			const result = await registerUser(data);
			if (result.success) {
				navigate("/dashboard", { replace: true });
			} else {
				setError("root", { message: result.error });
			}
		} catch (error) {
			setError("root", { message: "An unexpected error occurred" });
		}
	};

	const userTypes = [
		{
			value: "farmer",
			label: "Farmer",
			icon: Tractor,
			description: "Grow and sell crops directly",
			color: "primary",
		},
		{
			value: "buyer",
			label: "Buyer",
			icon: ShoppingCart,
			description: "Purchase fresh produce",
			color: "secondary",
		},
		{
			value: "admin",
			label: "Government Official",
			icon: Shield,
			description: "Provide support and oversight",
			color: "accent",
		},
	];

	return (
		<div className="min-h-screen bg-linear-to-br from-primary-50 via-white to-secondary-50 flex items-center justify-center p-4">
			{/* Background Pattern */}
			<div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGRlZnM+CjxwYXR0ZXJuIGlkPSJncmlkIiB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHBhdHRlcm5Vbml0cz0idXNlclNwYWNlT25Vc2UiPgo8cGF0aCBkPSJNIDEwIDAgTCAwIDAgMCAxMCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjZGNmY2U3IiBzdHJva2Utd2lkdGg9IjEiLz4KPC9wYXR0ZXJuPgo8L2RlZnM+CjxyZWN0IHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9InVybCgjZ3JpZCkiLz4KPHN2Zz4=')] opacity-20"></div>

			<div className="relative w-full max-w-2xl">
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
						<div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-2xl mb-4">
							<Leaf className="w-8 h-8 text-primary-600" />
						</div>
						<h1 className="text-2xl font-bold text-gray-900 mb-2">
							Join Our Agricultural Community! 🌾
						</h1>
						<p className="text-gray-600">
							Create your account to access advanced farming tools and direct
							market access
						</p>
					</div>

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
						{/* User Type Selection */}
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-3">
								I am a...
							</label>
							<div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
								{userTypes.map((type) => {
									const Icon = type.icon;
									const isSelected = watchUserType === type.value;
									return (
										<label
											key={type.value}
											className={`relative cursor-pointer rounded-xl border-2 p-4 transition-all ${
												isSelected
													? `border-${type.color}-500 bg-${type.color}-50`
													: "border-gray-200 hover:border-gray-300"
											}`}
										>
											<input
												{...register("userType")}
												type="radio"
												value={type.value}
												className="sr-only"
											/>
											<div className="text-center">
												<Icon
													className={`w-8 h-8 mx-auto mb-2 ${
														isSelected
															? `text-${type.color}-600`
															: "text-gray-400"
													}`}
												/>
												<h3
													className={`font-medium text-sm ${
														isSelected
															? `text-${type.color}-900`
															: "text-gray-900"
													}`}
												>
													{type.label}
												</h3>
												<p
													className={`text-xs mt-1 ${
														isSelected
															? `text-${type.color}-700`
															: "text-gray-500"
													}`}
												>
													{type.description}
												</p>
											</div>
											{isSelected && (
												<CheckCircle
													className={`absolute top-2 right-2 w-5 h-5 text-${type.color}-600`}
												/>
											)}
										</label>
									);
								})}
							</div>
							{errors.userType && (
								<p className="mt-2 text-sm text-red-600">
									{errors.userType.message}
								</p>
							)}
						</div>

						{/* Rest of the form continues with name, email, phone, location, password fields... */}
						{/* For brevity, keeping the existing form structure */}

						{/* Name Field */}
						<div>
							<label
								htmlFor="name"
								className="block text-sm font-medium text-gray-700 mb-2"
							>
								Full Name
							</label>
							<div className="relative">
								<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
									<User className="w-5 h-5 text-gray-400" />
								</div>
								<input
									{...register("name")}
									type="text"
									id="name"
									className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
										errors.name ? "border-red-300 bg-red-50" : "border-gray-300"
									}`}
									placeholder="Enter your full name"
								/>
							</div>
							{errors.name && (
								<p className="mt-1 text-sm text-red-600">
									{errors.name.message}
								</p>
							)}
						</div>

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

						{/* Phone Field */}
						<div>
							<label
								htmlFor="phone"
								className="block text-sm font-medium text-gray-700 mb-2"
							>
								Phone Number
							</label>
							<div className="relative">
								<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
									<Phone className="w-5 h-5 text-gray-400" />
								</div>
								<input
									{...register("phone")}
									type="tel"
									id="phone"
									className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
										errors.phone
											? "border-red-300 bg-red-50"
											: "border-gray-300"
									}`}
									placeholder="Enter your phone number"
								/>
							</div>
							{errors.phone && (
								<p className="mt-1 text-sm text-red-600">
									{errors.phone.message}
								</p>
							)}
						</div>

						{/* Location Field */}
						<div>
							<label
								htmlFor="location"
								className="block text-sm font-medium text-gray-700 mb-2"
							>
								Location
							</label>
							<div className="relative">
								<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
									<MapPin className="w-5 h-5 text-gray-400" />
								</div>
								<input
									{...register("location")}
									type="text"
									id="location"
									className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
										errors.location
											? "border-red-300 bg-red-50"
											: "border-gray-300"
									}`}
									placeholder="City, State/Province"
								/>
							</div>
							{errors.location && (
								<p className="mt-1 text-sm text-red-600">
									{errors.location.message}
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
									placeholder="Create a strong password"
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

						{/* Confirm Password Field */}
						<div>
							<label
								htmlFor="confirmPassword"
								className="block text-sm font-medium text-gray-700 mb-2"
							>
								Confirm Password
							</label>
							<div className="relative">
								<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
									<Lock className="w-5 h-5 text-gray-400" />
								</div>
								<input
									{...register("confirmPassword")}
									type={showConfirmPassword ? "text" : "password"}
									id="confirmPassword"
									className={`w-full pl-10 pr-12 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
										errors.confirmPassword
											? "border-red-300 bg-red-50"
											: "border-gray-300"
									}`}
									placeholder="Confirm your password"
								/>
								<button
									type="button"
									className="absolute inset-y-0 right-0 pr-3 flex items-center"
									onClick={() => setShowConfirmPassword(!showConfirmPassword)}
								>
									{showConfirmPassword ? (
										<EyeOff className="w-5 h-5 text-gray-400 hover:text-gray-600" />
									) : (
										<Eye className="w-5 h-5 text-gray-400 hover:text-gray-600" />
									)}
								</button>
							</div>
							{errors.confirmPassword && (
								<p className="mt-1 text-sm text-red-600">
									{errors.confirmPassword.message}
								</p>
							)}
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
									Creating Account...
								</>
							) : (
								<>
									<UserPlus className="w-5 h-5 mr-2" />
									Create Account
								</>
							)}
						</Button>

						{/* Sign In Link */}
						<div className="mt-8 text-center">
							<p className="text-gray-600">
								Already have an account?{" "}
								<Link
									to="/auth/signin"
									className="font-medium text-primary-600 hover:text-primary-700 transition-colors"
								>
									Sign in here
								</Link>
							</p>
						</div>
					</form>
				</div>
			</div>
		</div>
	);
};

export default SignUp;
