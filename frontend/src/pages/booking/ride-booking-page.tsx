import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useBookingStore } from '@/store/booking-store';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Bike, Car, Truck, Navigation, MapPin, Search, ArrowRight, Loader2 } from 'lucide-react';
import { toast } from 'sonner';

const rideTypes = [
  { id: 'ride-bike', name: 'Moto Ride', price: 3.50, time: '3 mins', icon: Bike, capacity: '1 Pax' },
  { id: 'ride-economy', name: 'Economy Car', price: 8.00, time: '5 mins', icon: Car, capacity: '4 Pax' },
  { id: 'ride-premium', name: 'Premium Sedan', price: 15.00, time: '4 mins', icon: Car, capacity: '4 Pax' },
  { id: 'ride-truck', name: 'Mini Truck / Delivery', price: 28.00, time: '10 mins', icon: Truck, capacity: '1 Ton' }
];

const mockLocations = [
  'Banani Road 11, Dhaka',
  'Gulshan 2 Circle, Dhaka',
  'Dhanmondi 27, Dhaka',
  'Hazrat Shahjalal International Airport, Dhaka',
  'Uttara Sector 4, Dhaka',
  'Motijheel Commercial Area, Dhaka'
];

export function RideBookingPage() {
  const navigate = useNavigate();
  const { addBooking } = useBookingStore();

  const [pickup, setPickup] = useState('');
  const [dropoff, setDropoff] = useState('');
  const [selectedRide, setSelectedRide] = useState('ride-economy');
  const [pickupSuggestions, setPickupSuggestions] = useState<string[]>([]);
  const [dropoffSuggestions, setDropoffSuggestions] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);

  const handlePickupChange = (val: string) => {
    setPickup(val);
    if (val.trim().length > 1) {
      setPickupSuggestions(mockLocations.filter(loc => loc.toLowerCase().includes(val.toLowerCase())));
    } else {
      setPickupSuggestions([]);
    }
  };

  const handleDropoffChange = (val: string) => {
    setDropoff(val);
    if (val.trim().length > 1) {
      setDropoffSuggestions(mockLocations.filter(loc => loc.toLowerCase().includes(val.toLowerCase())));
    } else {
      setDropoffSuggestions([]);
    }
  };

  const handleBookRide = async () => {
    if (!pickup.trim() || !dropoff.trim()) {
      toast.error('Please specify both pickup and drop-off addresses');
      return;
    }

    setLoading(true);
    await new Promise((r) => setTimeout(r, 1500));

    const selectedDetails = rideTypes.find(r => r.id === selectedRide) || rideTypes[1];
    const bookingId = `ride-${Date.now()}`;

    // Create booking in store
    addBooking({
      id: bookingId,
      serviceId: selectedRide,
      serviceName: `${selectedDetails.name} Booking`,
      providerId: 'driver-1',
      providerName: 'Kashem Driver',
      providerAvatar: 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=200',
      date: new Date().toISOString().split('T')[0],
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      address: `From: ${pickup} | To: ${dropoff}`,
      status: 'confirmed',
      amount: selectedDetails.price,
      paymentMethod: 'wallet',
      type: 'service',
      createdAt: new Date().toISOString().split('T')[0]
    });

    setLoading(false);
    toast.success('Driver Dispatched!', { description: 'Kashem Driver is heading your way' });
    navigate(`/track/${bookingId}`);
  };

  return (
    <div className="container mx-auto px-4 py-6 space-y-6">
      <div>
        <h1 className="text-3xl font-extrabold tracking-tight flex items-center gap-2">
          <Navigation className="h-8 w-8 text-primary animate-pulse" /> City Ride
        </h1>
        <p className="text-muted-foreground mt-1">Simulate safe, comfortable rides across the city instantly</p>
      </div>

      <div className="grid gap-6 lg:grid-cols-12">
        {/* Booking Card */}
        <Card className="lg:col-span-5 rounded-3xl border border-border/40 shadow-xl p-5">
          <CardContent className="space-y-5 p-0">
            {/* Pickup Input */}
            <div className="space-y-1.5 relative">
              <Label htmlFor="pickup" className="text-xs font-semibold text-muted-foreground uppercase flex items-center gap-1">
                <MapPin className="h-3.5 w-3.5 text-emerald-500 animate-bounce" /> Pickup Location
              </Label>
              <div className="relative">
                <Input
                  id="pickup"
                  placeholder="Enter pickup point..."
                  value={pickup}
                  onChange={(e) => handlePickupChange(e.target.value)}
                  className="rounded-xl pl-3 pr-10"
                />
                <Search className="absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              </div>
              {pickupSuggestions.length > 0 && (
                <div className="absolute z-10 w-full bg-background border border-border rounded-xl mt-1 shadow-lg max-h-40 overflow-y-auto">
                  {pickupSuggestions.map(s => (
                    <button
                      key={s}
                      onClick={() => { setPickup(s); setPickupSuggestions([]); }}
                      className="w-full text-left px-4 py-2 text-xs hover:bg-muted font-medium transition-colors"
                    >
                      {s}
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Dropoff Input */}
            <div className="space-y-1.5 relative">
              <Label htmlFor="dropoff" className="text-xs font-semibold text-muted-foreground uppercase flex items-center gap-1">
                <MapPin className="h-3.5 w-3.5 text-destructive" /> Destination Point
              </Label>
              <div className="relative">
                <Input
                  id="dropoff"
                  placeholder="Where to?"
                  value={dropoff}
                  onChange={(e) => handleDropoffChange(e.target.value)}
                  className="rounded-xl pl-3 pr-10"
                />
                <Search className="absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              </div>
              {dropoffSuggestions.length > 0 && (
                <div className="absolute z-10 w-full bg-background border border-border rounded-xl mt-1 shadow-lg max-h-40 overflow-y-auto">
                  {dropoffSuggestions.map(s => (
                    <button
                      key={s}
                      onClick={() => { setDropoff(s); setDropoffSuggestions([]); }}
                      className="w-full text-left px-4 py-2 text-xs hover:bg-muted font-medium transition-colors"
                    >
                      {s}
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Vehicle Selector Grid */}
            <div className="space-y-2">
              <Label className="text-xs font-semibold text-muted-foreground uppercase">Available Ride Categories</Label>
              <div className="grid grid-cols-2 gap-3">
                {rideTypes.map((type) => {
                  const Icon = type.icon;
                  const selected = selectedRide === type.id;
                  return (
                    <button
                      key={type.id}
                      onClick={() => setSelectedRide(type.id)}
                      className={`flex flex-col items-start p-3.5 rounded-2xl border text-left transition-all ${
                        selected ? 'border-primary bg-primary/5 shadow-md scale-[1.02]' : 'border-border/60 hover:bg-muted/30'
                      }`}
                    >
                      <div className="flex justify-between w-full items-start">
                        <div className={`p-2 rounded-xl ${selected ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground'}`}>
                          <Icon className="h-5 w-5" />
                        </div>
                        <Badge variant="secondary" className="text-[9px] px-1.5 py-0">{type.capacity}</Badge>
                      </div>
                      <p className="font-bold text-sm mt-3">{type.name}</p>
                      <div className="flex justify-between w-full items-center mt-1 text-[11px] text-muted-foreground font-medium">
                        <span>{type.time} away</span>
                        <span className="font-bold text-foreground text-xs">৳{type.price.toFixed(2)}</span>
                      </div>
                    </button>
                  );
                })}
              </div>
            </div>

            <Button onClick={handleBookRide} disabled={loading} className="w-full h-11 rounded-xl text-base font-bold shadow-lg mt-2">
              {loading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" /> Matching Drivers...
                </>
              ) : (
                <>
                  Request Dispatch <ArrowRight className="ml-2 h-4 w-4" />
                </>
              )}
            </Button>
          </CardContent>
        </Card>

        {/* Map Preview Panel */}
        <Card className="lg:col-span-7 rounded-3xl border border-border/40 overflow-hidden relative min-h-[400px] flex items-center justify-center bg-slate-950">
          <div className="absolute inset-0 opacity-20 pointer-events-none">
            {/* Simulated Grid Streets */}
            <svg width="100%" height="100%">
              <defs>
                <pattern id="street-grid" width="40" height="40" patternUnits="userSpaceOnUse">
                  <path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(255,255,255,0.4)" strokeWidth="1"/>
                </pattern>
              </defs>
              <rect width="100%" height="100%" fill="url(#street-grid)" />
            </svg>
          </div>
          
          <div className="text-center z-10 max-w-sm space-y-3 p-6 text-white">
            <div className="inline-flex h-14 w-14 items-center justify-center rounded-full bg-primary/10 text-primary border border-primary/20 animate-pulse">
              <Navigation className="h-7 w-7 rotate-45" />
            </div>
            <h3 className="text-lg font-bold">Interactive Route Estimate</h3>
            <p className="text-xs text-slate-400">
              Input pickup and destination address points to calculate distance, travel times, and generate real-time dispatch routes.
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
}
