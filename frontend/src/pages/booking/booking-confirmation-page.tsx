import { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { CheckCircle, Calendar, MapPin, CreditCard, MessageCircle, Eye } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Separator } from '@/components/ui/separator';
import { useBookingStore } from '@/store/booking-store';
import { useNotificationStore } from '@/store/notification-store';

export function BookingConfirmationPage() {
  const navigate = useNavigate();
  const { draft, clearDraft, cartTotal, addBooking } = useBookingStore();
  const { addNotification } = useNotificationStore();
  const [bookingId] = useState(() => `BK${Date.now().toString().slice(-8)}`);

  const cartTotalAmount = cartTotal();
  const baseAmount = draft ? (draft.price || cartTotalAmount) : 0;
  const deliveryFee = draft && draft.type === 'food' ? 2.5 : 0;
  const total = baseAmount + deliveryFee;

  useEffect(() => {
    if (draft) {
      addBooking({
        id: bookingId,
        serviceId: draft.serviceId,
        serviceName: draft.serviceName,
        providerId: draft.providerId,
        providerName: draft.providerName,
        providerAvatar: draft.providerAvatar,
        date: draft.date || new Date().toISOString().split('T')[0],
        time: draft.time || '10:00',
        address: draft.address?.fullAddress || 'House 12, Road 5, Gulshan, Dhaka',
        status: 'pending' as const,
        amount: total,
        paymentMethod: draft.paymentMethod || 'wallet',
        type: draft.type,
        createdAt: new Date().toISOString().split('T')[0],
      });

      addNotification({
        type: 'booking',
        title: 'Booking Confirmed!',
        message: `Your booking for ${draft.serviceName} with ${draft.providerName} has been confirmed.`,
        actionUrl: '/dashboard/bookings',
      });
    }
  }, []);

  if (!draft) {
    return (
      <div className="container mx-auto px-4 py-16 text-center">
        <p className="text-lg font-semibold">No booking to confirm</p>
        <Button asChild className="mt-4"><Link to="/search">Browse Services</Link></Button>
      </div>
    );
  }

  const handleDone = () => {
    clearDraft();
    navigate('/dashboard/bookings');
  };

  return (
    <div className="container mx-auto px-4 py-6 max-w-2xl">
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        className="text-center mb-8"
      >
        <motion.div
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          transition={{ delay: 0.2, type: 'spring' }}
          className="mx-auto flex h-20 w-20 items-center justify-center rounded-full bg-success/10 text-success mb-4"
        >
          <CheckCircle className="h-10 w-10" />
        </motion.div>
        <h1 className="text-3xl font-bold tracking-tight">Booking Confirmed!</h1>
        <p className="text-muted-foreground mt-2">Your booking has been successfully placed</p>
        <p className="text-sm text-muted-foreground mt-1">Booking ID: <span className="font-mono font-semibold text-foreground">{bookingId}</span></p>
      </motion.div>

      <Card className="mb-4">
        <CardContent className="p-6 space-y-4">
          <div className="flex items-center gap-3 pb-4 border-b">
            <Avatar className="h-14 w-14">
              <AvatarImage src={draft.providerAvatar} alt={draft.providerName} />
              <AvatarFallback>{draft.providerName.charAt(0)}</AvatarFallback>
            </Avatar>
            <div>
              <p className="font-semibold">{draft.serviceName}</p>
              <p className="text-sm text-muted-foreground">{draft.providerName}</p>
            </div>
          </div>

          <div className="space-y-3">
            {draft.date && (
              <div className="flex items-center gap-3">
                <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
                  <Calendar className="h-4 w-4" />
                </div>
                <div>
                  <p className="text-xs text-muted-foreground">Date & Time</p>
                  <p className="text-sm font-medium">{draft.date} at {draft.time}</p>
                </div>
              </div>
            )}

            {draft.address && (
              <div className="flex items-center gap-3">
                <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
                  <MapPin className="h-4 w-4" />
                </div>
                <div>
                  <p className="text-xs text-muted-foreground">Address</p>
                  <p className="text-sm font-medium">{draft.address.label} · {draft.address.fullAddress}</p>
                </div>
              </div>
            )}

            <div className="flex items-center gap-3">
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
                <CreditCard className="h-4 w-4" />
              </div>
              <div>
                <p className="text-xs text-muted-foreground">Payment Method</p>
                <p className="text-sm font-medium capitalize">{draft.paymentMethod}</p>
              </div>
            </div>
          </div>

          <Separator />

          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-muted-foreground">Subtotal</span>
              <span className="font-semibold">৳{baseAmount.toFixed(2)}</span>
            </div>
            {deliveryFee > 0 && (
              <div className="flex justify-between">
                <span className="text-muted-foreground">Delivery Fee</span>
                <span className="font-semibold">৳{deliveryFee.toFixed(2)}</span>
              </div>
            )}
            <Separator />
            <div className="flex justify-between text-base">
              <span className="font-semibold">Total Paid</span>
              <span className="font-bold">৳{total.toFixed(2)}</span>
            </div>
          </div>
        </CardContent>
      </Card>

      <div className="grid grid-cols-2 gap-3">
        <Button variant="outline" asChild>
          <Link to={`/chat/${draft.providerId}`}>
            <MessageCircle className="mr-2 h-4 w-4" /> Chat with Provider
          </Link>
        </Button>
        <Button variant="outline" asChild>
          <Link to={`/track/${bookingId}`}>
            <Eye className="mr-2 h-4 w-4" /> Track Booking
          </Link>
        </Button>
      </div>

      <Button className="w-full mt-4" size="lg" onClick={handleDone}>
        View My Bookings
      </Button>
    </div>
  );
}
