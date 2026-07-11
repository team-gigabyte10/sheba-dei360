import { Link } from 'react-router-dom';
import { cn } from '@/lib/utils';

interface LogoProps {
  className?: string;
  showText?: boolean;
  size?: 'sm' | 'md' | 'lg';
}

export function Logo({ className, showText = true, size = 'md' }: LogoProps) {
  const iconSize = size === 'sm' ? 'h-7 w-7' : size === 'md' ? 'h-9 w-9' : 'h-12 w-12';
  const textSize = size === 'sm' ? 'text-lg' : size === 'md' ? 'text-xl' : 'text-2xl';

  return (
    <Link to="/" className={cn('flex items-center gap-2 font-bold', className)}>
      <div className={cn('relative flex items-center justify-center rounded-xl bg-gradient-to-br from-primary to-sky-600 text-white shadow-lg', iconSize)}>
        <svg viewBox="0 0 24 24" fill="none" className="h-1/2 w-1/2" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
          <path d="M12 2C8 2 5 5 5 9c0 5 7 13 7 13s7-8 7-13c0-4-3-7-7-7z" />
          <circle cx="12" cy="9" r="2.5" />
        </svg>
      </div>
      {showText && (
        <span className={cn('font-display tracking-tight text-foreground', textSize)}>
          Serve<span className="text-primary">Near</span>
        </span>
      )}
    </Link>
  );
}
