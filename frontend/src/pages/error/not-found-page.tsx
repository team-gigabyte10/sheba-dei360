import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Home, Search } from 'lucide-react';
import { Button } from '@/components/ui/button';

export function NotFoundPage() {
  return (
    <div className="min-h-[60vh] flex items-center justify-center px-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-center max-w-md"
      >
        <div className="relative mb-8">
          <h1 className="text-[120px] font-bold gradient-text leading-none">404</h1>
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="h-32 w-32 rounded-full bg-primary/10 blur-3xl" />
          </div>
        </div>
        <h2 className="text-2xl font-bold mb-2">Page Not Found</h2>
        <p className="text-muted-foreground mb-6">
          The page you're looking for doesn't exist or has been moved. Let's get you back on track.
        </p>
        <div className="flex flex-col gap-3 sm:flex-row justify-center">
          <Button asChild>
            <Link to="/"><Home className="mr-2 h-4 w-4" /> Go Home</Link>
          </Button>
          <Button variant="outline" asChild>
            <Link to="/search"><Search className="mr-2 h-4 w-4" /> Browse Services</Link>
          </Button>
        </div>
      </motion.div>
    </div>
  );
}
