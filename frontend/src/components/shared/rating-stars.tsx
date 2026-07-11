import { Star } from 'lucide-react';
import { cn } from '@/lib/utils';

interface RatingStarsProps {
  rating: number;
  size?: 'sm' | 'md' | 'lg';
  showValue?: boolean;
  reviewCount?: number;
  className?: string;
}

export function RatingStars({ rating, size = 'sm', showValue = true, reviewCount, className }: RatingStarsProps) {
  const sizeClass = size === 'sm' ? 'h-3.5 w-3.5' : size === 'md' ? 'h-4 w-4' : 'h-5 w-5';
  const textSize = size === 'sm' ? 'text-xs' : size === 'md' ? 'text-sm' : 'text-base';

  return (
    <div className={cn('flex items-center gap-1', className)}>
      <div className="flex items-center">
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            className={cn(
              sizeClass,
              star <= Math.round(rating)
                ? 'fill-warning text-warning'
                : 'fill-muted text-muted-foreground/30'
            )}
          />
        ))}
      </div>
      {showValue && (
        <span className={cn('font-semibold text-foreground', textSize)}>{rating.toFixed(1)}</span>
      )}
      {reviewCount !== undefined && (
        <span className={cn('text-muted-foreground', textSize)}>({reviewCount})</span>
      )}
    </div>
  );
}
