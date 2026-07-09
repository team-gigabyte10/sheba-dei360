import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Logo } from '@/components/shared/logo';
import { Button } from '@/components/ui/button';

export function AuthLayout({ children, title, subtitle }: { children: React.ReactNode; title: string; subtitle: string }) {
  return (
    <div className="min-h-screen grid lg:grid-cols-2">
      <div className="hidden lg:flex flex-col justify-between p-12 bg-gradient-to-br from-primary to-sky-700 text-white relative overflow-hidden">
        <div className="absolute inset-0 opacity-10">
          <div className="absolute top-20 left-20 h-64 w-64 rounded-full bg-white blur-3xl" />
          <div className="absolute bottom-20 right-20 h-64 w-64 rounded-full bg-white blur-3xl" />
        </div>
        <Logo className="text-white relative" />
        <div className="relative space-y-6">
          <motion.h2
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-4xl font-bold leading-tight"
          >
            Everything you need, <br /> delivered to your door.
          </motion.h2>
          <motion.p
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="text-lg text-white/80"
          >
            Join 500K+ users who trust ServeNear for food, rides, home services, and more.
          </motion.p>
          <div className="flex gap-8">
            <div>
              <p className="text-3xl font-bold">2M+</p>
              <p className="text-sm text-white/70">Bookings</p>
            </div>
            <div>
              <p className="text-3xl font-bold">50K+</p>
              <p className="text-sm text-white/70">Providers</p>
            </div>
            <div>
              <p className="text-3xl font-bold">4.9</p>
              <p className="text-sm text-white/70">Rating</p>
            </div>
          </div>
        </div>
        <p className="text-sm text-white/60 relative">© 2024 ServeNear. All rights reserved.</p>
      </div>

      <div className="flex flex-col">
        <div className="flex items-center justify-between p-6 lg:hidden">
          <Logo />
          <Button variant="ghost" asChild>
            <Link to="/">Back to home</Link>
          </Button>
        </div>
        <div className="flex flex-1 items-center justify-center p-6">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="w-full max-w-md space-y-6"
          >
            <div className="space-y-2 text-center">
              <h1 className="text-3xl font-bold tracking-tight">{title}</h1>
              <p className="text-muted-foreground">{subtitle}</p>
            </div>
            {children}
          </motion.div>
        </div>
      </div>
    </div>
  );
}
