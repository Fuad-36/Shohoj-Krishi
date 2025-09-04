import {
	Sprout,
	Mail,
	Phone,
	MapPin,
	Facebook,
	Twitter,
	Instagram,
	Youtube,
} from "lucide-react";

const Footer = () => {
	const footerSections = [
		{
			title: "Platform",
			links: [
				{ label: "For Farmers", href: "#farmers" },
				{ label: "For Buyers", href: "#buyers" },
				{ label: "AI Tools", href: "#ai-tools" },
				{ label: "Government Portal", href: "#government" },
			],
		},
		{
			title: "Resources",
			links: [
				{ label: "Help Center", href: "#help" },
				{ label: "Tutorials", href: "#tutorials" },
				{ label: "API Documentation", href: "#api" },
				{ label: "Community Forum", href: "#forum" },
			],
		},
		{
			title: "Company",
			links: [
				{ label: "About Us", href: "#about" },
				{ label: "Our Mission", href: "#mission" },
				{ label: "Careers", href: "#careers" },
				{ label: "Press Kit", href: "#press" },
			],
		},
		{
			title: "Legal",
			links: [
				{ label: "Privacy Policy", href: "#privacy" },
				{ label: "Terms of Service", href: "#terms" },
				{ label: "Cookie Policy", href: "#cookies" },
				{ label: "Compliance", href: "#compliance" },
			],
		},
	];

	const socialLinks = [
		{ icon: Facebook, href: "#", label: "Facebook" },
		{ icon: Twitter, href: "#", label: "Twitter" },
		{ icon: Instagram, href: "#", label: "Instagram" },
		{ icon: Youtube, href: "#", label: "YouTube" },
	];

	const contactInfo = [
		{ icon: Mail, text: "support@shohoj-krishi.com" },
		{ icon: Phone, text: "+880 1XXX-XXXXXX" },
		{ icon: MapPin, text: "Dhaka, Bangladesh" },
	];

	return (
		<footer className="bg-gray-900 text-white">
			<div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
				{/* Main Footer Content */}
				<div className="grid lg:grid-cols-6 gap-8">
					{/* Brand Section */}
					<div className="lg:col-span-2">
						<div className="flex items-center space-x-2 mb-4">
							{/* <div className="flex items-center justify-center w-10 h-10 bg-primary-600 rounded-lg">
								<Sprout className="w-6 h-6 text-white" />
							</div> */}
							<div className="w-8 h-8 rounded-lg flex items-center justify-center">
								<img
									src="https://cdn-icons-png.flaticon.com/128/2713/2713479.png"
									alt="logo"
									className="w-8 h-8"
								/>
							</div>
							<div>
								<h3 className="text-xl font-display font-bold">
									Shohoj Krishi
								</h3>
								<p className="text-sm text-gray-400">Smart Agriculture</p>
							</div>
						</div>

						<p className="text-gray-400 mb-6 leading-relaxed">
							Empowering farmers across Bangladesh with AI-driven tools and
							direct market access. Building a sustainable future for
							agriculture.
						</p>

						{/* Contact Info */}
						<div className="space-y-3">
							{contactInfo.map(({ icon: Icon, text }) => (
								<div
									key={text}
									className="flex items-center space-x-3 text-gray-400"
								>
									<Icon className="w-4 h-4" />
									<span className="text-sm">{text}</span>
								</div>
							))}
						</div>
					</div>

					{/* Footer Links */}
					{footerSections.map(({ title, links }) => (
						<div key={title}>
							<h4 className="font-semibold text-white mb-4">{title}</h4>
							<ul className="space-y-3">
								{links.map(({ label, href }) => (
									<li key={label}>
										<a
											href={href}
											className="text-gray-400 hover:text-white transition-colors duration-200 text-sm"
										>
											{label}
										</a>
									</li>
								))}
							</ul>
						</div>
					))}
				</div>

				{/* Newsletter Subscription */}
				<div className="border-t border-gray-800 mt-12 pt-8">
					<div className="grid md:grid-cols-2 gap-8 items-center">
						<div>
							<h4 className="text-xl font-semibold mb-2">Stay Updated</h4>
							<p className="text-gray-400">
								Get the latest updates on new features, farming tips, and market
								insights.
							</p>
						</div>
						<div className="flex flex-col sm:flex-row gap-3">
							<input
								type="email"
								placeholder="Enter your email"
								className="flex-1 px-4 py-3 bg-gray-800 border border-gray-700 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
							/>
							<button className="px-6 py-3 bg-primary-600 text-white rounded-lg font-medium hover:bg-primary-700 transition-colors duration-200">
								Subscribe
							</button>
						</div>
					</div>
				</div>

				{/* Bottom Section */}
				<div className="border-t border-gray-800 mt-8 pt-8 flex flex-col md:flex-row justify-between items-center">
					<div className="text-gray-400 text-sm mb-4 md:mb-0">
						© 2024 Shohoj-Krishi. All rights reserved.
					</div>

					{/* Social Links */}
					<div className="flex space-x-4">
						{socialLinks.map(({ icon: Icon, href, label }) => (
							<a
								key={label}
								href={href}
								className="w-10 h-10 bg-gray-800 rounded-full flex items-center justify-center text-gray-400 hover:text-white hover:bg-primary-600 transition-all duration-200"
								aria-label={label}
							>
								<Icon className="w-5 h-5" />
							</a>
						))}
					</div>
				</div>

				{/* Government Recognition */}
				<div className="border-t border-gray-800 mt-8 pt-6 text-center">
					<p className="text-gray-500 text-sm">
						Recognized by the Government of Bangladesh | Ministry of Agriculture
					</p>
				</div>
			</div>
		</footer>
	);
};

export default Footer;
