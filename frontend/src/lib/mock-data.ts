import type {
  Category,
  Provider,
  Restaurant,
  Review,
  Booking,
  Address,
  WalletTransaction,
  AppNotification,
  SupportTicket,
  EmergencyService,
  User,
  Testimonial,
} from '@/types';

export const categories: Category[] = [
  { id: '1', name: 'Food Delivery', slug: 'food', icon: 'UtensilsCrossed', color: 'orange', description: 'Order from your favorite restaurants', serviceCount: 1250 },
  { id: '2', name: 'Ride Hailing', slug: 'ride', icon: 'Car', color: 'sky', description: 'Get a ride in minutes', serviceCount: 850 },
  { id: '3', name: 'Home Services', slug: 'home-services', icon: 'Home', color: 'emerald', description: 'Cleaning, repair, and more', serviceCount: 640 },
  { id: '4', name: 'Health & Wellness', slug: 'health', icon: 'HeartPulse', color: 'rose', description: 'Doctors, pharmacy, lab tests', serviceCount: 320 },
  { id: '5', name: 'Beauty & Spa', slug: 'beauty', icon: 'Sparkles', color: 'pink', description: 'Salon at your doorstep', serviceCount: 410 },
  { id: '6', name: 'Grocery', slug: 'grocery', icon: 'ShoppingCart', color: 'lime', description: 'Fresh groceries delivered', serviceCount: 530 },
  { id: '7', name: 'Emergency', slug: 'emergency', icon: 'Siren', color: 'red', description: '24/7 emergency services', serviceCount: 45 },
  { id: '8', name: 'Parcel Delivery', slug: 'parcel', icon: 'Package', color: 'amber', description: 'Send packages across the city', serviceCount: 290 },
];

export const providers: Provider[] = [
  {
    id: 'p1', name: 'Ahmed Rahman', avatar: 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=200',
    rating: 4.9, reviewCount: 312, distance: 1.2, category: 'home-services', service: 'Deep Home Cleaning', priceFrom: 35, priceUnit: 'visit',
    status: 'available', responseTime: '~15 min', completedJobs: 1240, verified: true, badges: ['Top Rated', 'Background Verified'],
    bio: 'Professional cleaning specialist with 8+ years of experience. Eco-friendly products, satisfaction guaranteed.',
    gallery: ['https://images.pexels.com/photos/4239036/pexels-photo-4239036.jpeg?auto=compress&cs=tinysrgb&w=600', 'https://images.pexels.com/photos/4108715/pexels-photo-4108715.jpeg?auto=compress&cs=tinysrgb&w=600'],
    availableSlots: ['09:00', '11:00', '14:00', '16:00', '18:00'],
    location: { lat: 23.8103, lng: 90.4125, address: 'Gulshan, Dhaka' },
  },
  {
    id: 'p2', name: 'Sofia Khan', avatar: 'https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&w=200',
    rating: 4.8, reviewCount: 208, distance: 2.5, category: 'beauty', service: 'At-Home Salon & Spa', priceFrom: 50, priceUnit: 'session',
    status: 'available', responseTime: '~30 min', completedJobs: 890, verified: true, badges: ['Premium', 'Certified'],
    bio: 'Certified beautician offering manicure, pedicure, facial, and spa treatments at your home.',
    gallery: ['https://images.pexels.com/photos/3993449/pexels-photo-3993449.jpeg?auto=compress&cs=tinysrgb&w=600', 'https://images.pexels.com/photos/3997379/pexels-photo-3997379.jpeg?auto=compress&cs=tinysrgb&w=600'],
    availableSlots: ['10:00', '12:00', '15:00', '17:00'],
    location: { lat: 23.7808, lng: 90.4193, address: 'Banani, Dhaka' },
  },
  {
    id: 'p3', name: 'Karim Hassan', avatar: 'https://images.pexels.com/photos/1043471/pexels-photo-1043471.jpeg?auto=compress&cs=tinysrgb&w=200',
    rating: 4.7, reviewCount: 156, distance: 3.1, category: 'home-services', service: 'AC Repair & Service', priceFrom: 25, priceUnit: 'visit',
    status: 'busy', responseTime: '~45 min', completedJobs: 670, verified: true, badges: ['Verified', 'Quick Service'],
    bio: 'Expert AC technician servicing all brands. Same-day repair available.',
    gallery: ['https://images.pexels.com/photos/8006302/pexels-photo-8006302.jpeg?auto=compress&cs=tinysrgb&w=600'],
    availableSlots: ['09:00', '13:00', '15:00'],
    location: { lat: 23.7508, lng: 90.3913, address: 'Dhanmondi, Dhaka' },
  },
  {
    id: 'p4', name: 'Dr. Nadia Islam', avatar: 'https://images.pexels.com/photos/5407206/pexels-photo-5407206.jpeg?auto=compress&cs=tinysrgb&w=200',
    rating: 5.0, reviewCount: 98, distance: 4.0, category: 'health', service: 'Home Doctor Visit', priceFrom: 60, priceUnit: 'visit',
    status: 'available', responseTime: '~20 min', completedJobs: 320, verified: true, badges: ['MBBS', 'Top Rated'],
    bio: 'General physician with 10+ years experience. Home visits for elderly and urgent care.',
    gallery: ['https://images.pexels.com/photos/263402/pexels-photo-263402.jpeg?auto=compress&cs=tinysrgb&w=600'],
    availableSlots: ['08:00', '10:00', '14:00', '16:00', '18:00', '20:00'],
    location: { lat: 23.7937, lng: 90.4066, address: 'Mohakhali, Dhaka' },
  },
  {
    id: 'p5', name: 'Rahul Das', avatar: 'https://images.pexels.com/photos/1681010/pexels-photo-1681010.jpeg?auto=compress&cs=tinysrgb&w=200',
    rating: 4.6, reviewCount: 412, distance: 1.8, category: 'ride', service: 'City Ride', priceFrom: 3, priceUnit: 'trip',
    status: 'available', responseTime: '~5 min', completedJobs: 5200, verified: true, badges: ['Pro Driver', '5-Star'],
    bio: 'Experienced driver with spotless safety record. Comfortable sedan, AC, clean.',
    gallery: [],
    availableSlots: [],
    location: { lat: 23.8103, lng: 90.4125, address: 'Gulshan, Dhaka' },
  },
  {
    id: 'p6', name: 'Fatima Begum', avatar: 'https://images.pexels.com/photos/1181686/pexels-photo-1181686.jpeg?auto=compress&cs=tinysrgb&w=200',
    rating: 4.8, reviewCount: 275, distance: 2.0, category: 'beauty', service: 'Bridal Makeup', priceFrom: 120, priceUnit: 'session',
    status: 'available', responseTime: '~1 hour', completedJobs: 450, verified: true, badges: ['Bridal Expert', 'Premium'],
    bio: 'Specialist in bridal and party makeup. Premium products, long-lasting results.',
    gallery: ['https://images.pexels.com/photos/3997389/pexels-photo-3997389.jpeg?auto=compress&cs=tinysrgb&w=600'],
    availableSlots: ['08:00', '10:00', '12:00'],
    location: { lat: 23.7708, lng: 90.4093, address: 'Baridhara, Dhaka' },
  },
  {
    id: 'p7', name: 'Tanvir Ahmed', avatar: 'https://images.pexels.com/photos/1300402/pexels-photo-1300402.jpeg?auto=compress&cs=tinysrgb&w=200',
    rating: 4.5, reviewCount: 189, distance: 5.2, category: 'home-services', service: 'Electrician', priceFrom: 20, priceUnit: 'visit',
    status: 'available', responseTime: '~30 min', completedJobs: 890, verified: true, badges: ['Licensed', 'Verified'],
    bio: 'Licensed electrician for all wiring, repair, and installation needs.',
    gallery: ['https://images.pexels.com/photos/8006365/pexels-photo-8006365.jpeg?auto=compress&cs=tinysrgb&w=600'],
    availableSlots: ['09:00', '11:00', '14:00', '17:00'],
    location: { lat: 23.7339, lng: 90.4053, address: 'Mohammadpur, Dhaka' },
  },
  {
    id: 'p8', name: 'Lima Akter', avatar: 'https://images.pexels.com/photos/1462630/pexels-photo-1462630.jpeg?auto=compress&cs=tinysrgb&w=200',
    rating: 4.9, reviewCount: 340, distance: 1.5, category: 'beauty', service: 'Hair Styling & Color', priceFrom: 40, priceUnit: 'session',
    status: 'available', responseTime: '~25 min', completedJobs: 1100, verified: true, badges: ['Top Rated', 'Trend Setter'],
    bio: 'Professional hair stylist specializing in cuts, color, and treatments.',
    gallery: ['https://images.pexels.com/photos/3992874/pexels-photo-3992874.jpeg?auto=compress&cs=tinysrgb&w=600'],
    availableSlots: ['10:00', '12:00', '15:00', '17:00', '19:00'],
    location: { lat: 23.7808, lng: 90.4193, address: 'Banani, Dhaka' },
  },
];

export const restaurants: Restaurant[] = [
  {
    id: 'r1', name: 'Sultan\'s Dine', image: 'https://images.pexels.com/photos/958545/pexels-photo-958545.jpeg?auto=compress&cs=tinysrgb&w=600',
    logo: 'https://images.pexels.com/photos/958545/pexels-photo-958545.jpeg?auto=compress&cs=tinysrgb&w=100',
    rating: 4.7, reviewCount: 1240, cuisine: ['Kacchi', 'Biryani', 'Mughlai'], deliveryTime: '30-40 min', deliveryFee: 2.5,
    priceLevel: 2, distance: 1.5, isOpen: true, promoted: true, tags: ['Best Biryani', 'Family'],
    menu: [
      { id: 'm1', name: 'Special Kacchi', description: 'Traditional mutton kacchi biryani with saffron rice', price: 12.99, image: 'https://images.pexels.com/photos/12737656/pexels-photo-12737656.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'Biryani', popular: true, vegetarian: false },
      { id: 'm2', name: 'Chicken Biryani', description: 'Aromatic basmati rice with tender chicken', price: 9.99, image: 'https://images.pexels.com/photos/7373723/pexels-photo-7373723.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'Biryani', popular: true, vegetarian: false },
      { id: 'm3', name: 'Shami Kebab', description: 'Spiced minced meat patties, 4 pieces', price: 5.99, image: 'https://images.pexels.com/photos/2233717/pexels-photo-2233717.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'Starters', popular: false, vegetarian: false },
    ],
  },
  {
    id: 'r2', name: 'Pizza Burg', image: 'https://images.pexels.com/photos/315755/pexels-photo-315755.jpeg?auto=compress&cs=tinysrgb&w=600',
    logo: 'https://images.pexels.com/photos/315755/pexels-photo-315755.jpeg?auto=compress&cs=tinysrgb&w=100',
    rating: 4.5, reviewCount: 890, cuisine: ['Pizza', 'Fast Food', 'Burgers'], deliveryTime: '25-35 min', deliveryFee: 1.99,
    priceLevel: 1, distance: 2.0, isOpen: true, promoted: false, tags: ['Fast Delivery'],
    menu: [
      { id: 'm4', name: 'Margherita Pizza', description: 'Classic pizza with fresh mozzarella and basil', price: 8.99, image: 'https://images.pexels.com/photos/2147491/pexels-photo-2147491.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'Pizza', popular: true, vegetarian: true },
      { id: 'm5', name: 'Chicken Burger', description: 'Juicy grilled chicken burger with fries', price: 6.99, image: 'https://images.pexels.com/photos/1639558/pexels-photo-1639558.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'Burgers', popular: true, vegetarian: false },
    ],
  },
  {
    id: 'r3', name: 'Green Leaf Vegan', image: 'https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?auto=compress&cs=tinysrgb&w=600',
    logo: 'https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?auto=compress&cs=tinysrgb&w=100',
    rating: 4.8, reviewCount: 567, cuisine: ['Vegan', 'Healthy', 'Salads'], deliveryTime: '20-30 min', deliveryFee: 1.5,
    priceLevel: 2, distance: 3.0, isOpen: true, promoted: true, tags: ['Healthy', 'Vegan'],
    menu: [
      { id: 'm6', name: 'Buddha Bowl', description: 'Quinoa, roasted veggies, avocado, tahini dressing', price: 10.99, image: 'https://images.pexels.com/photos/1640772/pexels-photo-1640772.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'Bowls', popular: true, vegetarian: true },
      { id: 'm7', name: 'Green Smoothie', description: 'Spinach, banana, apple, ginger', price: 4.99, image: 'https://images.pexels.com/photos/1346155/pexels-photo-1346155.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'Drinks', popular: false, vegetarian: true },
    ],
  },
  {
    id: 'r4', name: 'Star Kabab', image: 'https://images.pexels.com/photos/769289/pexels-photo-769289.jpeg?auto=compress&cs=tinysrgb&w=600',
    logo: 'https://images.pexels.com/photos/769289/pexels-photo-769289.jpeg?auto=compress&cs=tinysrgb&w=100',
    rating: 4.6, reviewCount: 2100, cuisine: ['Kebab', 'Middle Eastern', 'Grill'], deliveryTime: '35-45 min', deliveryFee: 2.0,
    priceLevel: 1, distance: 2.8, isOpen: false, promoted: false, tags: ['Late Night'],
    menu: [
      { id: 'm8', name: 'Mixed Grill Platter', description: 'Assorted kebabs with rice and salad', price: 14.99, image: 'https://images.pexels.com/photos/2233348/pexels-photo-2233348.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'Grill', popular: true, vegetarian: false },
    ],
  },
  {
    id: 'r5', name: 'Sushi Zen', image: 'https://images.pexels.com/photos/2098085/pexels-photo-2098085.jpeg?auto=compress&cs=tinysrgb&w=600',
    logo: 'https://images.pexels.com/photos/2098085/pexels-photo-2098085.jpeg?auto=compress&cs=tinysrgb&w=100',
    rating: 4.9, reviewCount: 950, cuisine: ['Japanese', 'Sushi', 'Asian'], deliveryTime: '30-40 min', deliveryFee: 3.0,
    priceLevel: 3, distance: 4.5, isOpen: true, promoted: true, tags: ['Premium', 'Trending'],
    menu: [
      { id: 'm9', name: 'Dragon Roll', description: 'Shrimp tempura, avocado, eel sauce (8 pieces)', price: 15.99, image: 'https://images.pexels.com/photos/2098085/pexels-photo-2098085.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'Sushi', popular: true, vegetarian: false },
      { id: 'm10', name: 'Salmon Sashimi', description: 'Fresh salmon sashimi, 6 pieces', price: 12.99, image: 'https://images.pexels.com/photos/896923/pexels-photo-896923.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'Sushi', popular: true, vegetarian: false },
    ],
  },
  {
    id: 'r6', name: 'Cha House', image: 'https://images.pexels.com/photos/395208/pexels-photo-395208.jpeg?auto=compress&cs=tinysrgb&w=600',
    logo: 'https://images.pexels.com/photos/395208/pexels-photo-395208.jpeg?auto=compress&cs=tinysrgb&w=100',
    rating: 4.4, reviewCount: 670, cuisine: ['Tea', 'Snacks', 'Desserts'], deliveryTime: '15-25 min', deliveryFee: 1.0,
    priceLevel: 1, distance: 0.8, isOpen: true, promoted: false, tags: ['Quick Bite'],
    menu: [
      { id: 'm11', name: 'Masala Chai', description: 'Traditional spiced milk tea', price: 1.99, image: 'https://images.pexels.com/photos/395208/pexels-photo-395208.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'Tea', popular: true, vegetarian: true },
    ],
  },
];

export const reviews: Review[] = [
  { id: 'rv1', author: 'Mahmudul Hasan', avatar: 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=100', rating: 5, comment: 'Absolutely amazing service! Arrived on time and did a thorough job. Will definitely book again.', date: '2024-01-15', service: 'Deep Home Cleaning', helpful: 24 },
  { id: 'rv2', author: 'Nusrat Jahan', avatar: 'https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&w=100', rating: 4, comment: 'Great experience overall. The provider was professional and friendly. Minor delay but work quality was excellent.', date: '2024-01-10', service: 'At-Home Salon & Spa', helpful: 12 },
  { id: 'rv3', author: 'Imran Khan', avatar: 'https://images.pexels.com/photos/1043471/pexels-photo-1043471.jpeg?auto=compress&cs=tinysrgb&w=100', rating: 5, comment: 'Best AC repair service in the city! Fixed the issue quickly and the price was very reasonable.', date: '2024-01-08', service: 'AC Repair & Service', helpful: 18 },
  { id: 'rv4', author: 'Sabrina Yasmin', avatar: 'https://images.pexels.com/photos/1181686/pexels-photo-1181686.jpeg?auto=compress&cs=tinysrgb&w=100', rating: 5, comment: 'The doctor was very caring and professional. Home visit saved me a trip to the clinic. Highly recommend!', date: '2024-01-05', service: 'Home Doctor Visit', helpful: 31 },
];

export const testimonials: Testimonial[] = [
  { id: 't1', name: 'Arif Hossain', role: 'Regular Customer', avatar: 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=200', rating: 5, comment: 'ServeNear has changed how I manage my home. From cleaning to repairs, everything is just a tap away. The providers are verified and reliable.', service: 'Home Services' },
  { id: 't2', name: 'Tania Sharmin', role: 'Working Professional', avatar: 'https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&w=200', rating: 5, comment: 'As a busy mom, ServeNear is a lifesaver. I can order groceries, book a salon appointment, and get a ride — all from one app. The live tracking gives me peace of mind.', service: 'Multiple Services' },
  { id: 't3', name: 'Rakibul Islam', role: 'Business Owner', avatar: 'https://images.pexels.com/photos/1043471/pexels-photo-1043471.jpeg?auto=compress&cs=tinysrgb&w=200', rating: 5, comment: 'The wallet feature and multiple payment options make transactions seamless. I have used ServeNear over 200 times and never had a bad experience.', service: 'Food & Rides' },
  { id: 't4', name: 'Farzana Akter', role: 'Teacher', avatar: 'https://images.pexels.com/photos/1181686/pexels-photo-1181686.jpeg?auto=compress&cs=tinysrgb&w=200', rating: 4, comment: 'Love the variety of services! The emergency section is especially useful. Once needed a plumber at midnight and got one in 20 minutes.', service: 'Emergency Services' },
];

export const emergencyServices: EmergencyService[] = [
  { id: 'e1', name: 'Ambulance', icon: 'Ambulance', number: '199', category: 'medical', available247: true, responseTime: '~8 min' },
  { id: 'e2', name: 'Fire Service', icon: 'Flame', number: '199', category: 'fire', available247: true, responseTime: '~10 min' },
  { id: 'e3', name: 'Police', icon: 'Shield', number: '999', category: 'police', available247: true, responseTime: '~5 min' },
  { id: 'e4', name: 'Emergency Plumber', icon: 'Wrench', number: '16567', category: 'plumber', available247: true, responseTime: '~20 min' },
  { id: 'e5', name: 'Emergency Electrician', icon: 'Zap', number: '16567', category: 'electrician', available247: true, responseTime: '~25 min' },
  { id: 'e6', name: 'Roadside Assistance', icon: 'Car', number: '16567', category: 'roadside', available247: true, responseTime: '~30 min' },
];

export const currentUser: User = {
  id: 'u1', name: 'John Doe', email: 'john.doe@email.com', phone: '+880 1700 000000',
  avatar: 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=200',
  walletBalance: 125.50, joinedDate: '2023-06-15', totalOrders: 87, memberLevel: 'gold',
  role: 'customer',
};

export const userBookings: Booking[] = [
  { id: 'b1', serviceId: 'p1', serviceName: 'Deep Home Cleaning', providerId: 'p1', providerName: 'Ahmed Rahman', providerAvatar: 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=100', date: '2024-01-20', time: '10:00', address: 'House 12, Road 5, Gulshan, Dhaka', status: 'completed', amount: 35, paymentMethod: 'wallet', type: 'service', createdAt: '2024-01-18', rating: 5, review: 'Excellent service!' },
  { id: 'b2', serviceId: 'r1', serviceName: 'Special Kacchi', providerId: 'r1', providerName: 'Sultan\'s Dine', providerAvatar: 'https://images.pexels.com/photos/958545/pexels-photo-958545.jpeg?auto=compress&cs=tinysrgb&w=100', date: '2024-01-22', time: '13:00', address: 'House 12, Road 5, Gulshan, Dhaka', status: 'in-progress', amount: 15.49, paymentMethod: 'bkash', type: 'food', createdAt: '2024-01-22' },
  { id: 'b3', serviceId: 'p5', serviceName: 'City Ride', providerId: 'p5', providerName: 'Rahul Das', providerAvatar: 'https://images.pexels.com/photos/1681010/pexels-photo-1681010.jpeg?auto=compress&cs=tinysrgb&w=100', date: '2024-01-21', time: '09:30', address: 'Gulshan to Dhanmondi', status: 'completed', amount: 8.50, paymentMethod: 'cash', type: 'ride', createdAt: '2024-01-21', rating: 4 },
  { id: 'b4', serviceId: 'p2', serviceName: 'At-Home Salon & Spa', providerId: 'p2', providerName: 'Sofia Khan', providerAvatar: 'https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&w=100', date: '2024-01-25', time: '15:00', address: 'House 12, Road 5, Gulshan, Dhaka', status: 'confirmed', amount: 50, paymentMethod: 'card', type: 'service', createdAt: '2024-01-19' },
  { id: 'b5', serviceId: 'p3', serviceName: 'AC Repair & Service', providerId: 'p3', providerName: 'Karim Hassan', providerAvatar: 'https://images.pexels.com/photos/1043471/pexels-photo-1043471.jpeg?auto=compress&cs=tinysrgb&w=100', date: '2024-01-26', time: '14:00', address: 'House 12, Road 5, Gulshan, Dhaka', status: 'pending', amount: 25, paymentMethod: 'wallet', type: 'service', createdAt: '2024-01-19' },
];

export const userAddresses: Address[] = [
  { id: 'a1', label: 'Home', fullAddress: 'House 12, Road 5, Gulshan 1, Dhaka 1212', lat: 23.8103, lng: 90.4125, isDefault: true, type: 'home' },
  { id: 'a2', label: 'Work', fullAddress: 'Level 6, BDBL Bhaban, Kawran Bazar, Dhaka 1215', lat: 23.7508, lng: 90.3913, isDefault: false, type: 'work' },
  { id: 'a3', label: 'Mom\'s House', fullAddress: 'House 45, Road 11, Banani, Dhaka 1213', lat: 23.7808, lng: 90.4193, isDefault: false, type: 'other' },
];

export const walletTransactions: WalletTransaction[] = [
  { id: 'w1', type: 'credit', amount: 50, description: 'Wallet Top-up via bKash', date: '2024-01-20', method: 'bkash', status: 'success' },
  { id: 'w2', type: 'debit', amount: 35, description: 'Payment for Deep Home Cleaning', date: '2024-01-20', method: 'wallet', status: 'success' },
  { id: 'w3', type: 'credit', amount: 10, description: 'Cashback reward', date: '2024-01-19', method: 'wallet', status: 'success' },
  { id: 'w4', type: 'debit', amount: 8.50, description: 'Payment for City Ride', date: '2024-01-21', method: 'cash', status: 'success' },
  { id: 'w5', type: 'credit', amount: 100, description: 'Wallet Top-up via Card', date: '2024-01-15', method: 'card', status: 'success' },
  { id: 'w6', type: 'debit', amount: 15.49, description: 'Payment for Special Kacchi', date: '2024-01-22', method: 'bkash', status: 'pending' },
];

export const notifications: AppNotification[] = [
  { id: 'n1', type: 'booking', title: 'Booking Confirmed', message: 'Your booking for At-Home Salon & Spa is confirmed for Jan 25, 3:00 PM', timestamp: '2024-01-19T10:30:00', read: false, actionUrl: '/dashboard/bookings' },
  { id: 'n2', type: 'promo', title: '50% Off This Weekend!', message: 'Use code WEEKEND50 for 50% off on all home services this weekend', timestamp: '2024-01-19T09:00:00', read: false, actionUrl: '/search' },
  { id: 'n3', type: 'chat', title: 'New Message', message: 'Ahmed Rahman: I will arrive in 15 minutes', timestamp: '2024-01-18T14:45:00', read: true, actionUrl: '/chat/p1' },
  { id: 'n4', type: 'payment', title: 'Payment Successful', message: 'Your payment of ৳35.00 for Deep Home Cleaning was successful', timestamp: '2024-01-18T11:00:00', read: true },
  { id: 'n5', type: 'system', title: 'Welcome to ServeNear Gold!', message: 'You have been upgraded to Gold member. Enjoy exclusive benefits!', timestamp: '2024-01-15T08:00:00', read: true },
];

export const supportTickets: SupportTicket[] = [
  { id: 's1', subject: 'Refund for cancelled booking', category: 'Payment', status: 'in-progress', priority: 'high', createdAt: '2024-01-17', lastUpdate: '2024-01-19',
    messages: [
      { id: 'sm1', sender: 'user', text: 'I cancelled my booking but haven\'t received the refund yet.', timestamp: '2024-01-17T10:00:00' },
      { id: 'sm2', sender: 'support', text: 'We apologize for the delay. Your refund is being processed and will reflect in 3-5 business days.', timestamp: '2024-01-18T14:00:00' },
      { id: 'sm3', sender: 'user', text: 'Thank you, I will wait.', timestamp: '2024-01-19T09:00:00' },
    ] },
  { id: 's2', subject: 'Provider didn\'t show up', category: 'Booking', status: 'resolved', priority: 'high', createdAt: '2024-01-10', lastUpdate: '2024-01-12',
    messages: [
      { id: 'sm4', sender: 'user', text: 'The provider didn\'t arrive for my scheduled appointment.', timestamp: '2024-01-10T15:00:00' },
      { id: 'sm5', sender: 'support', text: 'We\'re sorry for the inconvenience. A full refund has been issued and a ৳10 credit added to your wallet.', timestamp: '2024-01-11T10:00:00' },
    ] },
];

export const chatMessages: Record<string, { id: string; senderId: string; senderName: string; text: string; timestamp: string; type: 'text' | 'image' | 'location' | 'system' }[]> = {
  p1: [
    { id: 'c1', senderId: 'p1', senderName: 'Ahmed Rahman', text: 'Hello! I\'m Ahmed, your cleaning professional. I\'ll be arriving soon.', timestamp: '2024-01-20T09:45:00', type: 'text' },
    { id: 'c2', senderId: 'u1', senderName: 'You', text: 'Great! I\'m at home, please come to the 3rd floor.', timestamp: '2024-01-20T09:46:00', type: 'text' },
    { id: 'c3', senderId: 'p1', senderName: 'Ahmed Rahman', text: 'Sure, I\'m on my way. ETA 15 minutes.', timestamp: '2024-01-20T09:47:00', type: 'text' },
    { id: 'c4', senderId: 'p1', senderName: 'Ahmed Rahman', text: 'I\'ve arrived at your location.', timestamp: '2024-01-20T10:00:00', type: 'text' },
  ],
};

export const popularServices = [
  { id: 'ps1', name: 'Deep Home Cleaning', icon: 'Sparkles', category: 'home-services', price: 35, image: 'https://images.pexels.com/photos/4239036/pexels-photo-4239036.jpeg?auto=compress&cs=tinysrgb&w=400', bookings: '12k+' },
  { id: 'ps2', name: 'AC Repair', icon: 'Wind', category: 'home-services', price: 25, image: 'https://images.pexels.com/photos/8006302/pexels-photo-8006302.jpeg?auto=compress&cs=tinysrgb&w=400', bookings: '8.5k+' },
  { id: 'ps3', name: 'Salon at Home', icon: 'Sparkles', category: 'beauty', price: 50, image: 'https://images.pexels.com/photos/3993449/pexels-photo-3993449.jpeg?auto=compress&cs=tinysrgb&w=400', bookings: '15k+' },
  { id: 'ps4', name: 'Home Doctor Visit', icon: 'Stethoscope', category: 'health', price: 60, image: 'https://images.pexels.com/photos/263402/pexels-photo-263402.jpeg?auto=compress&cs=tinysrgb&w=400', bookings: '5k+' },
  { id: 'ps5', name: 'Electrician', icon: 'Zap', category: 'home-services', price: 20, image: 'https://images.pexels.com/photos/8006365/pexels-photo-8006365.jpeg?auto=compress&cs=tinysrgb&w=400', bookings: '9k+' },
  { id: 'ps6', name: 'City Ride', icon: 'Car', category: 'ride', price: 3, image: 'https://images.pexels.com/photos/170811/pexels-photo-170811.jpeg?auto=compress&cs=tinysrgb&w=400', bookings: '50k+' },
];

export const wishlistItems = [
  { id: 'w1', serviceId: 'p2', serviceName: 'At-Home Salon & Spa', providerName: 'Sofia Khan', price: 50, image: 'https://images.pexels.com/photos/3993449/pexels-photo-3993449.jpeg?auto=compress&cs=tinysrgb&w=400', rating: 4.8 },
  { id: 'w2', serviceId: 'r5', serviceName: 'Dragon Roll', providerName: 'Sushi Zen', price: 15.99, image: 'https://images.pexels.com/photos/2098085/pexels-photo-2098085.jpeg?auto=compress&cs=tinysrgb&w=400', rating: 4.9 },
  { id: 'w3', serviceId: 'p4', serviceName: 'Home Doctor Visit', providerName: 'Dr. Nadia Islam', price: 60, image: 'https://images.pexels.com/photos/263402/pexels-photo-263402.jpeg?auto=compress&cs=tinysrgb&w=400', rating: 5.0 },
];
