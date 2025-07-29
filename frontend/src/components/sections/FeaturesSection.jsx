import {
	MessageSquare,
	Camera,
	Mic,
	CloudRain,
	BookOpen,
	ShoppingCart,
	Users,
	Shield,
	TrendingUp,
} from "lucide-react";

const FeaturesSection = () => {
	const features = [
		{
			icon: MessageSquare,
			title: "Bengali-Speaking Chatbot",
			description:
				"Get personalized farming advice and connect with government agricultural experts through our AI-powered chatbot.",
			color: "text-primary-600",
			bgColor: "bg-primary-100",
		},
		{
			icon: Camera,
			title: "Pest & Disease Detection",
			description:
				"Upload photos of crops to instantly identify pests and diseases using advanced computer vision technology.",
			color: "text-secondary-600",
			bgColor: "bg-secondary-100",
		},
		{
			icon: Mic,
			title: "Voice Input/Output",
			description:
				"Interact with the platform using voice commands in Bengali, perfect for users with limited literacy.",
			color: "text-accent-600",
			bgColor: "bg-accent-100",
		},
		{
			icon: CloudRain,
			title: "Weather Alerts",
			description:
				"Receive real-time, location-specific weather alerts to plan your farming activities effectively.",
			color: "text-earth-600",
			bgColor: "bg-earth-100",
		},
		{
			icon: BookOpen,
			title: "Educational Content",
			description:
				"Access Bengali micro-courses, tutorials, and translated agricultural news to improve your farming knowledge.",
			color: "text-primary-600",
			bgColor: "bg-primary-100",
		},
		{
			icon: ShoppingCart,
			title: "Direct Marketplace",
			description:
				"Connect directly with buyers, post crop availability, and negotiate fair prices without intermediaries.",
			color: "text-secondary-600",
			bgColor: "bg-secondary-100",
		},
	];

	const benefits = [
		{
			icon: TrendingUp,
			title: "Increase Revenue",
			description:
				"Farmers see up to 40% increase in income by selling directly to buyers.",
			stats: "40% Average Increase",
		},
		{
			icon: Users,
			title: "Community Support",
			description:
				"Join a growing community of farmers and get peer-to-peer support.",
			stats: "10,000+ Farmers",
		},
		{
			icon: Shield,
			title: "Secure Platform",
			description:
				"Your data and transactions are protected with enterprise-grade security.",
			stats: "100% Secure",
		},
	];

	return (
		<section id="features" className="py-20 bg-white">
			<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
				{/* Section Header */}
				<div className="text-center mb-16">
					<h2 className="text-3xl sm:text-4xl font-display font-bold text-gray-900 mb-4">
						Powerful <span className="text-gradient">AI-Driven Features</span>
					</h2>
					<p className="text-xl text-gray-600 max-w-3xl mx-auto">
						Our platform combines cutting-edge artificial intelligence with
						practical farming needs to create tools that truly make a difference
						in agricultural productivity.
					</p>
				</div>

				{/* Features Grid */}
				<div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8 mb-20">
					{features.map(
						({ icon: Icon, title, description, color, bgColor }) => (
							<div
								key={title}
								className="card group hover:shadow-lg hover:scale-105 transition-all duration-300"
							>
								<div
									className={`inline-flex items-center justify-center w-12 h-12 ${bgColor} rounded-lg mb-4 group-hover:scale-110 transition-transform duration-300`}
								>
									<Icon className={`w-6 h-6 ${color}`} />
								</div>
								<h3 className="text-xl font-semibold text-gray-900 mb-3">
									{title}
								</h3>
								<p className="text-gray-600 leading-relaxed">{description}</p>
							</div>
						)
					)}
				</div>

				{/* Benefits Section */}
				<div className="bg-linear-to-r from-primary-50 to-secondary-50 rounded-2xl p-8 lg:p-12">
					<div className="text-center mb-12">
						<h3 className="text-2xl sm:text-3xl font-display font-bold text-gray-900 mb-4">
							Why Choose Shohoj-Krishi?
						</h3>
						<p className="text-lg text-gray-600">
							Real results from real farmers across Bangladesh
						</p>
					</div>

					<div className="grid md:grid-cols-3 gap-8">
						{benefits.map(({ icon: Icon, title, description, stats }) => (
							<div key={title} className="text-center group">
								<div className="inline-flex items-center justify-center w-16 h-16 bg-white rounded-full shadow-sm mb-6 group-hover:shadow-glow transition-all duration-300">
									<Icon className="w-8 h-8 text-primary-600" />
								</div>
								<h4 className="text-xl font-semibold text-gray-900 mb-3">
									{title}
								</h4>
								<p className="text-gray-600 mb-4">{description}</p>
								<div className="text-2xl font-bold text-primary-600">
									{stats}
								</div>
							</div>
						))}
					</div>
				</div>
			</div>
		</section>
	);
};

export default FeaturesSection;
