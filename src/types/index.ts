export type ServiceCategory =
  | 'food'
  | 'ride'
  | 'home-services'
  | 'health'
  | 'beauty'
  | 'grocery'
  | 'emergency'
  | 'parcel';

export type ServiceStatus = 'available' | 'busy' | 'offline';

export type BookingStatus =
  | 'pending'
  | 'confirmed'
  | 'in-progress'
  | 'completed'
  | 'cancelled';

export type PaymentMethod = 'wallet' | 'card' | 'bkash' | 'nagad' | 'cash';

export type OrderType = 'food' | 'ride' | 'service' | 'grocery' | 'parcel';

export interface Category {
  id: string;
  name: string;
  slug: ServiceCategory;
  icon: string;
  color: string;
  description: string;
  serviceCount: number;
}

export interface Provider {
  id: string;
  name: string;
  avatar: string;
  rating: number;
  reviewCount: number;
  distance: number;
  category: ServiceCategory;
  service: string;
  priceFrom: number;
  priceUnit: string;
  status: ServiceStatus;
  responseTime: string;
  completedJobs: number;
  verified: boolean;
  badges: string[];
  bio: string;
  gallery: string[];
  availableSlots: string[];
  location: { lat: number; lng: number; address: string };
}

export interface Restaurant {
  id: string;
  name: string;
  image: string;
  logo: string;
  rating: number;
  reviewCount: number;
  cuisine: string[];
  deliveryTime: string;
  deliveryFee: number;
  priceLevel: number;
  distance: number;
  isOpen: boolean;
  promoted: boolean;
  tags: string[];
  menu: MenuItem[];
}

export interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
  image: string;
  category: string;
  popular: boolean;
  vegetarian: boolean;
}

export interface Review {
  id: string;
  author: string;
  avatar: string;
  rating: number;
  comment: string;
  date: string;
  service: string;
  helpful: number;
}

export interface Booking {
  id: string;
  serviceId: string;
  serviceName: string;
  providerId: string;
  providerName: string;
  providerAvatar: string;
  date: string;
  time: string;
  address: string;
  status: BookingStatus;
  amount: number;
  paymentMethod: PaymentMethod;
  type: OrderType;
  createdAt: string;
  rating?: number;
  review?: string;
}

export interface Address {
  id: string;
  label: string;
  fullAddress: string;
  lat: number;
  lng: number;
  isDefault: boolean;
  type: 'home' | 'work' | 'other';
}

export interface WalletTransaction {
  id: string;
  type: 'credit' | 'debit';
  amount: number;
  description: string;
  date: string;
  method: PaymentMethod;
  status: 'success' | 'pending' | 'failed';
}

export interface AppNotification {
  id: string;
  type: 'booking' | 'promo' | 'system' | 'chat' | 'payment';
  title: string;
  message: string;
  timestamp: string;
  read: boolean;
  actionUrl?: string;
}

export interface SupportTicket {
  id: string;
  subject: string;
  category: string;
  status: 'open' | 'in-progress' | 'resolved' | 'closed';
  priority: 'low' | 'medium' | 'high';
  createdAt: string;
  lastUpdate: string;
  messages: { id: string; sender: 'user' | 'support'; text: string; timestamp: string }[];
}

export interface ChatMessage {
  id: string;
  senderId: string;
  senderName: string;
  text: string;
  timestamp: string;
  type: 'text' | 'image' | 'location' | 'system';
}

export interface EmergencyService {
  id: string;
  name: string;
  icon: string;
  number: string;
  category: 'medical' | 'fire' | 'police' | 'roadside' | 'plumber' | 'electrician';
  available247: boolean;
  responseTime: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  avatar: string;
  walletBalance: number;
  joinedDate: string;
  totalOrders: number;
  memberLevel: 'bronze' | 'silver' | 'gold' | 'platinum';
}

export interface Testimonial {
  id: string;
  name: string;
  role: string;
  avatar: string;
  rating: number;
  comment: string;
  service: string;
}
