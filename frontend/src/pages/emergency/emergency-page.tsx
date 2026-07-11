import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Link, useNavigate } from 'react-router-dom';
import {
  Ambulance, Flame, Shield, Wrench, Zap, Car, Phone, Clock,
  AlertTriangle, ArrowRight, Radio, Siren
} from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { emergencyServices } from '@/lib/mock-data';
import { useBookingStore } from '@/store/booking-store';
import { toast } from 'sonner';

const iconMap: Record<string, React.ComponentType<{ className?: string }>> = {
  Ambulance, Flame, Shield, Wrench, Zap, Car,
};

const categoryColors: Record<string, string> = {
  medical: 'bg-red-100 text-red-600 dark:bg-red-900/30 dark:text-red-400',
  fire: 'bg-orange-100 text-orange-600 dark:bg-orange-900/30 dark:text-orange-400',
  police: 'bg-blue-100 text-blue-600 dark:bg-blue-900/30 dark:text-blue-400',
  plumber: 'bg-cyan-100 text-cyan-600 dark:bg-cyan-900/30 dark:text-cyan-400',
  electrician: 'bg-amber-100 text-amber-600 dark:bg-amber-900/30 dark:text-amber-400',
  roadside: 'bg-purple-100 text-purple-600 dark:bg-purple-900/30 dark:text-purple-400',
};

export function EmergencyPage() {
  const navigate = useNavigate();
  const { addBooking } = useBookingStore();
  const [sosActive, setSosActive] = useState(false);
  const [sosStep, setSosStep] = useState<'triggering' | 'dispatched'>('triggering');
  const [countdown, setCountdown] = useState(5);

  const handleCall = (name: string, number: string) => {
    toast.success(`Calling ${name}...`, { description: `Dialing ${number}` });
  };

  const triggerSos = () => {
    setSosActive(true);
    setSosStep('triggering');
    setCountdown(5);
  };

  useEffect(() => {
    let timer: ReturnType<typeof setInterval>;
    if (sosActive && sosStep === 'triggering') {
      timer = setInterval(() => {
        setCountdown((c) => {
          if (c <= 1) {
            clearInterval(timer);
            setSosStep('dispatched');
            // Create mock ambulance booking immediately
            addBooking({
              id: 'sos-1',
              serviceId: 'p4',
              serviceName: 'Emergency Ambulance',
              providerId: 'p4',
              providerName: 'Aman Ambulance Corp',
              providerAvatar: 'https://images.pexels.com/photos/5407206/pexels-photo-5407206.jpeg?auto=compress&cs=tinysrgb&w=200',
              date: new Date().toISOString().split('T')[0],
              time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
              address: 'Current Location (GPS)',
              status: 'confirmed',
              amount: 0,
              paymentMethod: 'cash',
              type: 'service',
              createdAt: new Date().toISOString().split('T')[0]
            });
            toast.success('Ambulance Dispatched!');
            return 0;
          }
          return c - 1;
        });
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [sosActive, sosStep]);

  return (
    <div className="container mx-auto px-4 py-6 relative">
      <AnimatePresence>
        {sosActive && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-50 flex flex-col items-center justify-center bg-black/95 text-white p-6"
          >
            {/* Animated Radar Pulse Rings */}
            <div className="relative mb-12 flex items-center justify-center">
              <motion.div
                animate={{ scale: [1, 2.5, 1], opacity: [0.6, 0, 0.6] }}
                transition={{ duration: 3, repeat: Infinity }}
                className="absolute h-36 w-36 rounded-full border-4 border-destructive bg-destructive/10"
              />
              <motion.div
                animate={{ scale: [1, 1.8, 1], opacity: [0.8, 0, 0.8] }}
                transition={{ duration: 2, repeat: Infinity, delay: 0.5 }}
                className="absolute h-36 w-36 rounded-full border-4 border-destructive bg-destructive/10"
              />
              <div className="z-10 flex h-28 w-28 items-center justify-center rounded-full bg-destructive text-white shadow-2xl animate-pulse">
                <Siren className="h-14 w-14 animate-bounce" />
              </div>
            </div>

            {sosStep === 'triggering' ? (
              <div className="text-center space-y-6 max-w-sm">
                <div className="space-y-2">
                  <h2 className="text-3xl font-extrabold tracking-wider text-destructive uppercase animate-pulse">
                    Triggering SOS Alarm
                  </h2>
                  <p className="text-sm text-slate-400">
                    Connecting to emergency response grid and sharing your live location...
                  </p>
                </div>
                
                {/* Giant countdown timer */}
                <div className="text-7xl font-extrabold text-white font-mono tracking-tighter">
                  00:0{countdown}
                </div>

                <Button
                  size="lg"
                  variant="outline"
                  onClick={() => setSosActive(false)}
                  className="rounded-full px-8 bg-transparent text-white border-white/20 hover:bg-white/10 hover:text-white"
                >
                  Cancel Alarm
                </Button>
              </div>
            ) : (
              <div className="text-center space-y-6 max-w-md animate-scale-up">
                <div className="space-y-2">
                  <h2 className="text-3xl font-extrabold tracking-wider text-[#10b981] uppercase flex items-center justify-center gap-2">
                    SOS Alarm Dispatched!
                  </h2>
                  <p className="text-sm text-slate-300">
                    An emergency responder has been assigned and is heading to your GPS coordinates.
                  </p>
                </div>

                <div className="bg-white/5 border border-white/10 rounded-2xl p-4 text-left space-y-3">
                  <div className="flex justify-between items-center text-xs text-slate-400">
                    <span>Responder Unit</span>
                    <span className="font-semibold text-white">Aman Ambulance Corp</span>
                  </div>
                  <div className="flex justify-between items-center text-xs text-slate-400">
                    <span>Estimated Arrival</span>
                    <span className="font-semibold text-[#10b981]">~8 mins</span>
                  </div>
                  <div className="flex justify-between items-center text-xs text-slate-400">
                    <span>Signal Status</span>
                    <span className="font-semibold text-[#0ea5e9]">GPS Tracking Active</span>
                  </div>
                </div>

                <div className="flex gap-3">
                  <Button
                    size="lg"
                    variant="outline"
                    onClick={() => setSosActive(false)}
                    className="flex-1 rounded-full bg-transparent text-white border-white/20 hover:bg-white/10 hover:text-white"
                  >
                    Close Alarm
                  </Button>
                  <Button
                    size="lg"
                    onClick={() => {
                      setSosActive(false);
                      navigate('/track/sos-1');
                    }}
                    className="flex-1 rounded-full bg-[#10b981] hover:bg-[#0d9668] text-white font-semibold"
                  >
                    Track Dispatch
                  </Button>
                </div>
              </div>
            )}
          </motion.div>
        )}
      </AnimatePresence>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="mb-8"
      >
        <div className="flex items-center gap-3 mb-2">
          <div className="flex h-12 w-12 items-center justify-center rounded-full bg-destructive/10 text-destructive">
            <AlertTriangle className="h-6 w-6 animate-pulse" />
          </div>
          <div>
            <h1 className="text-3xl font-bold tracking-tight">Emergency Services</h1>
            <p className="text-muted-foreground">24/7 emergency assistance at your fingertips</p>
          </div>
        </div>
      </motion.div>

      <div className="grid gap-6 md:grid-cols-2 mb-8">
        <Card className="border-destructive/20 bg-gradient-to-br from-red-50 to-orange-50 dark:from-red-950/20 dark:to-orange-950/20">
          <CardContent className="p-6 flex flex-col justify-between h-full">
            <div>
              <h2 className="text-xl font-bold flex items-center gap-2">
                <Siren className="h-5 w-5 text-destructive animate-bounce" /> Call Direct Line
              </h2>
              <p className="text-sm text-muted-foreground mt-2">Tap to call emergency national services directly. Available 24/7 free of charge.</p>
            </div>
            <Button size="lg" variant="destructive" onClick={() => handleCall('Emergency Hotline', '199')} className="mt-6 w-full rounded-2xl shadow-md">
              <Phone className="mr-2 h-5 w-5" /> Call Hotline 199
            </Button>
          </CardContent>
        </Card>

        <Card className="border-destructive/30 bg-destructive/5 relative overflow-hidden">
          <CardContent className="p-6 flex flex-col justify-between h-full">
            <div>
              <h2 className="text-xl font-bold flex items-center gap-2">
                <Radio className="h-5 w-5 text-destructive" /> One-Tap SOS Dispatch
              </h2>
              <p className="text-sm text-muted-foreground mt-2">Instant dispatch of nearest emergency responder to your live GPS coordinates.</p>
            </div>
            <Button size="lg" className="mt-6 w-full rounded-2xl bg-destructive hover:bg-destructive/90 text-white font-bold tracking-wide shadow-lg border-none" onClick={triggerSos}>
              <Siren className="mr-2 h-5 w-5 animate-pulse" /> Trigger SOS Alarm
            </Button>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {emergencyServices.map((service, i) => {
          const Icon = iconMap[service.icon] ?? Ambulance;
          return (
            <motion.div
              key={service.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.05 }}
            >
              <Card className="group h-full overflow-hidden transition-all hover:-translate-y-1 hover:shadow-lg">
                <CardContent className="p-5">
                  <div className="flex items-start justify-between mb-4">
                    <div className={`flex h-14 w-14 items-center justify-center rounded-2xl ${categoryColors[service.category]} transition-transform group-hover:scale-110`}>
                      <Icon className="h-7 w-7" />
                    </div>
                    {service.available247 && (
                      <Badge className="bg-success text-success-foreground text-[10px]">24/7</Badge>
                    )}
                  </div>
                  <h3 className="font-semibold text-lg">{service.name}</h3>
                  <div className="mt-2 flex items-center gap-3 text-xs text-muted-foreground">
                    <span className="flex items-center gap-1"><Clock className="h-3 w-3" />{service.responseTime}</span>
                  </div>
                  <div className="mt-4 flex gap-2">
                    <Button className="flex-1" variant="destructive" onClick={() => handleCall(service.name, service.number)}>
                      <Phone className="mr-2 h-4 w-4" /> {service.number}
                    </Button>
                    <Button variant="outline" size="icon" asChild>
                      <Link to="/search"><ArrowRight className="h-4 w-4" /></Link>
                    </Button>
                  </div>
                </CardContent>
              </Card>
            </motion.div>
          );
        })}
      </div>

      <Card className="mt-6">
        <CardContent className="p-6">
          <h3 className="font-semibold mb-3">Emergency Safety Tips</h3>
          <div className="grid gap-3 sm:grid-cols-2">
            {[
              'Stay calm and provide your exact location to the emergency responder',
              'Keep your phone charged and location services enabled',
              'Save important emergency numbers in your contacts',
              'Share your live location with trusted contacts during emergencies',
            ].map((tip) => (
              <div key={tip} className="flex items-start gap-2 text-sm">
                <div className="flex h-5 w-5 items-center justify-center rounded-full bg-primary/10 text-primary shrink-0 mt-0.5">
                  <span className="text-xs">✓</span>
                </div>
                <span className="text-muted-foreground">{tip}</span>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
