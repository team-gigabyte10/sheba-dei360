import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import {
  Ambulance, Flame, Shield, Wrench, Zap, Car, Phone, Clock,
  AlertTriangle, ArrowRight,
} from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { emergencyServices } from '@/lib/mock-data';
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
  const handleCall = (name: string, number: string) => {
    toast.success(`Calling ${name}...`, { description: `Dialing ${number}` });
  };

  return (
    <div className="container mx-auto px-4 py-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="mb-8"
      >
        <div className="flex items-center gap-3 mb-2">
          <div className="flex h-12 w-12 items-center justify-center rounded-full bg-destructive/10 text-destructive">
            <AlertTriangle className="h-6 w-6" />
          </div>
          <div>
            <h1 className="text-3xl font-bold tracking-tight">Emergency Services</h1>
            <p className="text-muted-foreground">24/7 emergency assistance at your fingertips</p>
          </div>
        </div>
      </motion.div>

      <Card className="mb-6 border-destructive/20 bg-gradient-to-br from-red-50 to-orange-50 dark:from-red-950/20 dark:to-orange-950/20">
        <CardContent className="p-6">
          <div className="flex flex-col items-center gap-4 text-center sm:flex-row sm:text-left">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-destructive text-destructive-foreground shadow-lg">
              <Phone className="h-8 w-8" />
            </div>
            <div className="flex-1">
              <h2 className="text-xl font-bold">Need Immediate Help?</h2>
              <p className="text-sm text-muted-foreground mt-1">Tap to call emergency services directly. Available 24/7.</p>
            </div>
            <Button size="lg" variant="destructive" onClick={() => handleCall('Emergency Hotline', '199')}>
              <Phone className="mr-2 h-5 w-5" /> Call 199
            </Button>
          </div>
        </CardContent>
      </Card>

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
