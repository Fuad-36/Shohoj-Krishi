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
import { useTranslation } from "react-i18next";

const Footer = () => {
	const { t } = useTranslation();

	const footerSections = [
		{
			title: t("footer.sections.platform.title"),
			links: [
				{ label: t("footer.sections.platform.farmers"), href: "#farmers" },
				{ label: t("footer.sections.platform.buyers"), href: "#buyers" },
				{ label: t("footer.sections.platform.aiTools"), href: "#ai-tools" },
				{
					label: t("footer.sections.platform.government"),
					href: "#government",
				},
			],
		},
		{
			title: t("footer.sections.resources.title"),
			links: [
				{ label: t("footer.sections.resources.helpCenter"), href: "#help" },
				{ label: t("footer.sections.resources.tutorials"), href: "#tutorials" },
				{ label: t("footer.sections.resources.apiDocs"), href: "#api" },
				{ label: t("footer.sections.resources.forum"), href: "#forum" },
			],
		},
		{
			title: t("footer.sections.company.title"),
			links: [
				{ label: t("footer.sections.company.about"), href: "#about" },
				{ label: t("footer.sections.company.mission"), href: "#mission" },
				{ label: t("footer.sections.company.careers"), href: "#careers" },
				{ label: t("footer.sections.company.press"), href: "#press" },
			],
		},
		{
			title: t("footer.sections.legal.title"),
			links: [
				{ label: t("footer.sections.legal.privacy"), href: "#privacy" },
				{ label: t("footer.sections.legal.terms"), href: "#terms" },
				{ label: t("footer.sections.legal.cookies"), href: "#cookies" },
				{ label: t("footer.sections.legal.compliance"), href: "#compliance" },
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
		{ icon: Mail, text: t("footer.contact.email") },
		{ icon: Phone, text: t("footer.contact.phone") },
		{ icon: MapPin, text: t("footer.contact.address") },
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
								<p className="text-sm text-gray-400">{t("footer.tagline")}</p>
							</div>
						</div>

						<p className="text-gray-400 mb-6 leading-relaxed">
							{t("footer.description")}
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
							<h4 className="text-xl font-semibold mb-2">
								{t("footer.newsletter.title")}
							</h4>
							<p className="text-gray-400">{t("footer.newsletter.subtitle")}</p>
						</div>
						<div className="flex flex-col sm:flex-row gap-3">
							<input
								type="email"
								placeholder={t("footer.newsletter.placeholder")}
								className="flex-1 px-4 py-3 bg-gray-800 border border-gray-700 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
							/>
							<button className="px-6 py-3 bg-primary-600 text-white rounded-lg font-medium hover:bg-primary-700 transition-colors duration-200">
								{t("footer.newsletter.subscribe")}
							</button>
						</div>
					</div>
				</div>

				{/* Bottom Section */}
				<div className="border-t border-gray-800 mt-8 pt-8 flex flex-col md:flex-row justify-between items-center">
					<div className="text-gray-400 text-sm mb-4 md:mb-0">
						{t("footer.copyright")}
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
					<p className="text-gray-500 text-sm">{t("footer.recognition")}</p>
				</div>
			</div>
		</footer>
	);
};

export default Footer;
