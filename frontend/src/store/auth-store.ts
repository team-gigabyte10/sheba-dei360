import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { User } from '@/types';

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password?: string) => Promise<void>;
  loginWithOtp: (phone: string) => Promise<void>;
  register: (name: string, email: string, password?: string, phone?: string) => Promise<void>;
  loginWithGoogle: () => Promise<void>;
  loginWithFacebook: () => Promise<void>;
  logout: () => void;
  updateProfile: (data: Partial<User>) => void;
  setRole: (role: User['role']) => void;
}

const ADMIN_EMAILS = new Set([
  'teamgigabyte10@gmail.com',
  'eeerakib24@gmail.com',
  'admin@servenear.com',
  'admin@sheba.com'
]);

const ADMIN_PHONES = new Set([
  '01798771927',
  '+8801798771927'
]);

function determineRole(email?: string, phone?: string): User['role'] {
  const lowerEmail = email?.toLowerCase() || '';
  const cleanPhone = phone?.trim() || '';
  if (lowerEmail.includes('admin') || ADMIN_EMAILS.has(lowerEmail) || ADMIN_PHONES.has(cleanPhone)) {
    return 'admin';
  }
  if (lowerEmail.includes('provider')) {
    return 'provider';
  }
  if (lowerEmail.includes('business') || lowerEmail.includes('owner')) {
    return 'business';
  }
  return 'customer';
}

function createUserObject(details: Partial<User>): User {
  const email = details.email || '';
  const phone = details.phone || '';
  const role = details.role || determineRole(email, phone);
  
  let displayName = details.name;
  if (!displayName) {
    if (email) {
      const prefix = email.split('@')[0];
      displayName = prefix.charAt(0).toUpperCase() + prefix.slice(1);
    } else if (phone) {
      displayName = `User ${phone.slice(-4)}`;
    } else {
      displayName = 'User';
    }
  }

  return {
    id: details.id || `u_${Date.now()}`,
    name: displayName,
    email,
    phone,
    avatar: details.avatar || '',
    walletBalance: details.walletBalance ?? 0,
    joinedDate: details.joinedDate || new Date().toISOString().split('T')[0],
    totalOrders: details.totalOrders ?? 0,
    memberLevel: role === 'admin' ? 'platinum' : 'standard',
    role,
  };
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      isAuthenticated: false,
      isLoading: false,
      login: async (email) => {
        await new Promise((r) => setTimeout(r, 400));
        const role = determineRole(email);
        set({ user: createUserObject({ email, role }), isAuthenticated: true });
      },
      loginWithOtp: async (phone) => {
        await new Promise((r) => setTimeout(r, 400));
        const role = determineRole(undefined, phone);
        set({ user: createUserObject({ phone, role }), isAuthenticated: true });
      },
      register: async (name, email, _password, phone) => {
        await new Promise((r) => setTimeout(r, 400));
        const role = determineRole(email, phone);
        set({ user: createUserObject({ name, email, phone, role }), isAuthenticated: true });
      },
      loginWithGoogle: async () => {
        await new Promise((r) => setTimeout(r, 400));
        set({ user: createUserObject({ name: 'Google User', email: 'user@gmail.com' }), isAuthenticated: true });
      },
      loginWithFacebook: async () => {
        await new Promise((r) => setTimeout(r, 400));
        set({ user: createUserObject({ name: 'Facebook User', email: 'user@facebook.com' }), isAuthenticated: true });
      },
      logout: () => set({ user: null, isAuthenticated: false }),
      updateProfile: (data) =>
        set((state) => ({ user: state.user ? { ...state.user, ...data } : null })),
      setRole: (role) =>
        set((state) => {
          if (!state.user) {
            return { user: createUserObject({ role }), isAuthenticated: true };
          }
          return {
            user: { ...state.user, role },
            isAuthenticated: true,
          };
        }),
    }),
    { name: 'servenear-auth' }
  )
);
