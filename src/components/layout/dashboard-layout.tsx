import { Outlet, Link, useLocation, Navigate } from 'react-router-dom';
import {
  LayoutDashboard, User, Package, Wallet, MapPin, Bell,
  Heart, Star, LifeBuoy, Settings,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { useAuthStore } from '@/store/auth-store';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Logo } from '@/components/shared/logo';

const sidebarLinks = [
  { label: 'Dashboard', path: '/dashboard', icon: LayoutDashboard, exact: true },
  { label: 'Profile', path: '/dashboard/profile', icon: User },
  { label: 'My Orders', path: '/dashboard/orders', icon: Package },
  { label: 'My Bookings', path: '/dashboard/bookings', icon: Package },
  { label: 'Wallet', path: '/dashboard/wallet', icon: Wallet },
  { label: 'Saved Addresses', path: '/dashboard/addresses', icon: MapPin },
  { label: 'Notifications', path: '/dashboard/notifications', icon: Bell },
  { label: 'Wishlist', path: '/dashboard/wishlist', icon: Heart },
  { label: 'My Reviews', path: '/dashboard/reviews', icon: Star },
  { label: 'Support Tickets', path: '/dashboard/support', icon: LifeBuoy },
  { label: 'Settings', path: '/dashboard/settings', icon: Settings },
];

export function DashboardLayout() {
  const location = useLocation();
  const { user, isAuthenticated } = useAuthStore();

  if (!isAuthenticated) return <Navigate to="/login" replace />;

  const isActive = (path: string, exact?: boolean) =>
    exact ? location.pathname === path : location.pathname.startsWith(path);

  return (
    <div className="container mx-auto px-4 py-6">
      <div className="flex flex-col gap-6 lg:flex-row">
        <aside className="lg:w-64 shrink-0">
          <div className="sticky top-20 rounded-xl border bg-card p-4">
            <div className="mb-4 flex items-center gap-3 pb-4 border-b">
              <Avatar className="h-12 w-12">
                <AvatarImage src={user?.avatar} alt={user?.name} />
                <AvatarFallback>{user?.name?.charAt(0)}</AvatarFallback>
              </Avatar>
              <div className="min-w-0">
                <p className="text-sm font-semibold truncate">{user?.name}</p>
                <p className="text-xs text-muted-foreground truncate">{user?.email}</p>
              </div>
            </div>
            <nav className="flex flex-col gap-1">
              {sidebarLinks.map((link) => (
                <Link
                  key={link.path}
                  to={link.path}
                  className={cn(
                    'flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors',
                    isActive(link.path, link.exact)
                      ? 'bg-primary text-primary-foreground'
                      : 'text-muted-foreground hover:bg-muted hover:text-foreground'
                  )}
                >
                  <link.icon className="h-4 w-4" />
                  {link.label}
                </Link>
              ))}
            </nav>
            <div className="mt-4 pt-4 border-t">
              <Logo size="sm" showText={false} />
            </div>
          </div>
        </aside>

        <div className="flex-1 min-w-0">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
