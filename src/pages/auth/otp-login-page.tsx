import { useState, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Phone, Loader2, ArrowLeft } from 'lucide-react';
import { toast } from 'sonner';
import { AuthLayout } from '@/components/auth/auth-layout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useAuthStore } from '@/store/auth-store';
import { InputOTP, InputOTPGroup, InputOTPSlot } from '@/components/ui/input-otp';

export function OtpLoginPage() {
  const [step, setStep] = useState<'phone' | 'otp'>('phone');
  const [phone, setPhone] = useState('');
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [resendTimer, setResendTimer] = useState(0);
  const navigate = useNavigate();
  const { loginWithOtp } = useAuthStore();
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const sendOtp = async () => {
    if (phone.length < 10) {
      toast.error('Please enter a valid phone number');
      return;
    }
    setLoading(true);
    await new Promise((r) => setTimeout(r, 800));
    setLoading(false);
    setStep('otp');
    setResendTimer(60);
    timerRef.current = setInterval(() => {
      setResendTimer((t) => {
        if (t <= 1 && timerRef.current) {
          clearInterval(timerRef.current);
          return 0;
        }
        return t - 1;
      });
    }, 1000);
    toast.success('OTP sent to your phone');
  };

  const verifyOtp = async () => {
    if (otp.length !== 6) {
      toast.error('Please enter the 6-digit code');
      return;
    }
    setLoading(true);
    try {
      await loginWithOtp(phone);
      toast.success('Login successful!');
      navigate('/dashboard');
    } catch {
      toast.error('Invalid OTP. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const resendOtp = () => {
    if (resendTimer > 0) return;
    sendOtp();
  };

  return (
    <AuthLayout title="Login with OTP" subtitle="Quick and secure login using your phone number">
      {step === 'phone' ? (
        <div className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="phone">Phone Number</Label>
            <div className="relative">
              <Phone className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                id="phone"
                type="tel"
                placeholder="+880 1700 000000"
                className="pl-10"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
              />
            </div>
          </div>
          <Button className="w-full" onClick={sendOtp} disabled={loading}>
            {loading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : null}
            Send OTP
          </Button>
        </div>
      ) : (
        <div className="space-y-6">
          <div className="space-y-2">
            <Label>Enter Verification Code</Label>
            <p className="text-sm text-muted-foreground">We sent a 6-digit code to {phone}</p>
          </div>
          <div className="flex justify-center">
            <InputOTP maxLength={6} value={otp} onChange={(v) => setOtp(v)}>
              <InputOTPGroup>
                <InputOTPSlot index={0} />
                <InputOTPSlot index={1} />
                <InputOTPSlot index={2} />
                <InputOTPSlot index={3} />
                <InputOTPSlot index={4} />
                <InputOTPSlot index={5} />
              </InputOTPGroup>
            </InputOTP>
          </div>
          <Button className="w-full" onClick={verifyOtp} disabled={loading}>
            {loading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : null}
            Verify & Login
          </Button>
          <div className="flex items-center justify-between text-sm">
            <button onClick={() => setStep('phone')} className="flex items-center text-muted-foreground hover:text-foreground">
              <ArrowLeft className="mr-1 h-4 w-4" /> Change number
            </button>
            <button onClick={resendOtp} disabled={resendTimer > 0} className="text-primary hover:underline disabled:opacity-50">
              {resendTimer > 0 ? `Resend in ${resendTimer}s` : 'Resend OTP'}
            </button>
          </div>
        </div>
      )}

      <p className="text-center text-sm text-muted-foreground">
        Prefer password login?{' '}
        <Link to="/login" className="font-semibold text-primary hover:underline">Sign in here</Link>
      </p>
    </AuthLayout>
  );
}
