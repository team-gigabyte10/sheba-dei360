import { useState } from 'react';
import { useBookingStore } from '@/store/booking-store';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Search, ShoppingBag, Star, Pill, FileText, UploadCloud, CheckCircle2, AlertCircle } from 'lucide-react';
import { toast } from 'sonner';

const otcMedicines = [
  { id: 'm1', name: 'Napa Extend (Paracetamol)', price: 1.20, desc: '650mg extended release tablets for fever and pain relief (10pcs)', image: 'https://images.pexels.com/photos/3685934/pexels-photo-3685934.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'otc' },
  { id: 'm2', name: 'Ceevit Chewable Vitamin C', price: 2.50, desc: '250mg chewable orange flavor vitamin supplements (30pcs)', image: 'https://images.pexels.com/photos/14354958/pexels-photo-14354958.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'supplement' },
  { id: 'm3', name: 'Gaviscon Double Action', price: 5.80, desc: 'Heartburn and indigestion relief liquid suspension (150ml)', image: 'https://images.pexels.com/photos/5910953/pexels-photo-5910953.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'digestive' },
  { id: 'm4', name: 'Antiseptic Hand Sanitizer', price: 3.10, desc: 'Kills 99.9% germs, enriched with Aloe Vera extract (200ml)', image: 'https://images.pexels.com/photos/3987146/pexels-photo-3987146.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'hygiene' },
];

export function PharmacyPage() {
  const { addToCart } = useBookingStore();
  const [searchQuery, setSearchQuery] = useState('');
  const [uploadOpen, setUploadOpen] = useState(false);
  const [fileSelected, setFileSelected] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [uploaded, setUploaded] = useState(false);

  const handleAddToCart = (item: typeof otcMedicines[0]) => {
    addToCart({
      id: item.id,
      name: item.name,
      price: item.price,
      image: item.image,
      restaurantId: 'pharmacy-store-1' // Mock store ID
    });
    toast.success(`${item.name} added to cart!`);
  };

  const handleFileChange = () => {
    setFileSelected(true);
  };

  const handleUploadSubmit = async () => {
    if (!fileSelected) {
      toast.error('Please choose a prescription file first');
      return;
    }
    setUploading(true);
    await new Promise((r) => setTimeout(r, 2000));
    setUploading(false);
    setUploaded(true);
    toast.success('Prescription uploaded successfully!');
  };

  const resetUpload = () => {
    setUploadOpen(false);
    setFileSelected(false);
    setUploaded(false);
  };

  const filteredMeds = otcMedicines.filter((item) =>
    item.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    item.desc.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="container mx-auto px-4 py-6 space-y-6">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-extrabold tracking-tight flex items-center gap-2 text-sky-600 dark:text-sky-400">
            <Pill className="h-8 w-8" /> ServeNear Pharmacy
          </h1>
          <p className="text-muted-foreground mt-1">Order registered medicines or upload prescriptions for direct fulfillment</p>
        </div>
        
        <div className="flex flex-col sm:flex-row gap-2 w-full md:w-auto">
          {/* Prescription Button */}
          <Button onClick={() => setUploadOpen(true)} className="bg-sky-600 hover:bg-sky-700 text-white rounded-xl flex items-center gap-2 shrink-0">
            <FileText className="h-4 w-4" /> Upload Prescription
          </Button>

          {/* Search bar */}
          <div className="relative w-full sm:w-64">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Search medicines..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10 rounded-xl"
            />
          </div>
        </div>
      </div>

      <div className="grid gap-6 md:grid-cols-4">
        {/* Safe Ordering Banner */}
        <Card className="md:col-span-4 border-sky-200/50 bg-gradient-to-br from-sky-50 to-blue-50 dark:from-sky-950/20 dark:to-blue-950/20 rounded-3xl overflow-hidden">
          <CardContent className="p-6 flex flex-col md:flex-row md:items-center gap-6 justify-between">
            <div className="space-y-2">
              <h2 className="text-xl font-bold flex items-center gap-2 text-sky-800 dark:text-sky-300">
                <CheckCircle2 className="h-5 w-5 text-sky-600" /> Prescriptions Made Easy
              </h2>
              <p className="text-sm text-sky-700/80 dark:text-sky-400/80 max-w-2xl">
                Upload a photo of your doctor's prescription. Our certified pharmacist will verify the compounds, draft the order list, and send it straight to your app drawer.
              </p>
            </div>
            <Button size="lg" onClick={() => setUploadOpen(true)} className="bg-sky-600 hover:bg-sky-700 text-white font-bold rounded-2xl shadow-md border-none shrink-0">
              Get Started Now
            </Button>
          </CardContent>
        </Card>

        {/* Medicines List */}
        <div className="md:col-span-4 space-y-4">
          <h2 className="text-lg font-bold">Popular OTC Medicines</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {filteredMeds.map((med) => (
              <Card key={med.id} className="group overflow-hidden rounded-2xl border border-border/50 hover:shadow-xl hover:border-sky-500/20 transition-all duration-300 flex flex-col justify-between">
                <div className="relative aspect-square overflow-hidden bg-muted">
                  <img
                    src={med.image}
                    alt={med.name}
                    className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
                  />
                  <Badge className="absolute left-3 top-3 bg-background/80 backdrop-blur-md text-foreground capitalize text-[10px]">
                    {med.category}
                  </Badge>
                </div>
                
                <CardContent className="p-4 space-y-3 flex-1 flex flex-col justify-between">
                  <div className="space-y-1">
                    <h3 className="font-bold text-base truncate group-hover:text-sky-600 transition-colors">{med.name}</h3>
                    <p className="text-xs text-muted-foreground line-clamp-2 h-8">{med.desc}</p>
                    <div className="flex items-center gap-1 text-amber-500 text-xs font-semibold">
                      <Star className="h-3 w-3 fill-current" /> 4.9 <span className="text-muted-foreground font-normal">(18 reviews)</span>
                    </div>
                  </div>

                  <div className="flex items-center justify-between mt-4">
                    <span className="text-lg font-extrabold text-foreground">৳{med.price.toFixed(2)}</span>
                    <Button size="sm" onClick={() => handleAddToCart(med)} className="rounded-xl flex items-center gap-1 px-3 bg-sky-600 hover:bg-sky-700 text-white border-none">
                      <ShoppingBag className="h-3.5 w-3.5" /> Buy
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {filteredMeds.length === 0 && (
            <div className="text-center py-12 text-muted-foreground">
              No OTC medicines found matching your search.
            </div>
          )}
        </div>
      </div>

      {/* Prescription Upload Dialog */}
      <Dialog open={uploadOpen} onOpenChange={(val) => !val && resetUpload()}>
        <DialogContent className="max-w-md rounded-3xl p-6">
          <DialogHeader>
            <DialogTitle>Upload Doctor Prescription</DialogTitle>
          </DialogHeader>
          
          <div className="py-4 space-y-4">
            {!uploaded ? (
              <div className="space-y-4">
                <div className="border-2 border-dashed border-sky-300 dark:border-sky-800 rounded-2xl p-6 text-center flex flex-col items-center justify-center cursor-pointer hover:bg-sky-500/5 transition-colors relative">
                  <input
                    type="file"
                    accept="image/*,.pdf"
                    className="absolute inset-0 opacity-0 cursor-pointer"
                    onChange={handleFileChange}
                  />
                  <UploadCloud className="h-10 w-10 text-sky-500 mb-2 animate-bounce" />
                  <p className="text-sm font-semibold">Drag & drop or Click to browse</p>
                  <p className="text-xs text-muted-foreground mt-1">Supports JPG, PNG, PDF (Max 5MB)</p>
                  {fileSelected && (
                    <Badge className="mt-3 bg-emerald-500 hover:bg-emerald-600 text-white border-none">
                      prescription_scan.jpg Selected
                    </Badge>
                  )}
                </div>
                <div className="flex gap-2 p-3 bg-sky-500/10 border border-sky-500/20 text-sky-800 dark:text-sky-300 rounded-xl text-xs">
                  <AlertCircle className="h-4 w-4 shrink-0 text-sky-600" />
                  <span>Only upload registered prescriptions containing patient name, doctor registration seal, and clear handwriting.</span>
                </div>
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center py-8 text-center space-y-3">
                <div className="h-14 w-14 rounded-full bg-emerald-500/15 text-emerald-600 flex items-center justify-center">
                  <CheckCircle2 className="h-8 w-8" />
                </div>
                <h3 className="font-bold text-lg text-emerald-600">File Uploaded Successfully!</h3>
                <p className="text-sm text-muted-foreground max-w-xs">
                  Our dispatch team has received your prescription. You will receive an app notification with the checkout cart details in a few minutes.
                </p>
              </div>
            )}
          </div>

          <DialogFooter className="flex gap-2">
            {!uploaded ? (
              <>
                <Button variant="outline" onClick={resetUpload} className="rounded-xl">Cancel</Button>
                <Button onClick={handleUploadSubmit} disabled={uploading} className="bg-sky-600 hover:bg-sky-700 text-white rounded-xl">
                  {uploading ? 'Processing File...' : 'Upload Prescription'}
                </Button>
              </>
            ) : (
              <Button onClick={resetUpload} className="w-full bg-emerald-500 hover:bg-emerald-600 text-white rounded-xl border-none">
                Done
              </Button>
            )}
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
