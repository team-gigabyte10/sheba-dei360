import { useState } from 'react';
import { useAuthStore } from '@/store/auth-store';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuLabel,
  DropdownMenuSeparator,
} from '@/components/ui/dropdown-menu';
import { Shield, User, UserCheck, Briefcase, Eye } from 'lucide-react';
import { cn } from '@/lib/utils';

export function RoleSwitcher() {
  const { user, setRole } = useAuthStore();
  const [open, setOpen] = useState(false);

  if (!user) return null;

  const roles = [
    { value: 'customer', label: 'Customer', icon: User, color: 'text-sky-500' },
    { value: 'provider', label: 'Provider', icon: UserCheck, color: 'text-emerald-500' },
    { value: 'business', label: 'Business Owner', icon: Briefcase, color: 'text-orange-500' },
    { value: 'admin', label: 'Admin', icon: Shield, color: 'text-rose-500' },
  ] as const;

  const activeRole = roles.find((r) => r.value === user.role) || roles[0];
  const ActiveIcon = activeRole.icon;

  return (
    <div className="fixed bottom-4 left-4 z-50">
      <DropdownMenu open={open} onOpenChange={setOpen}>
        <DropdownMenuTrigger asChild>
          <Button
            variant="outline"
            size="sm"
            className={cn(
              "h-10 gap-2 rounded-full border shadow-lg bg-background/80 backdrop-blur-md px-4 pr-3 transition-all hover:scale-105 hover:bg-background/90",
              open && "ring-2 ring-primary"
            )}
          >
            <span className="flex h-2 w-2 rounded-full bg-emerald-500 animate-pulse" />
            <ActiveIcon className={cn("h-4 w-4", activeRole.color)} />
            <span className="text-xs font-semibold select-none capitalize">
              Role: {user.role}
            </span>
            <Eye className="h-3.5 w-3.5 opacity-60 ml-1" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="start" className="w-52 rounded-2xl p-1.5 shadow-xl backdrop-blur-lg bg-background/95">
          <DropdownMenuLabel className="text-xs text-muted-foreground font-medium px-2.5 py-1.5">
            Switch Sandbox Persona
          </DropdownMenuLabel>
          <DropdownMenuSeparator className="my-1" />
          {roles.map((item) => {
            const Icon = item.icon;
            const isSelected = user.role === item.value;
            return (
              <DropdownMenuItem
                key={item.value}
                onClick={() => setRole(item.value)}
                className={cn(
                  "flex items-center gap-2.5 rounded-xl px-2.5 py-2 text-sm cursor-pointer transition-colors focus:bg-primary/10",
                  isSelected && "bg-primary/10 text-primary font-medium"
                )}
              >
                <Icon className={cn("h-4 w-4 shrink-0", item.color)} />
                <span className="flex-1">{item.label}</span>
                {isSelected && (
                  <span className="h-1.5 w-1.5 rounded-full bg-primary" />
                )}
              </DropdownMenuItem>
            );
          })}
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
}
