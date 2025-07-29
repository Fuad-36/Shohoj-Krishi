import React from "react";

const AboutPage = () => {
	return (
		<div className="min-h-screen bg-primary-50 py-16">
			<div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
				<div className="text-center mb-12">
					<h1 className="text-4xl font-bold text-gray-900 mb-4">
						About Shohoj-Krishi
					</h1>
					<p className="text-xl text-gray-600">
						Revolutionizing Agriculture in Bangladesh
					</p>
				</div>

				<div className="bg-white rounded-xl p-8 shadow-sm">
					<p className="text-gray-700 mb-6">
						Shohoj-Krishi is a comprehensive digital platform that directly
						connects farmers, buyers, and government agricultural authorities.
						Our mission is to eliminate intermediaries, promote transparency,
						and ensure fair pricing for farmers.
					</p>

					<div className="grid grid-cols-1 md:grid-cols-3 gap-6">
						<div className="text-center p-6 bg-primary-50 rounded-lg">
							<div className="text-3xl mb-4">🌾</div>
							<h3 className="font-semibold text-primary-800 mb-2">
								For Farmers
							</h3>
							<p className="text-primary-600 text-sm">
								AI-powered tools, market access, and expert support
							</p>
						</div>
						<div className="text-center p-6 bg-secondary-50 rounded-lg">
							<div className="text-3xl mb-4">🛒</div>
							<h3 className="font-semibold text-secondary-800 mb-2">
								For Buyers
							</h3>
							<p className="text-secondary-600 text-sm">
								Direct access to fresh produce and farmers
							</p>
						</div>
						<div className="text-center p-6 bg-accent-50 rounded-lg">
							<div className="text-3xl mb-4">🏛️</div>
							<h3 className="font-semibold text-accent-800 mb-2">
								For Government
							</h3>
							<p className="text-accent-600 text-sm">
								Monitor, assist, and engage with farming communities
							</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
};

export default AboutPage;
