import { Eye } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { userBookings } from '@/lib/mock-data';
import { cn } from '@/lib/utils';

const statusColors: Record<string, string> = {
  completed: 'bg-success text-success-foreground',
  'in-progress': 'bg-primary text-primary-foreground',
  confirmed: 'bg-sky-500 text-white',
  pending: 'bg-warning text-warning-foreground',
  cancelled: 'bg-destructive text-destructive-foreground',
};

export function OrdersPage() {
  const foodOrders = userBookings.filter((b) => b.type === 'food');
  const rideOrders = userBookings.filter((b) => b.type === 'ride');

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">My Orders</h1>
        <p className="text-muted-foreground mt-1">Food and ride orders</p>
      </div>

      <div className="space-y-6">
        <section>
          <h2 className="text-lg font-semibold mb-3">Food Orders</h2>
          <div className="space-y-3">
            {foodOrders.length > 0 ? foodOrders.map((order) => (
              <Card key={order.id}>
                <CardContent className="flex items-center gap-3 p-4">
                  <Avatar className="h-12 w-12">
                    <AvatarImage src={order.providerAvatar} alt={order.providerName} />
                    <AvatarFallback>{order.providerName.charAt(0)}</AvatarFallback>
                  </Avatar>
                  <div className="flex-1 min-w-0">
                    <p className="font-semibold">{order.serviceName}</p>
                    <p className="text-sm text-muted-foreground">{order.providerName} · {order.date}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold">৳{order.amount}</p>
                    <Badge className={cn('text-xs capitalize', statusColors[order.status])}>{order.status.replace('-', ' ')}</Badge>
                  </div>
                  {order.status === 'in-progress' && (
                    <Button size="sm" variant="outline" asChild>
                      <Link to={`/track/${order.id}`}><Eye className="mr-1 h-3 w-3" /> Track</Link>
                    </Button>
                  )}
                </CardContent>
              </Card>
            )) : (
              <Card><CardContent className="p-8 text-center text-muted-foreground">No food orders yet</CardContent></Card>
            )}
          </div>
        </section>

        <section>
          <h2 className="text-lg font-semibold mb-3">Ride Orders</h2>
          <div className="space-y-3">
            {rideOrders.length > 0 ? rideOrders.map((order) => (
              <Card key={order.id}>
                <CardContent className="flex items-center gap-3 p-4">
                  <Avatar className="h-12 w-12">
                    <AvatarImage src={order.providerAvatar} alt={order.providerName} />
                    <AvatarFallback>{order.providerName.charAt(0)}</AvatarFallback>
                  </Avatar>
                  <div className="flex-1 min-w-0">
                    <p className="font-semibold">{order.serviceName}</p>
                    <p className="text-sm text-muted-foreground">{order.address} · {order.date}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold">৳{order.amount}</p>
                    <Badge className={cn('text-xs capitalize', statusColors[order.status])}>{order.status}</Badge>
                  </div>
                </CardContent>
              </Card>
            )) : (
              <Card><CardContent className="p-8 text-center text-muted-foreground">No ride orders yet</CardContent></Card>
            )}
          </div>
        </section>
      </div>
    </div>
  );
}
