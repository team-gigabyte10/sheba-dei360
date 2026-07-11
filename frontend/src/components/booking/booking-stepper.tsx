import { Check } from 'lucide-react';
import { cn } from '@/lib/utils';

const steps = [
  { label: 'Date & Time', path: '/booking/datetime' },
  { label: 'Address', path: '/booking/address' },
  { label: 'Payment', path: '/booking/payment' },
  { label: 'Confirm', path: '/booking/confirmation' },
];

export function BookingStepper({ currentStep }: { currentStep: number }) {
  return (
    <div className="mb-8">
      <div className="flex items-center justify-between">
        {steps.map((step, i) => (
          <div key={step.path} className="flex items-center flex-1 last:flex-none">
            <div className="flex flex-col items-center gap-2">
              <div
                className={cn(
                  'flex h-10 w-10 items-center justify-center rounded-full border-2 transition-colors',
                  i < currentStep
                    ? 'border-success bg-success text-success-foreground'
                    : i === currentStep
                    ? 'border-primary bg-primary text-primary-foreground'
                    : 'border-muted bg-muted text-muted-foreground'
                )}
              >
                {i < currentStep ? <Check className="h-5 w-5" /> : i + 1}
              </div>
              <span className={cn(
                'text-xs font-medium hidden sm:block',
                i <= currentStep ? 'text-foreground' : 'text-muted-foreground'
              )}>
                {step.label}
              </span>
            </div>
            {i < steps.length - 1 && (
              <div className={cn(
                'h-0.5 flex-1 mx-2 transition-colors',
                i < currentStep ? 'bg-success' : 'bg-muted'
              )} />
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
