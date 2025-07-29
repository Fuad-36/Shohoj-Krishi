import React, { useState } from "react";
import { Outlet, Link, useLocation, useNavigate } from "react-router-dom";
import {
	Menu,
	X,
	Bell,
	Search,
	LogOut,
	Settings,
	User,
	HelpCircle,
	BarChart3,
	Sprout,
	ShoppingCart,
	Bot,
	Cloud,
	BookOpen,
	Store,
	ClipboardList,
	Users,
	TrendingUp,
	Activity,
	FileText,
	Banknote,
	PieChart,
	UserCog,
	Shield,
	Server,
	Leaf,
} from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";
import { getUserDashboardTabs, USER_ROLES } from "../../constants/roles";
import Button from "../ui/Button";

// Icon mapping for dynamic rendering
const iconMap = {
	BarChart3,
	Sprout,
	ShoppingCart,
	Bot,
	Cloud,
	BookOpen,
	Store,
	ClipboardList,
	Users,
	TrendingUp,
	Activity,
	FileText,
	Banknote,
	PieChart,
	UserCog,
	Shield,
	Server,
};

const DashboardLayout = () => {
	const [sidebarOpen, setSidebarOpen] = useState(false);
	const [searchQuery, setSearchQuery] = useState("");
	const { user, logout } = useAuth();
	const location = useLocation();
	const navigate = useNavigate();

	const handleLogout = async () => {
		await logout();
		navigate("/");
	};

	const dashboardTabs = getUserDashboardTabs(user?.userType);

	const isActiveTab = (path) => {
		if (path === "/dashboard") {
			return location.pathname === "/dashboard";
		}
		return location.pathname.startsWith(path);
	};

	// Role-specific greeting and stats
	const getRoleSpecificContent = () => {
		switch (user?.userType) {
			case USER_ROLES.FARMER:
				return {
					greeting: "Welcome back to your farm dashboard! 🌾",
					stats: [
						{ label: "Active Crops", value: "12", color: "text-primary-600" },
						{
							label: "This Month Sales",
							value: "৳45,000",
							color: "text-secondary-600",
						},
						{ label: "Weather Alerts", value: "3", color: "text-accent-600" },
					],
				};
			case USER_ROLES.BUYER:
				return {
					greeting: "Welcome to your buying dashboard! 🛒",
					stats: [
						{ label: "Active Orders", value: "8", color: "text-secondary-600" },
						{
							label: "Farmer Contacts",
							value: "24",
							color: "text-primary-600",
						},
						{
							label: "This Month Purchases",
							value: "৳120,000",
							color: "text-accent-600",
						},
					],
				};
			case USER_ROLES.GOVERNMENT:
				return {
					greeting: "Government Dashboard - Krishi Odhidoptor 🏛️",
					stats: [
						{ label: "Farmer Queries", value: "156", color: "text-accent-600" },
						{
							label: "Content Published",
							value: "23",
							color: "text-primary-600",
						},
						{
							label: "Active Schemes",
							value: "12",
							color: "text-secondary-600",
						},
					],
				};
			case USER_ROLES.ADMIN:
				return {
					greeting: "Platform Administration Dashboard 🛡️",
					stats: [
						{ label: "Total Users", value: "2,456", color: "text-primary-600" },
						{ label: "System Health", value: "99.8%", color: "text-green-600" },
						{
							label: "Monthly Growth",
							value: "+12%",
							color: "text-secondary-600",
						},
					],
				};
			default:
				return {
					greeting: "Welcome to Shohoj-Krishi! 🌱",
					stats: [],
				};
		}
	};

	const roleContent = getRoleSpecificContent();

	return (
		<div className="min-h-screen bg-gray-50 flex">
			{/* Sidebar */}
			<div
				className={`fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform ${
					sidebarOpen ? "translate-x-0" : "-translate-x-full"
				} lg:translate-x-0 lg:static lg:inset-0 transition-transform duration-300 ease-in-out`}
			>
				{/* Sidebar Header */}
				<div className="flex items-center justify-between h-16 px-6 border-b border-gray-200">
					<div className="flex items-center space-x-2">
						<div className="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
							<Leaf className="w-5 h-5 text-white" />
						</div>
						<span className="font-bold text-primary-800">Shohoj-Krishi</span>
					</div>
					<button
						onClick={() => setSidebarOpen(false)}
						className="lg:hidden text-gray-500 hover:text-gray-700"
					>
						<X className="w-6 h-6" />
					</button>
				</div>

				{/* User Info */}
				<div className="p-6 border-b border-gray-200">
					<div className="flex items-center space-x-3">
						<div className="w-10 h-10 bg-primary-100 rounded-full flex items-center justify-center">
							<User className="w-5 h-5 text-primary-600" />
						</div>
						<div>
							<p className="font-medium text-gray-900">
								{user?.firstName} {user?.lastName}
							</p>
							<p className="text-sm text-gray-500 capitalize">
								{user?.userType} • {user?.location}
							</p>
						</div>
					</div>
				</div>

				{/* Navigation */}
				<nav className="flex-1 px-4 py-6 space-y-2">
					{dashboardTabs.map((tab) => {
						const IconComponent = iconMap[tab.icon];
						return (
							<Link
								key={tab.id}
								to={tab.path}
								className={`flex items-center px-3 py-3 rounded-lg text-sm font-medium transition-colors ${
									isActiveTab(tab.path)
										? "bg-primary-100 text-primary-700 border-r-2 border-primary-600"
										: "text-gray-700 hover:bg-gray-100"
								}`}
								onClick={() => setSidebarOpen(false)}
							>
								{IconComponent && <IconComponent className="w-5 h-5 mr-3" />}
								<div>
									<div>{tab.label}</div>
									<div className="text-xs text-gray-500">{tab.description}</div>
								</div>
							</Link>
						);
					})}
				</nav>

				{/* Sidebar Footer */}
				<div className="p-4 border-t border-gray-200 space-y-2">
					<Link
						to="/help"
						className="flex items-center px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-lg"
					>
						<HelpCircle className="w-4 h-4 mr-3" />
						Help & Support
					</Link>
					<Link
						to="/settings"
						className="flex items-center px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-lg"
					>
						<Settings className="w-4 h-4 mr-3" />
						Settings
					</Link>
					<button
						onClick={handleLogout}
						className="flex items-center w-full px-3 py-2 text-sm text-red-600 hover:bg-red-50 rounded-lg"
					>
						<LogOut className="w-4 h-4 mr-3" />
						Sign Out
					</button>
				</div>
			</div>

			{/* Main Content */}
			<div className="flex-1 lg:ml-0">
				{/* Top Bar */}
				<header className="bg-white shadow-sm border-b border-gray-200">
					<div className="flex items-center justify-between h-16 px-6">
						<div className="flex items-center space-x-4">
							<button
								onClick={() => setSidebarOpen(true)}
								className="lg:hidden text-gray-500 hover:text-gray-700"
							>
								<Menu className="w-6 h-6" />
							</button>

							{/* Search */}
							<div className="hidden md:block relative">
								<Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
								<input
									type="text"
									placeholder="Search..."
									value={searchQuery}
									onChange={(e) => setSearchQuery(e.target.value)}
									className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 w-64"
								/>
							</div>
						</div>

						<div className="flex items-center space-x-4">
							{/* Notifications */}
							<button className="relative p-2 text-gray-400 hover:text-gray-500">
								<Bell className="w-6 h-6" />
								<span className="absolute top-0 right-0 block h-2 w-2 rounded-full bg-red-400 transform translate-x-1/2 -translate-y-1/2"></span>
							</button>

							{/* User Menu */}
							<div className="flex items-center space-x-3">
								<div className="hidden md:block text-right">
									<p className="text-sm font-medium text-gray-900">
										{user?.firstName} {user?.lastName}
									</p>
									<p className="text-xs text-gray-500 capitalize">
										{user?.userType}
									</p>
								</div>
								<div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
									<User className="w-4 h-4 text-primary-600" />
								</div>
							</div>
						</div>
					</div>
				</header>

				{/* Page Content */}
				<main className="flex-1 p-6">
					{/* Welcome Section - Show on dashboard home */}
					{location.pathname === "/dashboard" && (
						<div className="mb-8">
							<h1 className="text-2xl font-bold text-gray-900 mb-2">
								{roleContent.greeting}
							</h1>
							<p className="text-gray-600 mb-6">
								Here's what's happening with your agricultural activities today.
							</p>

							{/* Quick Stats */}
							{roleContent.stats.length > 0 && (
								<div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
									{roleContent.stats.map((stat, index) => (
										<div
											key={index}
											className="bg-white rounded-xl p-6 shadow-sm border"
										>
											<h3 className="text-sm font-medium text-gray-500 mb-2">
												{stat.label}
											</h3>
											<p className={`text-2xl font-bold ${stat.color}`}>
												{stat.value}
											</p>
										</div>
									))}
								</div>
							)}
						</div>
					)}

					<Outlet />
				</main>
			</div>

			{/* Mobile Sidebar Overlay */}
			{sidebarOpen && (
				<div
					className="fixed inset-0 z-40 bg-black bg-opacity-50 lg:hidden"
					onClick={() => setSidebarOpen(false)}
				/>
			)}
		</div>
	);
};

export default DashboardLayout;
