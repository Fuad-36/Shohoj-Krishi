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
import { useTranslation } from "react-i18next";

const FeaturesSection = () => {
	const { t } = useTranslation();

	const features = [
		{
			icon: MessageSquare,
			title: t("features.list.chatbot.title"),
			description: t("features.list.chatbot.description"),
			color: "text-primary-600",
			bgColor: "bg-primary-100",
		},
		{
			icon: Camera,
			title: t("features.list.pestDetection.title"),
			description: t("features.list.pestDetection.description"),
			color: "text-secondary-600",
			bgColor: "bg-secondary-100",
		},
		{
			icon: Mic,
			title: t("features.list.voiceInput.title"),
			description: t("features.list.voiceInput.description"),
			color: "text-accent-600",
			bgColor: "bg-accent-100",
		},
		{
			icon: CloudRain,
			title: t("features.list.weatherAlerts.title"),
			description: t("features.list.weatherAlerts.description"),
			color: "text-earth-600",
			bgColor: "bg-earth-100",
		},
		{
			icon: BookOpen,
			title: t("features.list.education.title"),
			description: t("features.list.education.description"),
			color: "text-primary-600",
			bgColor: "bg-primary-100",
		},
		{
			icon: ShoppingCart,
			title: t("features.list.marketplace.title"),
			description: t("features.list.marketplace.description"),
			color: "text-secondary-600",
			bgColor: "bg-secondary-100",
		},
	];

	const benefits = [
		{
			icon: TrendingUp,
			title: t("features.benefits.revenue.title"),
			description: t("features.benefits.revenue.description"),
			stats: t("features.benefits.revenue.stats"),
		},
		{
			icon: Users,
			title: t("features.benefits.community.title"),
			description: t("features.benefits.community.description"),
			stats: t("features.benefits.community.stats"),
		},
		{
			icon: Shield,
			title: t("features.benefits.security.title"),
			description: t("features.benefits.security.description"),
			stats: t("features.benefits.security.stats"),
		},
	];

	return (
		<section id="features" className="py-20 bg-white">
			<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
				{/* Section Header */}
				<div className="text-center mb-16">
					<h2 className="text-3xl sm:text-4xl font-display font-bold text-gray-900 mb-4">
						{t("features.sectionTitle")}{" "}
						<span className="text-gradient">
							{t("features.sectionTitleSpan")}
						</span>
					</h2>
					<p className="text-xl text-gray-600 max-w-3xl mx-auto">
						{t("features.sectionSubtitle")}
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
							{t("features.benefits.title")}
						</h3>
						<p className="text-lg text-gray-600">
							{t("features.benefits.subtitle")}
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
