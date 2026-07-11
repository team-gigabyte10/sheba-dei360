import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Eye, Star } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Textarea } from '@/components/ui/textarea';
import { Label } from '@/components/ui/label';
import { userBookings } from '@/lib/mock-data';
import { cn } from '@/lib/utils';
import { toast } from 'sonner';
import { useBookingStore } from '@/store/booking-store';

const statusColors: Record<string, string> = {
  completed: 'bg-success text-success-foreground',
  'in-progress': 'bg-primary text-primary-foreground',
  confirmed: 'bg-sky-500 text-white',
  pending: 'bg-warning text-warning-foreground',
  cancelled: 'bg-destructive text-destructive-foreground',
};

export function BookingsPage() {
  const { bookings, updateBooking } = useBookingStore();
  const [reviewDialog, setReviewDialog] = useState<string | null>(null);
  const [rating, setRating] = useState(5);
  const [review, setReview] = useState('');

  const handleReview = () => {
    if (reviewDialog) {
      updateBooking(reviewDialog, { rating, review });
      toast.success('Review submitted successfully!');
      setReviewDialog(null);
      setReview('');
      setRating(5);
    }
  };

  const renderBooking = (booking: typeof userBookings[0]) => (
    <Card key={booking.id}>
      <CardContent className="p-4">
        <div className="flex items-start gap-3">
          <Avatar className="h-12 w-12">
            <AvatarImage src={booking.providerAvatar} alt={booking.providerName} />
            <AvatarFallback>{booking.providerName.charAt(0)}</AvatarFallback>
          </Avatar>
          <div className="flex-1 min-w-0">
            <div className="flex items-start justify-between gap-2">
              <div>
                <p className="font-semibold">{booking.serviceName}</p>
                <p className="text-sm text-muted-foreground">{booking.providerName}</p>
              </div>
              <Badge className={cn('text-xs capitalize shrink-0', statusColors[booking.status])}>
                {booking.status.replace('-', ' ')}
              </Badge>
            </div>
            <div className="mt-2 flex flex-wrap items-center gap-3 text-xs text-muted-foreground">
              <span>{booking.date} at {booking.time}</span>
              <span>·</span>
              <span className="capitalize">{booking.paymentMethod}</span>
              <span>·</span>
              <span className="font-semibold text-foreground">${booking.amount}</span>
            </div>
            <div className="mt-3 flex gap-2">
              {booking.status === 'completed' && !booking.rating && (
                <Button size="sm" variant="outline" onClick={() => setReviewDialog(booking.id)}>
                  <Star className="mr-1 h-3 w-3" /> Leave Review
                </Button>
              )}
              {booking.rating && (
                <div className="flex items-center gap-1 text-sm">
                  <Star className="h-4 w-4 fill-warning text-warning" />
                  <span className="font-semibold">{booking.rating}</span>
                </div>
              )}
              {(booking.status === 'in-progress' || booking.status === 'confirmed') && (
                <Button size="sm" variant="outline" asChild>
                  <Link to={`/track/${booking.id}`}><Eye className="mr-1 h-3 w-3" /> Track</Link>
                </Button>
              )}
              <Button size="sm" variant="ghost" asChild>
                <Link to={`/chat/${booking.providerId}`}>Chat</Link>
              </Button>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">My Bookings</h1>
        <p className="text-muted-foreground mt-1">Manage and track all your bookings</p>
      </div>

      <Tabs defaultValue="all">
        <TabsList>
          <TabsTrigger value="all">All ({bookings.length})</TabsTrigger>
          <TabsTrigger value="active">Active</TabsTrigger>
          <TabsTrigger value="completed">Completed</TabsTrigger>
          <TabsTrigger value="cancelled">Cancelled</TabsTrigger>
        </TabsList>

        <TabsContent value="all" className="space-y-3">
          {bookings.map(renderBooking)}
        </TabsContent>
        <TabsContent value="active" className="space-y-3">
          {bookings.filter((b) => ['pending', 'confirmed', 'in-progress'].includes(b.status)).map(renderBooking)}
        </TabsContent>
        <TabsContent value="completed" className="space-y-3">
          {bookings.filter((b) => b.status === 'completed').map(renderBooking)}
        </TabsContent>
        <TabsContent value="cancelled" className="space-y-3">
          {bookings.filter((b) => b.status === 'cancelled').map(renderBooking)}
          {bookings.filter((b) => b.status === 'cancelled').length === 0 && (
            <Card><CardContent className="p-8 text-center text-muted-foreground">No cancelled bookings</CardContent></Card>
          )}
        </TabsContent>
      </Tabs>

      <Dialog open={reviewDialog !== null} onOpenChange={(open) => !open && setReviewDialog(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Leave a Review</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div>
              <Label>Rating</Label>
              <div className="mt-2 flex gap-2">
                {[1, 2, 3, 4, 5].map((star) => (
                  <button key={star} onClick={() => setRating(star)}>
                    <Star className={cn('h-8 w-8 transition-colors', star <= rating ? 'fill-warning text-warning' : 'text-muted-foreground')} />
                  </button>
                ))}
              </div>
            </div>
            <div>
              <Label htmlFor="review">Your Review</Label>
              <Textarea id="review" placeholder="Share your experience..." value={review} onChange={(e) => setReview(e.target.value)} rows={4} />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setReviewDialog(null)}>Cancel</Button>
            <Button onClick={handleReview}>Submit Review</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
