import React, { useState } from "react";
import {
	Plus,
	Edit,
	Trash2,
	Eye,
	Calendar,
	MapPin,
	DollarSign,
} from "lucide-react";
import Button from "../../../components/ui/Button";

const FarmerCrops = () => {
	const [crops] = useState([
		{
			id: 1,
			name: "Rice (Aman)",
			variety: "BRRI dhan29",
			quantity: "50 tons",
			harvestDate: "2024-02-15",
			location: "Bogura, Rajshahi",
			pricePerKg: 45,
			status: "Ready for Harvest",
			description: "High-quality Aman rice, suitable for wholesale buyers",
			image: "/api/placeholder/300/200",
		},
		{
			id: 2,
			name: "Potatoes",
			variety: "Granola",
			quantity: "20 tons",
			harvestDate: "2024-01-20",
			location: "Munshiganj, Dhaka",
			pricePerKg: 35,
			status: "Available",
			description: "Premium quality potatoes for retail and wholesale",
			image: "/api/placeholder/300/200",
		},
		{
			id: 3,
			name: "Tomatoes",
			variety: "Roma",
			quantity: "5 tons",
			harvestDate: "2024-01-25",
			location: "Jessore, Khulna",
			pricePerKg: 80,
			status: "Harvesting",
			description: "Fresh organic tomatoes, pesticide-free",
			image: "/api/placeholder/300/200",
		},
	]);

	const getStatusColor = (status) => {
		switch (status) {
			case "Available":
				return "bg-green-100 text-green-800";
			case "Ready for Harvest":
				return "bg-yellow-100 text-yellow-800";
			case "Harvesting":
				return "bg-blue-100 text-blue-800";
			case "Sold":
				return "bg-gray-100 text-gray-800";
			default:
				return "bg-gray-100 text-gray-800";
		}
	};

	return (
		<div className="space-y-6">
			{/* Header */}
			<div className="flex justify-between items-center">
				<div>
					<h1 className="text-2xl font-bold text-gray-900">My Crops</h1>
					<p className="text-gray-600">
						Manage your crop listings and connect with buyers
					</p>
				</div>
				<Button variant="primary" className="flex items-center">
					<Plus className="w-4 h-4 mr-2" />
					Add New Crop
				</Button>
			</div>

			{/* Stats Cards */}
			<div className="grid grid-cols-1 md:grid-cols-4 gap-6">
				<div className="bg-white rounded-xl p-6 shadow-sm border">
					<h3 className="text-sm font-medium text-gray-500 mb-2">
						Total Crops
					</h3>
					<p className="text-2xl font-bold text-gray-900">12</p>
					<p className="text-sm text-green-600 mt-1">+2 this month</p>
				</div>
				<div className="bg-white rounded-xl p-6 shadow-sm border">
					<h3 className="text-sm font-medium text-gray-500 mb-2">
						Available for Sale
					</h3>
					<p className="text-2xl font-bold text-gray-900">8</p>
					<p className="text-sm text-blue-600 mt-1">Ready to sell</p>
				</div>
				<div className="bg-white rounded-xl p-6 shadow-sm border">
					<h3 className="text-sm font-medium text-gray-500 mb-2">
						Total Value
					</h3>
					<p className="text-2xl font-bold text-gray-900">৳2,40,000</p>
					<p className="text-sm text-secondary-600 mt-1">Estimated value</p>
				</div>
				<div className="bg-white rounded-xl p-6 shadow-sm border">
					<h3 className="text-sm font-medium text-gray-500 mb-2">
						Buyer Inquiries
					</h3>
					<p className="text-2xl font-bold text-gray-900">24</p>
					<p className="text-sm text-accent-600 mt-1">This week</p>
				</div>
			</div>

			{/* Crops Grid */}
			<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
				{crops.map((crop) => (
					<div
						key={crop.id}
						className="bg-white rounded-xl shadow-sm border overflow-hidden hover:shadow-md transition-shadow"
					>
						{/* Crop Image */}
						<div className="h-48 bg-primary-100 flex items-center justify-center">
							<div className="text-center">
								<div className="text-4xl mb-2">
									{crop.name.includes("Rice")
										? "🌾"
										: crop.name.includes("Potato")
										? "🥔"
										: crop.name.includes("Tomato")
										? "🍅"
										: "🌱"}
								</div>
								<p className="text-sm text-gray-600">Crop Image</p>
							</div>
						</div>

						{/* Crop Details */}
						<div className="p-6">
							<div className="flex justify-between items-start mb-3">
								<div>
									<h3 className="text-lg font-semibold text-gray-900">
										{crop.name}
									</h3>
									<p className="text-sm text-gray-600">{crop.variety}</p>
								</div>
								<span
									className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(
										crop.status
									)}`}
								>
									{crop.status}
								</span>
							</div>

							<div className="space-y-2 mb-4">
								<div className="flex items-center text-sm text-gray-600">
									<DollarSign className="w-4 h-4 mr-2 text-secondary-600" />৳
									{crop.pricePerKg}/kg • {crop.quantity}
								</div>
								<div className="flex items-center text-sm text-gray-600">
									<Calendar className="w-4 h-4 mr-2 text-primary-600" />
									Harvest: {new Date(crop.harvestDate).toLocaleDateString()}
								</div>
								<div className="flex items-center text-sm text-gray-600">
									<MapPin className="w-4 h-4 mr-2 text-accent-600" />
									{crop.location}
								</div>
							</div>

							<p className="text-sm text-gray-600 mb-4 line-clamp-2">
								{crop.description}
							</p>

							{/* Action Buttons */}
							<div className="flex space-x-2">
								<Button variant="outline" size="sm" className="flex-1">
									<Eye className="w-4 h-4 mr-1" />
									View
								</Button>
								<Button variant="ghost" size="sm">
									<Edit className="w-4 h-4" />
								</Button>
								<Button
									variant="ghost"
									size="sm"
									className="text-red-600 hover:text-red-700"
								>
									<Trash2 className="w-4 h-4" />
								</Button>
							</div>
						</div>
					</div>
				))}
			</div>

			{/* Add New Crop CTA */}
			<div className="bg-primary-50 rounded-xl p-8 text-center border-2 border-dashed border-primary-200 hover:border-primary-300 transition-colors cursor-pointer">
				<div className="text-4xl mb-4">🌱</div>
				<h3 className="text-lg font-semibold text-gray-900 mb-2">
					Add Your Next Crop
				</h3>
				<p className="text-gray-600 mb-4">
					List your crops to connect with buyers and get better prices for your
					harvest
				</p>
				<Button variant="primary">
					<Plus className="w-4 h-4 mr-2" />
					Add New Crop Listing
				</Button>
			</div>
		</div>
	);
};

export default FarmerCrops;
