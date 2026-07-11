import { useState } from 'react';
import { useNavigate, Navigate, Link } from 'react-router-dom';
import {
  ChevronLeft, Wallet, CreditCard, Smartphone, Banknote, Shield, Check, Loader2
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Separator } from '@/components/ui/separator';
import { BookingStepper } from '@/components/booking/booking-stepper';
import { useBookingStore } from '@/store/booking-store';
import { useAuthStore } from '@/store/auth-store';
import { cn } from '@/lib/utils';
import type { PaymentMethod } from '@/types';
import { toast } from 'sonner';
import { Dialog, DialogContent } from '@/components/ui/dialog';

const paymentMethods: { id: PaymentMethod; label: string; icon: React.ComponentType<{ className?: string }>; description: string }[] = [
  { id: 'wallet', label: 'ServeNear Wallet', icon: Wallet, description: 'Pay from your wallet balance' },
  { id: 'card', label: 'Credit/Debit Card', icon: CreditCard, description: 'Visa, Mastercard, Amex' },
  { id: 'bkash', label: 'bKash', icon: Smartphone, description: 'Pay with bKash mobile wallet' },
  { id: 'nagad', label: 'Nagad', icon: Smartphone, description: 'Pay with Nagad mobile wallet' },
  { id: 'cash', label: 'Cash on Delivery', icon: Banknote, description: 'Pay when service is completed' },
];

export function BookingPaymentPage() {
  const navigate = useNavigate();
  const { draft, updateDraft, cartTotal } = useBookingStore();
  const { user } = useAuthStore();
  const [selectedMethod, setSelectedMethod] = useState<PaymentMethod>('wallet');
  const [cardNumber, setCardNumber] = useState('');
  const [cardName, setCardName] = useState('');
  const [cardExpiry, setCardExpiry] = useState('');
  const [cardCvc, setCardCvc] = useState('');
  const [processing, setProcessing] = useState(false);

  // Gateway Simulation State
  const [showGateway, setShowGateway] = useState(false);
  const [gatewayStep, setGatewayStep] = useState<'phone' | 'otp' | 'pin'>('phone');
  const [gatewayPhone, setGatewayPhone] = useState('');
  const [gatewayOtp, setGatewayOtp] = useState('');
  const [gatewayPin, setGatewayPin] = useState('');

  if (!draft) return <Navigate to="/search" replace />;

  const cartTotalAmount = cartTotal();
  const baseAmount = draft.price || cartTotalAmount;
  const deliveryFee = draft.type === 'food' ? 2.5 : 0;
  const total = baseAmount + deliveryFee;

  const handlePay = async () => {
    if (selectedMethod === 'wallet' && (user?.walletBalance ?? 0) < total) {
      toast.error('Insufficient wallet balance');
      return;
    }
    if (selectedMethod === 'bkash' || selectedMethod === 'nagad') {
      setShowGateway(true);
      setGatewayStep('phone');
      setGatewayPhone('');
      setGatewayOtp('');
      setGatewayPin('');
      return;
    }
    setProcessing(true);
    updateDraft({ paymentMethod: selectedMethod });
    await new Promise((r) => setTimeout(r, 1500));
    setProcessing(false);
    navigate('/booking/confirmation');
  };

  const handleGatewayProceed = async () => {
    if (gatewayStep === 'phone') {
      if (!gatewayPhone || gatewayPhone.length < 11) {
        toast.error('Please enter a valid mobile number');
        return;
      }
      setProcessing(true);
      await new Promise((r) => setTimeout(r, 800));
      setProcessing(false);
      setGatewayStep('otp');
    } else if (gatewayStep === 'otp') {
      if (!gatewayOtp || gatewayOtp.length < 4) {
        toast.error('Please enter the verification code');
        return;
      }
      setProcessing(true);
      await new Promise((r) => setTimeout(r, 800));
      setProcessing(false);
      setGatewayStep('pin');
    } else if (gatewayStep === 'pin') {
      if (!gatewayPin || gatewayPin.length < 4) {
        toast.error('Please enter your PIN');
        return;
      }
      setProcessing(true);
      await new Promise((r) => setTimeout(r, 1500));
      setProcessing(false);
      setShowGateway(false);
      updateDraft({ paymentMethod: selectedMethod });
      toast.success('Payment successful!');
      navigate('/booking/confirmation');
    }
  };

  return (
    <div className="container mx-auto px-4 py-6 max-w-3xl">
      <Button variant="ghost" size="sm" asChild className="mb-4">
        <Link to="/booking/address"><ChevronLeft className="mr-1 h-4 w-4" /> Back</Link>
      </Button>

      <BookingStepper currentStep={2} />

      <div className="grid gap-6 md:grid-cols-2">
        <div>
          <h3 className="font-semibold mb-4">Payment Method</h3>
          <div className="space-y-3">
            {paymentMethods.map((method) => (
              <button
                key={method.id}
                onClick={() => setSelectedMethod(method.id)}
                className={cn(
                  'w-full flex items-center gap-3 rounded-xl border-2 p-4 text-left transition-colors',
                  selectedMethod === method.id
                    ? 'border-primary bg-primary/5'
                    : 'border-border hover:border-primary/50'
                )}
              >
                <div className={cn(
                  'flex h-10 w-10 items-center justify-center rounded-lg shrink-0',
                  selectedMethod === method.id ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground'
                )}>
                  <method.icon className="h-5 w-5" />
                </div>
                <div className="flex-1">
                  <p className="font-semibold text-sm">{method.label}</p>
                  <p className="text-xs text-muted-foreground">{method.description}</p>
                </div>
                {selectedMethod === method.id && (
                  <div className="flex h-5 w-5 items-center justify-center rounded-full bg-primary text-primary-foreground">
                    <Check className="h-3 w-3" />
                  </div>
                )}
              </button>
            ))}
          </div>

          {selectedMethod === 'wallet' && (
            <Card className="mt-4">
              <CardContent className="p-4">
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Wallet Balance</span>
                  <span className="font-semibold">${user?.walletBalance.toFixed(2)}</span>
                </div>
                {total > (user?.walletBalance ?? 0) && (
                  <p className="mt-2 text-xs text-destructive">Insufficient balance. Please top up or choose another method.</p>
                )}
              </CardContent>
            </Card>
          )}

          {selectedMethod === 'card' && (
            <Card className="mt-4">
              <CardContent className="p-4 space-y-3">
                <div className="space-y-2">
                  <Label htmlFor="cardNumber">Card Number</Label>
                  <Input id="cardNumber" placeholder="1234 5678 9012 3456" value={cardNumber} onChange={(e) => setCardNumber(e.target.value)} />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="cardName">Cardholder Name</Label>
                  <Input id="cardName" placeholder="John Doe" value={cardName} onChange={(e) => setCardName(e.target.value)} />
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <div className="space-y-2">
                    <Label htmlFor="expiry">Expiry</Label>
                    <Input id="expiry" placeholder="MM/YY" value={cardExpiry} onChange={(e) => setCardExpiry(e.target.value)} />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="cvc">CVC</Label>
                    <Input id="cvc" placeholder="123" value={cardCvc} onChange={(e) => setCardCvc(e.target.value)} />
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {(selectedMethod === 'bkash' || selectedMethod === 'nagad') && (
            <Card className="mt-4">
              <CardContent className="p-4 space-y-3">
                <div className="space-y-2">
                  <Label htmlFor="mobile">Mobile Number</Label>
                  <Input id="mobile" placeholder="01XXXXXXXXX" />
                </div>
                <p className="text-xs text-muted-foreground">You will receive a verification code on your phone to confirm the payment.</p>
              </CardContent>
            </Card>
          )}
        </div>

        <div>
          <Card className="sticky top-20">
            <CardContent className="p-6 space-y-4">
              <h3 className="font-semibold">Order Summary</h3>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Service</span>
                  <span className="font-medium">{draft.serviceName}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Provider</span>
                  <span className="font-medium">{draft.providerName}</span>
                </div>
                {draft.date && (
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Date & Time</span>
                    <span className="font-medium">{draft.date} · {draft.time}</span>
                  </div>
                )}
                {draft.address && (
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Address</span>
                    <span className="font-medium text-right max-w-[60%] truncate">{draft.address.label}</span>
                  </div>
                )}
              </div>
              <Separator />
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Subtotal</span>
                  <span className="font-semibold">${baseAmount.toFixed(2)}</span>
                </div>
                {deliveryFee > 0 && (
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Delivery Fee</span>
                    <span className="font-semibold">${deliveryFee.toFixed(2)}</span>
                  </div>
                )}
                <Separator />
                <div className="flex justify-between text-base">
                  <span className="font-semibold">Total</span>
                  <span className="font-bold">${total.toFixed(2)}</span>
                </div>
              </div>
              <Button className="w-full" size="lg" onClick={handlePay} disabled={processing}>
                {processing ? 'Processing...' : `Pay $${total.toFixed(2)}`}
              </Button>
              <div className="flex items-center justify-center gap-1.5 text-xs text-muted-foreground">
                <Shield className="h-3 w-3" /> Secure payment powered by ServeNear
              </div>
            </CardContent>
          </Card>
        </div>
      </div>

      <Dialog open={showGateway} onOpenChange={(open) => !open && setShowGateway(false)}>
        <DialogContent className="p-0 overflow-hidden border-none max-w-sm rounded-3xl shadow-2xl">
          {/* Header */}
          <div
            className={cn(
              "p-6 text-white text-center flex flex-col items-center justify-center relative",
              selectedMethod === 'bkash' ? "bg-[#e2125d]" : "bg-[#f69220]"
            )}
          >
            <div className="text-xl font-bold tracking-wider uppercase mb-1">
              {selectedMethod === 'bkash' ? 'bKash' : 'Nagad'} Checkout
            </div>
            <div className="text-xs opacity-90">Merchant: ServeNear Ltd.</div>
            <div className="mt-3 bg-white/10 px-4 py-1.5 rounded-full text-sm font-semibold">
              Amount BDT: ৳{(total * 120).toFixed(2)}
            </div>
          </div>

          {/* Body */}
          <div className="p-6 space-y-4 bg-card">
            {gatewayStep === 'phone' && (
              <div className="space-y-3">
                <div className="space-y-1">
                  <Label htmlFor="gatePhone" className="text-xs text-muted-foreground uppercase font-semibold">
                    Your {selectedMethod === 'bkash' ? 'bKash' : 'Nagad'} Account Number
                  </Label>
                  <Input
                    id="gatePhone"
                    type="tel"
                    placeholder="e.g. 017XXXXXXXX"
                    className="h-11 rounded-xl text-center text-lg tracking-wider"
                    value={gatewayPhone}
                    onChange={(e) => setGatewayPhone(e.target.value)}
                  />
                </div>
                <p className="text-[10px] text-muted-foreground text-center leading-relaxed">
                  By proceeding, you agree to our Terms and Conditions of Mobile Payments.
                </p>
              </div>
            )}

            {gatewayStep === 'otp' && (
              <div className="space-y-3">
                <div className="space-y-1">
                  <Label htmlFor="gateOtp" className="text-xs text-muted-foreground uppercase font-semibold">
                    Enter Verification Code (OTP)
                  </Label>
                  <Input
                    id="gateOtp"
                    type="text"
                    placeholder="Enter 6-digit code"
                    className="h-11 rounded-xl text-center text-lg tracking-widest font-mono"
                    value={gatewayOtp}
                    onChange={(e) => setGatewayOtp(e.target.value)}
                  />
                </div>
                <p className="text-xs text-muted-foreground text-center">
                  We've sent an OTP to your mobile number.
                </p>
              </div>
            )}

            {gatewayStep === 'pin' && (
              <div className="space-y-3">
                <div className="space-y-1">
                  <Label htmlFor="gatePin" className="text-xs text-muted-foreground uppercase font-semibold">
                    Enter Account PIN
                  </Label>
                  <Input
                    id="gatePin"
                    type="password"
                    placeholder="••••"
                    maxLength={5}
                    className="h-11 rounded-xl text-center text-lg tracking-widest font-mono"
                    value={gatewayPin}
                    onChange={(e) => setGatewayPin(e.target.value)}
                  />
                </div>
                <p className="text-[10px] text-destructive/80 text-center font-medium">
                  Never share your bKash/Nagad PIN with anyone.
                </p>
              </div>
            )}

            {/* Buttons */}
            <div className="flex gap-3 pt-2">
              <Button
                variant="outline"
                className="flex-1 h-11 rounded-xl"
                onClick={() => setShowGateway(false)}
                disabled={processing}
              >
                Close
              </Button>
              <Button
                className={cn(
                  "flex-1 h-11 rounded-xl text-white font-semibold transition-all",
                  selectedMethod === 'bkash' ? "bg-[#e2125d] hover:bg-[#c10e4f]" : "bg-[#f69220] hover:bg-[#de7c10]"
                )}
                onClick={handleGatewayProceed}
                disabled={processing}
              >
                {processing ? <Loader2 className="h-4 w-4 animate-spin shrink-0" /> : 'Proceed'}
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
