import React from "react";
import { Link } from "react-router-dom";
import {
	TrendingUp,
	Users,
	ShoppingCart,
	AlertTriangle,
	Calendar,
	Sprout,
	CloudRain,
	MessageCircle,
	BookOpen,
	Bell,
} from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";
import { USER_ROLES } from "../../constants/roles";

const DashboardOverview = () => {
	const { user } = useAuth();

	// Role-specific dashboard content
	const getRoleDashboardContent = () => {
		switch (user?.userType) {
			case USER_ROLES.FARMER:
				return <FarmerDashboard />;
			case USER_ROLES.BUYER:
				return <BuyerDashboard />;
			case USER_ROLES.GOVERNMENT:
				return <GovernmentDashboard />;
			case USER_ROLES.ADMIN:
				return <AdminDashboard />;
			default:
				return <DefaultDashboard />;
		}
	};

	return <div className="space-y-6">{getRoleDashboardContent()}</div>;
};

// Farmer Dashboard Content
const FarmerDashboard = () => {
	return (
		<>
			{/* Quick Actions */}
			<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
				<Link to="/dashboard/crops" className="block">
					<div className="bg-white rounded-xl p-6 shadow-sm border hover:shadow-md transition-shadow">
						<div className="flex items-center">
							<div className="p-3 bg-primary-100 rounded-lg">
								<Sprout className="w-6 h-6 text-primary-600" />
							</div>
							<div className="ml-4">
								<h3 className="text-lg font-semibold text-gray-900">
									Post New Crop
								</h3>
								<p className="text-gray-600">List your harvest</p>
							</div>
						</div>
					</div>
				</Link>

				<Link to="/dashboard/market" className="block">
					<div className="bg-white rounded-xl p-6 shadow-sm border hover:shadow-md transition-shadow">
						<div className="flex items-center">
							<div className="p-3 bg-secondary-100 rounded-lg">
								<ShoppingCart className="w-6 h-6 text-secondary-600" />
							</div>
							<div className="ml-4">
								<h3 className="text-lg font-semibold text-gray-900">
									Browse Market
								</h3>
								<p className="text-gray-600">Find buyers</p>
							</div>
						</div>
					</div>
				</Link>

				<Link to="/dashboard/ai-tools" className="block">
					<div className="bg-white rounded-xl p-6 shadow-sm border hover:shadow-md transition-shadow">
						<div className="flex items-center">
							<div className="p-3 bg-accent-100 rounded-lg">
								<MessageCircle className="w-6 h-6 text-accent-600" />
							</div>
							<div className="ml-4">
								<h3 className="text-lg font-semibold text-gray-900">
									AI Assistant
								</h3>
								<p className="text-gray-600">Get farming advice</p>
							</div>
						</div>
					</div>
				</Link>

				<Link to="/dashboard/weather" className="block">
					<div className="bg-white rounded-xl p-6 shadow-sm border hover:shadow-md transition-shadow">
						<div className="flex items-center">
							<div className="p-3 bg-blue-100 rounded-lg">
								<CloudRain className="w-6 h-6 text-blue-600" />
							</div>
							<div className="ml-4">
								<h3 className="text-lg font-semibold text-gray-900">Weather</h3>
								<p className="text-gray-600">Check forecasts</p>
							</div>
						</div>
					</div>
				</Link>
			</div>

			{/* Recent Activity & Notifications */}
			<div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
				<div className="bg-white rounded-xl p-6 shadow-sm border">
					<h3 className="text-lg font-semibold text-gray-900 mb-4">
						Recent Crop Listings
					</h3>
					<div className="space-y-3">
						{[
							{
								crop: "Rice (Aman)",
								quantity: "50 tons",
								date: "2 days ago",
								status: "Active",
							},
							{
								crop: "Potatoes",
								quantity: "20 tons",
								date: "5 days ago",
								status: "Sold",
							},
							{
								crop: "Tomatoes",
								quantity: "5 tons",
								date: "1 week ago",
								status: "Active",
							},
						].map((listing, index) => (
							<div
								key={index}
								className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
							>
								<div>
									<p className="font-medium text-gray-900">{listing.crop}</p>
									<p className="text-sm text-gray-600">
										{listing.quantity} • {listing.date}
									</p>
								</div>
								<span
									className={`px-3 py-1 rounded-full text-xs font-medium ${
										listing.status === "Active"
											? "bg-green-100 text-green-800"
											: "bg-blue-100 text-blue-800"
									}`}
								>
									{listing.status}
								</span>
							</div>
						))}
					</div>
				</div>

				<div className="bg-white rounded-xl p-6 shadow-sm border">
					<h3 className="text-lg font-semibold text-gray-900 mb-4">
						Weather Alerts
					</h3>
					<div className="space-y-3">
						<div className="flex items-start space-x-3 p-3 bg-yellow-50 rounded-lg border border-yellow-200">
							<AlertTriangle className="w-5 h-5 text-yellow-600 mt-0.5" />
							<div>
								<p className="font-medium text-yellow-800">
									Heavy Rain Warning
								</p>
								<p className="text-sm text-yellow-700">
									Expected rainfall: 50-80mm in next 24 hours
								</p>
							</div>
						</div>
						<div className="flex items-start space-x-3 p-3 bg-blue-50 rounded-lg border border-blue-200">
							<Bell className="w-5 h-5 text-blue-600 mt-0.5" />
							<div>
								<p className="font-medium text-blue-800">Irrigation Reminder</p>
								<p className="text-sm text-blue-700">
									Optimal time for irrigation: Early morning
								</p>
							</div>
						</div>
					</div>
				</div>
			</div>

			{/* Educational Content */}
			<div className="bg-white rounded-xl p-6 shadow-sm border">
				<h3 className="text-lg font-semibold text-gray-900 mb-4">
					Latest Agricultural News & Tips
				</h3>
				<div className="grid grid-cols-1 md:grid-cols-3 gap-4">
					{[
						{
							title: "Modern Rice Cultivation Techniques",
							description:
								"Learn about high-yield rice varieties and best practices",
							date: "Today",
							category: "Education",
						},
						{
							title: "Government Subsidy Update",
							description:
								"New fertilizer subsidy program launched for small farmers",
							date: "Yesterday",
							category: "News",
						},
						{
							title: "Pest Management Guide",
							description: "Identify and treat common crop pests this season",
							date: "2 days ago",
							category: "Guide",
						},
					].map((article, index) => (
						<div
							key={index}
							className="p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
						>
							<span className="inline-block px-2 py-1 bg-primary-100 text-primary-800 text-xs rounded mb-2">
								{article.category}
							</span>
							<h4 className="font-medium text-gray-900 mb-2">
								{article.title}
							</h4>
							<p className="text-sm text-gray-600 mb-2">
								{article.description}
							</p>
							<p className="text-xs text-gray-500">{article.date}</p>
						</div>
					))}
				</div>
			</div>
		</>
	);
};

// Buyer Dashboard Content
const BuyerDashboard = () => {
	return (
		<>
			{/* Quick Actions for Buyers */}
			<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
				<Link to="/dashboard/marketplace" className="block">
					<div className="bg-white rounded-xl p-6 shadow-sm border hover:shadow-md transition-shadow">
						<div className="flex items-center">
							<div className="p-3 bg-secondary-100 rounded-lg">
								<ShoppingCart className="w-6 h-6 text-secondary-600" />
							</div>
							<div className="ml-4">
								<h3 className="text-lg font-semibold text-gray-900">
									Browse Crops
								</h3>
								<p className="text-gray-600">Find fresh produce</p>
							</div>
						</div>
					</div>
				</Link>

				<Link to="/dashboard/requirements" className="block">
					<div className="bg-white rounded-xl p-6 shadow-sm border hover:shadow-md transition-shadow">
						<div className="flex items-center">
							<div className="p-3 bg-primary-100 rounded-lg">
								<MessageCircle className="w-6 h-6 text-primary-600" />
							</div>
							<div className="ml-4">
								<h3 className="text-lg font-semibold text-gray-900">
									Post Requirements
								</h3>
								<p className="text-gray-600">Let farmers know what you need</p>
							</div>
						</div>
					</div>
				</Link>

				<Link to="/dashboard/contacts" className="block">
					<div className="bg-white rounded-xl p-6 shadow-sm border hover:shadow-md transition-shadow">
						<div className="flex items-center">
							<div className="p-3 bg-accent-100 rounded-lg">
								<Users className="w-6 h-6 text-accent-600" />
							</div>
							<div className="ml-4">
								<h3 className="text-lg font-semibold text-gray-900">
									Farmer Network
								</h3>
								<p className="text-gray-600">Manage relationships</p>
							</div>
						</div>
					</div>
				</Link>

				<Link to="/dashboard/analytics" className="block">
					<div className="bg-white rounded-xl p-6 shadow-sm border hover:shadow-md transition-shadow">
						<div className="flex items-center">
							<div className="p-3 bg-green-100 rounded-lg">
								<TrendingUp className="w-6 h-6 text-green-600" />
							</div>
							<div className="ml-4">
								<h3 className="text-lg font-semibold text-gray-900">
									Analytics
								</h3>
								<p className="text-gray-600">Track purchases</p>
							</div>
						</div>
					</div>
				</Link>
			</div>

			{/* Available Crops & Market Trends */}
			<div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
				<div className="bg-white rounded-xl p-6 shadow-sm border">
					<h3 className="text-lg font-semibold text-gray-900 mb-4">
						Fresh Crops Available
					</h3>
					<div className="space-y-3">
						{[
							{
								crop: "Premium Rice",
								farmer: "Abdul Rahman",
								location: "Bogura",
								price: "৳45/kg",
							},
							{
								crop: "Organic Tomatoes",
								farmer: "Rashida Begum",
								location: "Jessore",
								price: "৳80/kg",
							},
							{
								crop: "Fresh Potatoes",
								farmer: "Karim Sheikh",
								location: "Munshiganj",
								price: "৳35/kg",
							},
						].map((item, index) => (
							<div
								key={index}
								className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
							>
								<div>
									<p className="font-medium text-gray-900">{item.crop}</p>
									<p className="text-sm text-gray-600">
										{item.farmer} • {item.location}
									</p>
								</div>
								<div className="text-right">
									<p className="font-semibold text-secondary-600">
										{item.price}
									</p>
									<button className="text-sm text-primary-600 hover:text-primary-700">
										Contact
									</button>
								</div>
							</div>
						))}
					</div>
				</div>

				<div className="bg-white rounded-xl p-6 shadow-sm border">
					<h3 className="text-lg font-semibold text-gray-900 mb-4">
						Market Price Trends
					</h3>
					<div className="space-y-4">
						{[
							{ crop: "Rice", current: "৳45", trend: "+5%", status: "up" },
							{ crop: "Wheat", current: "৳38", trend: "-2%", status: "down" },
							{ crop: "Potatoes", current: "৳35", trend: "+8%", status: "up" },
						].map((trend, index) => (
							<div key={index} className="flex items-center justify-between">
								<span className="font-medium text-gray-900">{trend.crop}</span>
								<div className="flex items-center space-x-2">
									<span className="font-semibold">{trend.current}</span>
									<span
										className={`text-sm ${
											trend.status === "up" ? "text-green-600" : "text-red-600"
										}`}
									>
										{trend.trend}
									</span>
								</div>
							</div>
						))}
					</div>
				</div>
			</div>
		</>
	);
};

// Government Dashboard Content
const GovernmentDashboard = () => {
	return (
		<div className="space-y-6">
			<div className="bg-white rounded-xl p-6 shadow-sm border">
				<h3 className="text-lg font-semibold text-gray-900 mb-4">
					System Overview
				</h3>
				<div className="grid grid-cols-1 md:grid-cols-4 gap-4">
					<div className="text-center p-4 bg-primary-50 rounded-lg">
						<p className="text-2xl font-bold text-primary-600">1,245</p>
						<p className="text-sm text-primary-700">Active Farmers</p>
					</div>
					<div className="text-center p-4 bg-secondary-50 rounded-lg">
						<p className="text-2xl font-bold text-secondary-600">324</p>
						<p className="text-sm text-secondary-700">Active Buyers</p>
					</div>
					<div className="text-center p-4 bg-accent-50 rounded-lg">
						<p className="text-2xl font-bold text-accent-600">156</p>
						<p className="text-sm text-accent-700">Pending Queries</p>
					</div>
					<div className="text-center p-4 bg-green-50 rounded-lg">
						<p className="text-2xl font-bold text-green-600">89%</p>
						<p className="text-sm text-green-700">Response Rate</p>
					</div>
				</div>
			</div>
		</div>
	);
};

// Admin Dashboard Content
const AdminDashboard = () => {
	return (
		<div className="space-y-6">
			<div className="bg-white rounded-xl p-6 shadow-sm border">
				<h3 className="text-lg font-semibold text-gray-900 mb-4">
					Platform Overview
				</h3>
				<div className="grid grid-cols-1 md:grid-cols-4 gap-4">
					<div className="text-center p-4 bg-blue-50 rounded-lg">
						<p className="text-2xl font-bold text-blue-600">2,456</p>
						<p className="text-sm text-blue-700">Total Users</p>
					</div>
					<div className="text-center p-4 bg-green-50 rounded-lg">
						<p className="text-2xl font-bold text-green-600">99.8%</p>
						<p className="text-sm text-green-700">Uptime</p>
					</div>
					<div className="text-center p-4 bg-purple-50 rounded-lg">
						<p className="text-2xl font-bold text-purple-600">1,847</p>
						<p className="text-sm text-purple-700">Daily Active Users</p>
					</div>
					<div className="text-center p-4 bg-orange-50 rounded-lg">
						<p className="text-2xl font-bold text-orange-600">+12%</p>
						<p className="text-sm text-orange-700">Monthly Growth</p>
					</div>
				</div>
			</div>
		</div>
	);
};

// Default Dashboard (fallback)
const DefaultDashboard = () => {
	return (
		<div className="bg-white rounded-xl p-8 shadow-sm border text-center">
			<h3 className="text-xl font-semibold text-gray-900 mb-4">
				Welcome to Shohoj-Krishi!
			</h3>
			<p className="text-gray-600">
				Your agricultural dashboard is being set up.
			</p>
		</div>
	);
};

export default DashboardOverview;
