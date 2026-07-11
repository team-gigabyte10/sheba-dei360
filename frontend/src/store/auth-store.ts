import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { User } from '@/types';

export const MOCK_USERS: Record<User['role'], User> = {
  customer: {
    id: 'u1',
    name: 'John Doe',
    email: 'john.doe@email.com',
    phone: '+880 1700 000000',
    avatar: 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=200',
    walletBalance: 125.50,
    joinedDate: '2023-06-15',
    totalOrders: 87,
    memberLevel: 'gold',
    role: 'customer',
  },
  provider: {
    id: 'u2',
    name: 'Ahmed Rahman',
    email: 'ahmed.rahman@email.com',
    phone: '+880 1800 111111',
    avatar: 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=200',
    walletBalance: 320.00,
    joinedDate: '2023-08-20',
    totalOrders: 1240,
    memberLevel: 'platinum',
    role: 'provider',
  },
  business: {
    id: 'u3',
    name: 'Tanveer Chowdhury',
    email: 'tanveer.sultans@email.com',
    phone: '+880 1900 222222',
    avatar: 'https://images.pexels.com/photos/1043471/pexels-photo-1043471.jpeg?auto=compress&cs=tinysrgb&w=200',
    walletBalance: 1450.75,
    joinedDate: '2023-05-10',
    totalOrders: 4200,
    memberLevel: 'platinum',
    role: 'business',
  },
  admin: {
    id: 'u4',
    name: 'Super Admin',
    email: 'admin@servenear.com',
    phone: '+880 1500 333333',
    avatar: 'https://images.pexels.com/photos/1681010/pexels-photo-1681010.jpeg?auto=compress&cs=tinysrgb&w=200',
    walletBalance: 8520.00,
    joinedDate: '2022-01-01',
    totalOrders: 0,
    memberLevel: 'platinum',
    role: 'admin',
  },
};

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
  setRole: (role: User['role']) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      isAuthenticated: false,
      isLoading: false,
      login: async (email) => {
        await new Promise((r) => setTimeout(r, 800));
        let selectedUser = MOCK_USERS.customer;
        if (email.includes('admin')) selectedUser = MOCK_USERS.admin;
        else if (email.includes('provider')) selectedUser = MOCK_USERS.provider;
        else if (email.includes('business') || email.includes('owner')) selectedUser = MOCK_USERS.business;
        
        set({ user: { ...selectedUser, email }, isAuthenticated: true });
      },
      loginWithOtp: async (phone) => {
        await new Promise((r) => setTimeout(r, 800));
        set({ user: { ...MOCK_USERS.customer, phone }, isAuthenticated: true });
      },
      register: async (name, email, _password, phone) => {
        await new Promise((r) => setTimeout(r, 800));
        set({ user: { ...MOCK_USERS.customer, name, email, phone }, isAuthenticated: true });
      },
      loginWithGoogle: async () => {
        await new Promise((r) => setTimeout(r, 800));
        set({ user: { ...MOCK_USERS.customer, email: 'google.user@gmail.com' }, isAuthenticated: true });
      },
      loginWithFacebook: async () => {
        await new Promise((r) => setTimeout(r, 800));
        set({ user: { ...MOCK_USERS.customer, email: 'fb.user@facebook.com' }, isAuthenticated: true });
      },
      logout: () => set({ user: null, isAuthenticated: false }),
      updateProfile: (data) =>
        set((state) => ({ user: state.user ? { ...state.user, ...data } : null })),
      setRole: (role) =>
        set((state) => {
          const baseUser = MOCK_USERS[role];
          const currentEmail = state.user?.email || baseUser.email;
          return {
            user: { ...baseUser, email: currentEmail },
            isAuthenticated: true,
          };
        }),
    }),
    { name: 'servenear-auth' }
  )
);
