import { clsx } from "clsx";

const Button = ({
	children,
	variant = "primary",
	size = "md",
	className,
	disabled = false,
	loading = false,
	...props
}) => {
	const baseClasses =
		"inline-flex items-center justify-center font-medium transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed";

	const variants = {
		primary:
			"bg-primary-600 text-white shadow-sm hover:bg-primary-700 hover:shadow-md focus:ring-primary-500",
		secondary:
			"border border-gray-300 bg-white text-gray-700 shadow-sm hover:bg-gray-50 hover:shadow-md focus:ring-primary-500",
		outline:
			"border-2 border-primary-600 bg-transparent text-primary-600 hover:bg-primary-600 hover:text-white focus:ring-primary-500",
		ghost:
			"bg-transparent text-primary-600 hover:bg-primary-50 focus:ring-primary-500",
		accent:
			"bg-accent-600 text-white shadow-sm hover:bg-accent-700 hover:shadow-md focus:ring-accent-500",
	};

	const sizes = {
		sm: "px-3 py-2 text-sm rounded-md",
		md: "px-6 py-3 text-sm rounded-lg",
		lg: "px-8 py-4 text-base rounded-lg",
		xl: "px-10 py-5 text-lg rounded-xl",
	};

	return (
		<button
			className={clsx(baseClasses, variants[variant], sizes[size], className)}
			disabled={disabled || loading}
			{...props}
		>
			{loading && (
				<svg
					className="animate-spin -ml-1 mr-2 h-4 w-4"
					fill="none"
					viewBox="0 0 24 24"
				>
					<circle
						className="opacity-25"
						cx="12"
						cy="12"
						r="10"
						stroke="currentColor"
						strokeWidth="4"
					/>
					<path
						className="opacity-75"
						fill="currentColor"
						d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
					/>
				</svg>
			)}
			{children}
		</button>
	);
};

export default Button;
