import { ArrowRight, Play, Users, TrendingUp, Shield } from "lucide-react";
import { useTranslation } from "react-i18next";
import Button from "../ui/Button";

const HeroSection = () => {
	const { t } = useTranslation();

	const stats = [
		{
			icon: Users,
			label: t("landing.stats.farmersConnected"),
			value: "10,000+",
			color: "text-primary-600",
		},
		{
			icon: TrendingUp,
			label: t("landing.stats.revenueIncreased"),
			value: "40%+",
			color: "text-secondary-600",
		},
		{
			icon: Shield,
			label: t("landing.stats.secureTransactions"),
			value: "100%",
			color: "text-accent-600",
		},
	];

	return (
		<section
			id="home"
			className="relative min-h-screen flex items-center overflow-hidden"
		>
			{/* Background Pattern */}
			<div className="absolute inset-0 bg-linear-to-br from-primary-50 via-white to-secondary-50">
				<div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGRlZnM+CjxwYXR0ZXJuIGlkPSJncmlkIiB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHBhdHRlcm5Vbml0cz0idXNlclNwYWNlT25Vc2UiPgo8cGF0aCBkPSJNIDEwIDAgTCAwIDAgMCAxMCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjZGNmY2U3IiBzdHJva2Utd2lkdGg9IjEiLz4KPC9wYXR0ZXJuPgo8L2RlZnM+CjxyZWN0IHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9InVybCgjZ3JpZCkiLz4KPHN2Zz4=')] opacity-30"></div>
			</div>

			<div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20">
				<div className="grid lg:grid-cols-2 gap-12 lg:gap-16 items-center">
					{/* Left Content */}
					<div className="text-center lg:text-left">
						<div className="inline-flex items-center px-4 py-2 bg-primary-100 rounded-full text-primary-700 text-sm font-medium mb-6">
							<span className="w-2 h-2 bg-primary-500 rounded-full mr-2"></span>
							{t("landing.badge")}
						</div>

						<h1 className="text-4xl sm:text-5xl lg:text-6xl font-display font-bold text-gray-900 mb-6 leading-tight">
							{t("landing.heroTitle")}
							<span className="text-gradient block">
								{t("landing.heroTitleSpan")}
							</span>
						</h1>

						<p className="text-xl text-gray-600 mb-8 leading-relaxed max-w-2xl">
							{t("landing.heroSubtitle")}
						</p>

						{/* CTA Buttons */}
						<div className="flex flex-col sm:flex-row gap-4 mb-12">
							<Button variant="primary" size="lg" className="group">
								{t("landing.getStartedToday")}
								<ArrowRight className="ml-2 w-5 h-5 group-hover:translate-x-1 transition-transform duration-200" />
							</Button>
							<Button variant="outline" size="lg" className="group">
								<Play className="mr-2 w-5 h-5" />
								{t("landing.watchDemo")}
							</Button>
						</div>

						{/* Stats */}
						<div className="grid grid-cols-3 gap-6">
							{stats.map(({ icon: Icon, label, value, color }) => (
								<div key={label} className="text-center">
									<div className="flex items-center justify-center mb-2">
										<Icon className={`w-6 h-6 ${color}`} />
									</div>
									<div className="text-2xl font-bold text-gray-900 mb-1">
										{value}
									</div>
									<div className="text-sm text-gray-600">{label}</div>
								</div>
							))}
						</div>
					</div>

					{/* Right Content - Hero Image/Illustration */}
					<div className="relative">
						<div className="relative rounded-2xl overflow-hidden shadow-2xl shadow-primary-200/50">
							{/* Placeholder for hero image */}
							<div className="aspect-square bg-linear-to-br from-primary-400 via-primary-500 to-primary-600 p-12 flex items-center justify-center">
								<div className="text-center text-white">
									<div className="w-24 h-24 bg-white/20 rounded-full flex items-center justify-center mx-auto mb-6">
										<Users className="w-12 h-12" />
									</div>
									<h3 className="text-2xl font-bold mb-4">
										{t("landing.communities.title")}
									</h3>
									<p className="text-primary-100">
										{t("landing.communities.subtitle")}
									</p>
								</div>
							</div>

							{/* Floating Cards */}
							<div className="absolute -top-4 -right-4 bg-white rounded-xl p-4 shadow-lg animate-bounce-gentle">
								<div className="text-sm font-medium text-gray-900">
									{t("landing.floatingCards.freshHarvest")}
								</div>
								<div className="text-xs text-gray-500">
									{t("landing.floatingCards.readyForDelivery")}
								</div>
							</div>

							<div
								className="absolute -bottom-4 -left-4 bg-white rounded-xl p-4 shadow-lg animate-bounce-gentle"
								style={{ animationDelay: "1s" }}
							>
								<div className="text-sm font-medium text-gray-900">
									{t("landing.floatingCards.aiAssistant")}
								</div>
								<div className="text-xs text-gray-500">
									{t("landing.floatingCards.alwaysAvailable")}
								</div>
							</div>
						</div>

						{/* Background Shapes */}
						<div className="absolute -z-10 top-8 right-8 w-32 h-32 bg-secondary-200 rounded-full opacity-60 animate-pulse-gentle"></div>
						<div
							className="absolute -z-10 bottom-8 left-8 w-24 h-24 bg-accent-200 rounded-full opacity-60 animate-pulse-gentle"
							style={{ animationDelay: "2s" }}
						></div>
					</div>
				</div>
			</div>

			{/* Scroll Indicator */}
			<div className="absolute bottom-8 left-1/2 transform -translate-x-1/2 animate-bounce">
				<div className="w-6 h-10 border-2 border-primary-400 rounded-full flex justify-center">
					<div className="w-1 h-3 bg-primary-400 rounded-full mt-2 animate-pulse"></div>
				</div>
			</div>
		</section>
	);
};

export default HeroSection;
