import { useState } from 'react';
import { useNavigate, Navigate, Link } from 'react-router-dom';
import { MapPin, ChevronLeft, Plus, Home, Briefcase, MapPinned } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { BookingStepper } from '@/components/booking/booking-stepper';
import { useBookingStore } from '@/store/booking-store';
import { cn } from '@/lib/utils';

const typeIcons = { home: Home, work: Briefcase, other: MapPinned };

export function BookingAddressPage() {
  const navigate = useNavigate();
  const { draft, updateDraft, addresses, addAddress } = useBookingStore();
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [newAddress, setNewAddress] = useState({ label: '', fullAddress: '', type: 'home' as 'home' | 'work' | 'other' });

  if (!draft) return <Navigate to="/search" replace />;

  const handleSelect = (addressId: string) => {
    const address = addresses.find((a) => a.id === addressId);
    if (address) {
      updateDraft({ address });
      navigate('/booking/payment');
    }
  };

  const handleAddAddress = () => {
    if (!newAddress.label || !newAddress.fullAddress) return;
    addAddress({
      label: newAddress.label,
      fullAddress: newAddress.fullAddress,
      lat: 23.8103,
      lng: 90.4125,
      isDefault: addresses.length === 0,
      type: newAddress.type,
    });
    setNewAddress({ label: '', fullAddress: '', type: 'home' });
    setShowAddDialog(false);
  };

  return (
    <div className="container mx-auto px-4 py-6 max-w-3xl">
      <Button variant="ghost" size="sm" asChild className="mb-4">
        <Link to="/booking/datetime"><ChevronLeft className="mr-1 h-4 w-4" /> Back</Link>
      </Button>

      <BookingStepper currentStep={1} />

      <Card className="mb-4">
        <CardContent className="p-6">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <MapPin className="h-5 w-5 text-primary" />
              <h3 className="font-semibold">Select Address</h3>
            </div>
            <Button size="sm" variant="outline" onClick={() => setShowAddDialog(true)}>
              <Plus className="mr-1 h-4 w-4" /> Add New
            </Button>
          </div>

          <div className="space-y-3">
            {addresses.map((address) => {
              const Icon = typeIcons[address.type];
              return (
                <button
                  key={address.id}
                  onClick={() => handleSelect(address.id)}
                  className={cn(
                    'w-full flex items-start gap-3 rounded-xl border-2 p-4 text-left transition-colors hover:border-primary/50',
                    draft.address?.id === address.id && 'border-primary bg-primary/5'
                  )}
                >
                  <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary shrink-0">
                    <Icon className="h-5 w-5" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2">
                      <p className="font-semibold">{address.label}</p>
                      {address.isDefault && <Badge variant="secondary" className="text-[10px]">Default</Badge>}
                    </div>
                    <p className="text-sm text-muted-foreground mt-0.5">{address.fullAddress}</p>
                  </div>
                </button>
              );
            })}
          </div>
        </CardContent>
      </Card>

      <div className="flex justify-between">
        <Button variant="outline" asChild>
          <Link to="/booking/datetime">Back</Link>
        </Button>
      </div>

      <Dialog open={showAddDialog} onOpenChange={setShowAddDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add New Address</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="label">Label</Label>
              <Input id="label" placeholder="e.g. Home, Office" value={newAddress.label} onChange={(e) => setNewAddress({ ...newAddress, label: e.target.value })} />
            </div>
            <div className="space-y-2">
              <Label htmlFor="address">Full Address</Label>
              <Input id="address" placeholder="House, Road, Area, City" value={newAddress.fullAddress} onChange={(e) => setNewAddress({ ...newAddress, fullAddress: e.target.value })} />
            </div>
            <div className="space-y-2">
              <Label>Type</Label>
              <div className="flex gap-2">
                {(['home', 'work', 'other'] as const).map((type) => {
                  const Icon = typeIcons[type];
                  return (
                    <Button
                      key={type}
                      variant={newAddress.type === type ? 'default' : 'outline'}
                      size="sm"
                      onClick={() => setNewAddress({ ...newAddress, type })}
                      className="flex items-center gap-1.5 capitalize"
                    >
                      <Icon className="h-3.5 w-3.5" /> {type}
                    </Button>
                  );
                })}
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowAddDialog(false)}>Cancel</Button>
            <Button onClick={handleAddAddress}>Add Address</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
