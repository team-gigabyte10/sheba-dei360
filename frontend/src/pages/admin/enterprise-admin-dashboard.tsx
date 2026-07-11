import { useState, useMemo, useEffect, useRef } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Label } from '@/components/ui/label';
import { Separator } from '@/components/ui/separator';
import { Switch } from '@/components/ui/switch';
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter
} from '@/components/ui/dialog';
import {
  Users, Shield, Star, Wallet, Compass, Search, Filter,
  Download, Trash2, AlertTriangle, PlusCircle, RefreshCw, Layers, Database, Lock, Eye, Check, X,
  Activity, Settings, Radio, HelpCircle, Truck, Car, ShoppingBag, CreditCard, Percent, Map, Upload, Send, Sliders
} from 'lucide-react';
import { toast } from 'sonner';
import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  BarChart, Bar, PieChart, Pie, Cell, Legend
} from 'recharts';

// --- DATA STRUCTURE TYPES ---
interface ModuleRecord {
  id: string;
  [key: string]: any;
}

type AdminRole = 'Super Admin' | 'Admin' | 'Support' | 'Finance' | 'Operations' | 'Moderator';

export function EnterpriseAdminDashboard() {
  // --- STATE MANAGEMENT ---
  const [activeRole, setActiveRole] = useState<AdminRole>('Super Admin');
  const [activeTab, setActiveTab] = useState<string>('overview');
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [showAdvancedFilters, setShowAdvancedFilters] = useState(false);

  // --- PLATFORM CONFIGURATIONS ---
  const [platformConfig, setPlatformConfig] = useState({
    multiVendor: true,
    multiService: true,
    realTimeTracking: true,
    realTimeUpdates: 'WebSocket', // WebSocket | Firebase
    pushNotifications: 'FCM', // FCM | OneSignal
    autoAssignment: true,
    mapProvider: 'Google Maps', // Google Maps | Mapbox
    rtlLanguage: false,
    darkMode: false,
    flexibleCommission: true,
    driverEarnings: true,
    vendorOpenCloseTimes: true,
    awesomeAnimations: true,
    googleMapsApiKey: 'AIzaSyBiols4lFvOc7_rGeOZVI6l-YE617w7xR0',
    mapboxApiKey: 'pk.eyJ1Ijoic2VydmVuZWFyIiwiYSI6ImNseGo0...'
  });

  // --- COMMISSION CONFIGS ---
  const [commissionConfig, setCommissionConfig] = useState({
    globalFee: 15,
    foodFee: 15,
    rideFee: 10,
    homeFee: 12,
    emergencyFee: 0,
    codHandlingFee: 20,
    driverSplit: 85, // 85% to driver, 15% to platform
  });

  // --- GATEWAY CONFIG ---
  const [gatewaysConfig, setGatewaysConfig] = useState({
    bKash: true,
    Nagad: true,
    Stripe: true,
    PayPal: false,
    cod: true,
    paymentOnPickup: true,
    offline: true,
  });

  const [gatewaySecrets, setGatewaySecrets] = useState({
    bKash: { appId: 'bk-app-9028', appSecret: '••••••••••••', mode: 'Sandbox' },
    Nagad: { merchantId: 'ng-merch-1049', appSecret: '••••••••••••', mode: 'Sandbox' },
    Stripe: { publishableKey: 'pk_test_51N...', secretKey: '••••••••••••', mode: 'Sandbox' },
    PayPal: { clientId: 'client_id_paypal...', clientSecret: '••••••••••••', mode: 'Sandbox' },
  });

  // --- EXCEL/SPREADSHEET IMPORT STATE ---
  const [isImportOpen, setIsImportOpen] = useState(false);
  const [importTarget, setImportTarget] = useState('users');
  const [csvContent, setCsvContent] = useState('');
  const [isParsing, setIsParsing] = useState(false);
  const [parseProgress, setParseProgress] = useState(0);
  const [parsedRows, setParsedRows] = useState<any[]>([]);
  const [uploadFileName, setUploadFileName] = useState('');

  // --- BROADCAST NOTIFICATION COMPOSER STATE ---
  const [isBroadcastOpen, setIsBroadcastOpen] = useState(false);
  const [broadcastTitle, setBroadcastTitle] = useState('');
  const [broadcastBody, setBroadcastBody] = useState('');
  const [broadcastTarget, setBroadcastTarget] = useState('All Users');
  const [broadcastChannel, setBroadcastChannel] = useState('FCM Push Notification');

  // --- SYSTEM AUDIT LOGS STATE ---
  const [auditLogs, setAuditLogs] = useState<Array<{ id: string; user: string; action: string; time: string; ip: string }>>([
    { id: '1', user: 'admin@servenear.com', action: 'Approved Provider: Kamrul Plumber', time: '10 mins ago', ip: '192.168.1.5' },
    { id: '2', user: 'admin@servenear.com', action: 'Platform fee updated to 15%', time: '1 hour ago', ip: '192.168.1.5' },
    { id: '3', user: 'support@servenear.com', action: 'Resolved ticket #1402', time: '2 hours ago', ip: '10.0.0.45' },
    { id: '4', user: 'finance@servenear.com', action: 'Disbursed ৳480.50 payout to Sultan\'s Dine', time: '3 hours ago', ip: '192.168.1.12' }
  ]);

  // --- DYNAMIC MODULE DATA DEFINITIONS ---
  const [records, setRecords] = useState<Record<string, ModuleRecord[]>>({
    users: [
      { id: 'u1', name: 'John Doe', email: 'john.doe@email.com', role: 'Customer', status: 'Active', joined: '2026-01-10' },
      { id: 'u2', name: 'Nadia Islam', email: 'nadia.islam@email.com', role: 'Customer', status: 'Active', joined: '2026-02-14' },
      { id: 'u3', name: 'Kamrul Plumber', email: 'kamrul@plumber.com', role: 'Provider', status: 'Active', joined: '2026-03-20' },
      { id: 'u4', name: 'Malicious Bot', email: 'spam@bot.com', role: 'Customer', status: 'Suspended', joined: '2026-06-01' }
    ],
    providers: [
      { id: 'p1', name: 'Kamrul Plumber', service: 'Plumbing', rating: 4.9, status: 'Active', city: 'Dhaka' },
      { id: 'p2', name: 'Sofia Salon Spa', service: 'Beauty Services', rating: 4.8, status: 'Active', city: 'Dhaka' },
      { id: 'p3', name: 'Rider Shakil', service: 'Delivery Driver', rating: 4.7, status: 'Inactive', city: 'Chittagong' },
      { id: 'p4', name: 'Electrician Tanvir', service: 'Electrical', rating: 4.2, status: 'Pending Review', city: 'Sylhet' }
    ],
    businesses: [
      { id: 'b1', name: "Sultan's Dine", type: 'Restaurant', outlets: 4, revenue: 15480.00, status: 'Verified' },
      { id: 'b2', name: 'Kacchi Bhai', type: 'Restaurant', outlets: 6, revenue: 24900.00, status: 'Verified' },
      { id: 'b3', name: 'Lazz Pharma', type: 'Pharmacy', outlets: 2, revenue: 8900.00, status: 'Verified' }
    ],
    restaurants: [
      { id: 'r1', name: "Sultan's Dine", cuisine: 'Kacchi Biryani', rating: 4.9, activeOrders: 18, status: 'Open' },
      { id: 'r2', name: 'Chillox', cuisine: 'Burgers', rating: 4.7, activeOrders: 8, status: 'Open' },
      { id: 'r3', name: 'Star Kabab', cuisine: 'Local Kebab', rating: 4.5, activeOrders: 0, status: 'Closed' }
    ],
    drivers: [
      { id: 'd1', name: 'Rider Shakil', vehicle: 'Motorcycle (Suzuki)', rating: 4.8, activeTrip: 'No', status: 'Offline' },
      { id: 'd2', name: 'Driver Kashem', vehicle: 'Car (Toyota Prius)', rating: 4.9, activeTrip: 'Yes', status: 'Online' },
      { id: 'd3', name: 'Rider Rahman', vehicle: 'Bicycle', rating: 4.6, activeTrip: 'No', status: 'Online' }
    ],
    doctors: [
      { id: 'doc1', name: 'Dr. Nadia Islam', specialty: 'Pediatrics', visits: 142, rating: 4.9, status: 'Active' },
      { id: 'doc2', name: 'Dr. Tanveer Ahmed', specialty: 'Cardiology', visits: 98, rating: 4.8, status: 'Active' }
    ],
    managers: [
      { id: 'mng1', name: 'Zamil Hossain', business: "Sultan's Dine", email: 'zamil@sultans.com', status: 'Active' },
      { id: 'mng2', name: 'Farah Anjum', business: 'Lazz Pharma', email: 'farah@lazz.com', status: 'Active' }
    ],
    categories: [
      { id: 'cat1', name: 'Food & Dining', slug: 'food', servicesCount: 42, commission: '15%', status: 'Active' },
      { id: 'cat2', name: 'Ride Sharing', slug: 'ride', servicesCount: 8, commission: '10%', status: 'Active' },
      { id: 'cat3', name: 'Home Services', slug: 'home', servicesCount: 15, commission: '15%', status: 'Active' },
      { id: 'cat4', name: 'Emergency SOS', slug: 'emergency', servicesCount: 4, commission: '0%', status: 'Active' }
    ],
    services: [
      { id: 's1', name: 'Mutton Kacchi', category: 'Food & Dining', basePrice: 12.99, provider: "Sultan's Dine", status: 'Listed' },
      { id: 's2', name: 'Plumbing Visit', category: 'Home Services', basePrice: 15.00, provider: 'Kamrul Plumber', status: 'Listed' },
      { id: 's3', name: 'Sedan Ride (KM)', category: 'Ride Sharing', basePrice: 1.50, provider: 'Driver Kashem', status: 'Listed' }
    ],
    products: [
      { id: 'pr1', name: 'Napa Extend (10 tablet)', category: 'Pharmacy', price: 15.00, stock: 150, status: 'Active' },
      { id: 'pr2', name: 'Mutton Kacchi Half', category: 'Food & Dining', price: 280.00, stock: 45, status: 'Active' },
      { id: 'pr3', name: 'Full AC Wash', category: 'Home Services', price: 1200.00, stock: 99, status: 'Active' }
    ],
    menusOptions: [
      { id: 'opt1', name: 'Extra Mutton Piece', type: 'Add-on', price: 120.00, appliesTo: 'Mutton Kacchi', status: 'Active' },
      { id: 'opt2', name: 'Choose Size (Medium/Large)', type: 'Option', price: 0.00, appliesTo: 'Chillox Burger', status: 'Active' }
    ],
    bookings: [
      { id: 'bk1', customer: 'John Doe', provider: 'Kamrul Plumber', date: '2026-07-09', amount: 35.00, status: 'Completed' },
      { id: 'bk2', customer: 'Nadia Islam', provider: 'Dr. Nadia Islam', date: '2026-07-09', amount: 50.00, status: 'In-Progress' },
      { id: 'bk3', customer: 'John Doe', provider: "Sultan's Dine", date: '2026-07-09', amount: 24.50, status: 'Pending' }
    ],
    orders: [
      { id: 'o1', customer: 'John Doe', shop: "Sultan's Dine", items: '2x Mutton Kacchi', total: 25.98, status: 'Dispatched' },
      { id: 'o2', customer: 'Nadia Islam', shop: 'Lazz Pharma', items: '1x Medicine Pack', total: 12.50, status: 'Completed' }
    ],
    rides: [
      { id: 'rd1', rider: 'John Doe', driver: 'Driver Kashem', route: 'Banani to Gulshan', fare: 250.00, status: 'In-Progress' },
      { id: 'rd2', rider: 'Nadia Islam', driver: 'Rider Shakil', route: 'Dhanmondi to Uttara', fare: 650.00, status: 'Completed' }
    ],
    deliveries: [
      { id: 'dl1', customer: 'John Doe', driver: 'Rider Rahman', shop: "Sultan's Dine", charge: 60.00, status: 'Dispatched' },
      { id: 'dl2', customer: 'Nadia Islam', driver: 'Rider Shakil', shop: 'Lazz Pharma', charge: 40.00, status: 'Completed' }
    ],
    payments: [
      { id: 'tx1', bookingId: 'bk1', amount: 35.00, method: 'bKash', transactionId: 'TXN-9028A', status: 'Paid' },
      { id: 'tx2', bookingId: 'bk2', amount: 50.00, method: 'Nagad', transactionId: 'TXN-1049B', status: 'Paid' }
    ],
    wallet: [
      { id: 'w1', user: 'John Doe', balance: 145.00, lastTopUp: '2026-07-08', currency: 'BDT' },
      { id: 'w2', user: 'Kamrul Plumber', balance: 520.50, lastTopUp: '2026-07-09', currency: 'BDT' }
    ],
    withdrawRequests: [
      { id: 'wd1', provider: 'Kamrul Plumber', amount: 250.00, method: 'bKash', account: '01712345678', status: 'Pending' },
      { id: 'wd2', provider: 'Sofia Salon Spa', amount: 180.00, method: 'Bank Transfer', account: '10293847', status: 'Approved' }
    ],
    coupons: [
      { id: 'c1', code: 'WEEKEND50', discount: '50%', expiry: '2026-08-30', usages: 142, status: 'Active' },
      { id: 'c2', code: 'SERVENEAR10', discount: '10%', expiry: '2026-12-31', usages: 502, status: 'Active' }
    ],
    notifications: [
      { id: 'n1', title: 'System Maintenance Scheduled', target: 'All Users', date: '2026-07-08', sentBy: 'Operations', status: 'Delivered' },
      { id: 'n2', title: 'Promo Code Weekend50 Active', target: 'Customers Only', date: '2026-07-09', sentBy: 'Marketing', status: 'Delivered' }
    ],
    complaints: [
      { id: 'comp1', user: 'John Doe', against: 'Rider Shakil', topic: 'Late Delivery', severity: 'Low', status: 'Under Review' },
      { id: 'comp2', user: 'Nadia Islam', against: 'Kamrul Plumber', topic: 'Overcharging', severity: 'High', status: 'Open' }
    ],
    supportTickets: [
      { id: 't1', user: 'john.doe@email.com', subject: 'Refund query on order #o1', category: 'Finance', status: 'Open', priority: 'High' },
      { id: 't2', user: 'provider@email.com', subject: 'Payout delays verification', category: 'Payouts', status: 'Resolved', priority: 'Medium' }
    ],
    reviews: [
      { id: 'rv1', author: 'John Doe', rating: 5, comment: 'Excellent prompt plumbing visit', target: 'Kamrul Plumber', date: '2026-07-08' },
      { id: 'rv2', author: 'Nadia Islam', rating: 4, comment: 'Good kacchi, slightly cold', target: "Sultan's Dine", date: '2026-07-09' }
    ],
    cms: [
      { id: 'page1', title: 'Privacy Policy', url: '/privacy', lastUpdated: '2026-01-01', author: 'Operations', status: 'Published' },
      { id: 'page2', title: 'Terms & Service', url: '/terms', lastUpdated: '2026-01-01', author: 'Operations', status: 'Published' }
    ],
    roleManagement: [
      { id: 'r_sa', name: 'Super Admin', usersAssigned: 3, permissions: 'Full Access', level: 'Level 10' },
      { id: 'r_ops', name: 'Operations', usersAssigned: 8, permissions: 'Manage Providers, Bookings', level: 'Level 7' },
      { id: 'r_mod', name: 'Moderator', usersAssigned: 12, permissions: 'Manage Reviews, Support', level: 'Level 5' }
    ],
    permissionManagement: [
      { id: 'p_users', permission: 'users.write', description: 'Can block, suspend, or delete users', module: 'Users', status: 'Enabled' },
      { id: 'p_payouts', permission: 'payouts.execute', description: 'Can release wire payouts', module: 'Finance', status: 'Enabled' }
    ]
  });

  // --- RECHARTS ANALYTICS MOCK DATA ---
  const revenueData = [
    { date: 'Mon', revenue: 14200, orders: 480 },
    { date: 'Tue', revenue: 16500, orders: 512 },
    { date: 'Wed', revenue: 11000, orders: 390 },
    { date: 'Thu', revenue: 22400, orders: 620 },
    { date: 'Fri', revenue: 19800, orders: 580 },
    { date: 'Sat', revenue: 31000, orders: 812 },
    { date: 'Sun', revenue: 28500, orders: 740 }
  ];

  const signupGrowthData = [
    { name: 'May', Customers: 18000, Providers: 2800 },
    { name: 'Jun', Customers: 32000, Providers: 4200 },
    { name: 'Jul', Customers: 42910, Providers: 5241 }
  ];

  const providerPerformanceData = [
    { name: 'Plumbing', rating: 4.9 },
    { name: 'Doctor Visit', rating: 4.85 },
    { name: 'Salon Spa', rating: 4.75 },
    { name: 'Kacchi Dine', rating: 4.88 },
    { name: 'Ride Hailing', rating: 4.65 }
  ];

  const topServicesData = [
    { name: 'Food Delivery', value: 45 },
    { name: 'Ride Sharing', value: 25 },
    { name: 'Home Services', value: 18 },
    { name: 'Doctor Visit', value: 12 }
  ];

  const COLORS = ['#0284c7', '#10b981', '#f59e0b', '#ef4444'];

  // --- ACTIONS LOG TIMELINE APPEND ---
  const logAdminAction = (actionStr: string) => {
    const newLog = {
      id: `l-${Date.now()}`,
      user: `${activeRole.toLowerCase()}@servenear.com`,
      action: actionStr,
      time: 'Just now',
      ip: '192.168.1.5'
    };
    setAuditLogs((prev) => [newLog, ...prev]);
  };

  // --- DYNAMIC SWITCH COMPONENT FOR MODULE TAB ---
  const getTabTitle = (tabKey: string) => {
    if (tabKey === 'overview') return 'Analytics & Trends Dashboard';
    if (tabKey === 'audit') return 'System Administrative Audit Logs';
    return tabKey.charAt(0).toUpperCase() + tabKey.slice(1).replace(/([A-Z])/g, ' $1');
  };

  // --- BULK OPERATIONS ACTIONS ---
  const handleBulkAction = (actionType: 'approve' | 'delete' | 'block') => {
    if (selectedIds.length === 0) {
      toast.warning('Please select one or more records first.');
      return;
    }

    if (activeTab === 'overview' || activeTab === 'audit') return;

    const list = records[activeTab];
    if (!list) return;

    if (actionType === 'delete') {
      setRecords((prev) => ({
        ...prev,
        [activeTab]: prev[activeTab].filter((item) => !selectedIds.includes(item.id))
      }));
      logAdminAction(`Bulk deleted ${selectedIds.length} items from ${activeTab}`);
      toast.error(`Bulk Deleted ${selectedIds.length} records successfully.`);
    } else if (actionType === 'approve') {
      setRecords((prev) => ({
        ...prev,
        [activeTab]: prev[activeTab].map((item) =>
          selectedIds.includes(item.id)
            ? { ...item, status: item.status === 'Pending Review' || item.status === 'Pending' ? 'Active' : item.status }
            : item
        )
      }));
      logAdminAction(`Bulk approved ${selectedIds.length} items in ${activeTab}`);
      toast.success(`Bulk approved selected records.`);
    } else {
      setRecords((prev) => ({
        ...prev,
        [activeTab]: prev[activeTab].map((item) =>
          selectedIds.includes(item.id) ? { ...item, status: 'Suspended' } : item
        )
      }));
      logAdminAction(`Bulk blocked ${selectedIds.length} items in ${activeTab}`);
      toast.info(`Suspended selected user records.`);
    }

    setSelectedIds([]);
  };

  // --- LOCAL EXPORTS SIMULATION ---
  const handleExport = (format: 'pdf' | 'excel') => {
    if (activeTab === 'overview' || activeTab === 'audit') return;
    const currentList = records[activeTab] || [];
    if (currentList.length === 0) {
      toast.warning('No records available to export.');
      return;
    }

    const headers = Object.keys(currentList[0]);
    const fileContent = [
      headers.join(','),
      ...currentList.map((row) => headers.map((h) => String(row[h])).join(','))
    ].join('\n');

    const blob = new Blob([fileContent], { type: format === 'pdf' ? 'text/plain' : 'text/csv' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `servenear_${activeTab}_report_${Date.now()}.${format === 'pdf' ? 'txt' : 'csv'}`;
    link.click();

    logAdminAction(`Exported ${activeTab} records as ${format.toUpperCase()}`);
    toast.success(`Successfully exported ${activeTab} data as ${format.toUpperCase()}!`);
  };

  // --- EXCEL/SPREADSHEET IMPORT SIMULATOR ---
  const handleSpreadsheetImport = (file: File) => {
    setUploadFileName(file.name);
    setIsParsing(true);
    setParseProgress(0);

    const interval = setInterval(() => {
      setParseProgress((p) => {
        if (p >= 100) {
          clearInterval(interval);
          setIsParsing(false);

          // Generate realistic mock records based on selection
          let mockRows: any[] = [];
          const randomId = () => Math.random().toString(36).substring(2, 9);

          if (importTarget === 'users') {
            mockRows = [
              { id: 'u_imp_' + randomId(), name: 'Alim Uddin', email: 'alim.uddin@email.com', role: 'Customer', status: 'Active', joined: new Date().toISOString().split('T')[0] },
              { id: 'u_imp_' + randomId(), name: 'Rowshon Ara', email: 'rowshon.ara@email.com', role: 'Provider', status: 'Active', joined: new Date().toISOString().split('T')[0] },
              { id: 'u_imp_' + randomId(), name: 'Mamun Bepari', email: 'mamun@email.com', role: 'Customer', status: 'Suspended', joined: new Date().toISOString().split('T')[0] }
            ];
          } else if (importTarget === 'products') {
            mockRows = [
              { id: 'pr_imp_' + randomId(), name: 'Napa Rapid (10 tab)', category: 'Pharmacy', price: 20.00, stock: 120, status: 'Active' },
              { id: 'pr_imp_' + randomId(), name: 'Chicken Biryani Full', category: 'Food & Dining', price: 450.00, stock: 40, status: 'Active' },
              { id: 'pr_imp_' + randomId(), name: 'Beef Tehari Half', category: 'Food & Dining', price: 180.00, stock: 30, status: 'Active' }
            ];
          } else if (importTarget === 'providers') {
            mockRows = [
              { id: 'p_imp_' + randomId(), name: 'Sajib Electrician', service: 'Electrical', rating: 4.8, status: 'Active', city: 'Dhaka' },
              { id: 'p_imp_' + randomId(), name: 'Clean Clean Co.', service: 'Cleaning Services', rating: 4.5, status: 'Active', city: 'Chittagong' }
            ];
          } else if (importTarget === 'drivers') {
            mockRows = [
              { id: 'd_imp_' + randomId(), name: 'Rider Nayan', vehicle: 'Motorcycle', rating: 4.7, activeTrip: 'No', status: 'Online' },
              { id: 'd_imp_' + randomId(), name: 'Driver Selim', vehicle: 'Microbus', rating: 4.9, activeTrip: 'No', status: 'Offline' }
            ];
          } else {
            mockRows = [
              { id: 'imp_' + randomId(), name: 'Imported Record A', status: 'Active', date: new Date().toISOString().split('T')[0] },
              { id: 'imp_' + randomId(), name: 'Imported Record B', status: 'Active', date: new Date().toISOString().split('T')[0] }
            ];
          }

          setParsedRows(mockRows);
          toast.success('Spreadsheet data parsed!', { description: `Found ${mockRows.length} rows inside ${file.name}` });
          return 100;
        }
        return p + 25;
      });
    }, 200);
  };

  const confirmImport = () => {
    if (parsedRows.length === 0) return;
    setRecords((prev) => ({
      ...prev,
      [importTarget]: [...parsedRows, ...(prev[importTarget] || [])]
    }));
    logAdminAction(`Excel Import: Added ${parsedRows.length} records to ${importTarget} directory`);
    toast.success('Data imported successfully!', { description: `Added ${parsedRows.length} records to ${getTabTitle(importTarget)}.` });
    setIsImportOpen(false);
    setParsedRows([]);
    setUploadFileName('');
  };

  // --- NOTIFICATION BROADCAST COMPOSER ---
  const handleBroadcast = (e: React.FormEvent) => {
    e.preventDefault();
    if (!broadcastTitle || !broadcastBody) {
      toast.error('Title and body content are required for broadcasting.');
      return;
    }

    const newNotif = {
      id: `n_bc_${Date.now()}`,
      title: broadcastTitle,
      target: broadcastTarget,
      date: new Date().toISOString().split('T')[0],
      sentBy: activeRole,
      status: 'Delivered'
    };

    setRecords((prev) => ({
      ...prev,
      notifications: [newNotif, ...(prev.notifications || [])]
    }));

    logAdminAction(`Broadcasting: Sent ${broadcastChannel} alert to ${broadcastTarget}`);
    toast.success('System broadcast delivered successfully!', {
      description: `Transmitted via ${broadcastChannel} network.`
    });

    setIsBroadcastOpen(false);
    setBroadcastTitle('');
    setBroadcastBody('');
  };

  // --- MAP VEHICLE SIMULATOR ---
  const [mapProgress, setMapProgress] = useState(0);
  useEffect(() => {
    let animId: ReturnType<typeof setInterval>;
    if (activeTab === 'realTimeMap') {
      animId = setInterval(() => {
        setMapProgress((p) => (p + 1.5) % 100);
      }, 100);
    }
    return () => {
      if (animId) clearInterval(animId);
    };
  }, [activeTab]);

  const mapPointsList = [
    { x: 40, y: 220 },
    { x: 120, y: 220 },
    { x: 120, y: 100 },
    { x: 220, y: 100 },
    { x: 220, y: 180 },
    { x: 320, y: 180 },
    { x: 320, y: 80 },
    { x: 420, y: 80 }
  ];

  const getPositionAlongMapPath = (percent: number) => {
    const numSegments = mapPointsList.length - 1;
    const segmentSize = 100 / numSegments;
    const segmentIndex = Math.min(
      numSegments - 1,
      Math.floor(percent / segmentSize)
    );

    const startPt = mapPointsList[segmentIndex];
    const endPt = mapPointsList[segmentIndex + 1];
    const segmentPercent = (percent - segmentIndex * segmentSize) / segmentSize;

    const x = startPt.x + (endPt.x - startPt.x) * segmentPercent;
    const y = startPt.y + (endPt.y - startPt.y) * segmentPercent;

    const dx = endPt.x - startPt.x;
    const dy = endPt.y - startPt.y;
    const angle = Math.atan2(dy, dx) * (180 / Math.PI);

    return { x, y, angle };
  };

  // --- FILTERED DATA SET ---
  const visibleData = useMemo(() => {
    if (activeTab === 'overview' || activeTab === 'audit') return [];
    const rawData = records[activeTab] || [];

    return rawData.filter((item) => {
      // Search matching
      const matchesSearch = Object.values(item).some((val) =>
        String(val).toLowerCase().includes(searchQuery.toLowerCase())
      );

      // Status matching
      const matchesStatus =
        statusFilter === 'all' ||
        String(item.status || '').toLowerCase() === statusFilter.toLowerCase();

      return matchesSearch && matchesStatus;
    });
  }, [records, activeTab, searchQuery, statusFilter]);

  // --- RENDER DYNAMIC DATA TABLE ---
  const renderModuleTable = () => {
    if (visibleData.length === 0) {
      return (
        <div className="py-12 text-center text-muted-foreground bg-muted/5 rounded-3xl border border-dashed border-border/80">
          <Database className="h-8 w-8 mx-auto text-muted-foreground/60 mb-2" />
          <p className="font-semibold text-sm">No matching records found</p>
          <p className="text-xs text-muted-foreground/80 mt-1">Try clearing query strings or status filter options</p>
        </div>
      );
    }

    const firstRow = visibleData[0];
    const columns = Object.keys(firstRow).filter((col) => col !== 'id');

    const handleCheckboxChange = (id: string) => {
      setSelectedIds((prev) =>
        prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
      );
    };

    const handleSelectAll = () => {
      if (selectedIds.length === visibleData.length) {
        setSelectedIds([]);
      } else {
        setSelectedIds(visibleData.map((d) => d.id));
      }
    };

    return (
      <div className="overflow-x-auto rounded-2xl border border-border/60 bg-card">
        <table className="w-full text-sm text-left text-muted-foreground">
          <thead className="text-xs uppercase bg-muted/40 text-foreground font-bold border-b border-border/60">
            <tr>
              <th className="p-4 w-12 text-center">
                <input
                  type="checkbox"
                  checked={selectedIds.length === visibleData.length && visibleData.length > 0}
                  onChange={handleSelectAll}
                  className="rounded border-input text-primary focus:ring-primary h-4 w-4 cursor-pointer"
                />
              </th>
              {columns.map((col) => (
                <th key={col} className="px-6 py-4 font-bold tracking-wider capitalize">
                  {col.replace(/([A-Z])/g, ' $1')}
                </th>
              ))}
              <th className="px-6 py-4 text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            {visibleData.map((row) => (
              <tr key={row.id} className="border-b last:border-0 border-border/60 bg-background hover:bg-muted/10 transition-colors">
                <td className="p-4 text-center">
                  <input
                    type="checkbox"
                    checked={selectedIds.includes(row.id)}
                    onChange={() => handleCheckboxChange(row.id)}
                    className="rounded border-input text-primary focus:ring-primary h-4 w-4 cursor-pointer"
                  />
                </td>
                {columns.map((col) => {
                  const val = row[col];
                  return (
                    <td key={col} className="px-6 py-4 font-medium text-foreground whitespace-nowrap">
                      {col === 'status' ? (
                        <Badge className={`text-[10px] uppercase font-bold ${String(val).toLowerCase() === 'active' || String(val).toLowerCase() === 'verified' || String(val).toLowerCase() === 'open' || String(val).toLowerCase() === 'paid' || String(val).toLowerCase() === 'approved' || String(val).toLowerCase() === 'online' || String(val).toLowerCase() === 'published' || String(val).toLowerCase() === 'enabled'
                          ? 'bg-emerald-500/10 border border-emerald-500/20 text-emerald-600 hover:bg-emerald-500/20'
                          : String(val).toLowerCase() === 'pending' || String(val).toLowerCase() === 'pending review' || String(val).toLowerCase() === 'under review'
                            ? 'bg-amber-500/10 border border-amber-500/20 text-amber-600 hover:bg-amber-500/20 animate-pulse'
                            : 'bg-rose-500/10 border border-rose-500/20 text-rose-600 hover:bg-rose-500/20'
                          }`}>
                          {val}
                        </Badge>
                      ) : typeof val === 'number' ? (
                        `৳${val.toFixed(2)}`
                      ) : (
                        String(val)
                      )}
                    </td>
                  );
                })}
                <td className="px-6 py-4 text-center">
                  <div className="flex justify-center gap-1.5">
                    <Button
                      size="icon"
                      variant="ghost"
                      className="h-8 w-8 text-primary hover:bg-primary/10 rounded-lg"
                      onClick={() => toast.info(`Viewing details for: ${row.name || row.id}`)}
                    >
                      <Eye className="h-4 w-4" />
                    </Button>
                    <Button
                      size="icon"
                      variant="ghost"
                      className="h-8 w-8 text-destructive hover:bg-destructive/10 rounded-lg"
                      onClick={() => {
                        setRecords((prev) => ({
                          ...prev,
                          [activeTab]: prev[activeTab].filter((item) => item.id !== row.id)
                        }));
                        logAdminAction(`Deleted record ID: ${row.id} from module ${activeTab}`);
                        toast.error('Record deleted successfully');
                      }}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  };

  return (
    <div className="space-y-6 animate-fade-in text-foreground" dir={platformConfig.rtlLanguage ? 'rtl' : 'ltr'}>
      {/* Main Core Columns */}
      <div className="grid gap-6 lg:grid-cols-4">
        {/* Navigation Sidebar Switchers */}
        <Card className="lg:col-span-1 rounded-xl border border-slate-800 bg-[#0f172a] text-slate-100 overflow-hidden shadow-xl">
          <CardHeader className="bg-[#1e293b]/20 py-4 border-b border-slate-800/80">
            <CardTitle className="text-xs font-bold uppercase tracking-wider text-slate-400 flex items-center gap-2">
              <Layers className="h-4 w-4 text-primary" /> Management Modules
            </CardTitle>
          </CardHeader>
          <CardContent className="p-3 bg-[#0f172a]">
            <div className="space-y-1 max-h-[580px] overflow-y-auto pr-1">
              <button
                onClick={() => { setActiveTab('overview'); setSearchQuery(''); }}
                className={`w-full text-left rounded-lg px-3 py-2 text-xs font-semibold transition-all flex items-center gap-2 ${activeTab === 'overview' ? 'bg-primary text-white shadow-md' : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-100'
                  }`}
              >
                <Activity className="h-4 w-4" /> Overview & Charts
              </button>

              <div className="pt-2.5 pb-1 px-3 text-[10px] font-bold uppercase text-slate-500 tracking-wider border-t border-slate-850 mt-2">
                Core Directories
              </div>
              {[
                { key: 'users', label: 'Users Directory', icon: Users },
                { key: 'providers', label: 'Service Providers', icon: Shield },
                { key: 'businesses', label: 'Businesses Ledger', icon: Wallet },
                { key: 'restaurants', label: 'Restaurants List', icon: Compass },
                { key: 'drivers', label: 'Riders & Drivers', icon: Compass },
                { key: 'doctors', label: 'Doctors Directory', icon: Compass },
                { key: 'managers', label: 'Managers List', icon: Users }
              ].map((m) => (
                <button
                  key={m.key}
                  onClick={() => { setActiveTab(m.key); setSearchQuery(''); setSelectedIds([]); }}
                  className={`w-full text-left rounded-lg px-3 py-2 text-xs font-semibold transition-all flex items-center gap-2 ${activeTab === m.key ? 'bg-primary text-white shadow-md' : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-100'
                    }`}
                >
                  <m.icon className="h-4 w-4" /> {m.label}
                </button>
              ))}

              <div className="pt-2.5 pb-1 px-3 text-[10px] font-bold uppercase text-slate-500 tracking-wider border-t border-slate-850 mt-2">
                Operations & Wallets
              </div>
              {[
                { key: 'bookings', label: 'Bookings Logs', icon: Compass },
                { key: 'orders', label: 'Orders List', icon: Compass },
                { key: 'rides', label: 'Rides Tracker', icon: Car },
                { key: 'deliveries', label: 'Deliveries List', icon: Truck },
                { key: 'payments', label: 'Payments Ledger', icon: Wallet },
                { key: 'wallet', label: 'Wallets Register', icon: Wallet },
                { key: 'withdrawRequests', label: 'Withdraw Requests', icon: Wallet },
                { key: 'coupons', label: 'Coupons & Promos', icon: RefreshCw }
              ].map((m) => (
                <button
                  key={m.key}
                  onClick={() => { setActiveTab(m.key); setSearchQuery(''); setSelectedIds([]); }}
                  className={`w-full text-left rounded-lg px-3 py-2 text-xs font-semibold transition-all flex items-center gap-2 ${activeTab === m.key ? 'bg-primary text-white shadow-md' : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-100'
                    }`}
                >
                  <m.icon className="h-4 w-4" /> {m.label}
                </button>
              ))}

              <div className="pt-2.5 pb-1 px-3 text-[10px] font-bold uppercase text-slate-500 tracking-wider border-t border-slate-850 mt-2">
                Products & Services
              </div>
              {[
                { key: 'categories', label: 'Service Categories', icon: Layers },
                { key: 'services', label: 'Marketplace Services', icon: Layers },
                { key: 'products', label: 'Products & Items', icon: ShoppingBag },
                { key: 'menusOptions', label: 'Menus & Options', icon: Sliders }
              ].map((m) => (
                <button
                  key={m.key}
                  onClick={() => { setActiveTab(m.key); setSearchQuery(''); setSelectedIds([]); }}
                  className={`w-full text-left rounded-lg px-3 py-2 text-xs font-semibold transition-all flex items-center gap-2 ${activeTab === m.key ? 'bg-primary text-white shadow-md' : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-100'
                    }`}
                >
                  <m.icon className="h-4 w-4" /> {m.label}
                </button>
              ))}

              <div className="pt-2.5 pb-1 px-3 text-[10px] font-bold uppercase text-slate-500 tracking-wider border-t border-slate-850 mt-2">
                Support & CMS
              </div>
              {[
                { key: 'complaints', label: 'Complaints Register', icon: AlertTriangle },
                { key: 'supportTickets', label: 'Support Tickets', icon: HelpCircle },
                { key: 'reviews', label: 'Reviews & Ratings', icon: Star },
                { key: 'notifications', label: 'System Alerts', icon: Radio },
                { key: 'cms', label: 'CMS Page Settings', icon: Settings }
              ].map((m) => (
                <button
                  key={m.key}
                  onClick={() => { setActiveTab(m.key); setSearchQuery(''); setSelectedIds([]); }}
                  className={`w-full text-left rounded-lg px-3 py-2 text-xs font-semibold transition-all flex items-center gap-2 ${activeTab === m.key ? 'bg-primary text-white shadow-md' : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-100'
                    }`}
                >
                  <m.icon className="h-4 w-4" /> {m.label}
                </button>
              ))}

              <div className="pt-2.5 pb-1 px-3 text-[10px] font-bold uppercase text-slate-500 tracking-wider border-t border-slate-850 mt-2">
                Configuration & Map
              </div>
              {[
                { key: 'realTimeMap', label: 'Real-time Map Tracker', icon: Map },
                { key: 'commissionsFees', label: 'Commissions & Fees', icon: Percent },
                { key: 'paymentGateways', label: 'Payment Gateways', icon: CreditCard },
                { key: 'platformSettings', label: 'Platform & Modules', icon: Settings },
                { key: 'roleManagement', label: 'Role Settings', icon: Lock },
                { key: 'permissionManagement', label: 'Permissions Management', icon: Shield }
              ].map((m) => (
                <button
                  key={m.key}
                  onClick={() => { setActiveTab(m.key); setSearchQuery(''); setSelectedIds([]); }}
                  className={`w-full text-left rounded-lg px-3 py-2 text-xs font-semibold transition-all flex items-center gap-2 ${activeTab === m.key ? 'bg-primary text-white shadow-md' : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-100'
                    }`}
                >
                  <m.icon className="h-4 w-4" /> {m.label}
                </button>
              ))}

              <button
                onClick={() => { setActiveTab('audit'); setSearchQuery(''); }}
                className={`w-full text-left rounded-lg px-3 py-2 text-xs font-semibold transition-all flex items-center gap-2 mt-2 border-t border-slate-800 pt-3 ${activeTab === 'audit' ? 'bg-primary text-white shadow-md' : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-100'
                  }`}
              >
                <Activity className="h-4 w-4 text-rose-500" /> Admin Audit Logs
              </button>
            </div>
          </CardContent>
        </Card>

        {/* Content Pane display */}
        <div className="lg:col-span-3 space-y-6">
          {activeTab === 'overview' ? (
            // --- OVERVIEW & ANALYTICS WIDGETS ---
            <div className="space-y-6 animate-fade-in">
              <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
                {[
                  { label: 'Weekly Revenue', value: '৳124,580.00', icon: Wallet, color: 'text-emerald-500', bg: 'bg-emerald-500/10' },
                  { label: 'Completed Bookings', value: '8,421', icon: Compass, color: 'text-primary', bg: 'bg-primary/10' },
                  { label: 'Active Customers', value: '42,910', icon: Users, color: 'text-amber-500', bg: 'bg-amber-500/10' },
                  { label: 'Verified Partners', value: '5,241', icon: Shield, color: 'text-purple-500', bg: 'bg-purple-500/10' }
                ].map((stat) => (
                  <Card key={stat.label} className="border border-border/50 shadow-sm">
                    <CardContent className="p-4 flex items-center justify-between">
                      <div>
                        <p className="text-[10px] text-muted-foreground uppercase font-bold">{stat.label}</p>
                        <p className="text-lg font-black text-foreground mt-1.5">{stat.value}</p>
                      </div>
                      <div className={`h-9 w-9 rounded-xl flex items-center justify-center ${stat.bg}`}>
                        <stat.icon className={`h-5 w-5 ${stat.color}`} />
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>

              {/* Graphical Charts Layout */}
              <div className="grid gap-6 sm:grid-cols-2">
                <Card>
                  <CardHeader><CardTitle className="text-sm font-bold uppercase">Weekly Revenue & Orders Trend</CardTitle></CardHeader>
                  <CardContent className="h-64">
                    <ResponsiveContainer width="100%" height="100%">
                      <AreaChart data={revenueData}>
                        <defs>
                          <linearGradient id="colorRev" x1="0" y1="0" x2="0" y2="1">
                            <stop offset="5%" stopColor="#0ea5e9" stopOpacity={0.2} />
                            <stop offset="95%" stopColor="#0ea5e9" stopOpacity={0} />
                          </linearGradient>
                        </defs>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} />
                        <XAxis dataKey="date" />
                        <YAxis />
                        <Tooltip />
                        <Area type="monotone" dataKey="revenue" stroke="#0ea5e9" fillOpacity={1} fill="url(#colorRev)" />
                      </AreaChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader><CardTitle className="text-sm font-bold uppercase">User Registrations Growth</CardTitle></CardHeader>
                  <CardContent className="h-64">
                    <ResponsiveContainer width="100%" height="100%">
                      <BarChart data={signupGrowthData}>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} />
                        <XAxis dataKey="name" />
                        <YAxis />
                        <Tooltip />
                        <Legend />
                        <Bar dataKey="Customers" fill="#0ea5e9" radius={[4, 4, 0, 0]} />
                        <Bar dataKey="Providers" fill="#10b981" radius={[4, 4, 0, 0]} />
                      </BarChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader><CardTitle className="text-sm font-bold uppercase">Ratings by Service Category</CardTitle></CardHeader>
                  <CardContent className="h-64">
                    <ResponsiveContainer width="100%" height="100%">
                      <BarChart layout="vertical" data={providerPerformanceData}>
                        <CartesianGrid strokeDasharray="3 3" horizontal={false} />
                        <XAxis type="number" domain={[4, 5]} />
                        <YAxis type="category" dataKey="name" width={80} />
                        <Tooltip />
                        <Bar dataKey="rating" fill="#f59e0b" radius={[0, 4, 4, 0]} />
                      </BarChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader><CardTitle className="text-sm font-bold uppercase">Service Distribution Share</CardTitle></CardHeader>
                  <CardContent className="h-64 flex items-center justify-center">
                    <div className="w-full h-full relative">
                      <ResponsiveContainer width="100%" height="100%">
                        <PieChart>
                          <Pie
                            data={topServicesData}
                            cx="50%"
                            cy="50%"
                            innerRadius={50}
                            outerRadius={80}
                            paddingAngle={5}
                            dataKey="value"
                          >
                            {topServicesData.map((_, index) => (
                              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                            ))}
                          </Pie>
                          <Tooltip />
                        </PieChart>
                      </ResponsiveContainer>
                      {/* Custom legend overlays */}
                      <div className="absolute right-2 bottom-2 bg-background/95 border p-2.5 rounded-xl space-y-1.5 max-w-[150px] text-[10px] font-semibold">
                        {topServicesData.map((d, i) => (
                          <div key={d.name} className="flex items-center gap-1.5">
                            <span className="h-2 w-2 rounded-full shrink-0" style={{ backgroundColor: COLORS[i] }} />
                            <span className="truncate">{d.name}</span>
                          </div>
                        ))}
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </div>
            </div>
          ) : activeTab === 'audit' ? (
            // --- AUDIT LOGS ACTIVITY TIMELINE ---
            <Card className="rounded-xl border border-border/40 shadow-sm">
              <CardHeader className="flex flex-row items-center justify-between pb-4 border-b">
                <div>
                  <CardTitle className="text-base font-bold flex items-center gap-2">
                    <Activity className="h-5 w-5 text-rose-500" /> Platform Audit Trail Logs
                  </CardTitle>
                  <CardDescription>Live timeline of administrative actions logged across the super-app workspace</CardDescription>
                </div>
                <Button size="sm" variant="outline" className="rounded-xl flex items-center gap-1" onClick={() => {
                  setAuditLogs((prev) => [
                    { id: `l-${Date.now()}`, user: 'system@servenear.com', action: 'Cleaned database temp indices', time: 'Just now', ip: 'localhost' },
                    ...prev
                  ]);
                  toast.success('Audit log timeline refreshed!');
                }}>
                  <RefreshCw className="h-4.5 w-4.5" /> Force Refresh
                </Button>
              </CardHeader>
              <CardContent className="p-6">
                <div className="space-y-6 relative border-l border-border/60 pl-6 ml-2">
                  {auditLogs.map((log) => (
                    <div key={log.id} className="relative space-y-1">
                      {/* Timeline Node Dot */}
                      <span className="absolute -left-[31px] top-1 h-2.5 w-2.5 rounded-full bg-rose-500 ring-4 ring-rose-500/10" />
                      <div className="flex justify-between items-center text-xs">
                        <span className="font-bold text-primary">{log.user}</span>
                        <span className="text-[10px] text-muted-foreground">{log.time}</span>
                      </div>
                      <p className="text-sm font-semibold text-foreground leading-relaxed">{log.action}</p>
                      <p className="text-[10px] text-muted-foreground font-mono">Logged IP Address: {log.ip}</p>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          ) : activeTab === 'realTimeMap' ? (
            // --- REAL-TIME LIVE MAP TRACKING ---
            <Card className="rounded-xl border border-border/40 shadow-sm overflow-hidden bg-card">
              <CardHeader className="border-b">
                <CardTitle className="text-base font-extrabold flex items-center gap-2">
                  <Map className="h-5 w-5 text-primary animate-pulse" /> Live Map Operations Dashboard
                </CardTitle>
                <CardDescription>
                  Monitor drivers, delivery riders, and customer location pins in real-time. Mode: <span className="font-bold text-foreground">{platformConfig.mapProvider}</span>.
                </CardDescription>
              </CardHeader>
              <CardContent className="p-0">
                <div className="grid grid-cols-1 lg:grid-cols-3">
                  <div className="lg:col-span-2 relative h-[420px] bg-slate-950 overflow-hidden border-r border-border/40">
                    {/* Visual Animated Map */}
                    <svg className="absolute inset-0 w-full h-full" xmlns="http://www.w3.org/2000/svg">
                      <defs>
                        <pattern id="gridPattern" width="40" height="40" patternUnits="userSpaceOnUse">
                          <path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(255,255,255,0.03)" strokeWidth="1" />
                        </pattern>
                      </defs>
                      <rect width="100%" height="100%" fill="url(#gridPattern)" />

                      {platformConfig.mapProvider === 'Google Maps' ? (
                        <g opacity="0.15">
                          <circle cx="200" cy="180" r="140" fill="rgba(14,165,233,0.15)" stroke="rgba(14,165,233,0.3)" strokeWidth="2" />
                          <circle cx="360" cy="120" r="80" fill="rgba(16,185,129,0.1)" stroke="rgba(16,185,129,0.2)" strokeWidth="2" />
                        </g>
                      ) : (
                        <g opacity="0.3">
                          <path d="M 0 50 H 500 M 0 150 H 500 M 0 250 H 500 M 0 350 H 500" stroke="rgba(0,180,255,0.05)" strokeWidth="2" />
                          <path d="M 80 0 V 420 M 180 0 V 420 M 280 0 V 420 M 380 0 V 420" stroke="rgba(0,180,255,0.05)" strokeWidth="2" />
                        </g>
                      )}

                      {/* Streets Layout */}
                      <g stroke="rgba(255,255,255,0.08)" strokeWidth="20" strokeLinecap="round" strokeLinejoin="round" fill="none">
                        <path d="M 40 220 L 420 220" />
                        <path d="M 120 10 L 120 400" />
                        <path d="M 220 10 L 220 400" />
                        <path d="M 40 100 L 420 100" />
                        <path d="M 220 180 L 320 180" />
                        <path d="M 320 80 L 420 80" />
                      </g>
                      <g stroke={platformConfig.mapProvider === 'Google Maps' ? 'rgba(30,41,59,0.7)' : 'rgba(15,23,42,0.8)'} strokeWidth="16" strokeLinecap="round" strokeLinejoin="round" fill="none">
                        <path d="M 40 220 L 420 220" />
                        <path d="M 120 10 L 120 400" />
                        <path d="M 220 10 L 220 400" />
                        <path d="M 40 100 L 420 100" />
                        <path d="M 220 180 L 320 180" />
                        <path d="M 320 80 L 420 80" />
                      </g>

                      {/* Active Route */}
                      <path
                        d="M 40 220 L 120 220 L 120 100 L 220 100 L 220 180 L 320 180 L 320 80 L 420 80"
                        fill="none"
                        stroke="rgba(16,185,129,0.4)"
                        strokeWidth="5"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                      />
                      <path
                        d="M 40 220 L 120 220 L 120 100 L 220 100 L 220 180 L 320 180 L 320 80 L 420 80"
                        fill="none"
                        stroke="#10b981"
                        strokeWidth="1.5"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeDasharray="4 4"
                        className="animate-dash"
                      />

                      {/* Pins */}
                      <g transform="translate(420, 80)">
                        <circle r="15" fill="rgba(16,185,129,0.2)" className="animate-ping" />
                        <circle r="7" fill="#10b981" />
                      </g>
                      <g transform="translate(40, 220)">
                        <circle r="7" fill="#f59e0b" />
                      </g>

                      {/* Moving Car */}
                      {(() => {
                        const { x, y, angle } = getPositionAlongMapPath(mapProgress);
                        return (
                          <g transform={`translate(${x}, ${y}) rotate(${angle})`}>
                            <circle r="16" fill="rgba(14,165,233,0.3)" />
                            <circle r="11" fill="#0ea5e9" />
                          </g>
                        );
                      })()}

                      {/* Moving Rider */}
                      {(() => {
                        const { x, y, angle } = getPositionAlongMapPath((mapProgress + 50) % 100);
                        return (
                          <g transform={`translate(${x}, ${y}) rotate(${angle})`}>
                            <circle r="16" fill="rgba(168,85,247,0.3)" />
                            <circle r="11" fill="#a855f7" />
                          </g>
                        );
                      })()}
                    </svg>

                    {/* API Key watermark */}
                    <div className="absolute top-3 left-3 bg-slate-900/90 border border-white/10 text-white rounded-xl p-2 text-[10px] space-y-0.5 backdrop-blur-sm">
                      <p className="font-mono text-slate-400">GEO_API_KEY Linked:</p>
                      <p className="font-mono font-bold text-sky-400">{platformConfig.googleMapsApiKey.substring(0, 15)}...</p>
                    </div>

                    <div className="absolute bottom-3 left-3 right-3 bg-slate-900/90 border border-white/10 text-white rounded-xl p-3.5 backdrop-blur-sm">
                      <div className="flex justify-between items-center text-xs">
                        <span className="flex items-center gap-1.5"><Compass className="h-4 w-4 text-sky-400 animate-spin-slow" /> Map Vector Simulation</span>
                        <Badge className="bg-sky-500/10 text-sky-400 border-sky-500/20">{platformConfig.mapProvider}</Badge>
                      </div>
                    </div>
                  </div>

                  {/* Sidebar stats */}
                  <div className="p-5 space-y-4">
                    <h4 className="font-bold text-sm text-foreground uppercase tracking-wider">Active Operations Logs</h4>
                    <div className="space-y-3">
                      <div className="p-3 bg-muted/30 border rounded-xl space-y-1">
                        <div className="flex justify-between items-center text-xs">
                          <span className="font-semibold text-foreground">Trip #rd1 (Ride)</span>
                          <Badge className="bg-amber-500/10 text-amber-500 border-amber-500/20 text-[9px] font-bold">IN-PROGRESS</Badge>
                        </div>
                        <p className="text-[11px] text-muted-foreground">Driver Kashem · Banani to Gulshan</p>
                        <p className="text-[10px] font-mono text-primary">GPS: 23.794° N, 90.404° E</p>
                      </div>

                      <div className="p-3 bg-muted/30 border rounded-xl space-y-1">
                        <div className="flex justify-between items-center text-xs">
                          <span className="font-semibold text-foreground">Delivery #dl1 (Food)</span>
                          <Badge className="bg-amber-500/10 text-amber-500 border-amber-500/20 text-[9px] font-bold">DISPATCHED</Badge>
                        </div>
                        <p className="text-[11px] text-muted-foreground">Rider Rahman · Sultan's Dine to John Doe</p>
                        <p className="text-[10px] font-mono text-purple-500">GPS: 23.778° N, 90.399° E</p>
                      </div>

                      <div className="p-3 bg-muted/30 border rounded-xl space-y-1">
                        <div className="flex justify-between items-center text-xs">
                          <span className="font-semibold text-foreground">Trip #rd2 (Ride)</span>
                          <Badge className="bg-emerald-500/10 text-emerald-500 border-emerald-500/20 text-[9px] font-bold">COMPLETED</Badge>
                        </div>
                        <p className="text-[11px] text-muted-foreground">Rider Shakil · Dhanmondi to Uttara</p>
                      </div>
                    </div>

                    <Separator />

                    <div className="space-y-2">
                      <div className="flex justify-between text-xs font-medium">
                        <span className="text-muted-foreground">Active Drivers Online</span>
                        <span className="text-foreground font-bold">3 Drivers</span>
                      </div>
                      <div className="flex justify-between text-xs font-medium">
                        <span className="text-muted-foreground">Live Delivery ETA Avg</span>
                        <span className="text-foreground font-bold">18 Mins</span>
                      </div>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          ) : activeTab === 'commissionsFees' ? (
            // --- COMMISSIONS & FEES SETTINGS ---
            <Card className="rounded-xl border border-border/40 shadow-sm">
              <CardHeader className="border-b">
                <CardTitle className="text-base font-extrabold flex items-center gap-2">
                  <Percent className="h-5 w-5 text-primary" /> Commissions & Handling Fees Settings
                </CardTitle>
                <CardDescription>
                  Configure system global platform commission percentages, category overrides, and driver pay splits.
                </CardDescription>
              </CardHeader>
              <CardContent className="p-6 space-y-6">
                <div className="grid gap-6 md:grid-cols-2">
                  <div className="space-y-4">
                    <h4 className="font-bold text-xs uppercase text-muted-foreground tracking-wide">Category Split Sliders</h4>

                    <div className="space-y-2">
                      <div className="flex justify-between text-xs font-bold text-foreground">
                        <Label>Global Default Platform Fee</Label>
                        <span>{commissionConfig.globalFee}%</span>
                      </div>
                      <input
                        type="range"
                        min="0"
                        max="40"
                        value={commissionConfig.globalFee}
                        onChange={(e) => setCommissionConfig(prev => ({ ...prev, globalFee: parseInt(e.target.value) }))}
                        className="w-full h-2 bg-muted rounded-lg appearance-none cursor-pointer accent-primary"
                      />
                    </div>

                    <div className="space-y-2">
                      <div className="flex justify-between text-xs font-bold text-foreground">
                        <Label>Food & Dining Commission Override</Label>
                        <span>{commissionConfig.foodFee}%</span>
                      </div>
                      <input
                        type="range"
                        min="0"
                        max="40"
                        value={commissionConfig.foodFee}
                        onChange={(e) => setCommissionConfig(prev => ({ ...prev, foodFee: parseInt(e.target.value) }))}
                        className="w-full h-2 bg-muted rounded-lg appearance-none cursor-pointer accent-primary"
                      />
                    </div>

                    <div className="space-y-2">
                      <div className="flex justify-between text-xs font-bold text-foreground">
                        <Label>Ride Sharing Commission Override</Label>
                        <span>{commissionConfig.rideFee}%</span>
                      </div>
                      <input
                        type="range"
                        min="0"
                        max="40"
                        value={commissionConfig.rideFee}
                        onChange={(e) => setCommissionConfig(prev => ({ ...prev, rideFee: parseInt(e.target.value) }))}
                        className="w-full h-2 bg-muted rounded-lg appearance-none cursor-pointer accent-primary"
                      />
                    </div>

                    <div className="space-y-2">
                      <div className="flex justify-between text-xs font-bold text-foreground">
                        <Label>Home Services Commission Override</Label>
                        <span>{commissionConfig.homeFee}%</span>
                      </div>
                      <input
                        type="range"
                        min="0"
                        max="40"
                        value={commissionConfig.homeFee}
                        onChange={(e) => setCommissionConfig(prev => ({ ...prev, homeFee: parseInt(e.target.value) }))}
                        className="w-full h-2 bg-muted rounded-lg appearance-none cursor-pointer accent-primary"
                      />
                    </div>
                  </div>

                  <div className="space-y-4">
                    <h4 className="font-bold text-xs uppercase text-muted-foreground tracking-wide">Handling Charges & Splits</h4>

                    <div className="space-y-2">
                      <div className="flex justify-between text-xs font-bold text-foreground">
                        <Label>Driver Split Ratio (Pay Out)</Label>
                        <span>{commissionConfig.driverSplit}% (Driver) / {100 - commissionConfig.driverSplit}% (Platform)</span>
                      </div>
                      <input
                        type="range"
                        min="50"
                        max="95"
                        value={commissionConfig.driverSplit}
                        onChange={(e) => setCommissionConfig(prev => ({ ...prev, driverSplit: parseInt(e.target.value) }))}
                        className="w-full h-2 bg-muted rounded-lg appearance-none cursor-pointer accent-primary"
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="codFeeInput" className="text-xs font-bold">Cash on Delivery Handling Surcharge (৳)</Label>
                      <Input
                        id="codFeeInput"
                        type="number"
                        value={commissionConfig.codHandlingFee}
                        onChange={(e) => setCommissionConfig(prev => ({ ...prev, codHandlingFee: parseInt(e.target.value) }))}
                        className="rounded-xl h-10 border-border bg-background"
                      />
                    </div>

                    {/* Split Visualizer */}
                    <div className="mt-6 p-4 rounded-2xl bg-gradient-to-r from-primary/5 via-sky-600/5 to-transparent border border-primary/20 space-y-2">
                      <p className="text-xs font-bold text-primary flex items-center gap-1.5"><Sliders className="h-4 w-4" /> Live Split Simulation</p>
                      <p className="text-[11px] text-muted-foreground">For a booking amount of ৳1,000 paid to a Rider/Driver:</p>
                      <div className="flex gap-2 font-bold text-xs pt-1">
                        <div className="flex-1 p-2 bg-emerald-500/10 text-emerald-600 rounded-xl text-center border border-emerald-500/20">
                          ৳{((1000 * commissionConfig.driverSplit) / 100).toFixed(0)} to Driver
                        </div>
                        <div className="flex-1 p-2 bg-primary/10 text-primary rounded-xl text-center border border-primary/20">
                          ৳{((1000 * (100 - commissionConfig.driverSplit)) / 100).toFixed(0)} to Platform
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="flex justify-end pt-4 border-t">
                  <Button className="rounded-xl" onClick={() => {
                    logAdminAction('Updated platform fee commission configurations and driver payout ratios');
                    toast.success('Commission configurations saved successfully!');
                  }}>Save Configurations</Button>
                </div>
              </CardContent>
            </Card>
          ) : activeTab === 'paymentGateways' ? (
            // --- PAYMENT GATEWAY CONFIGS ---
            <Card className="rounded-xl border border-border/40 shadow-sm">
              <CardHeader className="border-b">
                <CardTitle className="text-base font-extrabold flex items-center gap-2">
                  <CreditCard className="h-5 w-5 text-primary" /> Active Payment Gateways Configuration
                </CardTitle>
                <CardDescription>
                  Toggle checkout gateways, customize Sandbox credentials, and adjust customer payment workflows.
                </CardDescription>
              </CardHeader>
              <CardContent className="p-6 space-y-6">
                <div className="grid gap-6 md:grid-cols-2">
                  {/* bKash configuration card */}
                  <Card className="p-4 border rounded-2xl bg-card space-y-4">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2.5">
                        <span className="h-2 w-2 rounded-full bg-pink-500" />
                        <span className="font-bold text-sm">bKash MFS Checkout</span>
                      </div>
                      <Switch
                        checked={gatewaysConfig.bKash}
                        onCheckedChange={(val) => setGatewaysConfig(p => ({ ...p, bKash: val }))}
                      />
                    </div>
                    <Separator />
                    <div className="space-y-3">
                      <div className="space-y-1">
                        <Label className="text-[10px] uppercase font-bold text-muted-foreground">App ID</Label>
                        <Input
                          value={gatewaySecrets.bKash.appId}
                          onChange={(e) => setGatewaySecrets(p => ({ ...p, bKash: { ...p.bKash, appId: e.target.value } }))}
                          disabled={!gatewaysConfig.bKash}
                          className="h-9 text-xs rounded-xl"
                        />
                      </div>
                      <div className="space-y-1">
                        <Label className="text-[10px] uppercase font-bold text-muted-foreground">App Secret</Label>
                        <Input
                          type="password"
                          value={gatewaySecrets.bKash.appSecret}
                          onChange={(e) => setGatewaySecrets(p => ({ ...p, bKash: { ...p.bKash, appSecret: e.target.value } }))}
                          disabled={!gatewaysConfig.bKash}
                          className="h-9 text-xs rounded-xl"
                        />
                      </div>
                      <div className="flex items-center justify-between text-xs pt-1">
                        <span className="text-muted-foreground">Gateway Mode</span>
                        <select
                          value={gatewaySecrets.bKash.mode}
                          onChange={(e) => setGatewaySecrets(p => ({ ...p, bKash: { ...p.bKash, mode: e.target.value } }))}
                          disabled={!gatewaysConfig.bKash}
                          className="border bg-background rounded-lg text-xs p-1"
                        >
                          <option value="Sandbox">Sandbox</option>
                          <option value="Production">Production</option>
                        </select>
                      </div>
                    </div>
                  </Card>

                  {/* Nagad configuration card */}
                  <Card className="p-4 border rounded-2xl bg-card space-y-4">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2.5">
                        <span className="h-2 w-2 rounded-full bg-orange-500" />
                        <span className="font-bold text-sm">Nagad Payment Gateway</span>
                      </div>
                      <Switch
                        checked={gatewaysConfig.Nagad}
                        onCheckedChange={(val) => setGatewaysConfig(p => ({ ...p, Nagad: val }))}
                      />
                    </div>
                    <Separator />
                    <div className="space-y-3">
                      <div className="space-y-1">
                        <Label className="text-[10px] uppercase font-bold text-muted-foreground">Merchant ID</Label>
                        <Input
                          value={gatewaySecrets.Nagad.merchantId}
                          onChange={(e) => setGatewaySecrets(p => ({ ...p, Nagad: { ...p.Nagad, merchantId: e.target.value } }))}
                          disabled={!gatewaysConfig.Nagad}
                          className="h-9 text-xs rounded-xl"
                        />
                      </div>
                      <div className="space-y-1">
                        <Label className="text-[10px] uppercase font-bold text-muted-foreground">Private Passphrase</Label>
                        <Input
                          type="password"
                          value={gatewaySecrets.Nagad.appSecret}
                          onChange={(e) => setGatewaySecrets(p => ({ ...p, Nagad: { ...p.Nagad, appSecret: e.target.value } }))}
                          disabled={!gatewaysConfig.Nagad}
                          className="h-9 text-xs rounded-xl"
                        />
                      </div>
                      <div className="flex items-center justify-between text-xs pt-1">
                        <span className="text-muted-foreground">Gateway Mode</span>
                        <select
                          value={gatewaySecrets.Nagad.mode}
                          onChange={(e) => setGatewaySecrets(p => ({ ...p, Nagad: { ...p.Nagad, mode: e.target.value } }))}
                          disabled={!gatewaysConfig.Nagad}
                          className="border bg-background rounded-lg text-xs p-1"
                        >
                          <option value="Sandbox">Sandbox</option>
                          <option value="Production">Production</option>
                        </select>
                      </div>
                    </div>
                  </Card>

                  {/* Stripe Card Configuration */}
                  <Card className="p-4 border rounded-2xl bg-card space-y-4">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2.5">
                        <span className="h-2 w-2 rounded-full bg-violet-600" />
                        <span className="font-bold text-sm">Stripe Credit Cards</span>
                      </div>
                      <Switch
                        checked={gatewaysConfig.Stripe}
                        onCheckedChange={(val) => setGatewaysConfig(p => ({ ...p, Stripe: val }))}
                      />
                    </div>
                    <Separator />
                    <div className="space-y-3">
                      <div className="space-y-1">
                        <Label className="text-[10px] uppercase font-bold text-muted-foreground">Publishable Key</Label>
                        <Input
                          value={gatewaySecrets.Stripe.publishableKey}
                          onChange={(e) => setGatewaySecrets(p => ({ ...p, Stripe: { ...p.Stripe, publishableKey: e.target.value } }))}
                          disabled={!gatewaysConfig.Stripe}
                          className="h-9 text-xs rounded-xl"
                        />
                      </div>
                      <div className="space-y-1">
                        <Label className="text-[10px] uppercase font-bold text-muted-foreground">Secret Key</Label>
                        <Input
                          type="password"
                          value={gatewaySecrets.Stripe.secretKey}
                          onChange={(e) => setGatewaySecrets(p => ({ ...p, Stripe: { ...p.Stripe, secretKey: e.target.value } }))}
                          disabled={!gatewaysConfig.Stripe}
                          className="h-9 text-xs rounded-xl"
                        />
                      </div>
                    </div>
                  </Card>

                  {/* COD & Pickup Configuration */}
                  <Card className="p-4 border rounded-2xl bg-card space-y-4 flex flex-col justify-between">
                    <div className="space-y-4">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2.5">
                          <span className="h-2 w-2 rounded-full bg-emerald-500" />
                          <span className="font-bold text-sm">Cash on Delivery (COD)</span>
                        </div>
                        <Switch
                          checked={gatewaysConfig.cod}
                          onCheckedChange={(val) => setGatewaysConfig(p => ({ ...p, cod: val }))}
                        />
                      </div>
                      <Separator />
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2.5">
                          <span className="h-2 w-2 rounded-full bg-blue-500" />
                          <span className="font-bold text-sm">Payment on Pickup</span>
                        </div>
                        <Switch
                          checked={gatewaysConfig.paymentOnPickup}
                          onCheckedChange={(val) => setGatewaysConfig(p => ({ ...p, paymentOnPickup: val }))}
                        />
                      </div>
                      <Separator />
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2.5">
                          <span className="h-2 w-2 rounded-full bg-amber-500" />
                          <span className="font-bold text-sm">Offline Bank Transfer</span>
                        </div>
                        <Switch
                          checked={gatewaysConfig.offline}
                          onCheckedChange={(val) => setGatewaysConfig(p => ({ ...p, offline: val }))}
                        />
                      </div>
                    </div>
                  </Card>
                </div>

                <div className="flex justify-end pt-4 border-t">
                  <Button className="rounded-xl" onClick={() => {
                    logAdminAction('Updated merchant checkout credentials for bKash, Nagad, Stripe gateways');
                    toast.success('Gateway settings updated successfully!');
                  }}>Save Gateway Keys</Button>
                </div>
              </CardContent>
            </Card>
          ) : activeTab === 'platformSettings' ? (
            // --- PLATFORM MODULE SWITCHES ---
            <Card className="rounded-xl border border-border/40 shadow-sm">
              <CardHeader className="border-b">
                <CardTitle className="text-base font-extrabold flex items-center gap-2">
                  <Settings className="h-5 w-5 text-primary" /> Platform Settings & SDK Modules
                </CardTitle>
                <CardDescription>
                  Configure core ecosystem modules, maps routing integrations, push providers, and accessibility.
                </CardDescription>
              </CardHeader>
              <CardContent className="p-6 space-y-6">
                <div className="grid gap-6 md:grid-cols-2">
                  <div className="space-y-4">
                    <h4 className="font-bold text-xs uppercase text-muted-foreground tracking-wide">Core Modules</h4>

                    <div className="flex items-center justify-between">
                      <div>
                        <Label className="font-bold text-xs">Multi-Vendor Marketplace Support</Label>
                        <p className="text-[10px] text-muted-foreground">Manage nested restaurants and store layouts</p>
                      </div>
                      <Switch
                        checked={platformConfig.multiVendor}
                        onCheckedChange={(val) => setPlatformConfig(p => ({ ...p, multiVendor: val }))}
                      />
                    </div>

                    <div className="flex items-center justify-between">
                      <div>
                        <Label className="font-bold text-xs">Multi-Service Business Modules</Label>
                        <p className="text-[10px] text-muted-foreground">Ecosystem includes food, doctor, ambulance, courier, home nurse</p>
                      </div>
                      <Switch
                        checked={platformConfig.multiService}
                        onCheckedChange={(val) => setPlatformConfig(p => ({ ...p, multiService: val }))}
                      />
                    </div>

                    <div className="flex items-center justify-between">
                      <div>
                        <Label className="font-bold text-xs">Automatic Driver Auto-Assignment</Label>
                        <p className="text-[10px] text-muted-foreground">Riders matching by nearest GPS algorithm</p>
                      </div>
                      <Switch
                        checked={platformConfig.autoAssignment}
                        onCheckedChange={(val) => setPlatformConfig(p => ({ ...p, autoAssignment: val }))}
                      />
                    </div>

                    <div className="flex items-center justify-between">
                      <div>
                        <Label className="font-bold text-xs">Awesome Swiping / Sliding Animations</Label>
                        <p className="text-[10px] text-muted-foreground">Apply parallax and swiping to mobile apps</p>
                      </div>
                      <Switch
                        checked={platformConfig.awesomeAnimations}
                        onCheckedChange={(val) => setPlatformConfig(p => ({ ...p, awesomeAnimations: val }))}
                      />
                    </div>
                  </div>

                  <div className="space-y-4">
                    <h4 className="font-bold text-xs uppercase text-muted-foreground tracking-wide">Maps, Push & Translations</h4>

                    <div className="space-y-1.5">
                      <Label className="text-xs font-bold">Map Routing Provider</Label>
                      <select
                        value={platformConfig.mapProvider}
                        onChange={(e) => setPlatformConfig(p => ({ ...p, mapProvider: e.target.value }))}
                        className="w-full h-10 text-xs border rounded-xl bg-background px-3"
                      >
                        <option value="Google Maps">Google Maps API (Satellite + Streets)</option>
                        <option value="Mapbox">Mapbox SDK (Blueprint Dark Layout)</option>
                      </select>
                    </div>

                    <div className="space-y-1.5">
                      <Label className="text-xs font-bold" htmlFor="googleApiKeyInput">Google Maps API Key</Label>
                      <Input
                        id="googleApiKeyInput"
                        value={platformConfig.googleMapsApiKey}
                        onChange={(e) => setPlatformConfig(p => ({ ...p, googleMapsApiKey: e.target.value }))}
                        className="h-9 text-xs rounded-xl"
                      />
                    </div>

                    <div className="flex items-center justify-between pt-1">
                      <div>
                        <Label className="font-bold text-xs">RTL Language Direction Support</Label>
                        <p className="text-[10px] text-muted-foreground">Mirror user dashboard layouts for Arabic/Bangla translation</p>
                      </div>
                      <Switch
                        checked={platformConfig.rtlLanguage}
                        onCheckedChange={(val) => {
                          setPlatformConfig(p => ({ ...p, rtlLanguage: val }));
                          toast.info(`Direction set to ${val ? 'RTL' : 'LTR'}`);
                        }}
                      />
                    </div>

                    <div className="flex items-center justify-between pt-1">
                      <div>
                        <Label className="font-bold text-xs">System Global Dark Mode</Label>
                        <p className="text-[10px] text-muted-foreground">Apply dark theme across admin client</p>
                      </div>
                      <Switch
                        checked={platformConfig.darkMode}
                        onCheckedChange={(val) => setPlatformConfig(p => ({ ...p, darkMode: val }))}
                      />
                    </div>
                  </div>
                </div>

                <div className="flex justify-end pt-4 border-t">
                  <Button className="rounded-xl" onClick={() => {
                    logAdminAction('Updated global platforms setting layout configuration values');
                    toast.success('Platform configurations saved!');
                  }}>Save Configurations</Button>
                </div>
              </CardContent>
            </Card>
          ) : (
            // --- DYNAMIC DATA MANAGEMENT PANELS ---
            <div className="space-y-6">
              <Card className="rounded-xl border border-border/40 shadow-sm">
                <CardHeader className="pb-4 border-b border-border/40">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div>
                      <CardTitle className="text-base font-extrabold flex items-center gap-2">
                        <Database className="h-5 w-5 text-primary" /> {getTabTitle(activeTab)}
                      </CardTitle>
                      <CardDescription>Search records, toggle statuses, and export ledger contents</CardDescription>
                    </div>
                    <div className="flex gap-2 flex-wrap">
                      <Button onClick={() => { setImportTarget(activeTab); setIsImportOpen(true); }} size="sm" variant="outline" className="rounded-xl text-xs flex items-center gap-1">
                        <Upload className="h-4 w-4 text-sky-500" /> Import Spreadsheet
                      </Button>
                      <Button onClick={() => handleExport('excel')} size="sm" variant="outline" className="rounded-xl text-xs flex items-center gap-1">
                        <Download className="h-4 w-4 text-emerald-600" /> Export Excel
                      </Button>
                      <Button onClick={() => handleExport('pdf')} size="sm" variant="outline" className="rounded-xl text-xs flex items-center gap-1">
                        <Download className="h-4 w-4 text-rose-500" /> Export PDF
                      </Button>
                      {activeTab === 'notifications' ? (
                        <Button size="sm" className="rounded-xl text-xs flex items-center gap-1 bg-rose-600 hover:bg-rose-700 text-white hover:text-white" onClick={() => setIsBroadcastOpen(true)}>
                          <Send className="h-4 w-4" /> Broadcast Alerts
                        </Button>
                      ) : (
                        <Button size="sm" className="rounded-xl text-xs flex items-center gap-1" onClick={() => {
                          toast.success('Add dialog loaded', { description: `Creating new record in ${activeTab}` });
                        }}>
                          <PlusCircle className="h-4 w-4" /> Add Record
                        </Button>
                      )}
                    </div>
                  </div>
                </CardHeader>
                <CardContent className="p-6 space-y-4">
                  {/* Controls Filters Row */}
                  <div className="flex flex-col sm:flex-row gap-3 items-center justify-between">
                    <div className="relative w-full sm:max-w-sm">
                      <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                      <Input
                        placeholder="Search active records..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="pl-10 h-10 rounded-xl text-xs border border-border/60 bg-muted/10 focus:bg-background"
                      />
                    </div>
                    <div className="flex gap-2 w-full sm:w-auto shrink-0 justify-end">
                      <Button size="sm" variant="outline" onClick={() => setShowAdvancedFilters(!showAdvancedFilters)} className="rounded-xl h-10 flex items-center gap-1.5">
                        <Filter className="h-4 w-4" /> Filters
                      </Button>
                      <select
                        value={statusFilter}
                        onChange={(e) => setStatusFilter(e.target.value)}
                        className="h-10 text-xs font-semibold rounded-xl border border-input bg-background px-3"
                      >
                        <option value="all">All Statuses</option>
                        <option value="active">Active / Verified / Open / Listed / Enabled</option>
                        <option value="pending">Pending Review</option>
                        <option value="suspended">Suspended / Inactive / Closed</option>
                      </select>
                    </div>
                  </div>

                  {/* Bulk Actions Panel Overlay */}
                  {selectedIds.length > 0 && (
                    <div className="p-3 border border-primary/20 bg-primary/5 rounded-2xl flex items-center justify-between gap-4 animate-slide-up">
                      <span className="text-xs font-semibold text-primary">
                        Selected <span className="font-extrabold">{selectedIds.length}</span> records
                      </span>
                      <div className="flex gap-2">
                        <Button size="sm" variant="ghost" className="text-emerald-600 hover:bg-emerald-500/10 rounded-xl text-xs flex items-center gap-1" onClick={() => handleBulkAction('approve')}>
                          <Check className="h-4 w-4" /> Approve Selected
                        </Button>
                        <Button size="sm" variant="ghost" className="text-amber-600 hover:bg-amber-500/10 rounded-xl text-xs flex items-center gap-1" onClick={() => handleBulkAction('block')}>
                          <X className="h-4 w-4" /> Suspend Selected
                        </Button>
                        <Button size="sm" variant="ghost" className="text-destructive hover:bg-destructive/10 rounded-xl text-xs flex items-center gap-1" onClick={() => handleBulkAction('delete')}>
                          <Trash2 className="h-4 w-4" /> Delete Selected
                        </Button>
                      </div>
                    </div>
                  )}

                  {/* Dynamic Table component */}
                  {renderModuleTable()}
                </CardContent>
              </Card>
            </div>
          )}
        </div>
      </div>

      {/* SPREADSHEET IMPORT DIALOG */}
      <Dialog open={isImportOpen} onOpenChange={setIsImportOpen}>
        <DialogContent className="max-w-md rounded-2xl">
          <DialogHeader>
            <DialogTitle>Import Data Spreadsheet</DialogTitle>
            <DialogDescription>
              Upload Excel (.xlsx) or CSV data directly into the <span className="font-bold text-foreground capitalize">{importTarget}</span> directory.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-2">
            <div className="space-y-1">
              <Label className="text-xs font-bold text-muted-foreground">Target Directory Table</Label>
              <select
                value={importTarget}
                onChange={(e) => setImportTarget(e.target.value)}
                className="w-full h-10 border text-xs bg-background rounded-xl px-3"
              >
                <option value="users">Users Directory</option>
                <option value="providers">Service Providers</option>
                <option value="products">Products & Items</option>
                <option value="drivers">Riders & Drivers</option>
                <option value="businesses">Businesses Ledger</option>
              </select>
            </div>

            <div className="border border-dashed border-border/80 rounded-2xl p-6 text-center bg-muted/5 flex flex-col items-center justify-center space-y-2">
              <Upload className="h-8 w-8 text-sky-500" />
              <p className="text-xs font-semibold">Drag & Drop files or click to browse</p>
              <p className="text-[10px] text-muted-foreground">Supports CSV, XLS, XLSX spreadsheets up to 10MB</p>
              <input
                type="file"
                accept=".csv,.xlsx,.xls"
                onChange={(e) => {
                  const file = e.target.files?.[0];
                  if (file) handleSpreadsheetImport(file);
                }}
                className="hidden"
                id="excelFileInput"
              />
              <Button size="sm" variant="outline" className="rounded-xl text-xs mt-2" onClick={() => document.getElementById('excelFileInput')?.click()}>
                Choose File
              </Button>
            </div>

            {uploadFileName && (
              <div className="p-3 border bg-muted/10 rounded-xl space-y-2">
                <div className="flex justify-between text-xs font-semibold">
                  <span className="truncate max-w-[200px]">{uploadFileName}</span>
                  <span className="text-primary">{parseProgress}%</span>
                </div>
                {isParsing && (
                  <div className="w-full bg-muted h-1 rounded-full overflow-hidden">
                    <div className="bg-primary h-full transition-all" style={{ width: `${parseProgress}%` }} />
                  </div>
                )}
                {parsedRows.length > 0 && (
                  <div className="text-[10px] text-emerald-600 font-semibold flex items-center gap-1">
                    <Check className="h-3.5 w-3.5" /> Parsed {parsedRows.length} valid rows from spreadsheet file.
                  </div>
                )}
              </div>
            )}
          </div>
          <DialogFooter className="flex gap-2">
            <Button variant="outline" className="rounded-xl text-xs" onClick={() => { setIsImportOpen(false); setParsedRows([]); setUploadFileName(''); }}>
              Cancel
            </Button>
            <Button className="rounded-xl text-xs" onClick={confirmImport} disabled={parsedRows.length === 0}>
              Confirm Import ({parsedRows.length} rows)
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* BROADCAST ALERTS COMPOSER DIALOG */}
      <Dialog open={isBroadcastOpen} onOpenChange={setIsBroadcastOpen}>
        <DialogContent className="max-w-md rounded-2xl">
          <DialogHeader>
            <DialogTitle>Compose Broadcast Notification</DialogTitle>
            <DialogDescription>
              Broadcast alerts to customers, service providers, or drivers immediately.
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleBroadcast} className="space-y-4 pt-2">
            <div className="space-y-1">
              <Label className="text-xs font-bold text-muted-foreground">Broadcast Platform Channel</Label>
              <select
                value={broadcastChannel}
                onChange={(e) => setBroadcastChannel(e.target.value)}
                className="w-full h-10 border text-xs bg-background rounded-xl px-3"
              >
                <option value="FCM Push Notification">Firebase Cloud Messaging (FCM) Push</option>
                <option value="WhatsApp Broadcast">WhatsApp Broadcaster Engine</option>
                <option value="SMS Text Broadcast">SMS Network Broadcast</option>
                <option value="Direct Email">SMTP Server Broadcast Email</option>
              </select>
            </div>

            <div className="space-y-1">
              <Label className="text-xs font-bold text-muted-foreground">Target Audience</Label>
              <select
                value={broadcastTarget}
                onChange={(e) => setBroadcastTarget(e.target.value)}
                className="w-full h-10 border text-xs bg-background rounded-xl px-3"
              >
                <option value="All Users">All Active Users</option>
                <option value="Customers Only">Customers Only</option>
                <option value="Providers Only">Service Providers Only</option>
                <option value="Drivers Only">Drivers & Riders Only</option>
              </select>
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="bcTitle" className="text-xs font-bold text-muted-foreground">Notification Title</Label>
              <Input
                id="bcTitle"
                placeholder="e.g. Weekend Special Food Sale is Live!"
                value={broadcastTitle}
                onChange={(e) => setBroadcastTitle(e.target.value)}
                className="rounded-xl h-10 text-xs border-border bg-background"
                required
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="bcBody" className="text-xs font-bold text-muted-foreground">Notification Body</Label>
              <textarea
                id="bcBody"
                placeholder="Compose announcement body content..."
                value={broadcastBody}
                onChange={(e) => setBroadcastBody(e.target.value)}
                rows={3}
                className="w-full border rounded-xl text-xs bg-background p-3 focus:outline-none focus:ring-1 focus:ring-primary"
                required
              />
            </div>

            <DialogFooter className="flex gap-2">
              <Button type="button" variant="outline" className="rounded-xl text-xs" onClick={() => setIsBroadcastOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" className="rounded-xl text-xs bg-rose-600 hover:bg-rose-700 text-white hover:text-white">
                Transmit Broadcast
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
