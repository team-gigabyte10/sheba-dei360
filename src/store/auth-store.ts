import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { User } from '@/types';
import { currentUser } from '@/lib/mock-data';

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  loginWithOtp: (phone: string) => Promise<void>;
  register: (name: string, email: string, password: string, phone: string) => Promise<void>;
  loginWithGoogle: () => Promise<void>;
  loginWithFacebook: () => Promise<void>;
  logout: () => void;
  updateProfile: (data: Partial<User>) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      isAuthenticated: false,
      isLoading: false,
      login: async (email) => {
        await new Promise((r) => setTimeout(r, 800));
        set({ user: { ...currentUser, email }, isAuthenticated: true });
      },
      loginWithOtp: async (phone) => {
        await new Promise((r) => setTimeout(r, 800));
        set({ user: { ...currentUser, phone }, isAuthenticated: true });
      },
      register: async (name, email, _password, phone) => {
        await new Promise((r) => setTimeout(r, 800));
        set({ user: { ...currentUser, name, email, phone }, isAuthenticated: true });
      },
      loginWithGoogle: async () => {
        await new Promise((r) => setTimeout(r, 800));
        set({ user: { ...currentUser, email: 'google.user@gmail.com' }, isAuthenticated: true });
      },
      loginWithFacebook: async () => {
        await new Promise((r) => setTimeout(r, 800));
        set({ user: { ...currentUser, email: 'fb.user@facebook.com' }, isAuthenticated: true });
      },
      logout: () => set({ user: null, isAuthenticated: false }),
      updateProfile: (data) =>
        set((state) => ({ user: state.user ? { ...state.user, ...data } : null })),
    }),
    { name: 'servenear-auth' }
  )
);
