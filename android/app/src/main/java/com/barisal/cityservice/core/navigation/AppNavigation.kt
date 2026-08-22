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
    val context = androidx.compose.ui.platform.LocalContext.current

    fun navigateWithAuthCheck(targetRoute: String) {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            com.barisal.cityservice.core.utils.UserPreferences.setOtpVerified(context, true)
            navController.navigate(targetRoute)
        } else {
            android.widget.Toast.makeText(
                context,
                "পোস্ট করতে হলে আপনাকে অবশ্যই ইমেইল বা ফোন নম্বর দিয়ে লগইন করতে হবে।",
                android.widget.Toast.LENGTH_LONG
            ).show()
            val encodedTarget = try { java.net.URLEncoder.encode(targetRoute, "UTF-8") } catch (e: Exception) { targetRoute }
            navController.navigate("login?redirectRoute=$encodedTarget")
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
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
        composable(
            route = "login?redirectRoute={redirectRoute}",
            arguments = listOf(
                androidx.navigation.navArgument("redirectRoute") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val redirectRoute = backStackEntry.arguments?.getString("redirectRoute")
            LoginScreen(
                redirectRoute = redirectRoute,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRedirectTarget = { target ->
                    val decoded = try { java.net.URLDecoder.decode(target, "UTF-8") } catch (e: Exception) { target }
                    navController.navigate(decoded) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToOtp = { target, verificationId, isEmailMode ->
                    navController.navigate("otp?target=$target&verificationId=$verificationId&isEmailMode=$isEmailMode")
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
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToOtp = { target, verificationId, isEmailMode ->
                    navController.navigate("otp?target=$target&verificationId=$verificationId&isEmailMode=$isEmailMode")
                }
            )
        }
        composable(
            route = "otp?target={target}&verificationId={verificationId}&isEmailMode={isEmailMode}",
            arguments = listOf(
                androidx.navigation.navArgument("target") {
                    type = androidx.navigation.NavType.StringType
                    defaultValue = ""
                },
                androidx.navigation.navArgument("verificationId") {
                    type = androidx.navigation.NavType.StringType
                    defaultValue = ""
                },
                androidx.navigation.navArgument("isEmailMode") {
                    type = androidx.navigation.NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val target = backStackEntry.arguments?.getString("target") ?: ""
            val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
            val isEmailMode = backStackEntry.arguments?.getBoolean("isEmailMode") ?: false

            OtpScreen(
                target = target,
                verificationId = verificationId,
                isEmailMode = isEmailMode,
                onVerifySuccess = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
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
                onNavigateToTrainingAcademy = {
                    navController.navigate("training_academy")
                },
                onNavigateToJob = {
                    navController.navigate("job_screen")
                },
                onNavigateToDomesticHelp = {
                    navController.navigate("domestic_help_screen")
                },
                onNavigateToLegalService = {
                    navController.navigate("legal_service_screen")
                },
                onNavigateToDeedAmin = {
                    navController.navigate("deed_amin_screen")
                },
                onNavigateToHajjUmrah = {
                    navController.navigate("hajj_umrah_screen")
                },
                onNavigateToTourTravels = {
                    navController.navigate("tour_travels_screen")
                },
                onNavigateToMoneyExchange = {
                    navController.navigate("money_exchange_screen")
                },
                onNavigateToMissingFound = {
                    navController.navigate("missing_found_screen")
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
                },
                onNavigateToAdminApproval = {
                    navController.navigate("admin_approval")
                },
                onLogout = {
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                    com.barisal.cityservice.core.utils.UserPreferences.setOtpVerified(context, false)
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
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
            com.barisal.cityservice.feature.ride.RentCarScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostRentCar = { navigateWithAuthCheck("post_rent_car") },
                onNavigateToRentCarDetail = { carId -> navController.navigate("rent_car_detail/$carId") }
            )
        }
        composable("post_rent_car") {
            com.barisal.cityservice.feature.ride.PostRentCarScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("rent_car_detail/{carId}") { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            com.barisal.cityservice.feature.ride.RentCarDetailScreen(
                carId = carId,
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
                    when (categoryKey.lowercase()) {
                        "health", "health_services" -> navController.navigate("health_services")
                        "doctor" -> navController.navigate("doctor_category")
                        "hospital" -> navController.navigate("hospital_list")
                        "houserent", "house_rent" -> navController.navigate("house_rent_list")
                        "shopping" -> navController.navigate("shopping_list")
                        "matrimony" -> navController.navigate("matrimony_home")
                        "event" -> navController.navigate("event_service")
                        "ride", "ride_sharing", "rentcar", "rent_car" -> navController.navigate("ride_screen")
                        "mistri" -> navController.navigate("mistri_service")
                        "tutor" -> navController.navigate("tutor")
                        "hotel" -> navController.navigate("hotel")
                        "restaurant" -> navController.navigate("restaurant")
                        "flatland", "flat_land" -> navController.navigate("flat_land")
                        "training_academy", "training" -> navController.navigate("training_academy")
                        "job", "job_screen", "jobs" -> navController.navigate("job_screen")
                        "domestic_help", "domestic_help_screen", "maid" -> navController.navigate("domestic_help_screen")
                        else -> navController.navigate("category_map/$categoryKey")
                    }
                }
            )
        }
        composable("training_academy") {
            com.barisal.cityservice.feature.training.TrainingAcademyScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostTrainingAcademy = { navigateWithAuthCheck("post_training_academy") },
                onNavigateToTrainingAcademyDetail = { courseId -> navController.navigate("training_academy_detail/$courseId") }
            )
        }
        composable("post_training_academy") {
            com.barisal.cityservice.feature.training.PostTrainingAcademyScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("training_academy_detail/{courseId}") { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            com.barisal.cityservice.feature.training.TrainingAcademyDetailScreen(
                courseId = courseId,
                onBack = { navController.popBackStack() }
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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDonor = { navigateWithAuthCheck("post_blood_donor") },
                onNavigateToPostRequest = { navigateWithAuthCheck("post_blood_request") }
            )
        }
        composable("post_blood_donor") {
            com.barisal.cityservice.feature.blood.PostBloodDonorScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("post_blood_request") {
            com.barisal.cityservice.feature.blood.PostBloodRequestScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("admin_approval") {
            com.barisal.cityservice.feature.admin.AdminApprovalScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("admin_approval/{categoryKey}") { backStackEntry ->
            val categoryKey = backStackEntry.arguments?.getString("categoryKey")
            com.barisal.cityservice.feature.admin.AdminApprovalScreen(
                initialCategoryKey = categoryKey,
                onBack = { navController.popBackStack() }
            )
        }
        composable("event_service") {
            com.barisal.cityservice.feature.event.EventServiceScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostEvent = { navigateWithAuthCheck("post_event_service") }
            )
        }
        composable("job_screen") {
            com.barisal.cityservice.feature.job.JobScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostJob = { navigateWithAuthCheck("post_job") },
                onNavigateToDetail = { jobId -> navController.navigate("job_detail/$jobId") }
            )
        }
        composable("post_job") {
            com.barisal.cityservice.feature.job.PostJobScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("job_detail/{jobId}") { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            com.barisal.cityservice.feature.job.JobDetailScreen(
                jobId = jobId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("domestic_help_screen") {
            com.barisal.cityservice.feature.domestichelp.DomesticHelpScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDomesticHelp = { navigateWithAuthCheck("post_domestic_help") },
                onNavigateToDetail = { helpId -> navController.navigate("domestic_help_detail/$helpId") }
            )
        }
        composable("post_domestic_help") {
            com.barisal.cityservice.feature.domestichelp.PostDomesticHelpScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("domestic_help_detail/{helpId}") { backStackEntry ->
            val helpId = backStackEntry.arguments?.getString("helpId") ?: ""
            com.barisal.cityservice.feature.domestichelp.DomesticHelpDetailScreen(
                helpId = helpId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("legal_service_screen") {
            com.barisal.cityservice.feature.legal.LegalServiceScreen(
                categoryKey = "legal",
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostLegalService = { navigateWithAuthCheck("post_legal_service/legal") },
                onNavigateToDetail = { serviceId -> navController.navigate("legal_service_detail/$serviceId") }
            )
        }
        composable("deed_amin_screen") {
            com.barisal.cityservice.feature.legal.LegalServiceScreen(
                categoryKey = "deed_amin",
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostLegalService = { navigateWithAuthCheck("post_legal_service/deed_amin") },
                onNavigateToDetail = { serviceId -> navController.navigate("legal_service_detail/$serviceId") }
            )
        }
        composable("post_legal_service/{categoryKey}") { backStackEntry ->
            val catKey = backStackEntry.arguments?.getString("categoryKey") ?: "legal"
            com.barisal.cityservice.feature.legal.PostLegalServiceScreen(
                initialCategoryKey = catKey,
                onBack = { navController.popBackStack() }
            )
        }
        composable("legal_service_detail/{serviceId}") { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            com.barisal.cityservice.feature.legal.LegalServiceDetailScreen(
                serviceId = serviceId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("hajj_umrah_screen") {
            com.barisal.cityservice.feature.hajjtour.HajjTourScreen(
                categoryKey = "hajj",
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostHajjTour = { navigateWithAuthCheck("post_hajj_tour/hajj") },
                onNavigateToDetail = { postId -> navController.navigate("hajj_tour_detail/$postId") }
            )
        }
        composable("tour_travels_screen") {
            com.barisal.cityservice.feature.hajjtour.HajjTourScreen(
                categoryKey = "tour",
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostHajjTour = { navigateWithAuthCheck("post_hajj_tour/tour") },
                onNavigateToDetail = { postId -> navController.navigate("hajj_tour_detail/$postId") }
            )
        }
        composable("post_hajj_tour/{categoryKey}") { backStackEntry ->
            val catKey = backStackEntry.arguments?.getString("categoryKey") ?: "hajj"
            com.barisal.cityservice.feature.hajjtour.PostHajjTourScreen(
                initialCategoryKey = catKey,
                onBack = { navController.popBackStack() }
            )
        }
        composable("hajj_tour_detail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            com.barisal.cityservice.feature.hajjtour.HajjTourDetailScreen(
                postId = postId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("money_exchange_screen") {
            com.barisal.cityservice.feature.moneyexchange.MoneyExchangeScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostMoneyExchange = { navigateWithAuthCheck("post_money_exchange") },
                onNavigateToDetail = { id -> navController.navigate("money_exchange_detail/$id") }
            )
        }
        composable("post_money_exchange") {
            com.barisal.cityservice.feature.moneyexchange.PostMoneyExchangeScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("money_exchange_detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            com.barisal.cityservice.feature.moneyexchange.MoneyExchangeDetailScreen(
                id = id,
                onBack = { navController.popBackStack() }
            )
        }
        composable("missing_found_screen") {
            com.barisal.cityservice.feature.missingfound.MissingFoundScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostMissingFound = { navigateWithAuthCheck("post_missing_found") },
                onNavigateToDetail = { id -> navController.navigate("missing_found_detail/$id") }
            )
        }
        composable("post_missing_found") {
            com.barisal.cityservice.feature.missingfound.PostMissingFoundScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("missing_found_detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            com.barisal.cityservice.feature.missingfound.MissingFoundDetailScreen(
                id = id,
                onBack = { navController.popBackStack() }
            )
        }
        composable("post_event_service") {
            com.barisal.cityservice.feature.event.PostEventServiceScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("house_rent_list") {
            com.barisal.cityservice.feature.houserent.HouseRentListScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPostHouseRent = { category ->
                    val route = if (!category.isNullOrBlank()) {
                        "post_house_rent?subCategory=${android.net.Uri.encode(category)}"
                    } else {
                        "post_house_rent"
                    }
                    navigateWithAuthCheck(route)
                },
                onNavigateToHouseRentDetail = { houseId ->
                    navController.navigate("house_rent_detail/$houseId")
                }
            )
        }
        composable("house_rent_detail/{houseId}") { backStackEntry ->
            val houseId = backStackEntry.arguments?.getString("houseId") ?: ""
            com.barisal.cityservice.feature.houserent.HouseRentDetailScreen(
                houseId = houseId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "post_house_rent?subCategory={subCategory}",
            arguments = listOf(
                androidx.navigation.navArgument("subCategory") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val subCategory = backStackEntry.arguments?.getString("subCategory")
            com.barisal.cityservice.feature.houserent.PostHouseRentScreen(
                initialSubCategory = subCategory,
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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostHotel = { navigateWithAuthCheck("post_hotel") },
                onNavigateToHotelDetail = { hotelId -> navController.navigate("hotel_detail/$hotelId") }
            )
        }
        composable("post_hotel") {
            com.barisal.cityservice.feature.hotel.PostHotelScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("hotel_detail/{hotelId}") { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
            com.barisal.cityservice.feature.hotel.HotelDetailScreen(
                hotelId = hotelId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("restaurant") {
            com.barisal.cityservice.feature.restaurant.RestaurantScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostRestaurant = { navigateWithAuthCheck("post_restaurant") },
                onNavigateToRestaurantDetail = { restaurantId -> navController.navigate("restaurant_detail/$restaurantId") }
            )
        }
        composable("post_restaurant") {
            com.barisal.cityservice.feature.restaurant.PostRestaurantScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("restaurant_detail/{restaurantId}") { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""
            com.barisal.cityservice.feature.restaurant.RestaurantDetailScreen(
                restaurantId = restaurantId,
                onBack = { navController.popBackStack() }
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
                    navigateWithAuthCheck("post_product")
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
                onNavigateToHomeCare = { navController.navigate("home_care_list") },
                onNavigateToCategoryMap = { categoryKey -> navController.navigate("category_map/$categoryKey") },
                onNavigateToPostHealthService = { cat ->
                    navigateWithAuthCheck("post_health_service?category=$cat")
                }
            )
        }
        composable("home_care_list") {
            com.barisal.cityservice.feature.doctor.HomeCareListScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPostHomeCare = {
                    navigateWithAuthCheck("post_health_service?category=home_care")
                }
            )
        }
        composable("hospital_list") {
            com.barisal.cityservice.feature.hospital.HospitalListScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPostHealthService = { cat ->
                    navigateWithAuthCheck("post_health_service?category=$cat")
                }
            )
        }
        composable(
            route = "post_health_service?category={categoryKey}",
            arguments = listOf(
                androidx.navigation.navArgument("categoryKey") {
                    type = androidx.navigation.NavType.StringType
                    defaultValue = "hospital"
                }
            )
        ) { backStackEntry ->
            val initialCategory = backStackEntry.arguments?.getString("categoryKey") ?: "hospital"
            com.barisal.cityservice.feature.doctor.PostHealthServiceScreen(
                initialCategory = initialCategory,
                onBack = { navController.popBackStack() }
            )
        }
        composable("doctor_category") {
            com.barisal.cityservice.feature.doctor.DoctorCategoryScreen(
                onBack = { navController.popBackStack() },
                onCategoryClick = { categoryName ->
                    navController.navigate("doctor_list/$categoryName")
                },
                onNavigateToPostDoctor = {
                    navigateWithAuthCheck("post_doctor")
                }
            )
        }
        composable("doctor_list/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            com.barisal.cityservice.feature.doctor.DoctorListScreen(
                categoryName = categoryName,
                onBack = { navController.popBackStack() },
                onNavigateToPostDoctor = { cat ->
                    navigateWithAuthCheck("post_doctor?category=$cat")
                }
            )
        }
        composable(
            route = "post_doctor?category={categoryName}",
            arguments = listOf(
                androidx.navigation.navArgument("categoryName") {
                    type = androidx.navigation.NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val initialCategory = backStackEntry.arguments?.getString("categoryName") ?: ""
            com.barisal.cityservice.feature.doctor.PostDoctorScreen(
                initialCategory = initialCategory,
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
