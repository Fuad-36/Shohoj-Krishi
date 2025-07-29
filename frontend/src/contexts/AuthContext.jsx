import React, { createContext, useContext, useReducer, useEffect } from "react";
import { authAPI } from "../services/api";

// Initial state
const initialState = {
	user: null,
	isAuthenticated: false,
	isLoading: true,
	error: null,
};

// Action types
const AUTH_ACTIONS = {
	AUTH_START: "AUTH_START",
	AUTH_SUCCESS: "AUTH_SUCCESS",
	AUTH_FAILURE: "AUTH_FAILURE",
	LOGOUT: "LOGOUT",
	CLEAR_ERROR: "CLEAR_ERROR",
};

// Reducer
const authReducer = (state, action) => {
	switch (action.type) {
		case AUTH_ACTIONS.AUTH_START:
			return {
				...state,
				isLoading: true,
				error: null,
			};
		case AUTH_ACTIONS.AUTH_SUCCESS:
			return {
				...state,
				user: action.payload.user,
				isAuthenticated: true,
				isLoading: false,
				error: null,
			};
		case AUTH_ACTIONS.AUTH_FAILURE:
			return {
				...state,
				user: null,
				isAuthenticated: false,
				isLoading: false,
				error: action.payload.error,
			};
		case AUTH_ACTIONS.LOGOUT:
			return {
				...state,
				user: null,
				isAuthenticated: false,
				isLoading: false,
				error: null,
			};
		case AUTH_ACTIONS.CLEAR_ERROR:
			return {
				...state,
				error: null,
			};
		default:
			return state;
	}
};

// Create context
const AuthContext = createContext();

// Custom hook to use auth context
export const useAuth = () => {
	const context = useContext(AuthContext);
	if (!context) {
		throw new Error("useAuth must be used within an AuthProvider");
	}
	return context;
};

// Auth provider component
export const AuthProvider = ({ children }) => {
	const [state, dispatch] = useReducer(authReducer, initialState);

	// Check for existing token on app start
	useEffect(() => {
		const checkAuthStatus = async () => {
			try {
				const token = localStorage.getItem("authToken");
				if (token) {
					dispatch({ type: AUTH_ACTIONS.AUTH_START });

					// Verify token with backend
					const response = await authAPI.verifyToken();

					dispatch({
						type: AUTH_ACTIONS.AUTH_SUCCESS,
						payload: { user: response.data.user },
					});
				} else {
					dispatch({
						type: AUTH_ACTIONS.AUTH_FAILURE,
						payload: { error: null },
					});
				}
			} catch (error) {
				localStorage.removeItem("authToken");
				dispatch({
					type: AUTH_ACTIONS.AUTH_FAILURE,
					payload: { error: "Session expired. Please log in again." },
				});
			}
		};

		checkAuthStatus();
	}, []);

	// Login function
	const login = async (credentials) => {
		try {
			dispatch({ type: AUTH_ACTIONS.AUTH_START });

			const response = await authAPI.login(credentials);
			const { token, user } = response.data;

			// Store token in localStorage
			localStorage.setItem("authToken", token);

			dispatch({
				type: AUTH_ACTIONS.AUTH_SUCCESS,
				payload: { user },
			});

			return { success: true, user };
		} catch (error) {
			const errorMessage =
				error.response?.data?.message || "Login failed. Please try again.";
			dispatch({
				type: AUTH_ACTIONS.AUTH_FAILURE,
				payload: { error: errorMessage },
			});
			return { success: false, error: errorMessage };
		}
	};

	// Register function
	const register = async (userData) => {
		try {
			dispatch({ type: AUTH_ACTIONS.AUTH_START });

			const response = await authAPI.register(userData);
			const { token, user } = response.data;

			// Store token in localStorage
			localStorage.setItem("authToken", token);

			dispatch({
				type: AUTH_ACTIONS.AUTH_SUCCESS,
				payload: { user },
			});

			return { success: true, user };
		} catch (error) {
			const errorMessage =
				error.response?.data?.message ||
				"Registration failed. Please try again.";
			dispatch({
				type: AUTH_ACTIONS.AUTH_FAILURE,
				payload: { error: errorMessage },
			});
			return { success: false, error: errorMessage };
		}
	};

	// Logout function
	const logout = async () => {
		try {
			await authAPI.logout();
		} catch (error) {
			console.error("Logout error:", error);
		} finally {
			localStorage.removeItem("authToken");
			dispatch({ type: AUTH_ACTIONS.LOGOUT });
		}
	};

	// Clear error function
	const clearError = () => {
		dispatch({ type: AUTH_ACTIONS.CLEAR_ERROR });
	};

	// Update user profile
	const updateProfile = async (updateData) => {
		try {
			const response = await authAPI.updateProfile(updateData);
			dispatch({
				type: AUTH_ACTIONS.AUTH_SUCCESS,
				payload: { user: response.data.user },
			});
			return { success: true, user: response.data.user };
		} catch (error) {
			const errorMessage =
				error.response?.data?.message || "Profile update failed.";
			return { success: false, error: errorMessage };
		}
	};

	const value = {
		...state,
		login,
		register,
		logout,
		clearError,
		updateProfile,
	};

	return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
