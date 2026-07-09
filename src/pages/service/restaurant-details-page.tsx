import { useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Star, MapPin, Clock, ChevronLeft, Share2, Heart, Plus, Minus, ShoppingCart, Info } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Sheet, SheetContent, SheetHeader, SheetTitle } from '@/components/ui/sheet';
import { useBookingStore } from '@/store/booking-store';
import { restaurants } from '@/lib/mock-data';
import { toast } from 'sonner';
import { cn } from '@/lib/utils';

export function RestaurantDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { cart, addToCart, removeFromCart, updateQuantity, cartTotal, clearCart } = useBookingStore();
  const [cartOpen, setCartOpen] = useState(false);
  const [isWishlisted, setIsWishlisted] = useState(false);

  const restaurant = restaurants.find((r) => r.id === id);

  if (!restaurant) {
    return (
      <div className="container mx-auto px-4 py-16 text-center">
        <p className="text-lg font-semibold">Restaurant not found</p>
        <Button asChild className="mt-4"><Link to="/search">Back to Search</Link></Button>
      </div>
    );
  }

  const menuCategories = [...new Set(restaurant.menu.map((m) => m.category))];
  const cartItemCount = cart.reduce((sum, item) => sum + item.quantity, 0);

  const handleCheckout = () => {
    navigate('/booking/address');
  };

  return (
    <div className="container mx-auto px-4 py-6">
      <Button variant="ghost" size="sm" asChild className="mb-4">
        <Link to="/search"><ChevronLeft className="mr-1 h-4 w-4" /> Back</Link>
      </Button>

      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="relative aspect-[21/9] rounded-xl overflow-hidden mb-6">
        <img src={restaurant.image} alt={restaurant.name} className="h-full w-full object-cover" />
        <div className="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent" />
        <div className="absolute bottom-0 left-0 right-0 p-6 text-white">
          <div className="flex items-end justify-between">
            <div>
              <h1 className="text-3xl font-bold">{restaurant.name}</h1>
              <p className="mt-1 text-white/80">{restaurant.cuisine.join(' · ')}</p>
              <div className="mt-2 flex flex-wrap items-center gap-3 text-sm">
                <span className="flex items-center gap-1"><Star className="h-4 w-4 fill-warning text-warning" /> {restaurant.rating} ({restaurant.reviewCount})</span>
                <span className="flex items-center gap-1"><Clock className="h-4 w-4" /> {restaurant.deliveryTime}</span>
                <span className="flex items-center gap-1"><MapPin className="h-4 w-4" /> {restaurant.distance} km</span>
                <span>${restaurant.deliveryFee} delivery</span>
              </div>
            </div>
            <div className="flex gap-2">
              <Button size="icon" variant="secondary" onClick={() => setIsWishlisted(!isWishlisted)}>
                <Heart className={isWishlisted ? 'h-4 w-4 fill-destructive text-destructive' : 'h-4 w-4'} />
              </Button>
              <Button size="icon" variant="secondary" onClick={() => toast.success('Link copied!')}>
                <Share2 className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </div>
        <Badge className={cn('absolute top-4 right-4 text-white', restaurant.isOpen ? 'bg-success' : 'bg-destructive')}>
          {restaurant.isOpen ? 'Open Now' : 'Closed'}
        </Badge>
      </motion.div>

      <div className="grid gap-6 lg:grid-cols-3">
        <div className="lg:col-span-2">
          <Tabs defaultValue={menuCategories[0]}>
            <TabsList className="w-full justify-start overflow-x-auto scrollbar-hide">
              {menuCategories.map((cat) => (
                <TabsTrigger key={cat} value={cat}>{cat}</TabsTrigger>
              ))}
            </TabsList>

            {menuCategories.map((cat) => (
              <TabsContent key={cat} value={cat} className="space-y-3">
                {restaurant.menu
                  .filter((m) => m.category === cat)
                  .map((item) => {
                    const cartItem = cart.find((c) => c.id === item.id);
                    return (
                      <Card key={item.id}>
                        <CardContent className="flex gap-4 p-4">
                          <div className="h-20 w-20 shrink-0 rounded-lg overflow-hidden">
                            <img src={item.image} alt={item.name} className="h-full w-full object-cover" />
                          </div>
                          <div className="flex-1 min-w-0">
                            <div className="flex items-start justify-between gap-2">
                              <div>
                                <div className="flex items-center gap-2">
                                  <h3 className="font-semibold">{item.name}</h3>
                                  {item.popular && <Badge variant="secondary" className="text-[10px]">Popular</Badge>}
                                </div>
                                <p className="text-sm text-muted-foreground mt-0.5 line-clamp-2">{item.description}</p>
                                <p className="font-semibold mt-1">${item.price}</p>
                              </div>
                            </div>
                          </div>
                          <div className="flex flex-col justify-center">
                            {cartItem ? (
                              <div className="flex items-center gap-2">
                                <Button size="icon" variant="outline" className="h-8 w-8" onClick={() => updateQuantity(item.id, cartItem.quantity - 1)}>
                                  <Minus className="h-3 w-3" />
                                </Button>
                                <span className="font-semibold w-6 text-center">{cartItem.quantity}</span>
                                <Button size="icon" variant="outline" className="h-8 w-8" onClick={() => updateQuantity(item.id, cartItem.quantity + 1)}>
                                  <Plus className="h-3 w-3" />
                                </Button>
                              </div>
                            ) : (
                              <Button size="sm" onClick={() => addToCart({ id: item.id, name: item.name, price: item.price, image: item.image, restaurantId: restaurant.id })}>
                                <Plus className="h-4 w-4" /> Add
                              </Button>
                            )}
                          </div>
                        </CardContent>
                      </Card>
                    );
                  })}
              </TabsContent>
            ))}
          </Tabs>
        </div>

        <div className="lg:col-span-1">
          <div className="sticky top-20">
            <Card>
              <CardContent className="p-6">
                <h3 className="font-semibold mb-4">Order Summary</h3>
                {cart.length === 0 ? (
                  <div className="text-center py-8">
                    <ShoppingCart className="h-12 w-12 text-muted-foreground mx-auto mb-2" />
                    <p className="text-sm text-muted-foreground">Your cart is empty</p>
                    <p className="text-xs text-muted-foreground mt-1">Add items to get started</p>
                  </div>
                ) : (
                  <>
                    <div className="space-y-3 max-h-64 overflow-y-auto">
                      {cart.map((item) => (
                        <div key={item.id} className="flex items-center gap-2">
                          <img src={item.image} alt={item.name} className="h-10 w-10 rounded object-cover" />
                          <div className="flex-1 min-w-0">
                            <p className="text-sm font-medium truncate">{item.name}</p>
                            <p className="text-xs text-muted-foreground">${item.price} × {item.quantity}</p>
                          </div>
                          <Button size="icon" variant="ghost" className="h-7 w-7" onClick={() => removeFromCart(item.id)}>
                            <Minus className="h-3 w-3" />
                          </Button>
                        </div>
                      ))}
                    </div>
                    <Separator className="my-4" />
                    <div className="space-y-2 text-sm">
                      <div className="flex justify-between">
                        <span className="text-muted-foreground">Subtotal</span>
                        <span className="font-semibold">${cartTotal().toFixed(2)}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-muted-foreground">Delivery Fee</span>
                        <span className="font-semibold">${restaurant.deliveryFee}</span>
                      </div>
                      <Separator />
                      <div className="flex justify-between text-base">
                        <span className="font-semibold">Total</span>
                        <span className="font-bold">${(cartTotal() + restaurant.deliveryFee).toFixed(2)}</span>
                      </div>
                    </div>
                    <Button className="w-full mt-4" onClick={handleCheckout}>
                      Checkout · ${cartItemCount} items
                    </Button>
                    <Button variant="ghost" className="w-full mt-1" onClick={clearCart}>Clear Cart</Button>
                  </>
                )}
                <div className="mt-4 flex items-start gap-2 text-xs text-muted-foreground">
                  <Info className="h-3 w-3 mt-0.5 shrink-0" />
                  <p>Delivery time: {restaurant.deliveryTime}. Free delivery on orders over $25.</p>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      {cartItemCount > 0 && (
        <div className="fixed bottom-4 left-1/2 -translate-x-1/2 z-40 lg:hidden">
          <Button size="lg" className="shadow-xl" onClick={() => setCartOpen(true)}>
            <ShoppingCart className="mr-2 h-5 w-5" />
            {cartItemCount} items · ${(cartTotal() + restaurant.deliveryFee).toFixed(2)}
          </Button>
        </div>
      )}

      <Sheet open={cartOpen} onOpenChange={setCartOpen}>
        <SheetContent side="bottom" className="h-[60vh]">
          <SheetHeader>
            <SheetTitle>Your Cart</SheetTitle>
          </SheetHeader>
          <div className="mt-4 space-y-3 overflow-y-auto">
            {cart.map((item) => (
              <div key={item.id} className="flex items-center gap-3">
                <img src={item.image} alt={item.name} className="h-12 w-12 rounded object-cover" />
                <div className="flex-1">
                  <p className="text-sm font-medium">{item.name}</p>
                  <p className="text-xs text-muted-foreground">${item.price} × {item.quantity}</p>
                </div>
                <div className="flex items-center gap-2">
                  <Button size="icon" variant="outline" className="h-7 w-7" onClick={() => updateQuantity(item.id, item.quantity - 1)}>
                    <Minus className="h-3 w-3" />
                  </Button>
                  <span className="font-semibold text-sm">{item.quantity}</span>
                  <Button size="icon" variant="outline" className="h-7 w-7" onClick={() => updateQuantity(item.id, item.quantity + 1)}>
                    <Plus className="h-3 w-3" />
                  </Button>
                </div>
              </div>
            ))}
          </div>
          <div className="mt-4 border-t pt-4">
            <div className="flex justify-between mb-3">
              <span className="font-semibold">Total</span>
              <span className="font-bold">${(cartTotal() + restaurant.deliveryFee).toFixed(2)}</span>
            </div>
            <Button className="w-full" onClick={() => { setCartOpen(false); handleCheckout(); }}>Checkout</Button>
          </div>
        </SheetContent>
      </Sheet>
    </div>
  );
}
