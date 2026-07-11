import { useState } from 'react';
import { Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Package, Wallet, Bell, Star, ArrowRight,
  Users, Check, X, Trash2, MapPin, PlusCircle, Clock
} from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Progress } from '@/components/ui/progress';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { useAuthStore } from '@/store/auth-store';
import { useNotificationStore } from '@/store/notification-store';
import { useBookingStore } from '@/store/booking-store';
import { cn } from '@/lib/utils';
import { toast } from 'sonner';
import { Separator } from '@/components/ui/separator';
import { EnterpriseAdminDashboard } from '@/pages/admin/enterprise-admin-dashboard';

const statusColors: Record<string, string> = {
  completed: 'bg-success text-success-foreground',
  'in-progress': 'bg-primary text-primary-foreground',
  confirmed: 'bg-sky-500 text-white',
  pending: 'bg-warning text-warning-foreground',
  cancelled: 'bg-destructive text-destructive-foreground',
};

export function DashboardHome() {
  const { user } = useAuthStore();

  if (!user) return null;

  switch (user.role) {
    case 'admin':
      return <AdminDashboard />;
    case 'provider':
      return <ProviderDashboard />;
    case 'business':
      return <BusinessDashboard />;
    case 'customer':
    default:
      return <CustomerDashboard />;
  }
}

// ----------------------------------------------------
// 1. CUSTOMER DASHBOARD
// ----------------------------------------------------
function CustomerDashboard() {
  const { user } = useAuthStore();
  const { unreadCount } = useNotificationStore();
  const { bookings } = useBookingStore();

  const stats = [
    { label: 'Total Orders', value: user?.totalOrders ?? 0, icon: Package, color: 'text-primary', bg: 'bg-primary/10' },
    { label: 'Wallet Balance', value: `$${user?.walletBalance.toFixed(2) ?? '0'}`, icon: Wallet, color: 'text-success', bg: 'bg-success/10' },
    { label: 'Notifications', value: unreadCount, icon: Bell, color: 'text-warning', bg: 'bg-warning/10' },
    { label: 'Member Level', value: user?.memberLevel ?? 'bronze', icon: Star, color: 'text-amber-500', bg: 'bg-amber-500/10' },
  ];

  const recentBookings = bookings.slice(0, 3);

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Welcome back, {user?.name?.split(' ')[0]}!</h1>
        <p className="text-muted-foreground mt-1">Here's what's happening with your account</p>
      </div>

      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        {stats.map((stat, i) => (
          <motion.div
            key={stat.label}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.05 }}
          >
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-xs text-muted-foreground">{stat.label}</p>
                    <p className="text-2xl font-bold mt-1 capitalize">{stat.value}</p>
                  </div>
                  <div className={cn('flex h-10 w-10 items-center justify-center rounded-lg', stat.bg)}>
                    <stat.icon className={cn('h-5 w-5', stat.color)} />
                  </div>
                </div>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <CardHeader className="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Recent Bookings</CardTitle>
              <CardDescription>Track and manage your active or past orders</CardDescription>
            </div>
            <Button variant="ghost" size="sm" asChild>
              <Link to="/dashboard/bookings">View All <ArrowRight className="ml-1 h-3 w-3" /></Link>
            </Button>
          </CardHeader>
          <CardContent className="space-y-3">
            {recentBookings.map((booking) => (
              <Link key={booking.id} to="/dashboard/bookings" className="flex items-center gap-3 rounded-lg border p-3 hover:bg-muted/50 transition-colors">
                <Avatar className="h-10 w-10">
                  <AvatarImage src={booking.providerAvatar} alt={booking.providerName} />
                  <AvatarFallback>{booking.providerName.charAt(0)}</AvatarFallback>
                </Avatar>
                <div className="flex-1 min-w-0">
                  <p className="font-medium text-sm truncate">{booking.serviceName}</p>
                  <p className="text-xs text-muted-foreground">{booking.date} · {booking.time}</p>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-sm">${booking.amount}</p>
                  <Badge className={cn('text-[10px] capitalize border-none', statusColors[booking.status])}>
                    {booking.status.replace('-', ' ')}
                  </Badge>
                </div>
              </Link>
            ))}
            {recentBookings.length === 0 && (
              <p className="text-center text-muted-foreground py-6 text-sm">No bookings found</p>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Membership Status</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center gap-3">
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-amber-500/10 text-amber-500">
                <Star className="h-6 w-6 fill-current" />
              </div>
              <div>
                <p className="font-semibold capitalize">{user?.memberLevel} Member</p>
                <p className="text-xs text-muted-foreground">{user?.totalOrders} orders completed</p>
              </div>
            </div>
            <div>
              <div className="flex justify-between text-xs mb-1">
                <span className="text-muted-foreground">Progress to Platinum</span>
                <span className="font-semibold">87%</span>
              </div>
              <Progress value={87} className="h-2" />
              <p className="text-xs text-muted-foreground mt-2">13 more orders to reach Platinum</p>
            </div>
            <Button variant="outline" className="w-full" size="sm">View Benefits</Button>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

// ----------------------------------------------------
// 2. PROVIDER DASHBOARD
// ----------------------------------------------------
function ProviderDashboard() {
  const { user } = useAuthStore();
  const [online, setOnline] = useState(true);
  const [requests, setRequests] = useState([
    { id: 'req1', customer: 'Imran Khan', service: 'Deep Home Cleaning', address: 'House 5, Road 11, Banani, Dhaka', price: 45, type: 'service', avatar: 'https://images.pexels.com/photos/1043471/pexels-photo-1043471.jpeg?auto=compress&cs=tinysrgb&w=80' },
    { id: 'req2', customer: 'Nusrat Jahan', service: 'At-Home Salon & Spa', address: 'Apartment 4B, Gulshan-2, Dhaka', price: 55, type: 'service', avatar: 'https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&w=80' }
  ]);

  const stats = [
    { label: 'Today\'s Earnings', value: '$135.00', icon: Wallet, color: 'text-success', bg: 'bg-success/10' },
    { label: 'Completed Jobs', value: '1,240', icon: Package, color: 'text-primary', bg: 'bg-primary/10' },
    { label: 'Active Requests', value: requests.length, icon: Bell, color: 'text-warning', bg: 'bg-warning/10' },
    { label: 'Rating', value: '4.92 / 5.0', icon: Star, color: 'text-amber-500', bg: 'bg-amber-500/10' },
  ];

  const handleOnlineToggle = (val: boolean) => {
    setOnline(val);
    toast.success(val ? 'You are now Online and accepting jobs!' : 'You are now Offline.');
  };

  const handleJobAction = (id: string, action: 'accept' | 'reject') => {
    setRequests((reqs) => reqs.filter((r) => r.id !== id));
    if (action === 'accept') {
      toast.success('Job request accepted! Navigation route mapped.');
    } else {
      toast.info('Job request declined.');
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Provider Hub: {user?.name}</h1>
          <p className="text-muted-foreground mt-1">Manage jobs, earnings and your availability</p>
        </div>
        <Card className="p-3 flex items-center justify-between gap-4 w-fit border-2 border-primary/20">
          <div className="flex items-center gap-2">
            <span className={cn('h-3.5 w-3.5 rounded-full animate-pulse', online ? 'bg-emerald-500' : 'bg-slate-400')} />
            <span className="text-sm font-semibold select-none">{online ? 'Online & Available' : 'Offline'}</span>
          </div>
          <Switch checked={online} onCheckedChange={handleOnlineToggle} />
        </Card>
      </div>

      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        {stats.map((stat) => (
          <Card key={stat.label}>
            <CardContent className="p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs text-muted-foreground">{stat.label}</p>
                  <p className="text-2xl font-bold mt-1">{stat.value}</p>
                </div>
                <div className={cn('flex h-10 w-10 items-center justify-center rounded-lg', stat.bg)}>
                  <stat.icon className={cn('h-5 w-5', stat.color)} />
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle>Incoming Work Requests</CardTitle>
            <CardDescription>Review and accept tasks near your current location</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <AnimatePresence>
              {online && requests.map((req) => (
                <motion.div
                  key={req.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: 20 }}
                  className="rounded-xl border border-border p-4 bg-muted/30 flex flex-col md:flex-row md:items-center justify-between gap-4"
                >
                  <div className="flex items-start gap-3">
                    <Avatar className="h-11 w-11 border border-border">
                      <AvatarImage src={req.avatar} alt={req.customer} />
                      <AvatarFallback>{req.customer.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <div className="space-y-1">
                      <p className="font-semibold text-sm">{req.service}</p>
                      <p className="text-xs text-muted-foreground flex items-center gap-1">
                        Customer: <span className="text-foreground font-medium">{req.customer}</span>
                      </p>
                      <p className="text-xs text-muted-foreground flex items-center gap-1">
                        <MapPin className="h-3 w-3 shrink-0 text-primary" /> {req.address}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center justify-between md:flex-col md:items-end gap-3 shrink-0">
                    <div className="text-lg font-bold text-success">${req.price}</div>
                    <div className="flex gap-2">
                      <Button size="sm" variant="destructive" onClick={() => handleJobAction(req.id, 'reject')} className="rounded-xl px-4">
                        <X className="h-4 w-4 mr-1" /> Decline
                      </Button>
                      <Button size="sm" className="bg-success text-success-foreground hover:bg-success/90 rounded-xl px-4" onClick={() => handleJobAction(req.id, 'accept')}>
                        <Check className="h-4 w-4 mr-1" /> Accept
                      </Button>
                    </div>
                  </div>
                </motion.div>
              ))}
            </AnimatePresence>
            {(!online || requests.length === 0) && (
              <div className="text-center py-8 text-muted-foreground text-sm">
                {!online ? 'Go online to receive incoming requests.' : 'No new requests available right now.'}
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Daily Earnings Summary</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <div className="flex justify-between text-xs">
                <span>AC Repair visit</span>
                <span className="font-semibold text-success">+$25.00</span>
              </div>
              <div className="flex justify-between text-xs">
                <span>Deep Cleaning</span>
                <span className="font-semibold text-success">+$35.00</span>
              </div>
              <div className="flex justify-between text-xs">
                <span>Home Salon Visit</span>
                <span className="font-semibold text-success">+$75.00</span>
              </div>
            </div>
            <Separator />
            <div className="flex justify-between text-sm font-semibold">
              <span>Gross Total</span>
              <span className="text-success">$135.00</span>
            </div>
            <Button className="w-full rounded-xl" size="sm" variant="outline">Withdraw Earnings</Button>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

// ----------------------------------------------------
// 3. BUSINESS DASHBOARD (RESTAURANT OWNER)
// ----------------------------------------------------
function BusinessDashboard() {
  const [menuItems, setMenuItems] = useState([
    { id: 'm1', name: 'Special Kacchi', description: 'Traditional mutton kacchi biryani with saffron rice', price: 12.99, image: 'https://images.pexels.com/photos/12737656/pexels-photo-12737656.jpeg?auto=compress&cs=tinysrgb&w=200', category: 'Biryani' },
    { id: 'm2', name: 'Chicken Biryani', description: 'Aromatic basmati rice with tender chicken', price: 9.99, image: 'https://images.pexels.com/photos/7373723/pexels-photo-7373723.jpeg?auto=compress&cs=tinysrgb&w=200', category: 'Biryani' },
    { id: 'm3', name: 'Shami Kebab', description: 'Spiced minced meat patties, 4 pieces', price: 5.99, image: 'https://images.pexels.com/photos/2233717/pexels-photo-2233717.jpeg?auto=compress&cs=tinysrgb&w=200', category: 'Starters' }
  ]);

  const [dialogOpen, setDialogOpen] = useState(false);
  const [newItemName, setNewItemName] = useState('');
  const [newItemPrice, setNewItemPrice] = useState('');
  const [newItemCat, setNewItemCat] = useState('Biryani');
  const [newItemDesc, setNewItemDesc] = useState('');

  const [employees, setEmployees] = useState([
    { id: 'emp1', name: 'Rider Rahman', role: 'Delivery Driver', shift: 'Morning (08:00 AM - 04:00 PM)', status: 'Active' },
    { id: 'emp2', name: 'Chef Kashem', role: 'Head Chef', shift: 'Evening (04:00 PM - 12:00 AM)', status: 'Active' },
    { id: 'emp3', name: 'Rider Shakil', role: 'Delivery Driver', shift: 'Evening (04:00 PM - 12:00 AM)', status: 'Off Duty' }
  ]);
  const [shiftRosterOpen, setShiftRosterOpen] = useState(false);
  const [rosterEmpId, setRosterEmpId] = useState('');
  const [newShiftVal, setNewShiftVal] = useState('Morning (08:00 AM - 04:00 PM)');

  const handleUpdateShift = (id: string, shift: string) => {
    setEmployees(prev => prev.map(emp => emp.id === id ? { ...emp, shift } : emp));
    toast.success('Shift schedule updated successfully!');
  };

  const toggleEmpStatus = (id: string) => {
    setEmployees(prev => prev.map(emp => emp.id === id ? { ...emp, status: emp.status === 'Active' ? 'Off Duty' : 'Active' } : emp));
    toast.info('Employee status toggled');
  };

  const handleDeleteItem = (id: string) => {
    setMenuItems((items) => items.filter((item) => item.id !== id));
    toast.success('Menu item deleted');
  };

  const handleAddItem = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newItemName || !newItemPrice) {
      toast.error('Name and price are required');
      return;
    }
    const newItem = {
      id: `m-${Date.now()}`,
      name: newItemName,
      price: parseFloat(newItemPrice),
      category: newItemCat,
      description: newItemDesc,
      image: 'https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?auto=compress&cs=tinysrgb&w=200'
    };
    setMenuItems((items) => [...items, newItem]);
    setDialogOpen(false);
    setNewItemName('');
    setNewItemPrice('');
    setNewItemDesc('');
    toast.success('New menu item added!');
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Business: Sultan's Dine</h1>
          <p className="text-muted-foreground mt-1">Manage your storefront menu, products and inventory</p>
        </div>
        <Button onClick={() => setDialogOpen(true)} className="rounded-xl flex items-center gap-1.5 self-start">
          <PlusCircle className="h-4 w-4" /> Add Menu Item
        </Button>
      </div>

      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-muted-foreground">Today's Sales</p>
                <p className="text-2xl font-bold mt-1">$412.50</p>
              </div>
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-success/10 text-success">
                <Wallet className="h-5 w-5" />
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-muted-foreground">Orders Today</p>
                <p className="text-2xl font-bold mt-1">28</p>
              </div>
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary">
                <Package className="h-5 w-5" />
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-muted-foreground">Menu Items Count</p>
                <p className="text-2xl font-bold mt-1">{menuItems.length}</p>
              </div>
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-amber-500/10 text-amber-500">
                <Star className="h-5 w-5" />
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-xs text-muted-foreground">Active Drivers</p>
                <p className="text-2xl font-bold mt-1">5</p>
              </div>
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-purple-500/10 text-purple-500">
                <Users className="h-5 w-5" />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Restaurant Menu Editor</CardTitle>
          <CardDescription>Update prices, categories, and item listings</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {menuItems.map((item) => (
              <div key={item.id} className="rounded-xl border border-border p-3 bg-muted/20 flex gap-3 relative group">
                <img src={item.image} alt={item.name} className="h-16 w-16 object-cover rounded-lg shrink-0" />
                <div className="flex-1 min-w-0 pr-6">
                  <Badge variant="outline" className="text-[9px] mb-1 font-semibold border-primary/30 text-primary capitalize">
                    {item.category}
                  </Badge>
                  <p className="font-semibold text-sm truncate">{item.name}</p>
                  <p className="text-xs text-muted-foreground truncate">{item.description}</p>
                  <p className="font-bold text-sm text-foreground mt-1">${item.price.toFixed(2)}</p>
                </div>
                <button
                  onClick={() => handleDeleteItem(item.id)}
                  className="absolute right-3 top-3 h-7 w-7 rounded-lg flex items-center justify-center bg-destructive/10 text-destructive hover:bg-destructive hover:text-white transition-colors"
                >
                  <Trash2 className="h-4 w-4" />
                </button>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      <Card className="mt-6">
        <CardHeader>
          <CardTitle>Staff Shift Allocation & Status</CardTitle>
          <CardDescription>Roster driver and kitchen staff schedules for business operations</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {employees.map((emp) => (
              <div key={emp.id} className="rounded-xl border border-border p-4 bg-muted/10 flex flex-col justify-between gap-3">
                <div className="space-y-1">
                  <div className="flex justify-between items-center">
                    <p className="font-bold text-sm">{emp.name}</p>
                    <Badge variant={emp.status === 'Active' ? 'default' : 'secondary'} className="text-[10px] capitalize">
                      {emp.status}
                    </Badge>
                  </div>
                  <p className="text-xs text-primary font-semibold">{emp.role}</p>
                  <p className="text-xs text-muted-foreground mt-1.5 flex items-center gap-1">
                    <Clock className="h-3 w-3" /> {emp.shift}
                  </p>
                </div>
                <div className="flex gap-2 mt-2">
                  <Button size="sm" variant="outline" className="flex-1 rounded-xl text-xs" onClick={() => { setRosterEmpId(emp.id); setNewShiftVal(emp.shift); setShiftRosterOpen(true); }}>
                    Change Shift
                  </Button>
                  <Button size="sm" variant="ghost" className="rounded-xl text-xs text-destructive hover:bg-destructive/10" onClick={() => toggleEmpStatus(emp.id)}>
                    Toggle Status
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      <Dialog open={shiftRosterOpen} onOpenChange={setShiftRosterOpen}>
        <DialogContent className="max-w-sm rounded-3xl p-6">
          <DialogHeader>
            <DialogTitle>Update Shift Schedule</DialogTitle>
          </DialogHeader>
          <div className="py-4 space-y-3">
            <Label className="text-xs font-semibold uppercase text-muted-foreground">Select Shift Timing</Label>
            <select
              value={newShiftVal}
              onChange={(e) => setNewShiftVal(e.target.value)}
              className="w-full h-10 rounded-xl border border-input bg-background px-3 py-2 text-sm"
            >
              <option value="Morning (08:00 AM - 04:00 PM)">Morning (08:00 AM - 04:00 PM)</option>
              <option value="Evening (04:00 PM - 12:00 AM)">Evening (04:00 PM - 12:00 AM)</option>
              <option value="Night (12:00 AM - 08:00 AM)">Night (12:00 AM - 08:00 AM)</option>
            </select>
          </div>
          <DialogFooter className="flex gap-2">
            <Button variant="outline" onClick={() => setShiftRosterOpen(false)} className="rounded-xl">Cancel</Button>
            <Button onClick={() => { handleUpdateShift(rosterEmpId, newShiftVal); setShiftRosterOpen(false); }} className="rounded-xl">
              Save Shift
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Add Item Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-md rounded-2xl">
          <DialogHeader>
            <DialogTitle>Add New Menu Item</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleAddItem} className="space-y-4 pt-2">
            <div className="space-y-2">
              <Label htmlFor="itemName">Item Name</Label>
              <Input id="itemName" placeholder="e.g. Special Mutton Biryani" value={newItemName} onChange={(e) => setNewItemName(e.target.value)} required />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="itemPrice">Price ($)</Label>
                <Input id="itemPrice" type="number" step="0.01" placeholder="10.99" value={newItemPrice} onChange={(e) => setNewItemPrice(e.target.value)} required />
              </div>
              <div className="space-y-2">
                <Label htmlFor="itemCategory">Category</Label>
                <Input id="itemCategory" placeholder="Biryani" value={newItemCat} onChange={(e) => setNewItemCat(e.target.value)} />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="itemDesc">Description</Label>
              <Input id="itemDesc" placeholder="Brief description..." value={newItemDesc} onChange={(e) => setNewItemDesc(e.target.value)} />
            </div>
            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => setDialogOpen(false)} className="rounded-xl">Cancel</Button>
              <Button type="submit" className="rounded-xl">Add Item</Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}

// ----------------------------------------------------
// 4. ADMIN DASHBOARD
// ----------------------------------------------------
function AdminDashboard() {
  return <EnterpriseAdminDashboard />;
}

