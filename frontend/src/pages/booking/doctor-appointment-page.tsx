import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useBookingStore } from '@/store/booking-store';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Stethoscope, Calendar, Clock, Star, Search, Heart } from 'lucide-react';
import { toast } from 'sonner';

const doctorsList = [
  { id: 'doc1', name: 'Dr. Sabrina Rahman', specialty: 'Pediatrics', fee: 20.00, rating: 4.9, chamber: 'Banani Care, Dhaka', avatar: 'https://images.pexels.com/photos/4173251/pexels-photo-4173251.jpeg?auto=compress&cs=tinysrgb&w=200', degree: 'MBBS, FCPS (Pediatrics)' },
  { id: 'doc2', name: 'Dr. Zulfiqar Ali', specialty: 'Cardiology', fee: 35.00, rating: 4.8, chamber: 'Gulshan Heart Point, Dhaka', avatar: 'https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=200', degree: 'MBBS, MD (Cardiology)' },
  { id: 'doc3', name: 'Dr. Tanvir Ahmed', specialty: 'General Medicine', fee: 15.00, rating: 4.7, chamber: 'Dhanmondi Clinix, Dhaka', avatar: 'https://images.pexels.com/photos/5327656/pexels-photo-5327656.jpeg?auto=compress&cs=tinysrgb&w=200', degree: 'MBBS, BCS (Health)' },
  { id: 'doc4', name: 'Dr. Farhana Islam', specialty: 'Dermatology', fee: 25.00, rating: 4.9, chamber: 'Uttara Skin Clinic, Dhaka', avatar: 'https://images.pexels.com/photos/3825586/pexels-photo-3825586.jpeg?auto=compress&cs=tinysrgb&w=200', degree: 'MBBS, DDV (Dermatology)' }
];

const mockSlots = ['09:00 AM', '10:30 AM', '11:00 AM', '02:00 PM', '04:30 PM', '06:00 PM'];

export function DoctorAppointmentPage() {
  const navigate = useNavigate();
  const { addBooking } = useBookingStore();

  const [selectedSpecialty, setSelectedSpecialty] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [bookingDoc, setBookingDoc] = useState<typeof doctorsList[0] | null>(null);
  
  // Form states
  const [patientName, setPatientName] = useState('');
  const [patientAge, setPatientAge] = useState('');
  const [patientGender, setPatientGender] = useState('male');
  const [selectedDate, setSelectedDate] = useState('');
  const [selectedSlot, setSelectedSlot] = useState('');
  const [loading, setLoading] = useState(false);

  const filteredDocs = doctorsList.filter((doc) => {
    const matchesSpec = selectedSpecialty === 'all' || doc.specialty.toLowerCase() === selectedSpecialty.toLowerCase();
    const matchesSearch = doc.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          doc.specialty.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesSpec && matchesSearch;
  });

  const handleOpenBooking = (doc: typeof doctorsList[0]) => {
    setBookingDoc(doc);
    // Set default date as tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    setSelectedDate(tomorrow.toISOString().split('T')[0]);
    setSelectedSlot(mockSlots[0]);
  };

  const handleConfirmBooking = async () => {
    if (!patientName.trim() || !patientAge.trim() || !selectedDate || !selectedSlot || !bookingDoc) {
      toast.error('Please fill in all appointment details');
      return;
    }

    setLoading(true);
    await new Promise((r) => setTimeout(r, 1500));

    const bookingId = `med-${Date.now()}`;
    addBooking({
      id: bookingId,
      serviceId: bookingDoc.id,
      serviceName: `Doctor Visit: ${bookingDoc.name}`,
      providerId: bookingDoc.id,
      providerName: bookingDoc.name,
      providerAvatar: bookingDoc.avatar,
      date: selectedDate,
      time: selectedSlot,
      address: `${bookingDoc.chamber} (Patient: ${patientName}, Age: ${patientAge})`,
      status: 'confirmed',
      amount: bookingDoc.fee,
      paymentMethod: 'wallet',
      type: 'service',
      createdAt: new Date().toISOString().split('T')[0]
    });

    setLoading(false);
    toast.success('Appointment Scheduled!', { description: `Appointment with ${bookingDoc.name} is confirmed.` });
    setBookingDoc(null);
    navigate('/dashboard/bookings');
  };

  return (
    <div className="container mx-auto px-4 py-6 space-y-6">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-extrabold tracking-tight flex items-center gap-2 text-rose-500 dark:text-rose-400">
            <Heart className="h-8 w-8 text-rose-500 animate-pulse animate-duration-1000" /> Healthcare Consultations
          </h1>
          <p className="text-muted-foreground mt-1">Book certified medical consultations and doctor appointments online</p>
        </div>
        
        {/* Search bar */}
        <div className="relative w-full md:w-80">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Search doctors or specialties..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10 rounded-xl"
          />
        </div>
      </div>

      {/* Specialties Horizontal Banner */}
      <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-thin">
        {[
          { id: 'all', label: 'All Specialties' },
          { id: 'pediatrics', label: 'Pediatrics (Children)' },
          { id: 'cardiology', label: 'Cardiology (Heart)' },
          { id: 'general medicine', label: 'General Medicine' },
          { id: 'dermatology', label: 'Dermatology (Skin)' }
        ].map((spec) => {
          const active = selectedSpecialty === spec.id;
          return (
            <Button
              key={spec.id}
              variant={active ? 'default' : 'outline'}
              className="rounded-full px-5 py-1.5 shrink-0 text-xs font-semibold"
              onClick={() => setSelectedSpecialty(spec.id)}
            >
              {spec.label}
            </Button>
          );
        })}
      </div>

      {/* Doctors Grid List */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {filteredDocs.map((doc) => (
          <Card key={doc.id} className="group overflow-hidden rounded-3xl border border-border/50 hover:shadow-xl hover:border-rose-500/20 transition-all duration-300">
            <CardContent className="p-5 flex flex-col sm:flex-row gap-4 items-center sm:items-start text-center sm:text-left h-full">
              <img
                src={doc.avatar}
                alt={doc.name}
                className="h-24 w-24 object-cover rounded-2xl shrink-0 border border-border"
              />
              <div className="flex-1 space-y-2 min-w-0">
                <div className="flex justify-between items-start gap-2">
                  <div>
                    <h3 className="font-bold text-lg truncate group-hover:text-rose-500 transition-colors">{doc.name}</h3>
                    <p className="text-xs text-rose-500 font-semibold">{doc.degree}</p>
                  </div>
                  <Badge variant="secondary" className="text-[10px] hidden sm:inline-block">
                    {doc.specialty}
                  </Badge>
                </div>
                
                <p className="text-xs text-muted-foreground">{doc.chamber}</p>
                
                <div className="flex items-center justify-center sm:justify-start gap-3 mt-1 text-xs">
                  <div className="flex items-center gap-0.5 text-amber-500 font-semibold">
                    <Star className="h-3 w-3 fill-current" /> {doc.rating}
                  </div>
                  <div className="text-muted-foreground">·</div>
                  <div className="font-bold text-foreground">Fee: ${doc.fee.toFixed(2)}</div>
                </div>

                <div className="pt-2 flex justify-end">
                  <Button size="sm" onClick={() => handleOpenBooking(doc)} className="bg-rose-500 hover:bg-rose-600 text-white rounded-xl border-none">
                    Book Visit
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Scheduling Appointment Dialog */}
      <Dialog open={!!bookingDoc} onOpenChange={(val) => !val && setBookingDoc(null)}>
        <DialogContent className="max-w-md rounded-3xl p-6">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Stethoscope className="h-5 w-5 text-rose-500" /> Book Consultation
            </DialogTitle>
          </DialogHeader>

          {bookingDoc && (
            <div className="py-4 space-y-4 text-sm">
              <div className="flex gap-3 items-center bg-muted/30 p-3 rounded-2xl border">
                <img src={bookingDoc.avatar} alt={bookingDoc.name} className="h-12 w-12 object-cover rounded-xl border" />
                <div>
                  <p className="font-bold">{bookingDoc.name}</p>
                  <p className="text-xs text-muted-foreground">{bookingDoc.degree} · Fee: ${bookingDoc.fee}</p>
                </div>
              </div>

              {/* Patient Details Form */}
              <div className="space-y-3">
                <div className="space-y-1">
                  <Label htmlFor="patient" className="text-xs font-semibold">Patient Full Name</Label>
                  <Input id="patient" placeholder="Enter patient name..." value={patientName} onChange={(e) => setPatientName(e.target.value)} className="rounded-xl h-10" />
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <div className="space-y-1">
                    <Label htmlFor="age" className="text-xs font-semibold">Age</Label>
                    <Input id="age" type="number" placeholder="Years" value={patientAge} onChange={(e) => setPatientAge(e.target.value)} className="rounded-xl h-10" />
                  </div>
                  <div className="space-y-1">
                    <Label htmlFor="gender" className="text-xs font-semibold">Gender</Label>
                    <select
                      id="gender"
                      value={patientGender}
                      onChange={(e) => setPatientGender(e.target.value)}
                      className="w-full h-10 rounded-xl border border-input bg-background px-3 py-2 text-sm"
                    >
                      <option value="male">Male</option>
                      <option value="female">Female</option>
                      <option value="other">Other</option>
                    </select>
                  </div>
                </div>
              </div>

              {/* Appointment Schedule Inputs */}
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1">
                  <Label className="text-xs font-semibold flex items-center gap-1"><Calendar className="h-3.5 w-3.5" /> Date</Label>
                  <Input type="date" value={selectedDate} onChange={(e) => setSelectedDate(e.target.value)} className="rounded-xl h-10" />
                </div>
                <div className="space-y-1">
                  <Label className="text-xs font-semibold flex items-center gap-1"><Clock className="h-3.5 w-3.5" /> Time Slot</Label>
                  <select
                    value={selectedSlot}
                    onChange={(e) => setSelectedSlot(e.target.value)}
                    className="w-full h-10 rounded-xl border border-input bg-background px-3 py-2 text-sm"
                  >
                    {mockSlots.map(slot => (
                      <option key={slot} value={slot}>{slot}</option>
                    ))}
                  </select>
                </div>
              </div>
            </div>
          )}

          <DialogFooter className="flex gap-2">
            <Button variant="outline" onClick={() => setBookingDoc(null)} className="rounded-xl">Cancel</Button>
            <Button onClick={handleConfirmBooking} disabled={loading} className="bg-rose-500 hover:bg-rose-600 text-white rounded-xl border-none">
              {loading ? 'Confirming appointment...' : 'Confirm Booking'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
