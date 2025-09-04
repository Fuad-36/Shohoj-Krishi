import React from "react";
import { Link } from "react-router-dom";
import { ArrowLeft, Mail, Leaf } from "lucide-react";
import Button from "../../components/ui/Button";

const ForgotPassword = () => {
	return (
		<div className="min-h-screen bg-linear-to-br from-primary-50 via-white to-secondary-50 flex items-center justify-center p-4">
			<div className="relative w-full max-w-md">
				<div className="bg-white/80 backdrop-blur-md rounded-3xl shadow-2xl border border-white/20 p-8">
					<div className="text-center mb-8">
						<div className="inline-flex items-center justify-center w-16 h-16 bg-primary-100 rounded-2xl mb-4">
							<Leaf className="w-8 h-8 text-primary-600" />
						</div>
						<h1 className="text-2xl font-bold text-gray-900 mb-2">
							Reset Password
						</h1>
						<p className="text-gray-600">
							Enter your email to receive reset instructions
						</p>
					</div>

					<form className="space-y-6">
						<div>
							<label
								htmlFor="email"
								className="block text-sm font-medium text-gray-700 mb-2"
							>
								Email Address
							</label>
							<div className="relative">
								<div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
									<Mail className="w-5 h-5 text-gray-400" />
								</div>
								<input
									type="email"
									id="email"
									className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
									placeholder="Enter your email"
								/>
							</div>
						</div>

						<Button
							type="submit"
							variant="primary"
							size="lg"
							className="w-full"
						>
							Send Reset Instructions
						</Button>
					</form>

					<div className="mt-6 text-center">
						<Link
							to="/auth/signin"
							className="inline-flex items-center text-sm text-primary-600 hover:text-primary-700"
						>
							<ArrowLeft className="w-4 h-4 mr-2" />
							Back to Sign In
						</Link>
					</div>
				</div>
			</div>
		</div>
	);
};

export default ForgotPassword;
