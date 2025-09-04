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
	Building,
	FileText,
	Hash,
	Image,
} from "lucide-react";
import { authAPI } from "../../services/api";
import { getSignUpSchema } from "../../utils/validationSchemas";
import Button from "../../components/ui/Button";

const SignUp = () => {
	const [showPassword, setShowPassword] = useState(false);
	const [showConfirmPassword, setShowConfirmPassword] = useState(false);
	const [selectedRole, setSelectedRole] = useState("");
	const [isRegistering, setIsRegistering] = useState(false);
	const [error, setError] = useState("");
	const navigate = useNavigate();

	const {
		register,
		handleSubmit,
		formState: { errors, isSubmitting },
		setError: setFormError,
		watch,
		reset,
	} = useForm({
		resolver: yupResolver(getSignUpSchema(selectedRole)),
		mode: "onChange",
	});

	const watchRole = watch("role");

	// Update selected role and reset form when role changes
	useEffect(() => {
		if (watchRole && watchRole !== selectedRole) {
			setSelectedRole(watchRole);
			// Reset form with new schema
			setTimeout(() => {
				reset({}, { keepErrors: false });
			}, 0);
		}
	}, [watchRole, selectedRole, reset]);

	// Clear errors when component mounts
	useEffect(() => {
		setError("");
	}, []);

	const onSubmit = async (data) => {
		try {
			setIsRegistering(true);
			setError("");

			// Transform data to match API expectations
			const apiData = {
				email: data.email,
				phone: data.phone,
				role: data.role,
				fullName: data.fullName,
				division: data.division || "",
				district: data.district || "",
				upazila: data.upazila || "",
				union: data.union || "",
				address: data.address || "",
				nidNumber: data.nidNumber || "",
			};

			// Add password for FARMER and BUYER
			if (data.role === "FARMER" || data.role === "BUYER") {
				apiData.password = data.password;
			}

			// Add role-specific fields
			if (data.role === "FARMER") {
				if (data.farmSizeAc) apiData.farmSizeAc = parseFloat(data.farmSizeAc);
				if (data.farmType) apiData.farmType = data.farmType;
			} else if (data.role === "BUYER") {
				if (data.organisation) apiData.organisation = data.organisation;
			} else if (data.role === "AUTHORITY") {
				if (data.designation) apiData.designation = data.designation;
				if (data.employeeId) apiData.employeeId = data.employeeId;
				if (data.employeeIdImageUrl)
					apiData.employeeIdImageUrl = data.employeeIdImageUrl;
				if (data.officeDivision) apiData.officeDivision = data.officeDivision;
				if (data.officeDistrict) apiData.officeDistrict = data.officeDistrict;
				if (data.officeUpazila) apiData.officeUpazila = data.officeUpazila;
				if (data.officeUnion) apiData.officeUnion = data.officeUnion;
			}

			const response = await authAPI.register(apiData);

			if (response.status === 201) {
				// Navigate to OTP verification page
				navigate("/auth/verify-otp", {
					state: {
						email: data.email,
						userId: response.data.userId,
						userRole: data.role,
					},
				});
			}
		} catch (error) {
			// Handle API errors based on documentation
			if (error.response?.status === 409) {
				const message = error.response.data.message;
				if (message.includes("Email")) {
					setFormError("email", { message });
				} else if (message.includes("Phone")) {
					setFormError("phone", { message });
				}
				setError(message);
			} else if (error.response?.status === 400) {
				const errorData = error.response.data;
				if (errorData.errors) {
					// Validation errors
					Object.keys(errorData.errors).forEach((field) => {
						setFormError(field, { message: errorData.errors[field] });
					});
				} else {
					setError(
						errorData.message ||
							"Registration failed. Please check your inputs."
					);
				}
			} else {
				setError("Registration failed. Please try again.");
			}
		} finally {
			setIsRegistering(false);
		}
	};

	const userTypes = [
		{
			value: "FARMER",
			label: "Farmer",
			icon: Tractor,
			description: "Grow and sell crops directly",
			color: "green",
		},
		{
			value: "BUYER",
			label: "Buyer",
			icon: ShoppingCart,
			description: "Purchase fresh produce",
			color: "blue",
		},
		{
			value: "AUTHORITY",
			label: "Government Official",
			icon: Shield,
			description: "Provide support and oversight",
			color: "purple",
		},
	];

	return (
		<div className="min-h-screen bg-linear-to-br from-primary-50 via-white to-secondary-50 flex items-center justify-center p-4">
			{/* Background Pattern */}
			<div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGRlZnM+CjxwYXR0ZXJuIGlkPSJncmlkIiB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHBhdHRlcm5Vbml0cz0idXNlclNwYWNlT25Vc2UiPgo8cGF0aCBkPSJNIDEwIDAgTCAwIDAgMCAxMCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjZGNmY2U3IiBzdHJva2Utd2lkdGg9IjEiLz4KPC9wYXR0ZXJuPgo8L2RlZnM+CjxyZWN0IHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9InVybCgjZ3JpZCkiLz4KPHN2Zz4=')] opacity-20"></div>

			<div className="relative w-full max-w-4xl">
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
					{error && (
						<div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-center gap-3">
							<AlertCircle className="w-5 h-5 text-red-500 flex-shrink-0" />
							<span className="text-sm text-red-700">{error}</span>
						</div>
					)}

					{/* Form */}
					<form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
						{/* Role Selection */}
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-3">
								I am a... <span className="text-red-500">*</span>
							</label>
							<div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
								{userTypes.map((type) => {
									const Icon = type.icon;
									const isSelected = selectedRole === type.value;
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
												{...register("role")}
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
							{errors.role && (
								<p className="mt-2 text-sm text-red-600">
									{errors.role.message}
								</p>
							)}
						</div>

						{/* Show form fields only if role is selected */}
						{selectedRole && (
							<>
								{/* Basic Information */}
								<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
									{/* Full Name Field */}
									<div>
										<label
											htmlFor="fullName"
											className="block text-sm font-medium text-gray-700 mb-2"
										>
											Full Name <span className="text-red-500">*</span>
										</label>
										<div className="relative">
											<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
												<User className="w-5 h-5 text-gray-400" />
											</div>
											<input
												{...register("fullName")}
												type="text"
												id="fullName"
												className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
													errors.fullName
														? "border-red-300 bg-red-50"
														: "border-gray-300"
												}`}
												placeholder="Enter your full name"
											/>
										</div>
										{errors.fullName && (
											<p className="mt-1 text-sm text-red-600">
												{errors.fullName.message}
											</p>
										)}
									</div>

									{/* Email Field */}
									<div>
										<label
											htmlFor="email"
											className="block text-sm font-medium text-gray-700 mb-2"
										>
											Email Address <span className="text-red-500">*</span>
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
								</div>

								{/* Contact Information */}
								<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
									{/* Phone Field */}
									<div>
										<label
											htmlFor="phone"
											className="block text-sm font-medium text-gray-700 mb-2"
										>
											Phone Number <span className="text-red-500">*</span>
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
												placeholder="e.g., 01700000000"
											/>
										</div>
										{errors.phone && (
											<p className="mt-1 text-sm text-red-600">
												{errors.phone.message}
											</p>
										)}
									</div>

									{/* NID Number Field */}
									<div>
										<label
											htmlFor="nidNumber"
											className="block text-sm font-medium text-gray-700 mb-2"
										>
											NID Number
										</label>
										<div className="relative">
											<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
												<Hash className="w-5 h-5 text-gray-400" />
											</div>
											<input
												{...register("nidNumber")}
												type="text"
												id="nidNumber"
												className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
													errors.nidNumber
														? "border-red-300 bg-red-50"
														: "border-gray-300"
												}`}
												placeholder="Enter your NID number"
											/>
										</div>
										{errors.nidNumber && (
											<p className="mt-1 text-sm text-red-600">
												{errors.nidNumber.message}
											</p>
										)}
									</div>
								</div>

								{/* Location Information */}
								<div>
									<h3 className="text-lg font-medium text-gray-900 mb-4">
										Location Information
									</h3>
									<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
										{/* Division */}
										<div>
											<label
												htmlFor="division"
												className="block text-sm font-medium text-gray-700 mb-2"
											>
												Division
											</label>
											<div className="relative">
												<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
													<MapPin className="w-5 h-5 text-gray-400" />
												</div>
												<input
													{...register("division")}
													type="text"
													id="division"
													className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
														errors.division
															? "border-red-300 bg-red-50"
															: "border-gray-300"
													}`}
													placeholder="e.g., Dhaka"
												/>
											</div>
											{errors.division && (
												<p className="mt-1 text-sm text-red-600">
													{errors.division.message}
												</p>
											)}
										</div>

										{/* District */}
										<div>
											<label
												htmlFor="district"
												className="block text-sm font-medium text-gray-700 mb-2"
											>
												District
											</label>
											<div className="relative">
												<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
													<MapPin className="w-5 h-5 text-gray-400" />
												</div>
												<input
													{...register("district")}
													type="text"
													id="district"
													className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
														errors.district
															? "border-red-300 bg-red-50"
															: "border-gray-300"
													}`}
													placeholder="e.g., Dhaka"
												/>
											</div>
											{errors.district && (
												<p className="mt-1 text-sm text-red-600">
													{errors.district.message}
												</p>
											)}
										</div>

										{/* Upazila */}
										<div>
											<label
												htmlFor="upazila"
												className="block text-sm font-medium text-gray-700 mb-2"
											>
												Upazila
											</label>
											<div className="relative">
												<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
													<MapPin className="w-5 h-5 text-gray-400" />
												</div>
												<input
													{...register("upazila")}
													type="text"
													id="upazila"
													className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
														errors.upazila
															? "border-red-300 bg-red-50"
															: "border-gray-300"
													}`}
													placeholder="e.g., Savar"
												/>
											</div>
											{errors.upazila && (
												<p className="mt-1 text-sm text-red-600">
													{errors.upazila.message}
												</p>
											)}
										</div>

										{/* Union */}
										<div>
											<label
												htmlFor="union"
												className="block text-sm font-medium text-gray-700 mb-2"
											>
												Union
											</label>
											<div className="relative">
												<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
													<MapPin className="w-5 h-5 text-gray-400" />
												</div>
												<input
													{...register("union")}
													type="text"
													id="union"
													className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
														errors.union
															? "border-red-300 bg-red-50"
															: "border-gray-300"
													}`}
													placeholder="e.g., Tetuljhora"
												/>
											</div>
											{errors.union && (
												<p className="mt-1 text-sm text-red-600">
													{errors.union.message}
												</p>
											)}
										</div>
									</div>

									{/* Address */}
									<div className="mt-6">
										<label
											htmlFor="address"
											className="block text-sm font-medium text-gray-700 mb-2"
										>
											Address
										</label>
										<div className="relative">
											<div className="absolute top-3 left-3 flex items-center pointer-events-none">
												<MapPin className="w-5 h-5 text-gray-400" />
											</div>
											<textarea
												{...register("address")}
												id="address"
												rows={3}
												className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors resize-none ${
													errors.address
														? "border-red-300 bg-red-50"
														: "border-gray-300"
												}`}
												placeholder="Enter your full address"
											/>
										</div>
										{errors.address && (
											<p className="mt-1 text-sm text-red-600">
												{errors.address.message}
											</p>
										)}
									</div>
								</div>

								{/* Role-specific fields */}
								{selectedRole === "FARMER" && (
									<div>
										<h3 className="text-lg font-medium text-gray-900 mb-4">
											Farm Information
										</h3>
										<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
											{/* Farm Size */}
											<div>
												<label
													htmlFor="farmSizeAc"
													className="block text-sm font-medium text-gray-700 mb-2"
												>
													Farm Size (Acres)
												</label>
												<div className="relative">
													<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
														<Tractor className="w-5 h-5 text-gray-400" />
													</div>
													<input
														{...register("farmSizeAc")}
														type="number"
														step="0.1"
														id="farmSizeAc"
														className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
															errors.farmSizeAc
																? "border-red-300 bg-red-50"
																: "border-gray-300"
														}`}
														placeholder="e.g., 2.5"
													/>
												</div>
												{errors.farmSizeAc && (
													<p className="mt-1 text-sm text-red-600">
														{errors.farmSizeAc.message}
													</p>
												)}
											</div>

											{/* Farm Type */}
											<div>
												<label
													htmlFor="farmType"
													className="block text-sm font-medium text-gray-700 mb-2"
												>
													Farm Type
												</label>
												<div className="relative">
													<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
														<Leaf className="w-5 h-5 text-gray-400" />
													</div>
													<input
														{...register("farmType")}
														type="text"
														id="farmType"
														className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
															errors.farmType
																? "border-red-300 bg-red-50"
																: "border-gray-300"
														}`}
														placeholder="e.g., Vegetables, Rice, Mixed"
													/>
												</div>
												{errors.farmType && (
													<p className="mt-1 text-sm text-red-600">
														{errors.farmType.message}
													</p>
												)}
											</div>
										</div>
									</div>
								)}

								{selectedRole === "BUYER" && (
									<div>
										<h3 className="text-lg font-medium text-gray-900 mb-4">
											Organization Information
										</h3>
										<div>
											<label
												htmlFor="organisation"
												className="block text-sm font-medium text-gray-700 mb-2"
											>
												Organization Name
											</label>
											<div className="relative">
												<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
													<Building className="w-5 h-5 text-gray-400" />
												</div>
												<input
													{...register("organisation")}
													type="text"
													id="organisation"
													className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
														errors.organisation
															? "border-red-300 bg-red-50"
															: "border-gray-300"
													}`}
													placeholder="e.g., Agri Corp, Fresh Mart Ltd"
												/>
											</div>
											{errors.organisation && (
												<p className="mt-1 text-sm text-red-600">
													{errors.organisation.message}
												</p>
											)}
										</div>
									</div>
								)}

								{selectedRole === "AUTHORITY" && (
									<div>
										<h3 className="text-lg font-medium text-gray-900 mb-4">
											Official Information
										</h3>
										<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
											{/* Designation */}
											<div>
												<label
													htmlFor="designation"
													className="block text-sm font-medium text-gray-700 mb-2"
												>
													Designation
												</label>
												<div className="relative">
													<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
														<FileText className="w-5 h-5 text-gray-400" />
													</div>
													<input
														{...register("designation")}
														type="text"
														id="designation"
														className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
															errors.designation
																? "border-red-300 bg-red-50"
																: "border-gray-300"
														}`}
														placeholder="e.g., Agricultural Officer"
													/>
												</div>
												{errors.designation && (
													<p className="mt-1 text-sm text-red-600">
														{errors.designation.message}
													</p>
												)}
											</div>

											{/* Employee ID */}
											<div>
												<label
													htmlFor="employeeId"
													className="block text-sm font-medium text-gray-700 mb-2"
												>
													Employee ID
												</label>
												<div className="relative">
													<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
														<Hash className="w-5 h-5 text-gray-400" />
													</div>
													<input
														{...register("employeeId")}
														type="text"
														id="employeeId"
														className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
															errors.employeeId
																? "border-red-300 bg-red-50"
																: "border-gray-300"
														}`}
														placeholder="e.g., A-1001"
													/>
												</div>
												{errors.employeeId && (
													<p className="mt-1 text-sm text-red-600">
														{errors.employeeId.message}
													</p>
												)}
											</div>
										</div>

										{/* Employee ID Image URL */}
										<div className="mt-6">
											<label
												htmlFor="employeeIdImageUrl"
												className="block text-sm font-medium text-gray-700 mb-2"
											>
												Employee ID Card Image URL
											</label>
											<div className="relative">
												<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
													<Image className="w-5 h-5 text-gray-400" />
												</div>
												<input
													{...register("employeeIdImageUrl")}
													type="url"
													id="employeeIdImageUrl"
													className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
														errors.employeeIdImageUrl
															? "border-red-300 bg-red-50"
															: "border-gray-300"
													}`}
													placeholder="https://example.com/id-card.jpg"
												/>
											</div>
											{errors.employeeIdImageUrl && (
												<p className="mt-1 text-sm text-red-600">
													{errors.employeeIdImageUrl.message}
												</p>
											)}
										</div>

										{/* Office Location */}
										<div className="mt-6">
											<h4 className="text-md font-medium text-gray-800 mb-4">
												Office Location
											</h4>
											<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
												{/* Office Division */}
												<div>
													<label
														htmlFor="officeDivision"
														className="block text-sm font-medium text-gray-700 mb-2"
													>
														Office Division
													</label>
													<div className="relative">
														<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
															<Building className="w-5 h-5 text-gray-400" />
														</div>
														<input
															{...register("officeDivision")}
															type="text"
															id="officeDivision"
															className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
																errors.officeDivision
																	? "border-red-300 bg-red-50"
																	: "border-gray-300"
															}`}
															placeholder="e.g., Rajshahi"
														/>
													</div>
													{errors.officeDivision && (
														<p className="mt-1 text-sm text-red-600">
															{errors.officeDivision.message}
														</p>
													)}
												</div>

												{/* Office District */}
												<div>
													<label
														htmlFor="officeDistrict"
														className="block text-sm font-medium text-gray-700 mb-2"
													>
														Office District
													</label>
													<div className="relative">
														<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
															<Building className="w-5 h-5 text-gray-400" />
														</div>
														<input
															{...register("officeDistrict")}
															type="text"
															id="officeDistrict"
															className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
																errors.officeDistrict
																	? "border-red-300 bg-red-50"
																	: "border-gray-300"
															}`}
															placeholder="e.g., Rajshahi"
														/>
													</div>
													{errors.officeDistrict && (
														<p className="mt-1 text-sm text-red-600">
															{errors.officeDistrict.message}
														</p>
													)}
												</div>

												{/* Office Upazila */}
												<div>
													<label
														htmlFor="officeUpazila"
														className="block text-sm font-medium text-gray-700 mb-2"
													>
														Office Upazila
													</label>
													<div className="relative">
														<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
															<Building className="w-5 h-5 text-gray-400" />
														</div>
														<input
															{...register("officeUpazila")}
															type="text"
															id="officeUpazila"
															className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
																errors.officeUpazila
																	? "border-red-300 bg-red-50"
																	: "border-gray-300"
															}`}
															placeholder="e.g., Paba"
														/>
													</div>
													{errors.officeUpazila && (
														<p className="mt-1 text-sm text-red-600">
															{errors.officeUpazila.message}
														</p>
													)}
												</div>

												{/* Office Union */}
												<div>
													<label
														htmlFor="officeUnion"
														className="block text-sm font-medium text-gray-700 mb-2"
													>
														Office Union
													</label>
													<div className="relative">
														<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
															<Building className="w-5 h-5 text-gray-400" />
														</div>
														<input
															{...register("officeUnion")}
															type="text"
															id="officeUnion"
															className={`w-full pl-10 pr-4 py-3 border rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
																errors.officeUnion
																	? "border-red-300 bg-red-50"
																	: "border-gray-300"
															}`}
															placeholder="e.g., Haripur"
														/>
													</div>
													{errors.officeUnion && (
														<p className="mt-1 text-sm text-red-600">
															{errors.officeUnion.message}
														</p>
													)}
												</div>
											</div>
										</div>
									</div>
								)}

								{/* Password fields - only for FARMER and BUYER */}
								{(selectedRole === "FARMER" || selectedRole === "BUYER") && (
									<div>
										<h3 className="text-lg font-medium text-gray-900 mb-4">
											Security Information
										</h3>
										<div className="grid grid-cols-1 md:grid-cols-2 gap-6">
											{/* Password Field */}
											<div>
												<label
													htmlFor="password"
													className="block text-sm font-medium text-gray-700 mb-2"
												>
													Password <span className="text-red-500">*</span>
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
													Confirm Password{" "}
													<span className="text-red-500">*</span>
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
														onClick={() =>
															setShowConfirmPassword(!showConfirmPassword)
														}
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
										</div>
									</div>
								)}

								{/* Submit Button */}
								<Button
									type="submit"
									variant="primary"
									size="lg"
									className="w-full"
									disabled={isSubmitting || isRegistering}
								>
									{isSubmitting || isRegistering ? (
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
							</>
						)}

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
