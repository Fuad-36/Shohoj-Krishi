import React, { useState, useEffect, useRef } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { Link, useNavigate, useLocation } from "react-router-dom";
import {
	Mail,
	Loader2,
	AlertCircle,
	CheckCircle,
	Leaf,
	RefreshCw,
	ArrowLeft,
	Timer,
} from "lucide-react";
import { authAPI } from "../../services/api";
import { otpVerificationSchema } from "../../utils/validationSchemas";
import Button from "../../components/ui/Button";

const VerifyOTP = () => {
	const [isVerifying, setIsVerifying] = useState(false);
	const [isResending, setIsResending] = useState(false);
	const [resendCooldown, setResendCooldown] = useState(0);
	const [verificationSuccess, setVerificationSuccess] = useState(false);
	const [error, setError] = useState("");
	const [otp, setOTP] = useState(["", "", "", "", "", ""]);

	const navigate = useNavigate();
	const location = useLocation();
	const otpRefs = useRef([]);

	// Get email from location state (passed from registration)
	const email = location.state?.email || "";
	const userRole = location.state?.userRole || "";
	const userId = location.state?.userId || "";

	const {
		register,
		handleSubmit,
		formState: { errors },
		setValue,
		setError: setFormError,
	} = useForm({
		resolver: yupResolver(otpVerificationSchema),
		defaultValues: {
			email: email,
			otp: "",
		},
	});

	// Redirect if no email provided
	useEffect(() => {
		if (!email) {
			navigate("/auth/signup", { replace: true });
		}
	}, [email, navigate]);

	// Handle resend cooldown timer
	useEffect(() => {
		let timer;
		if (resendCooldown > 0) {
			timer = setInterval(() => {
				setResendCooldown((prev) => prev - 1);
			}, 1000);
		}
		return () => clearInterval(timer);
	}, [resendCooldown]);

	// Handle OTP input change
	const handleOTPChange = (value, index) => {
		if (!/^\d*$/.test(value)) return; // Only allow numbers

		const newOTP = [...otp];
		newOTP[index] = value;
		setOTP(newOTP);

		// Update form value
		setValue("otp", newOTP.join(""));

		// Auto-focus next input
		if (value && index < 5) {
			otpRefs.current[index + 1]?.focus();
		}

		// Auto-submit when all fields are filled
		if (newOTP.every((digit) => digit !== "") && newOTP.join("").length === 6) {
			const otpValue = newOTP.join("");
			setTimeout(() => handleVerifyOTP({ email, otp: otpValue }), 100);
		}
	};

	// Handle backspace
	const handleKeyDown = (e, index) => {
		if (e.key === "Backspace" && !otp[index] && index > 0) {
			otpRefs.current[index - 1]?.focus();
		}
	};

	// Handle paste
	const handlePaste = (e) => {
		e.preventDefault();
		const pastedData = e.clipboardData.getData("text").replace(/\D/g, "");
		if (pastedData.length === 6) {
			const newOTP = pastedData.split("");
			setOTP(newOTP);
			setValue("otp", pastedData);
			otpRefs.current[5]?.focus();
		}
	};

	const handleVerifyOTP = async (data) => {
		try {
			setIsVerifying(true);
			setError("");

			const response = await authAPI.verifyOTP(data);

			if (response.status === 200) {
				setVerificationSuccess(true);

				// Show success message based on role
				setTimeout(() => {
					if (userRole === "AUTHORITY") {
						navigate("/auth/signin", {
							state: {
								message:
									"Email verified successfully. Please wait for admin approval before signing in.",
								type: "info",
							},
						});
					} else {
						navigate("/auth/signin", {
							state: {
								message:
									"Email verified successfully. You can now sign in with your credentials.",
								type: "success",
							},
						});
					}
				}, 2000);
			}
		} catch (error) {
			const errorMessage =
				error.response?.data?.message ||
				"Verification failed. Please try again.";
			setError(errorMessage);
			setFormError("otp", { message: errorMessage });

			// Clear OTP fields on error
			setOTP(["", "", "", "", "", ""]);
			setValue("otp", "");
			otpRefs.current[0]?.focus();
		} finally {
			setIsVerifying(false);
		}
	};

	const handleResendOTP = async () => {
		try {
			setIsResending(true);
			setError("");

			await authAPI.resendOTP(email);

			// Start cooldown
			setResendCooldown(60);

			// Clear current OTP
			setOTP(["", "", "", "", "", ""]);
			setValue("otp", "");
			otpRefs.current[0]?.focus();
		} catch (error) {
			const errorMessage =
				error.response?.data?.message ||
				"Failed to resend OTP. Please try again.";
			setError(errorMessage);
		} finally {
			setIsResending(false);
		}
	};

	const onSubmit = (data) => {
		handleVerifyOTP(data);
	};

	if (verificationSuccess) {
		return (
			<div className="min-h-screen bg-linear-to-br from-primary-50 via-white to-secondary-50 flex items-center justify-center p-4">
				<div className="relative w-full max-w-md">
					<div className="bg-white/80 backdrop-blur-md rounded-3xl shadow-2xl border border-white/20 p-8 text-center">
						<div className="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-2xl mb-4">
							<CheckCircle className="w-8 h-8 text-green-600" />
						</div>
						<h1 className="text-2xl font-bold text-gray-900 mb-2">
							Email Verified! ✅
						</h1>
						<p className="text-gray-600 mb-6">
							{userRole === "AUTHORITY"
								? "Your email has been verified. Please wait for admin approval before signing in."
								: "Your email has been verified successfully. Redirecting to sign in..."}
						</p>
						<div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto"></div>
					</div>
				</div>
			</div>
		);
	}

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
						<div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-2xl mb-4">
							<Mail className="w-8 h-8 text-primary-600" />
						</div>
						<h1 className="text-2xl font-bold text-gray-900 mb-2">
							Verify Your Email 📧
						</h1>
						<p className="text-gray-600 mb-2">We've sent a 6-digit code to</p>
						<p className="text-primary-600 font-medium">{email}</p>
					</div>

					{/* Back Button */}
					<div className="mb-6">
						<Link
							to="/auth/signup"
							className="inline-flex items-center text-sm text-gray-600 hover:text-gray-800 transition-colors"
						>
							<ArrowLeft className="w-4 h-4 mr-1" />
							Back to registration
						</Link>
					</div>

					{/* Error Message */}
					{error && (
						<div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-center gap-3">
							<AlertCircle className="w-5 h-5 text-red-500 flex-shrink-0" />
							<span className="text-sm text-red-700">{error}</span>
						</div>
					)}

					{/* OTP Form */}
					<form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
						{/* Hidden email field for form validation */}
						<input type="hidden" {...register("email")} />
						<input type="hidden" {...register("otp")} />

						{/* OTP Input */}
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-3 text-center">
								Enter verification code
							</label>
							<div
								className="flex justify-center gap-3 mb-4"
								onPaste={handlePaste}
							>
								{otp.map((digit, index) => (
									<input
										key={index}
										ref={(el) => (otpRefs.current[index] = el)}
										type="text"
										maxLength={1}
										value={digit}
										onChange={(e) => handleOTPChange(e.target.value, index)}
										onKeyDown={(e) => handleKeyDown(e, index)}
										className={`w-12 h-12 text-center border-2 rounded-xl text-lg font-semibold focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors ${
											errors.otp
												? "border-red-300 bg-red-50"
												: "border-gray-300"
										}`}
										placeholder="0"
									/>
								))}
							</div>
							{errors.otp && (
								<p className="text-sm text-red-600 text-center">
									{errors.otp.message}
								</p>
							)}
						</div>

						{/* Timer and Resend */}
						<div className="text-center space-y-3">
							{resendCooldown > 0 ? (
								<div className="flex items-center justify-center gap-2 text-sm text-gray-600">
									<Timer className="w-4 h-4" />
									<span>Resend code in {resendCooldown}s</span>
								</div>
							) : (
								<button
									type="button"
									onClick={handleResendOTP}
									disabled={isResending}
									className="inline-flex items-center gap-2 text-sm text-primary-600 hover:text-primary-700 transition-colors disabled:opacity-50"
								>
									{isResending ? (
										<Loader2 className="w-4 h-4 animate-spin" />
									) : (
										<RefreshCw className="w-4 h-4" />
									)}
									{isResending ? "Sending..." : "Resend code"}
								</button>
							)}
							<p className="text-xs text-gray-500">
								Didn't receive the code? Check your spam folder
							</p>
						</div>

						{/* Submit Button */}
						<Button
							type="submit"
							variant="primary"
							size="lg"
							className="w-full"
							disabled={isVerifying || otp.some((digit) => digit === "")}
						>
							{isVerifying ? (
								<>
									<Loader2 className="w-5 h-5 mr-2 animate-spin" />
									Verifying...
								</>
							) : (
								<>
									<CheckCircle className="w-5 h-5 mr-2" />
									Verify Email
								</>
							)}
						</Button>
					</form>

					{/* Help Text */}
					<div className="mt-6 text-center">
						<p className="text-xs text-gray-500">
							The verification code will expire in 5 minutes
						</p>
					</div>
				</div>
			</div>
		</div>
	);
};

export default VerifyOTP;
