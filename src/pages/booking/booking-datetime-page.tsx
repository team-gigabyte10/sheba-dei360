import { useState } from 'react';
import { useNavigate, Navigate, Link } from 'react-router-dom';
import { Calendar, Clock, ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Textarea } from '@/components/ui/textarea';
import { Label } from '@/components/ui/label';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { BookingStepper } from '@/components/booking/booking-stepper';
import { useBookingStore } from '@/store/booking-store';
import { cn } from '@/lib/utils';

export function BookingDateTimePage() {
  const navigate = useNavigate();
  const { draft, updateDraft } = useBookingStore();
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [selectedTime, setSelectedTime] = useState<string | null>(null);
  const [notes, setNotes] = useState('');

  if (!draft) return <Navigate to="/search" replace />;

  const today = new Date();
  const nextDays = Array.from({ length: 14 }, (_, i) => {
    const d = new Date(today);
    d.setDate(today.getDate() + i);
    return d;
  });

  const timeSlots = ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00'];

  const handleContinue = () => {
    if (!selectedDate || !selectedTime) return;
    updateDraft({
      date: selectedDate.toISOString().split('T')[0],
      time: selectedTime,
      notes: notes || undefined,
    });
    navigate('/booking/address');
  };

  return (
    <div className="container mx-auto px-4 py-6 max-w-3xl">
      <Button variant="ghost" size="sm" asChild className="mb-4">
        <Link to={`/service/${draft.providerId}`}><ChevronLeft className="mr-1 h-4 w-4" /> Back</Link>
      </Button>

      <BookingStepper currentStep={0} />

      <Card className="mb-4">
        <CardContent className="flex items-center gap-3 p-4">
          <Avatar className="h-12 w-12">
            <AvatarImage src={draft.providerAvatar} alt={draft.providerName} />
            <AvatarFallback>{draft.providerName.charAt(0)}</AvatarFallback>
          </Avatar>
          <div>
            <p className="font-semibold">{draft.serviceName}</p>
            <p className="text-sm text-muted-foreground">{draft.providerName} · ${draft.price}</p>
          </div>
        </CardContent>
      </Card>

      <Card className="mb-4">
        <CardContent className="p-6">
          <div className="flex items-center gap-2 mb-4">
            <Calendar className="h-5 w-5 text-primary" />
            <h3 className="font-semibold">Select Date</h3>
          </div>
          <div className="flex gap-2 overflow-x-auto scrollbar-hide pb-2">
            {nextDays.map((day) => {
              const isSelected = selectedDate?.toDateString() === day.toDateString();
              return (
                <button
                  key={day.toISOString()}
                  onClick={() => setSelectedDate(day)}
                  className={cn(
                    'flex flex-col items-center justify-center gap-1 rounded-xl border-2 p-3 min-w-[70px] transition-colors',
                    isSelected
                      ? 'border-primary bg-primary text-primary-foreground'
                      : 'border-border hover:border-primary/50'
                  )}
                >
                  <span className="text-xs">{day.toLocaleDateString('en', { weekday: 'short' })}</span>
                  <span className="text-lg font-bold">{day.getDate()}</span>
                  <span className="text-xs">{day.toLocaleDateString('en', { month: 'short' })}</span>
                </button>
              );
            })}
          </div>
        </CardContent>
      </Card>

      <Card className="mb-4">
        <CardContent className="p-6">
          <div className="flex items-center gap-2 mb-4">
            <Clock className="h-5 w-5 text-primary" />
            <h3 className="font-semibold">Select Time</h3>
          </div>
          <div className="grid grid-cols-3 gap-2 sm:grid-cols-4 md:grid-cols-6">
            {timeSlots.map((slot) => (
              <Button
                key={slot}
                variant={selectedTime === slot ? 'default' : 'outline'}
                size="sm"
                onClick={() => setSelectedTime(slot)}
                disabled={!selectedDate}
              >
                {slot}
              </Button>
            ))}
          </div>
        </CardContent>
      </Card>

      <Card className="mb-4">
        <CardContent className="p-6">
          <Label htmlFor="notes" className="mb-2 block">Special Instructions (Optional)</Label>
          <Textarea
            id="notes"
            placeholder="Any specific requirements or instructions for the provider..."
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            rows={3}
          />
        </CardContent>
      </Card>

      <div className="flex justify-between">
        <Button variant="outline" asChild>
          <Link to={`/service/${draft.providerId}`}>Cancel</Link>
        </Button>
        <Button onClick={handleContinue} disabled={!selectedDate || !selectedTime}>
          Continue <ChevronRight className="ml-1 h-4 w-4" />
        </Button>
      </div>
    </div>
  );
}
