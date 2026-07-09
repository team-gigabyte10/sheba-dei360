import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { Address, PaymentMethod } from '@/types';
import { userAddresses } from '@/lib/mock-data';

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
}

export const useBookingStore = create<BookingStoreState>()(
  persist(
    (set, get) => ({
      draft: null,
      cart: [],
      addresses: userAddresses,
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
    }),
    { name: 'servenear-booking' }
  )
);
