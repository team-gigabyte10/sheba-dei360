import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useBookingStore } from '@/store/booking-store';
import { Button } from '@/components/ui/button';
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetDescription } from '@/components/ui/sheet';
import { Separator } from '@/components/ui/separator';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { ScrollArea } from '@/components/ui/scroll-area';
import { ShoppingCart, Plus, Minus, Trash2, Tag, Percent } from 'lucide-react';
import { toast } from 'sonner';

interface CartDrawerProps {
  open: boolean;
  onClose: () => void;
}

export function CartDrawer({ open, onClose }: CartDrawerProps) {
  const navigate = useNavigate();
  const {
    cart,
    updateQuantity,
    removeFromCart,
    cartTotal,
    discountedTotal,
    promoCode,
    promoDiscount,
    applyPromoCode,
    removePromo,
    setDraft
  } = useBookingStore();

  const [promoInput, setPromoInput] = useState('');

  const handleApplyPromo = () => {
    const success = applyPromoCode(promoInput);
    if (success) {
      toast.success(`Promo code applied successfully!`);
      setPromoInput('');
    } else {
      toast.error('Invalid promo code');
    }
  };

  const handleCheckout = () => {
    if (cart.length === 0) return;
    
    // Create draft from first item/vendor details
    const totalAmount = promoCode ? discountedTotal() : cartTotal();
    setDraft({
      serviceId: cart[0].id,
      serviceName: 'Food Delivery Order',
      providerId: cart[0].restaurantId,
      providerName: 'Sultan\'s Dine', // Mock restaurant name
      providerAvatar: cart[0].image,
      price: totalAmount,
      type: 'food'
    });
    
    onClose();
    navigate('/booking/datetime');
  };

  return (
    <Sheet open={open} onOpenChange={(val) => !val && onClose()}>
      <SheetContent className="w-full sm:max-w-md flex flex-col h-full rounded-l-3xl p-6 shadow-2xl bg-background/95 backdrop-blur-lg">
        <SheetHeader className="mb-4">
          <div className="flex items-center gap-2">
            <ShoppingCart className="h-5 w-5 text-primary" />
            <SheetTitle className="text-lg font-bold">Shopping Cart</SheetTitle>
          </div>
          <SheetDescription className="text-xs">
            Review items before proceeding to checkout
          </SheetDescription>
        </SheetHeader>

        {/* Cart Items List */}
        <ScrollArea className="flex-1 -mx-2 px-2">
          {cart.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-60 text-muted-foreground gap-2">
              <ShoppingCart className="h-10 w-10 opacity-30 animate-pulse" />
              <p className="text-sm font-medium">Your cart is empty</p>
              <p className="text-xs opacity-75">Add delicious dishes to get started</p>
            </div>
          ) : (
            <div className="space-y-4 pr-1">
              {cart.map((item) => (
                <div key={item.id} className="flex gap-3 rounded-xl border border-border p-3 bg-muted/20 relative group transition-all hover:bg-muted/40">
                  <img src={item.image} alt={item.name} className="h-16 w-16 object-cover rounded-lg shrink-0 border border-border" />
                  <div className="flex-1 min-w-0 pr-6 space-y-1">
                    <p className="font-semibold text-sm truncate">{item.name}</p>
                    <p className="font-bold text-sm text-foreground">${item.price.toFixed(2)}</p>
                    
                    <div className="flex items-center gap-2 mt-1">
                      <Button
                        size="icon"
                        variant="outline"
                        className="h-6 w-6 rounded-md"
                        onClick={() => updateQuantity(item.id, item.quantity - 1)}
                      >
                        <Minus className="h-3 w-3" />
                      </Button>
                      <span className="text-xs font-semibold w-5 text-center">{item.quantity}</span>
                      <Button
                        size="icon"
                        variant="outline"
                        className="h-6 w-6 rounded-md"
                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                      >
                        <Plus className="h-3 w-3" />
                      </Button>
                    </div>
                  </div>

                  <button
                    onClick={() => removeFromCart(item.id)}
                    className="absolute right-3 top-3 h-6 w-6 rounded-md flex items-center justify-center text-muted-foreground hover:bg-destructive hover:text-white transition-colors"
                  >
                    <Trash2 className="h-3.5 w-3.5" />
                  </button>
                </div>
              ))}
            </div>
          )}
        </ScrollArea>

        {/* Promo Codes & Checkout summary */}
        {cart.length > 0 && (
          <div className="space-y-4 pt-4 border-t mt-auto">
            {/* Promo Code Input */}
            <div className="space-y-2">
              <Label className="text-xs font-semibold text-muted-foreground uppercase flex items-center gap-1">
                <Tag className="h-3 w-3" /> Promo Code
              </Label>
              {promoCode ? (
                <div className="flex items-center justify-between bg-emerald-500/10 border border-emerald-500/20 text-emerald-600 rounded-xl p-2.5 text-sm font-semibold">
                  <span className="flex items-center gap-1.5"><Percent className="h-4 w-4" /> Code {promoCode} Applied ({promoDiscount}% Off)</span>
                  <button onClick={removePromo} className="text-xs hover:underline text-destructive font-bold">Remove</button>
                </div>
              ) : (
                <div className="flex gap-2">
                  <Input
                    placeholder="Enter code (e.g. WEEKEND50)"
                    value={promoInput}
                    onChange={(e) => setPromoInput(e.target.value)}
                    className="h-10 rounded-xl"
                  />
                  <Button onClick={handleApplyPromo} className="rounded-xl px-4 h-10">
                    Apply
                  </Button>
                </div>
              )}
            </div>

            <Separator />

            {/* Calculations Breakdown */}
            <div className="space-y-2 text-sm">
              <div className="flex justify-between text-muted-foreground">
                <span>Subtotal</span>
                <span>${cartTotal().toFixed(2)}</span>
              </div>
              
              {promoCode && (
                <div className="flex justify-between text-emerald-600 font-medium">
                  <span>Discount ({promoDiscount}%)</span>
                  <span>-${(cartTotal() * (promoDiscount / 100)).toFixed(2)}</span>
                </div>
              )}
              
              <div className="flex justify-between text-muted-foreground">
                <span>Delivery Charge</span>
                <span>$2.50</span>
              </div>

              <Separator className="my-1" />

              <div className="flex justify-between text-base font-bold text-foreground">
                <span>Total</span>
                {promoCode ? (
                  <div className="flex items-center gap-2">
                    <span className="text-xs line-through text-muted-foreground">${(cartTotal() + 2.5).toFixed(2)}</span>
                    <span className="text-emerald-600">${(discountedTotal() + 2.5).toFixed(2)}</span>
                  </div>
                ) : (
                  <span>${(cartTotal() + 2.5).toFixed(2)}</span>
                )}
              </div>
            </div>

            <Button onClick={handleCheckout} className="w-full h-11 rounded-xl text-base font-bold shadow-lg" size="lg">
              Proceed to Checkout
            </Button>
          </div>
        )}
      </SheetContent>
    </Sheet>
  );
}
