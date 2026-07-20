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
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("onboarding") {
            OnboardingScreen(onFinishOnboarding = {
                navController.navigate("home") {
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
                },
                onNavigateToVendorDashboard = {
                    navController.navigate("vendor_dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
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
                },
                onNavigateToDoctor = {
                    navController.navigate("doctor_category")
                },
                onNavigateToHospital = {
                    navController.navigate("hospital_list")
                },
                onNavigateToHouseRent = {
                    navController.navigate("house_rent_list")
                },
                onNavigateToShopping = {
                    navController.navigate("shopping_list")
                },
                onNavigateToMatrimony = {
                    navController.navigate("matrimony_home")
                },
                onNavigateToBloodDonor = {
                    navController.navigate("blood_donor")
                },
                onNavigateToEventService = {
                    navController.navigate("event_service")
                },
                onNavigateToMistriService = {
                    navController.navigate("mistri_service")
                },
                onNavigateToTutor = {
                    navController.navigate("tutor")
                },
                onNavigateToHotel = {
                    navController.navigate("hotel")
                },
                onNavigateToRestaurant = {
                    navController.navigate("restaurant")
                },
                onNavigateToFlatLand = {
                    navController.navigate("flat_land")
                }
            )
        }
        composable("matrimony_home") {
            com.nayem.sheba_dei.feature.matrimony.MatrimonyHomeScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSearch = { navController.navigate("matrimony_search") },
                onNavigateToProfile = { profileId -> navController.navigate("matrimony_profile/$profileId") }
            )
        }
        composable("matrimony_search") {
            com.nayem.sheba_dei.feature.matrimony.MatrimonySearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { profileId -> navController.navigate("matrimony_profile/$profileId") }
            )
        }
        composable("blood_donor") {
            com.nayem.sheba_dei.feature.blood.BloodDonationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("event_service") {
            com.nayem.sheba_dei.feature.event.EventServiceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("house_rent_list") {
            com.nayem.sheba_dei.feature.houserent.HouseRentListScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("mistri_service") {
            com.nayem.sheba_dei.feature.mistri.MistriServiceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("tutor") {
            com.nayem.sheba_dei.feature.tutor.TutorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("hotel") {
            com.nayem.sheba_dei.feature.hotel.HotelScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("restaurant") {
            com.nayem.sheba_dei.feature.restaurant.RestaurantScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("flat_land") {
            com.nayem.sheba_dei.feature.property.FlatLandScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("shopping_list") {
            com.nayem.sheba_dei.feature.shopping.ShoppingListScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetails = { productId ->
                    navController.navigate("shopping_details/$productId")
                },
                onNavigateToPostProduct = {
                    navController.navigate("post_product")
                }
            )
        }
        composable("post_product") {
            com.nayem.sheba_dei.feature.shopping.PostProductScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("shopping_details/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            com.nayem.sheba_dei.feature.shopping.ShoppingDetailsScreen(
                productId = productId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("hospital_list") {
            com.nayem.sheba_dei.feature.hospital.HospitalListScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("doctor_category") {
            com.nayem.sheba_dei.feature.doctor.DoctorCategoryScreen(
                onBack = { navController.popBackStack() },
                onCategoryClick = { categoryName ->
                    navController.navigate("doctor_list/$categoryName")
                }
            )
        }
        composable("doctor_list/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            com.nayem.sheba_dei.feature.doctor.DoctorListScreen(
                categoryName = categoryName,
                onBack = { navController.popBackStack() }
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
        // Vendor Panel Routes
        composable("vendor_dashboard") {
            com.nayem.sheba_dei.feature.vendor.dashboard.VendorDashboardScreen(
                onNavigateToProducts = { navController.navigate("vendor_products") },
                onNavigateToOrders = { navController.navigate("vendor_orders") },
                onNavigateToReports = { navController.navigate("vendor_reports") }
            )
        }
        composable("vendor_products") {
            com.nayem.sheba_dei.feature.vendor.products.ProductListScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddProduct = { navController.navigate("vendor_product_add_edit") },
                onEditProduct = { productId -> navController.navigate("vendor_product_add_edit?productId=$productId") }
            )
        }
        composable("vendor_product_add_edit?productId={productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            com.nayem.sheba_dei.feature.vendor.products.AddEditProductScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("vendor_orders") {
            com.nayem.sheba_dei.feature.vendor.orders.OrderListScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderClick = { orderId -> navController.navigate("vendor_order_detail/$orderId") }
            )
        }
        composable("vendor_order_detail/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            com.nayem.sheba_dei.feature.vendor.orders.OrderDetailScreen(
                orderId = orderId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("vendor_reports") {
            com.nayem.sheba_dei.feature.vendor.reports.VendorReportsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
