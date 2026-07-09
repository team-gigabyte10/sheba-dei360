import { useState, useEffect, useRef } from 'react';
import { useParams, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  MapPin, Navigation, Phone, MessageCircle, Clock, CheckCircle,
  Car, Star, ChevronLeft,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { userBookings } from '@/lib/mock-data';
import { cn } from '@/lib/utils';

const trackingSteps = [
  { label: 'Booking Confirmed', icon: CheckCircle, time: '10:00 AM' },
  { label: 'Provider Assigned', icon: User, time: '10:05 AM' },
  { label: 'On the Way', icon: Car, time: '10:10 AM' },
  { label: 'Arrived', icon: MapPin, time: 'ETA 10:25 AM' },
  { label: 'Service Started', icon: Star, time: 'Pending' },
];

function User(props: React.ComponentProps<'svg'>) {
  return <svg {...props} />;
}

export function LiveTrackingPage() {
  const { bookingId } = useParams();
  const booking = userBookings.find((b) => b.id === bookingId) || userBookings[0];
  const [currentStep] = useState(2);
  const [eta, setEta] = useState(15);
  const [progress, setProgress] = useState(40);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    intervalRef.current = setInterval(() => {
      setEta((e) => Math.max(0, e - 1));
      setProgress((p) => Math.min(100, p + 2));
    }, 3000);
    return () => { if (intervalRef.current) clearInterval(intervalRef.current); };
  }, []);

  return (
    <div className="container mx-auto px-4 py-6 max-w-4xl">
      <Button variant="ghost" size="sm" asChild className="mb-4">
        <Link to="/dashboard/bookings"><ChevronLeft className="mr-1 h-4 w-4" /> Back</Link>
      </Button>

      <div className="grid gap-6 lg:grid-cols-2">
        <div>
          <Card className="overflow-hidden">
            <div className="relative h-80 bg-gradient-to-br from-sky-100 to-emerald-100 dark:from-slate-800 dark:to-slate-900">
              <div className="absolute inset-0 opacity-20" style={{
                backgroundImage: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23000' fill-opacity='0.1'%3E%3Cpath d='M30 30c0-11.046-8.954-20-20-20v40c11.046 0 20-8.954 20-20zm10 0c0 11.046 8.954 20 20 20V10c-11.046 0-20 8.954-20 20z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
              }} />

              <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2">
                <motion.div
                  animate={{ scale: [1, 1.2, 1] }}
                  transition={{ duration: 2, repeat: Infinity }}
                  className="flex h-16 w-16 items-center justify-center rounded-full bg-primary text-primary-foreground shadow-xl"
                >
                  <Car className="h-8 w-8" />
                </motion.div>
                <div className="absolute -inset-4 rounded-full border-2 border-primary/30 animate-ping" />
              </div>

              <div className="absolute bottom-4 left-4 right-4">
                <div className="rounded-xl bg-background/90 backdrop-blur p-3 shadow-lg">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <Navigation className="h-4 w-4 text-primary" />
                      <span className="text-sm font-semibold">Live Tracking</span>
                    </div>
                    <Badge className="bg-success text-success-foreground">
                      <span className="mr-1 h-1.5 w-1.5 rounded-full bg-success-foreground animate-pulse" />
                      Live
                    </Badge>
                  </div>
                </div>
              </div>

              <div className="absolute top-4 left-4 right-4">
                <div className="rounded-xl bg-background/90 backdrop-blur p-3 shadow-lg">
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-muted-foreground">ETA</span>
                    <span className="font-bold text-lg">{eta} min</span>
                  </div>
                  <Progress value={progress} className="mt-2 h-1.5" />
                </div>
              </div>
            </div>
          </Card>

          <Card className="mt-4">
            <CardContent className="p-4">
              <div className="flex items-center gap-3">
                <Avatar className="h-14 w-14">
                  <AvatarImage src={booking.providerAvatar} alt={booking.providerName} />
                  <AvatarFallback>{booking.providerName.charAt(0)}</AvatarFallback>
                </Avatar>
                <div className="flex-1">
                  <p className="font-semibold">{booking.providerName}</p>
                  <p className="text-sm text-muted-foreground">{booking.serviceName}</p>
                  <div className="mt-1 flex items-center gap-1 text-xs">
                    <Star className="h-3 w-3 fill-warning text-warning" />
                    <span className="font-semibold">4.9</span>
                    <span className="text-muted-foreground">· 3 min away</span>
                  </div>
                </div>
                <div className="flex gap-2">
                  <Button size="icon" variant="outline">
                    <Phone className="h-4 w-4" />
                  </Button>
                  <Button size="icon" variant="outline" asChild>
                    <Link to={`/chat/${booking.providerId}`}>
                      <MessageCircle className="h-4 w-4" />
                    </Link>
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <div>
          <Card>
            <CardContent className="p-6">
              <h3 className="font-semibold mb-4">Tracking Timeline</h3>
              <div className="space-y-4">
                {trackingSteps.map((step, i) => {
                  const isCompleted = i < currentStep;
                  const isCurrent = i === currentStep;
                  return (
                    <div key={step.label} className="flex items-start gap-3">
                      <div className="flex flex-col items-center">
                        <div className={cn(
                          'flex h-9 w-9 items-center justify-center rounded-full border-2 transition-colors',
                          isCompleted ? 'border-success bg-success text-success-foreground' :
                          isCurrent ? 'border-primary bg-primary text-primary-foreground' :
                          'border-muted bg-muted text-muted-foreground'
                        )}>
                          <step.icon className="h-4 w-4" />
                        </div>
                        {i < trackingSteps.length - 1 && (
                          <div className={cn('w-0.5 h-8 mt-1', isCompleted ? 'bg-success' : 'bg-muted')} />
                        )}
                      </div>
                      <div className="flex-1 pt-1">
                        <p className={cn('text-sm font-medium', !isCompleted && !isCurrent && 'text-muted-foreground')}>
                          {step.label}
                        </p>
                        <p className="text-xs text-muted-foreground">{step.time}</p>
                        {isCurrent && (
                          <Badge className="mt-1 bg-primary text-primary-foreground text-[10px]">In Progress</Badge>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            </CardContent>
          </Card>

          <Card className="mt-4">
            <CardContent className="p-4">
              <div className="flex items-center gap-2 mb-2">
                <MapPin className="h-4 w-4 text-primary" />
                <p className="text-sm font-semibold">Service Address</p>
              </div>
              <p className="text-sm text-muted-foreground">{booking.address}</p>
              <div className="mt-3 flex items-center gap-2">
                <Clock className="h-4 w-4 text-muted-foreground" />
                <p className="text-sm text-muted-foreground">{booking.date} at {booking.time}</p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
