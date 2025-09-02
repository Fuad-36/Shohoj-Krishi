import * as yup from "yup";

// Sign in validation schema
export const signInSchema = yup.object({
	email: yup
		.string()
		.email("Please enter a valid email address")
		.required("Email is required"),
	password: yup
		.string()
		.min(6, "Password must be at least 6 characters")
		.required("Password is required"),
});

// Sign up validation schema
export const signUpSchema = yup.object({
	firstName: yup
		.string()
		.min(2, "First name must be at least 2 characters")
		.max(50, "First name must not exceed 50 characters")
		.required("First name is required"),
	lastName: yup
		.string()
		.min(2, "Last name must be at least 2 characters")
		.max(50, "Last name must not exceed 50 characters")
		.required("Last name is required"),
	email: yup
		.string()
		.email("Please enter a valid email address")
		.required("Email is required"),
	phone: yup
		.string()
		.matches(
			/^(\+880|880|0)?[13-9]\d{8}$/,
			"Please enter a valid Bangladeshi phone number"
		)
		.required("Phone number is required"),
	password: yup
		.string()
		.min(8, "Password must be at least 8 characters")
		.matches(
			/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
			"Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
		)
		.required("Password is required"),
	confirmPassword: yup
		.string()
		.oneOf([yup.ref("password"), null], "Passwords must match")
		.required("Please confirm your password"),
	userType: yup
		.string()
		.oneOf(["farmer", "buyer", "admin"], "Please select a valid user type")
		.required("User type is required"),
	location: yup
		.string()
		.min(2, "Location must be at least 2 characters")
		.required("Location is required"),
	acceptTerms: yup
		.boolean()
		.oneOf([true], "You must accept the terms and conditions"),
});

// Forgot password validation schema
export const forgotPasswordSchema = yup.object({
	email: yup
		.string()
		.email("Please enter a valid email address")
		.required("Email is required"),
});

// Reset password validation schema
export const resetPasswordSchema = yup.object({
	password: yup
		.string()
		.min(8, "Password must be at least 8 characters")
		.matches(
			/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
			"Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
		)
		.required("Password is required"),
	confirmPassword: yup
		.string()
		.oneOf([yup.ref("password"), null], "Passwords must match")
		.required("Please confirm your password"),
});

// Change password validation schema
export const changePasswordSchema = yup.object({
	currentPassword: yup.string().required("Current password is required"),
	password: yup
		.string()
		.min(8, "Password must be at least 8 characters")
		.matches(
			/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
			"Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
		)
		.required("New password is required"),
	confirmPassword: yup
		.string()
		.oneOf([yup.ref("password"), null], "Passwords must match")
		.required("Please confirm your new password"),
});
