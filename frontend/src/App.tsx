import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from '@/components/ui/sonner';
import { RootLayout } from '@/components/layout/root-layout';
import { DashboardLayout } from '@/components/layout/dashboard-layout';
import { ProtectedRoute } from '@/components/auth/protected-route';

import { LandingPage } from '@/pages/landing/landing-page';
import { LoginPage } from '@/pages/auth/login-page';
import { RegisterPage } from '@/pages/auth/register-page';
import { OtpLoginPage } from '@/pages/auth/otp-login-page';
import { ForgotPasswordPage } from '@/pages/auth/forgot-password-page';

import { SearchPage } from '@/pages/search/search-page';
import { ServiceDetailsPage } from '@/pages/service/service-details-page';
import { RestaurantDetailsPage } from '@/pages/service/restaurant-details-page';
import { GroceryPage } from '@/pages/service/grocery-page';
import { PharmacyPage } from '@/pages/service/pharmacy-page';
import { EmergencyPage } from '@/pages/emergency/emergency-page';

import { BookingProviderPage } from '@/pages/booking/booking-provider-page';
import { BookingDateTimePage } from '@/pages/booking/booking-datetime-page';
import { BookingAddressPage } from '@/pages/booking/booking-address-page';
import { BookingPaymentPage } from '@/pages/booking/booking-payment-page';
import { BookingConfirmationPage } from '@/pages/booking/booking-confirmation-page';
import { RideBookingPage } from '@/pages/booking/ride-booking-page';
import { DoctorAppointmentPage } from '@/pages/booking/doctor-appointment-page';

import { DashboardHome } from '@/pages/dashboard/dashboard-home';
import { ProfilePage } from '@/pages/dashboard/profile-page';
import { OrdersPage } from '@/pages/dashboard/orders-page';
import { ReportsPage } from '@/pages/dashboard/reports-page';
import { BookingsPage } from '@/pages/dashboard/bookings-page';
import { WalletPage } from '@/pages/dashboard/wallet-page';
import { AddressesPage } from '@/pages/dashboard/addresses-page';
import { NotificationsPage } from '@/pages/dashboard/notifications-page';
import { WishlistPage } from '@/pages/dashboard/wishlist-page';
import { ReviewsPage } from '@/pages/dashboard/reviews-page';
import { SupportPage } from '@/pages/dashboard/support-page';
import { SettingsPage } from '@/pages/dashboard/settings-page';

import { LiveTrackingPage } from '@/pages/realtime/live-tracking-page';
import { ChatPage } from '@/pages/realtime/chat-page';
import { NotFoundPage } from '@/pages/error/not-found-page';
import { ServerErrorPage } from '@/pages/error/server-error-page';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000,
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route element={<RootLayout />}>
            <Route path="/" element={<LandingPage />} />
            <Route path="/search" element={<SearchPage />} />
            <Route path="/service/:id" element={<ServiceDetailsPage />} />
            <Route path="/restaurant/:id" element={<RestaurantDetailsPage />} />
            <Route path="/emergency" element={<EmergencyPage />} />
            <Route path="/grocery" element={<GroceryPage />} />
            <Route path="/pharmacy" element={<PharmacyPage />} />
            <Route path="/track/:bookingId" element={<LiveTrackingPage />} />
            <Route path="/chat/:providerId" element={<ChatPage />} />

            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/otp-login" element={<OtpLoginPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />

            <Route path="/booking/provider" element={<BookingProviderPage />} />
            <Route path="/booking/datetime" element={<BookingDateTimePage />} />
            <Route path="/booking/address" element={<BookingAddressPage />} />
            <Route path="/booking/payment" element={<BookingPaymentPage />} />
            <Route path="/booking/confirmation" element={<BookingConfirmationPage />} />
            <Route path="/ride" element={<RideBookingPage />} />
            <Route path="/doctor-appointment" element={<DoctorAppointmentPage />} />

            <Route element={<ProtectedRoute><DashboardLayout /></ProtectedRoute>}>
              <Route path="/dashboard" element={<DashboardHome />} />
              <Route path="/dashboard/profile" element={<ProfilePage />} />
              <Route path="/dashboard/orders" element={<OrdersPage />} />
              <Route path="/dashboard/reports" element={<ReportsPage />} />
              <Route path="/dashboard/bookings" element={<BookingsPage />} />
              <Route path="/dashboard/wallet" element={<WalletPage />} />
              <Route path="/dashboard/addresses" element={<AddressesPage />} />
              <Route path="/dashboard/notifications" element={<NotificationsPage />} />
              <Route path="/dashboard/wishlist" element={<WishlistPage />} />
              <Route path="/dashboard/reviews" element={<ReviewsPage />} />
              <Route path="/dashboard/support" element={<SupportPage />} />
              <Route path="/dashboard/settings" element={<SettingsPage />} />
            </Route>

            <Route path="/500" element={<ServerErrorPage />} />
            <Route path="*" element={<NotFoundPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
      <Toaster />
    </QueryClientProvider>
  );
}
