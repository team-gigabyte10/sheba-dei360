package com.nayem.sheba_dei.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nayem.sheba_dei.feature.auth.LoginScreen
import com.nayem.sheba_dei.feature.auth.OnboardingScreen
import com.nayem.sheba_dei.feature.auth.OtpScreen
import com.nayem.sheba_dei.feature.auth.RegisterScreen
import com.nayem.sheba_dei.feature.home.HomeScreen
import com.nayem.sheba_dei.feature.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onNavigateToHome = {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("onboarding") {
            OnboardingScreen(onFinishOnboarding = {
                navController.navigate("login") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToOtp = {
                    navController.navigate("otp")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true } // Clear up to login
                    }
                }
            )
        }
        composable("otp") {
            OtpScreen(
                onVerifySuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToProviderDetails = { providerId ->
                    navController.navigate("booking/$providerId")
                },
                onNavigateToBookings = {
                    navController.navigate("bookings")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        composable("booking/{providerId}") { backStackEntry ->
            val providerId = backStackEntry.arguments?.getString("providerId") ?: ""
            com.nayem.sheba_dei.feature.booking.BookingScreen(
                providerId = providerId,
                onBack = { navController.popBackStack() },
                onConfirmBooking = {
                    navController.navigate("booking_success")
                }
            )
        }
        composable("booking_success") {
            com.nayem.sheba_dei.feature.booking.BookingSuccessScreen(
                onTrackBooking = {
                    navController.navigate("tracking")
                },
                onReturnHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }
        composable("tracking") {
            com.nayem.sheba_dei.feature.booking.TrackingScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("bookings") {
            com.nayem.sheba_dei.feature.booking.BookingsScreen(
                onBack = { navController.popBackStack() },
                onTrackOrder = { bookingId ->
                    navController.navigate("tracking")
                }
            )
        }
        composable("profile") {
            com.nayem.sheba_dei.feature.profile.ProfileScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        composable("settings") {
            com.nayem.sheba_dei.feature.settings.SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToUpdateProfile = {
                    navController.navigate("update_profile")
                },
                onNavigateToChangePassword = {
                    navController.navigate("change_password")
                }
            )
        }
        composable("update_profile") {
            com.nayem.sheba_dei.feature.profile.UpdateProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("change_password") {
            com.nayem.sheba_dei.feature.profile.ChangePasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
