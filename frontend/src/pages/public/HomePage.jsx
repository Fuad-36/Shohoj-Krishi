import React from "react";
import HeroSection from "../../components/sections/HeroSection";
import FeaturesSection from "../../components/sections/FeaturesSection";

const HomePage = () => {
	return (
		<div className="min-h-screen">
			<HeroSection />
			<FeaturesSection />
		</div>
	);
};

export default HomePage;
