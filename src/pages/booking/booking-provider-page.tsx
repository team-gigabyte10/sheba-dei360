import { Navigate } from 'react-router-dom';
import { useBookingStore } from '@/store/booking-store';

export function BookingProviderPage() {
  const { draft } = useBookingStore();
  if (!draft) return <Navigate to="/search" replace />;
  return <Navigate to="/booking/datetime" replace />;
}
