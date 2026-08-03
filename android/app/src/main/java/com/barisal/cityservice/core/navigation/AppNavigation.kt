package com.barisal.cityservice.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.barisal.cityservice.feature.auth.LoginScreen
import com.barisal.cityservice.feature.auth.OnboardingScreen
import com.barisal.cityservice.feature.auth.OtpScreen
import com.barisal.cityservice.feature.auth.RegisterScreen
import com.barisal.cityservice.feature.home.HomeScreen
import com.barisal.cityservice.feature.splash.SplashScreen


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
                },
                onNavigateToCategoryMap = { categoryKey ->
                    navController.navigate("category_map/$categoryKey")
                },
                onNavigateToAllServices = {
                    navController.navigate("all_services")
                },
                onNavigateToHealthServices = {
                    navController.navigate("health_services")
                },
                onNavigateToRide = {
                    navController.navigate("ride_screen")
                },
                onNavigateToCourier = {
                    navController.navigate("courier_screen")
                },
                onNavigateToTransportService = {
                    navController.navigate("transport_service")
                },
                onNavigateToEmergencyService = {
                    navController.navigate("emergency_service")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onNavigateToAddCategory = {
                    navController.navigate("add_category")
                },
                onNavigateToAddSubCategory = {
                    navController.navigate("add_subcategory")
                }
            )
        }
        composable("add_category") {
            com.barisal.cityservice.feature.admin.AddCategoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("add_subcategory") {
            com.barisal.cityservice.feature.admin.AddSubCategoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("subcategories/{categoryId}") { backStackEntry ->
            val categoryIdInt = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull() ?: 1
            com.barisal.cityservice.feature.service.SubCategoryScreen(
                categoryId = categoryIdInt,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("emergency_service") {
            com.barisal.cityservice.feature.emergency.EmergencyServiceScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategoryMap = { categoryKey ->
                    navController.navigate("category_map/$categoryKey")
                }
            )
        }
        composable("transport_service") {
            com.barisal.cityservice.feature.transport.TransportServiceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("ride_screen") {
            com.barisal.cityservice.feature.ride.RideScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("courier_screen") {
            com.barisal.cityservice.feature.courier.CourierScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("all_services") {
            com.barisal.cityservice.feature.service.AllServicesScreen(
                onBack = { navController.popBackStack() },
                onCategoryClick = { categoryKey ->
                    if (categoryKey == "health_services") {
                        navController.navigate("health_services")
                    } else {
                        navController.navigate("category_map/$categoryKey")
                    }
                }
            )
        }
        composable("category_map/{categoryKey}") { backStackEntry ->
            val categoryKey = backStackEntry.arguments?.getString("categoryKey") ?: ""
            com.barisal.cityservice.feature.service.CategoryMapScreen(
                categoryKey = categoryKey,
                onBack = { navController.popBackStack() }
            )
        }
        composable("matrimony_home") {
            com.barisal.cityservice.feature.matrimony.MatrimonyHomeScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSearch = { navController.navigate("matrimony_search") },
                onNavigateToProfile = { profileId -> navController.navigate("matrimony_profile/$profileId") },
                onNavigateToCreateProfile = { navController.navigate("create_matrimony_profile") }
            )
        }
        composable("create_matrimony_profile") {
            com.barisal.cityservice.feature.matrimony.CreateMatrimonyProfileScreen(
                onBack = { navController.popBackStack() },
                onProfileCreated = { newProfile ->
                    // Profile created handler
                }
            )
        }
        composable("matrimony_search") {
            com.barisal.cityservice.feature.matrimony.MatrimonySearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { profileId -> navController.navigate("matrimony_profile/$profileId") }
            )
        }
        composable("matrimony_profile/{profileId}") { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId") ?: ""
            com.barisal.cityservice.feature.matrimony.MatrimonyProfileScreen(
                profileId = profileId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("blood_donor") {
            com.barisal.cityservice.feature.blood.BloodDonationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("event_service") {
            com.barisal.cityservice.feature.event.EventServiceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("house_rent_list") {
            com.barisal.cityservice.feature.houserent.HouseRentListScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("mistri_service") {
            com.barisal.cityservice.feature.mistri.MistriServiceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("tutor") {
            com.barisal.cityservice.feature.tutor.TutorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("hotel") {
            com.barisal.cityservice.feature.hotel.HotelScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("restaurant") {
            com.barisal.cityservice.feature.restaurant.RestaurantScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("flat_land") {
            com.barisal.cityservice.feature.property.FlatLandScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("shopping_list") {
            com.barisal.cityservice.feature.shopping.ShoppingListScreen(
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
            com.barisal.cityservice.feature.shopping.PostProductScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("shopping_details/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            com.barisal.cityservice.feature.shopping.ShoppingDetailsScreen(
                productId = productId,
                onBack = { navController.popBackStack() },
                onNavigateToDetails = { newProductId ->
                    navController.navigate("shopping_details/$newProductId")
                }
            )
        }
        composable("health_services") {
            com.barisal.cityservice.feature.doctor.HealthServicesScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDoctor = { navController.navigate("doctor_category") },
                onNavigateToHospital = { navController.navigate("hospital_list") },
                onNavigateToBloodDonor = { navController.navigate("blood_donor") },
                onNavigateToCategoryMap = { categoryKey -> navController.navigate("category_map/$categoryKey") }
            )
        }
        composable("hospital_list") {
            com.barisal.cityservice.feature.hospital.HospitalListScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("doctor_category") {
            com.barisal.cityservice.feature.doctor.DoctorCategoryScreen(
                onBack = { navController.popBackStack() },
                onCategoryClick = { categoryName ->
                    navController.navigate("doctor_list/$categoryName")
                }
            )
        }
        composable("doctor_list/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            com.barisal.cityservice.feature.doctor.DoctorListScreen(
                categoryName = categoryName,
                onBack = { navController.popBackStack() }
            )
        }
        composable("booking/{providerId}") { backStackEntry ->
            val providerId = backStackEntry.arguments?.getString("providerId") ?: ""
            com.barisal.cityservice.feature.booking.BookingScreen(
                providerId = providerId,
                onBack = { navController.popBackStack() },
                onConfirmBooking = {
                    navController.navigate("booking_success")
                }
            )
        }
        composable("booking_success") {
            com.barisal.cityservice.feature.booking.BookingSuccessScreen(
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
            com.barisal.cityservice.feature.booking.TrackingScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("bookings") {
            com.barisal.cityservice.feature.booking.BookingsScreen(
                onBack = { navController.popBackStack() },
                onTrackOrder = { bookingId ->
                    navController.navigate("tracking")
                }
            )
        }
        composable("profile") {
            com.barisal.cityservice.feature.profile.ProfileScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToAllServices = {
                    navController.navigate("all_services")
                },
                onNavigateToBookings = {
                    navController.navigate("bookings")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToUpdateProfile = {
                    navController.navigate("update_profile")
                },
                onNavigateToChangePassword = {
                    navController.navigate("change_password")
                }
            )
        }
        composable("settings") {
            com.barisal.cityservice.feature.settings.SettingsScreen(
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
            com.barisal.cityservice.feature.profile.UpdateProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("change_password") {
            com.barisal.cityservice.feature.profile.ChangePasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }
        // Vendor Panel Routes
        composable("vendor_dashboard") {
            com.barisal.cityservice.feature.vendor.dashboard.VendorDashboardScreen(
                onNavigateToProducts = { navController.navigate("vendor_products") },
                onNavigateToOrders = { navController.navigate("vendor_orders") },
                onNavigateToReports = { navController.navigate("vendor_reports") }
            )
        }
        composable("vendor_products") {
            com.barisal.cityservice.feature.vendor.products.ProductListScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddProduct = { navController.navigate("vendor_product_add_edit") },
                onEditProduct = { productId -> navController.navigate("vendor_product_add_edit?productId=$productId") }
            )
        }
        composable("vendor_product_add_edit?productId={productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            com.barisal.cityservice.feature.vendor.products.AddEditProductScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("vendor_orders") {
            com.barisal.cityservice.feature.vendor.orders.OrderListScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderClick = { orderId -> navController.navigate("vendor_order_detail/$orderId") }
            )
        }
        composable("vendor_order_detail/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            com.barisal.cityservice.feature.vendor.orders.OrderDetailScreen(
                orderId = orderId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("vendor_reports") {
            com.barisal.cityservice.feature.vendor.reports.VendorReportsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
