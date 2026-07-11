import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { Address, PaymentMethod, Booking } from '@/types';
import { userAddresses, userBookings } from '@/lib/mock-data';
import { useAuthStore } from './auth-store';
import { useNotificationStore } from './notification-store';
import { toast } from 'sonner';

export interface BookingDraft {
  serviceId: string;
  serviceName: string;
  providerId: string;
  providerName: string;
  providerAvatar: string;
  price: number;
  type: 'service' | 'food' | 'ride' | 'grocery' | 'parcel';
  date?: string;
  time?: string;
  address?: Address;
  paymentMethod?: PaymentMethod;
  notes?: string;
}

interface BookingStoreState {
  draft: BookingDraft | null;
  cart: { id: string; name: string; price: number; image: string; quantity: number; restaurantId: string }[];
  addresses: Address[];
  bookings: Booking[];
  promoCode: string | null;
  promoDiscount: number;
  setDraft: (draft: BookingDraft) => void;
  updateDraft: (data: Partial<BookingDraft>) => void;
  clearDraft: () => void;
  addToCart: (item: { id: string; name: string; price: number; image: string; restaurantId: string }) => void;
  removeFromCart: (id: string) => void;
  updateQuantity: (id: string, quantity: number) => void;
  clearCart: () => void;
  addAddress: (address: Omit<Address, 'id'>) => void;
  removeAddress: (id: string) => void;
  setDefaultAddress: (id: string) => void;
  cartTotal: () => number;
  addBooking: (booking: Booking) => void;
  updateBooking: (id: string, data: Partial<Booking>) => void;
  applyPromoCode: (code: string) => boolean;
  removePromo: () => void;
  discountedTotal: () => number;
}

export const useBookingStore = create<BookingStoreState>()(
  persist(
    (set, get) => ({
      draft: null,
      cart: [],
      addresses: userAddresses,
      bookings: userBookings,
      promoCode: null,
      promoDiscount: 0,
      setDraft: (draft) => set({ draft }),
      updateDraft: (data) =>
        set((state) => ({ draft: state.draft ? { ...state.draft, ...data } : null })),
      clearDraft: () => set({ draft: null }),
      addToCart: (item) =>
        set((state) => {
          const existing = state.cart.find((c) => c.id === item.id);
          if (existing) {
            return {
              cart: state.cart.map((c) =>
                c.id === item.id ? { ...c, quantity: c.quantity + 1 } : c
              ),
            };
          }
          return { cart: [...state.cart, { ...item, quantity: 1 }] };
        }),
      removeFromCart: (id) =>
        set((state) => ({ cart: state.cart.filter((c) => c.id !== id) })),
      updateQuantity: (id, quantity) =>
        set((state) => ({
          cart: quantity <= 0
            ? state.cart.filter((c) => c.id !== id)
            : state.cart.map((c) => (c.id === id ? { ...c, quantity } : c)),
        })),
      clearCart: () => set({ cart: [] }),
      addAddress: (address) =>
        set((state) => ({
          addresses: [
            ...state.addresses.map((a) => (address.isDefault ? { ...a, isDefault: false } : a)),
            { ...address, id: `a${Date.now()}` },
          ],
        })),
      removeAddress: (id) =>
        set((state) => ({ addresses: state.addresses.filter((a) => a.id !== id) })),
      setDefaultAddress: (id) =>
        set((state) => ({
          addresses: state.addresses.map((a) => ({ ...a, isDefault: a.id === id })),
        })),
      cartTotal: () => get().cart.reduce((sum, item) => sum + item.price * item.quantity, 0),
      addBooking: (booking) => {
        set((state) => ({
          bookings: [booking, ...state.bookings],
        }));

        // Trigger wallet debit if wallet paid
        if (booking.paymentMethod === 'wallet') {
          const authUser = useAuthStore.getState().user;
          if (authUser) {
            useAuthStore.getState().updateProfile({
              walletBalance: Math.max(0, authUser.walletBalance - booking.amount),
            });
          }
        }

        // --- STATUS PROGRESSION SIMULATOR ---
        // 1. Pending -> Confirmed (6 seconds)
        setTimeout(() => {
          get().updateBooking(booking.id, { status: 'confirmed' });
          useNotificationStore.getState().addNotification({
            type: 'booking',
            title: 'Provider Accepted!',
            message: `${booking.providerName} has accepted your request for ${booking.serviceName}.`,
            actionUrl: `/track/${booking.id}`,
          });
          toast.info(`Booking Accepted: ${booking.providerName} is on their way!`);
        }, 6000);

        // 2. Confirmed -> In-Progress (18 seconds)
        setTimeout(() => {
          get().updateBooking(booking.id, { status: 'in-progress' });
          useNotificationStore.getState().addNotification({
            type: 'booking',
            title: 'Service Started!',
            message: `${booking.providerName} has started the service for ${booking.serviceName}.`,
            actionUrl: `/track/${booking.id}`,
          });
          toast.info(`Service In Progress: ${booking.serviceName} has started.`);
        }, 18000);

        // 3. In-Progress -> Completed (36 seconds)
        setTimeout(() => {
          get().updateBooking(booking.id, { status: 'completed' });
          useNotificationStore.getState().addNotification({
            type: 'booking',
            title: 'Service Completed!',
            message: `${booking.providerName} has completed the service for ${booking.serviceName}.`,
            actionUrl: '/dashboard/bookings',
          });
          toast.success(`Service Completed: Please rate ${booking.providerName}!`);
        }, 36000);
      },
      updateBooking: (id, data) =>
        set((state) => ({
          bookings: state.bookings.map((b) => (b.id === id ? { ...b, ...data } : b)),
        })),
      applyPromoCode: (code) => {
        const cleaned = code.trim().toUpperCase();
        if (cleaned === 'WEEKEND50') {
          set({ promoCode: 'WEEKEND50', promoDiscount: 50 });
          return true;
        } else if (cleaned === 'SAVENEAR10') {
          set({ promoCode: 'SAVENEAR10', promoDiscount: 10 });
          return true;
        }
        return false;
      },
      removePromo: () => set({ promoCode: null, promoDiscount: 0 }),
      discountedTotal: () => {
        const total = get().cartTotal();
        const discount = get().promoDiscount;
        return total - (total * (discount / 100));
      },
    }),
    { name: 'servenear-booking' }
  )
);
