import { Link } from 'react-router-dom';
import { Heart, Trash2 } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { RatingStars } from '@/components/shared/rating-stars';
import { wishlistItems } from '@/lib/mock-data';
import { toast } from 'sonner';

export function WishlistPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">My Wishlist</h1>
        <p className="text-muted-foreground mt-1">Services and restaurants you've saved</p>
      </div>

      {wishlistItems.length === 0 ? (
        <Card>
          <CardContent className="p-8 text-center">
            <Heart className="h-12 w-12 text-muted-foreground mx-auto mb-3" />
            <p className="text-muted-foreground">Your wishlist is empty</p>
            <Button asChild className="mt-4"><Link to="/search">Browse Services</Link></Button>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {wishlistItems.map((item) => (
            <Card key={item.id} className="overflow-hidden">
              <div className="relative aspect-[16/9]">
                <img src={item.image} alt={item.serviceName} className="h-full w-full object-cover" />
                <Button
                  size="icon"
                  variant="secondary"
                  className="absolute top-2 right-2 h-8 w-8"
                  onClick={() => toast.success('Removed from wishlist')}
                >
                  <Trash2 className="h-4 w-4" />
                </Button>
              </div>
              <CardContent className="p-4">
                <p className="font-semibold">{item.serviceName}</p>
                <p className="text-sm text-muted-foreground">{item.providerName}</p>
                <div className="mt-2 flex items-center justify-between">
                  <RatingStars rating={item.rating} showValue={false} />
                  <span className="font-semibold">${item.price}</span>
                </div>
                <Button className="w-full mt-3" size="sm" asChild>
                  <Link to={item.serviceId.startsWith('r') ? `/restaurant/${item.serviceId}` : `/service/${item.serviceId}`}>
                    Book Now
                  </Link>
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
