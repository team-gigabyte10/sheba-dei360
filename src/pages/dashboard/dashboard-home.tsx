import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  Package, Wallet, Bell, Star, TrendingUp, ArrowRight,
} from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Progress } from '@/components/ui/progress';
import { useAuthStore } from '@/store/auth-store';
import { useNotificationStore } from '@/store/notification-store';
import { userBookings, walletTransactions } from '@/lib/mock-data';
import { cn } from '@/lib/utils';

const statusColors: Record<string, string> = {
  completed: 'bg-success text-success-foreground',
  in_progress: 'bg-primary text-primary-foreground',
  confirmed: 'bg-sky-500 text-white',
  pending: 'bg-warning text-warning-foreground',
  cancelled: 'bg-destructive text-destructive-foreground',
};

export function DashboardHome() {
  const { user } = useAuthStore();
  const { unreadCount } = useNotificationStore();

  const stats = [
    { label: 'Total Orders', value: user?.totalOrders ?? 0, icon: Package, color: 'text-primary', bg: 'bg-primary/10' },
    { label: 'Wallet Balance', value: `$${user?.walletBalance.toFixed(2) ?? '0'}`, icon: Wallet, color: 'text-success', bg: 'bg-success/10' },
    { label: 'Notifications', value: unreadCount, icon: Bell, color: 'text-warning', bg: 'bg-warning/10' },
    { label: 'Member Level', value: user?.memberLevel ?? 'bronze', icon: Star, color: 'text-amber-500', bg: 'bg-amber-500/10' },
  ];

  const recentBookings = userBookings.slice(0, 3);
  const recentTransactions = walletTransactions.slice(0, 4);

  return (
    <div className="space-y-6">
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
          <CardHeader className="flex-row items-center justify-between">
            <CardTitle>Recent Bookings</CardTitle>
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
                  <Badge className={cn('text-[10px] capitalize', statusColors[booking.status])}>{booking.status}</Badge>
                </div>
              </Link>
            ))}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Membership</CardTitle>
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

      <Card>
        <CardHeader className="flex-row items-center justify-between">
          <CardTitle>Recent Transactions</CardTitle>
          <Button variant="ghost" size="sm" asChild>
            <Link to="/dashboard/wallet">View All <ArrowRight className="ml-1 h-3 w-3" /></Link>
          </Button>
        </CardHeader>
        <CardContent className="space-y-2">
          {recentTransactions.map((tx) => (
            <div key={tx.id} className="flex items-center justify-between py-2 border-b last:border-0">
              <div className="flex items-center gap-3">
                <div className={cn(
                  'flex h-8 w-8 items-center justify-center rounded-full',
                  tx.type === 'credit' ? 'bg-success/10 text-success' : 'bg-destructive/10 text-destructive'
                )}>
                  {tx.type === 'credit' ? <TrendingUp className="h-4 w-4" /> : <ArrowRight className="h-4 w-4" />}
                </div>
                <div>
                  <p className="text-sm font-medium">{tx.description}</p>
                  <p className="text-xs text-muted-foreground">{tx.date}</p>
                </div>
              </div>
              <p className={cn('font-semibold text-sm', tx.type === 'credit' ? 'text-success' : 'text-foreground')}>
                {tx.type === 'credit' ? '+' : '-'}${tx.amount.toFixed(2)}
              </p>
            </div>
          ))}
        </CardContent>
      </Card>
    </div>
  );
}
