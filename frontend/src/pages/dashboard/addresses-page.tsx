import { useState } from 'react';
import { Plus, Home, Briefcase, MapPinned, Trash2, Star } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { useBookingStore } from '@/store/booking-store';
import { toast } from 'sonner';

const typeIcons = { home: Home, work: Briefcase, other: MapPinned };

export function AddressesPage() {
  const { addresses, addAddress, removeAddress, setDefaultAddress } = useBookingStore();
  const [showAdd, setShowAdd] = useState(false);
  const [newAddr, setNewAddr] = useState({ label: '', fullAddress: '', type: 'home' as 'home' | 'work' | 'other' });

  const handleAdd = () => {
    if (!newAddr.label || !newAddr.fullAddress) {
      toast.error('Please fill all fields');
      return;
    }
    addAddress({
      label: newAddr.label,
      fullAddress: newAddr.fullAddress,
      lat: 23.8103,
      lng: 90.4125,
      isDefault: addresses.length === 0,
      type: newAddr.type,
    });
    setNewAddr({ label: '', fullAddress: '', type: 'home' });
    setShowAdd(false);
    toast.success('Address added successfully');
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Saved Addresses</h1>
          <p className="text-muted-foreground mt-1">Manage your delivery and service addresses</p>
        </div>
        <Button onClick={() => setShowAdd(true)}>
          <Plus className="mr-1 h-4 w-4" /> Add Address
        </Button>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        {addresses.map((address) => {
          const Icon = typeIcons[address.type];
          return (
            <Card key={address.id}>
              <CardContent className="p-4">
                <div className="flex items-start gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary shrink-0">
                    <Icon className="h-5 w-5" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2">
                      <p className="font-semibold">{address.label}</p>
                      {address.isDefault && <Badge variant="secondary" className="text-[10px]">Default</Badge>}
                    </div>
                    <p className="text-sm text-muted-foreground mt-1">{address.fullAddress}</p>
                    <div className="mt-3 flex gap-2">
                      {!address.isDefault && (
                        <Button size="sm" variant="outline" onClick={() => { setDefaultAddress(address.id); toast.success('Default address updated'); }}>
                          <Star className="mr-1 h-3 w-3" /> Set Default
                        </Button>
                      )}
                      <Button size="sm" variant="ghost" className="text-destructive" onClick={() => { removeAddress(address.id); toast.success('Address removed'); }}>
                        <Trash2 className="mr-1 h-3 w-3" /> Remove
                      </Button>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <Dialog open={showAdd} onOpenChange={setShowAdd}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add New Address</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="label">Label</Label>
              <Input id="label" placeholder="e.g. Home, Office" value={newAddr.label} onChange={(e) => setNewAddr({ ...newAddr, label: e.target.value })} />
            </div>
            <div className="space-y-2">
              <Label htmlFor="addr">Full Address</Label>
              <Input id="addr" placeholder="House, Road, Area, City" value={newAddr.fullAddress} onChange={(e) => setNewAddr({ ...newAddr, fullAddress: e.target.value })} />
            </div>
            <div className="space-y-2">
              <Label>Type</Label>
              <div className="flex gap-2">
                {(['home', 'work', 'other'] as const).map((type) => {
                  const Icon = typeIcons[type];
                  return (
                    <Button key={type} variant={newAddr.type === type ? 'default' : 'outline'} size="sm" onClick={() => setNewAddr({ ...newAddr, type })} className="flex items-center gap-1.5 capitalize">
                      <Icon className="h-3.5 w-3.5" /> {type}
                    </Button>
                  );
                })}
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowAdd(false)}>Cancel</Button>
            <Button onClick={handleAdd}>Add Address</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
