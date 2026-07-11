import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { AlertTriangle, RefreshCw, Home } from 'lucide-react';
import { Button } from '@/components/ui/button';

export function ServerErrorPage() {
  return (
    <div className="min-h-[60vh] flex items-center justify-center px-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-center max-w-md"
      >
        <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-destructive/10 text-destructive">
          <AlertTriangle className="h-10 w-10" />
        </div>
        <h1 className="text-5xl font-bold mb-2">500</h1>
        <h2 className="text-2xl font-bold mb-2">Server Error</h2>
        <p className="text-muted-foreground mb-6">
          Something went wrong on our end. We're working to fix it. Please try again later.
        </p>
        <div className="flex flex-col gap-3 sm:flex-row justify-center">
          <Button onClick={() => window.location.reload()}>
            <RefreshCw className="mr-2 h-4 w-4" /> Try Again
          </Button>
          <Button variant="outline" asChild>
            <Link to="/"><Home className="mr-2 h-4 w-4" /> Go Home</Link>
          </Button>
        </div>
      </motion.div>
    </div>
  );
}
