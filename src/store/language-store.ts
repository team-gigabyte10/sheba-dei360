import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export type Language = 'en' | 'bn';

interface LanguageState {
  language: Language;
  setLanguage: (lang: Language) => void;
}

export const useLanguageStore = create<LanguageState>()(
  persist(
    (set) => ({
      language: 'en',
      setLanguage: (language) => set({ language }),
    }),
    { name: 'servenear-language' }
  )
);

export const translations: Record<Language, Record<string, string>> = {
  en: {
    'nav.home': 'Home',
    'nav.search': 'Search',
    'nav.bookings': 'Bookings',
    'nav.wallet': 'Wallet',
    'nav.profile': 'Profile',
    'cta.bookNow': 'Book Now',
    'cta.orderNow': 'Order Now',
    'cta.viewAll': 'View All',
    'cta.getStarted': 'Get Started',
    'cta.login': 'Login',
    'cta.signup': 'Sign Up',
    'hero.title': 'Everything you need, delivered to your door',
    'hero.subtitle': 'Food, rides, home services, and more — from trusted local providers in your neighborhood.',
    'section.categories': 'Browse Categories',
    'section.providers': 'Nearby Providers',
    'section.restaurants': 'Featured Restaurants',
    'section.emergency': 'Emergency Services',
    'section.testimonials': 'What Our Customers Say',
    'section.download': 'Get the ServeNear App',
  },
  bn: {
    'nav.home': 'হোম',
    'nav.search': 'খুঁজুন',
    'nav.bookings': 'বুকিং',
    'nav.wallet': 'ওয়ালেট',
    'nav.profile': 'প্রোফাইল',
    'cta.bookNow': 'বুক করুন',
    'cta.orderNow': 'অর্ডার করুন',
    'cta.viewAll': 'সব দেখুন',
    'cta.getStarted': 'শুরু করুন',
    'cta.login': 'লগইন',
    'cta.signup': 'সাইন আপ',
    'hero.title': 'আপনার প্রয়োজনীয় সবকিছু, আপনার দরজায়',
    'hero.subtitle': 'খাবার, রাইড, হোম সার্ভিস এবং আরও — আপনার এলাকার বিশ্বস্ত স্থানীয় প্রোভাইডার থেকে।',
    'section.categories': 'ক্যাটাগরি ব্রাউজ করুন',
    'section.providers': 'কাছাকাছি প্রোভাইডার',
    'section.restaurants': 'ফিচার্ড রেস্টুরেন্ট',
    'section.emergency': 'ইমার্জেন্সি সার্ভিস',
    'section.testimonials': 'আমাদের গ্রাহকরা যা বলেন',
    'section.download': 'ServeNear অ্যাপ পান',
  },
};

export function useTranslation() {
  const language = useLanguageStore((s) => s.language);
  return (key: string) => translations[language][key] ?? key;
}
