import { useState, useEffect, useRef } from 'react';
import { useParams, Link } from 'react-router-dom';
import {
  MapPin, Phone, MessageCircle, Clock, CheckCircle,
  Star, ChevronLeft, Compass
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { useBookingStore } from '@/store/booking-store';
import { cn } from '@/lib/utils';

// SVG map street markers
const mapPoints = [
  { x: 50, y: 220 },
  { x: 120, y: 220 },
  { x: 120, y: 100 },
  { x: 220, y: 100 },
  { x: 220, y: 180 },
  { x: 300, y: 180 }
];

const getPositionAlongPath = (percent: number) => {
  const numSegments = mapPoints.length - 1;
  const segmentSize = 100 / numSegments;
  const segmentIndex = Math.min(
    numSegments - 1,
    Math.floor(percent / segmentSize)
  );
  
  const startPt = mapPoints[segmentIndex];
  const endPt = mapPoints[segmentIndex + 1];
  const segmentPercent = (percent - segmentIndex * segmentSize) / segmentSize;
  
  const x = startPt.x + (endPt.x - startPt.x) * segmentPercent;
  const y = startPt.y + (endPt.y - startPt.y) * segmentPercent;
  
  // Calculate simple rotation angle
  const dx = endPt.x - startPt.x;
  const dy = endPt.y - startPt.y;
  const angle = Math.atan2(dy, dx) * (180 / Math.PI);
  
  return { x, y, angle };
};

export function LiveTrackingPage() {
  const { bookingId } = useParams();
  const { bookings } = useBookingStore();
  
  const booking = bookings.find((b) => b.id === bookingId) || bookings[0];
  
  const [eta, setEta] = useState(15);
  const [progress, setProgress] = useState(15);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  // Status mapping
  let currentStep = 0;
  let statusText = 'Booking Confirmed';
  
  if (booking.status === 'confirmed') {
    currentStep = 2;
    statusText = 'Provider assigned and on the way';
  } else if (booking.status === 'in-progress') {
    currentStep = 4;
    statusText = 'Service has started';
  } else if (booking.status === 'completed') {
    currentStep = 5;
    statusText = 'Service completed successfully';
  }

  useEffect(() => {
    // Start tracking animations
    if (booking.status === 'completed') {
      setProgress(100);
      setEta(0);
    } else {
      setProgress(25);
      setEta(12);
    }

    intervalRef.current = setInterval(() => {
      setProgress((p) => {
        if (booking.status === 'completed') return 100;
        if (booking.status === 'in-progress') return Math.min(85, p + 1);
        if (p >= 98) return 98;
        return p + 2;
      });
      setEta((e) => {
        if (booking.status === 'completed') return 0;
        if (e <= 1) return 1;
        return e - 1;
      });
    }, 4000);

    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [booking.status]);

  const { x: carX, y: carY, angle: carAngle } = getPositionAlongPath(progress);

  const trackingSteps = [
    { label: 'Booking Confirmed', time: '10:00 AM' },
    { label: 'Provider Assigned', time: '10:05 AM' },
    { label: 'On the Way', time: '10:10 AM' },
    { label: 'Arrived', time: 'ETA 10:25 AM' },
    { label: 'Service Started', time: 'Pending' },
  ];

  return (
    <div className="container mx-auto px-4 py-6 max-w-4xl animate-fade-in">
      <Button variant="ghost" size="sm" asChild className="mb-4">
        <Link to="/dashboard/bookings"><ChevronLeft className="mr-1 h-4 w-4" /> Back</Link>
      </Button>

      <div className="grid gap-6 lg:grid-cols-2">
        <div>
          <Card className="overflow-hidden shadow-xl border-2 border-primary/10">
            {/* Styled Interactive SVG Vector Map */}
            <div className="relative h-80 bg-slate-950 overflow-hidden">
              <svg className="absolute inset-0 w-full h-full" xmlns="http://www.w3.org/2000/svg">
                {/* Background Grid Pattern */}
                <defs>
                  <pattern id="grid" width="30" height="30" patternUnits="userSpaceOnUse">
                    <path d="M 30 0 L 0 0 0 30" fill="none" stroke="rgba(255,255,255,0.03)" strokeWidth="1" />
                  </pattern>
                </defs>
                <rect width="100%" height="100%" fill="url(#grid)" />

                {/* Styled Map Blocks / Buildings */}
                <rect x="20" y="20" width="70" height="70" rx="6" fill="rgba(255,255,255,0.04)" />
                <rect x="150" y="20" width="120" height="50" rx="6" fill="rgba(255,255,255,0.04)" />
                <rect x="20" y="120" width="70" height="70" rx="6" fill="rgba(255,255,255,0.04)" />
                <rect x="150" y="160" width="50" height="80" rx="6" fill="rgba(255,255,255,0.04)" />
                <rect x="250" y="220" width="110" height="40" rx="6" fill="rgba(255,255,255,0.04)" />

                {/* Streets Layout */}
                <g stroke="rgba(255,255,255,0.08)" strokeWidth="24" strokeLinecap="round" strokeLinejoin="round" fill="none">
                  {/* Main Roads */}
                  <path d="M 10 220 L 370 220" />
                  <path d="M 120 10 L 120 290" />
                  <path d="M 220 10 L 220 290" />
                  <path d="M 10 100 L 370 100" />
                  <path d="M 220 180 L 370 180" />
                </g>
                <g stroke="rgba(15,23,42,0.6)" strokeWidth="20" strokeLinecap="round" strokeLinejoin="round" fill="none">
                  {/* Road Center Overlay */}
                  <path d="M 10 220 L 370 220" />
                  <path d="M 120 10 L 120 290" />
                  <path d="M 220 10 L 220 290" />
                  <path d="M 10 100 L 370 100" />
                  <path d="M 220 180 L 370 180" />
                </g>

                {/* Active Routing Path Line */}
                <path
                  d="M 50 220 L 120 220 L 120 100 L 220 100 L 220 180 L 300 180"
                  fill="none"
                  stroke="rgba(16,185,129,0.4)"
                  strokeWidth="6"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
                <path
                  d="M 50 220 L 120 220 L 120 100 L 220 100 L 220 180 L 300 180"
                  fill="none"
                  stroke="#10b981"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeDasharray="4 4"
                  className="animate-dash"
                />

                {/* Customer Destination Marker */}
                <g transform="translate(300, 180)">
                  <circle r="18" fill="rgba(16,185,129,0.2)" className="animate-ping" />
                  <circle r="10" fill="#10b981" />
                  <circle r="4" fill="#ffffff" />
                </g>

                {/* Provider Moving Marker */}
                <g
                  transform={`translate(${carX}, ${carY}) rotate(${carAngle})`}
                  className="transition-transform duration-500 ease-out"
                >
                  <circle r="22" fill="rgba(14,165,233,0.3)" />
                  <circle r="15" fill="#0ea5e9" className="shadow-lg" />
                  {/* Mini Vehicle Icon */}
                  <g transform="translate(-7, -7) scale(0.6)" fill="#ffffff">
                    <path d="M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-1h12v1c0 .55.45 1 1 1h1c.55 0 1-.45 1-1v-8l-2.08-5.99zM6.5 16c-.83 0-1.5-.67-1.5-1.5S5.67 13 6.5 13s1.5.67 1.5 1.5S7.33 16 6.5 16zm11 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 16zM5 10l1.5-4.5h11L19 10H5z" fill="#ffffff"/>
                  </g>
                </g>
              </svg>

              {/* Glassmorphism Overlays */}
              <div className="absolute bottom-4 left-4 right-4">
                <div className="rounded-xl bg-slate-900/80 border border-white/10 backdrop-blur-md p-3.5 shadow-lg text-white">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <Compass className="h-4 w-4 text-[#0ea5e9] animate-spin-slow" />
                      <span className="text-sm font-semibold tracking-wide">Live Route Simulator</span>
                    </div>
                    <Badge className="bg-success text-success-foreground border-none">
                      <span className="mr-1.5 h-1.5 w-1.5 rounded-full bg-success-foreground animate-pulse" />
                      Active
                    </Badge>
                  </div>
                  <p className="text-xs text-slate-300 mt-1 capitalize">{statusText}</p>
                </div>
              </div>

              <div className="absolute top-4 left-4 right-4">
                <div className="rounded-xl bg-slate-900/80 border border-white/10 backdrop-blur-md p-3.5 shadow-lg text-white">
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-slate-400">Estimated Arrival</span>
                    <span className="font-bold text-lg text-[#0ea5e9]">{eta} min</span>
                  </div>
                  <Progress value={progress} className="mt-2 h-1.5 bg-slate-800" />
                </div>
              </div>
            </div>
          </Card>

          <Card className="mt-4 shadow-md">
            <CardContent className="p-4">
              <div className="flex items-center gap-3">
                <Avatar className="h-14 w-14 border border-border">
                  <AvatarImage src={booking.providerAvatar} alt={booking.providerName} />
                  <AvatarFallback>{booking.providerName.charAt(0)}</AvatarFallback>
                </Avatar>
                <div className="flex-1">
                  <p className="font-semibold">{booking.providerName}</p>
                  <p className="text-sm text-muted-foreground">{booking.serviceName}</p>
                  <div className="mt-1 flex items-center gap-1 text-xs">
                    <Star className="h-3.5 w-3.5 fill-warning text-warning" />
                    <span className="font-semibold">4.9</span>
                    <span className="text-muted-foreground">· {booking.status === 'completed' ? 'Arrived' : `${eta} mins away`}</span>
                  </div>
                </div>
                <div className="flex gap-2">
                  <Button size="icon" variant="outline" className="rounded-full">
                    <a href={`tel:${booking.id}`}>
                      <Phone className="h-4 w-4" />
                    </a>
                  </Button>
                  <Button size="icon" variant="outline" className="rounded-full" asChild>
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
          <Card className="shadow-lg">
            <CardContent className="p-6">
              <h3 className="font-semibold mb-4 text-base">Tracking Timeline</h3>
              <div className="space-y-4">
                {trackingSteps.map((step, i) => {
                  const isCompleted = i < currentStep;
                  const isCurrent = i === currentStep;
                  return (
                    <div key={step.label} className="flex items-start gap-3">
                      <div className="flex flex-col items-center">
                        <div className={cn(
                          'flex h-9 w-9 items-center justify-center rounded-full border-2 transition-all duration-300',
                          isCompleted ? 'border-success bg-success text-success-foreground' :
                          isCurrent ? 'border-primary bg-primary text-primary-foreground scale-110 shadow-md' :
                          'border-muted bg-muted text-muted-foreground'
                        )}>
                          {isCompleted ? <CheckCircle className="h-4 w-4" /> : <MapPin className="h-4 w-4" />}
                        </div>
                        {i < trackingSteps.length - 1 && (
                          <div className={cn('w-0.5 h-8 mt-1 transition-colors duration-300', isCompleted ? 'bg-success' : 'bg-muted')} />
                        )}
                      </div>
                      <div className="flex-1 pt-1">
                        <p className={cn('text-sm font-medium transition-colors', (isCompleted || isCurrent) ? 'text-foreground font-semibold' : 'text-muted-foreground')}>
                          {step.label}
                        </p>
                        <p className="text-xs text-muted-foreground">{step.time}</p>
                        {isCurrent && (
                          <Badge className="mt-1 bg-primary text-primary-foreground text-[10px] animate-pulse">In Progress</Badge>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            </CardContent>
          </Card>

          {booking.status === 'in-progress' && (
            <Card className="mb-4 border-primary/20 bg-primary/5">
              <CardContent className="p-4 flex items-center justify-between">
                <div>
                  <p className="text-xs text-muted-foreground uppercase font-bold tracking-wider">Service Verification OTP</p>
                  <p className="text-3xl font-extrabold tracking-widest text-primary mt-1">4892</p>
                </div>
                <div className="text-right text-xs text-muted-foreground max-w-[180px] leading-relaxed">
                  Share this secure code with your provider to verify arrival and initiate the service.
                </div>
              </CardContent>
            </Card>
          )}

          <Card className="mt-4 shadow-md">
            <CardContent className="p-4">
              <div className="flex items-center gap-2 mb-2">
                <MapPin className="h-4 w-4 text-primary" />
                <p className="text-sm font-semibold">Service Address</p>
              </div>
              <p className="text-sm text-muted-foreground leading-relaxed">{booking.address}</p>
              <div className="mt-3 flex items-center gap-2 text-xs text-muted-foreground">
                <Clock className="h-4 w-4" />
                <span>{booking.date} at {booking.time}</span>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
