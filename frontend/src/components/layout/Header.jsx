import React, { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import {
	Menu,
	X,
	User,
	LogOut,
	Settings,
	ChevronDown,
	Leaf,
} from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";
import Button from "../ui/Button";

const Header = () => {
	const [isMenuOpen, setIsMenuOpen] = useState(false);
	const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
	const { user, isAuthenticated, logout } = useAuth();
	const navigate = useNavigate();
	const location = useLocation();

	const handleLogout = async () => {
		await logout();
		navigate("/");
	};

	const isActiveLink = (path) => {
		return (
			location.pathname === path || location.pathname.startsWith(path + "/")
		);
	};

	const navigationLinks = [
		{ name: "Home", href: "/" },
		{ name: "About", href: "/about" },
		{ name: "Features", href: "/features" },
		{ name: "Contact", href: "/contact" },
	];

	return (
		<header className="bg-white/90 backdrop-blur-md border-b border-gray-200 sticky top-0 z-50">
			<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
				<div className="flex justify-between items-center h-16">
					{/* Logo */}
					<Link to="/" className="flex items-center space-x-2">
						<div className="w-8 h-8 rounded-lg flex items-center justify-center">
							<img
								src="https://cdn-icons-png.flaticon.com/128/2713/2713479.png"
								alt="logo"
								className="w-8 h-8"
							/>
						</div>
						<span className="font-bold text-xl text-primary-800">
							Shohoj Krishi
						</span>
					</Link>

					{/* Desktop Navigation */}
					<nav className="hidden md:flex items-center space-x-8">
						{navigationLinks.map((link) => (
							<Link
								key={link.name}
								to={link.href}
								className={`text-sm font-medium transition-colors ${
									isActiveLink(link.href)
										? "text-primary-600"
										: "text-gray-700 hover:text-primary-600"
								}`}
							>
								{link.name}
							</Link>
						))}
					</nav>

					{/* Auth Section */}
					<div className="hidden md:flex items-center space-x-4">
						{isAuthenticated ? (
							<div className="relative">
								<button
									onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
									className="flex items-center space-x-2 p-2 rounded-lg hover:bg-gray-100 transition-colors"
								>
									<div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
										<User className="w-4 h-4 text-primary-600" />
									</div>
									<span className="text-sm font-medium text-gray-900">
										{user?.firstName || "User"}
									</span>
									<ChevronDown className="w-4 h-4 text-gray-500" />
								</button>

								{/* User Dropdown */}
								{isUserMenuOpen && (
									<div className="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-gray-200 py-2 z-10">
										<div className="px-4 py-2 border-b border-gray-100">
											<p className="text-sm font-medium text-gray-900">
												{user?.firstName} {user?.lastName}
											</p>
											<p className="text-xs text-gray-500 capitalize">
												{user?.userType}
											</p>
										</div>
										<Link
											to="/dashboard"
											className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
											onClick={() => setIsUserMenuOpen(false)}
										>
											<User className="w-4 h-4 mr-2" />
											Dashboard
										</Link>
										<Link
											to="/settings"
											className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
											onClick={() => setIsUserMenuOpen(false)}
										>
											<Settings className="w-4 h-4 mr-2" />
											Settings
										</Link>
										<button
											onClick={() => {
												setIsUserMenuOpen(false);
												handleLogout();
											}}
											className="flex items-center w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50"
										>
											<LogOut className="w-4 h-4 mr-2" />
											Sign Out
										</button>
									</div>
								)}
							</div>
						) : (
							<div className="flex items-center space-x-3">
								<Link to="/auth/signin">
									<Button variant="ghost" size="sm">
										Sign In
									</Button>
								</Link>
								<Link to="/auth/signup">
									<Button variant="primary" size="sm">
										Join Now
									</Button>
								</Link>
							</div>
						)}
					</div>

					{/* Mobile menu button */}
					<div className="md:hidden">
						<button
							onClick={() => setIsMenuOpen(!isMenuOpen)}
							className="p-2 rounded-lg text-gray-600 hover:text-gray-900 hover:bg-gray-100"
						>
							{isMenuOpen ? (
								<X className="w-6 h-6" />
							) : (
								<Menu className="w-6 h-6" />
							)}
						</button>
					</div>
				</div>

				{/* Mobile Navigation Menu */}
				{isMenuOpen && (
					<div className="md:hidden border-t border-gray-200 py-4">
						<div className="space-y-2">
							{navigationLinks.map((link) => (
								<Link
									key={link.name}
									to={link.href}
									className={`block px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
										isActiveLink(link.href)
											? "text-primary-600 bg-primary-50"
											: "text-gray-700 hover:text-primary-600 hover:bg-gray-50"
									}`}
									onClick={() => setIsMenuOpen(false)}
								>
									{link.name}
								</Link>
							))}

							{/* Mobile Auth */}
							<div className="pt-4 mt-4 border-t border-gray-200">
								{isAuthenticated ? (
									<div className="space-y-2">
										<div className="px-3 py-2">
											<p className="text-sm font-medium text-gray-900">
												{user?.firstName} {user?.lastName}
											</p>
											<p className="text-xs text-gray-500 capitalize">
												{user?.userType}
											</p>
										</div>
										<Link
											to="/dashboard"
											className="block px-3 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-lg"
											onClick={() => setIsMenuOpen(false)}
										>
											Dashboard
										</Link>
										<Link
											to="/settings"
											className="block px-3 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-lg"
											onClick={() => setIsMenuOpen(false)}
										>
											Settings
										</Link>
										<button
											onClick={() => {
												setIsMenuOpen(false);
												handleLogout();
											}}
											className="block w-full text-left px-3 py-2 text-sm text-red-600 hover:bg-red-50 rounded-lg"
										>
											Sign Out
										</button>
									</div>
								) : (
									<div className="space-y-2">
										<Link
											to="/auth/signin"
											className="block px-3 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-lg"
											onClick={() => setIsMenuOpen(false)}
										>
											Sign In
										</Link>
										<Link
											to="/auth/signup"
											className="block px-3 py-2 text-sm text-primary-600 hover:bg-primary-50 rounded-lg"
											onClick={() => setIsMenuOpen(false)}
										>
											Join Now
										</Link>
									</div>
								)}
							</div>
						</div>
					</div>
				)}
			</div>

			{/* Backdrop for user menu */}
			{isUserMenuOpen && (
				<div
					className="fixed inset-0 z-0"
					onClick={() => setIsUserMenuOpen(false)}
				/>
			)}
		</header>
	);
};

export default Header;
