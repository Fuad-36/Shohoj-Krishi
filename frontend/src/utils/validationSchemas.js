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

// Base validation schema for common fields
const baseRegisterSchema = {
	email: yup
		.string()
		.email("Please enter a valid email address")
		.required("Email is required"),
	phone: yup
		.string()
		.min(10, "Phone number must be at least 10 digits")
		.max(15, "Phone number must not exceed 15 digits")
		.matches(/^[+]?[\d\s\-()]+$/, "Please enter a valid phone number")
		.required("Phone number is required"),
	role: yup
		.string()
		.oneOf(["FARMER", "BUYER", "AUTHORITY"], "Please select a valid role")
		.required("Role is required"),
	fullName: yup
		.string()
		.min(2, "Full name must be at least 2 characters")
		.max(100, "Full name must not exceed 100 characters")
		.required("Full name is required"),
	division: yup.string().optional(),
	district: yup.string().optional(),
	upazila: yup.string().optional(),
	union: yup.string().optional(),
	address: yup.string().optional(),
	nidNumber: yup.string().optional(),
};

// Password schema - required for FARMER and BUYER, not for AUTHORITY
const passwordSchema = {
	password: yup
		.string()
		.min(8, "Password must be at least 8 characters")
		.required("Password is required"),
	confirmPassword: yup
		.string()
		.oneOf([yup.ref("password"), null], "Passwords must match")
		.required("Please confirm your password"),
};

// Farmer specific fields
const farmerFields = {
	farmSizeAc: yup.number().positive("Farm size must be positive").optional(),
	farmType: yup.string().optional(),
};

// Buyer specific fields
const buyerFields = {
	organisation: yup.string().optional(),
};

// Authority specific fields
const authorityFields = {
	designation: yup.string().optional(),
	employeeId: yup.string().optional(),
	employeeIdImageUrl: yup.string().url("Please enter a valid URL").optional(),
	officeDivision: yup.string().optional(),
	officeDistrict: yup.string().optional(),
	officeUpazila: yup.string().optional(),
	officeUnion: yup.string().optional(),
};

// Dynamic schema based on role
export const getSignUpSchema = (role) => {
	let schema = { ...baseRegisterSchema };

	// Add password for FARMER and BUYER
	if (role === "FARMER" || role === "BUYER") {
		schema = { ...schema, ...passwordSchema };
	}

	// Add role-specific fields
	if (role === "FARMER") {
		schema = { ...schema, ...farmerFields };
	} else if (role === "BUYER") {
		schema = { ...schema, ...buyerFields };
	} else if (role === "AUTHORITY") {
		schema = { ...schema, ...authorityFields };
	}

	return yup.object(schema);
};

// Default sign up schema (for backward compatibility)
export const signUpSchema = yup.object({
	email: yup
		.string()
		.email("Please enter a valid email address")
		.required("Email is required"),
	phone: yup
		.string()
		.min(10, "Phone number must be at least 10 digits")
		.max(15, "Phone number must not exceed 15 digits")
		.matches(/^[+]?[\d\s\-()]+$/, "Please enter a valid phone number")
		.required("Phone number is required"),
	...passwordSchema,
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

// OTP verification validation schema
export const otpVerificationSchema = yup.object({
	email: yup
		.string()
		.email("Please enter a valid email address")
		.required("Email is required"),
	otp: yup
		.string()
		.matches(/^\d{6}$/, "OTP must be exactly 6 digits")
		.required("OTP is required"),
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
