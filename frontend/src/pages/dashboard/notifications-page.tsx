import { Check, Trash2, Package, Tag, MessageSquare, CreditCard, Info } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { useNotificationStore } from '@/store/notification-store';
import { cn } from '@/lib/utils';

const typeIcons = {
  booking: Package,
  promo: Tag,
  chat: MessageSquare,
  payment: CreditCard,
  system: Info,
};

const typeColors = {
  booking: 'bg-primary/10 text-primary',
  promo: 'bg-amber-500/10 text-amber-500',
  chat: 'bg-sky-500/10 text-sky-500',
  payment: 'bg-success/10 text-success',
  system: 'bg-muted text-muted-foreground',
};

export function NotificationsPage() {
  const { notifications, markAsRead, markAllAsRead, removeNotification, unreadCount } = useNotificationStore();

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Notifications</h1>
          <p className="text-muted-foreground mt-1">{unreadCount} unread notifications</p>
        </div>
        {unreadCount > 0 && (
          <Button variant="outline" onClick={markAllAsRead}>
            <Check className="mr-1 h-4 w-4" /> Mark All Read
          </Button>
        )}
      </div>

      <div className="space-y-2">
        {notifications.length === 0 ? (
          <Card><CardContent className="p-8 text-center text-muted-foreground">No notifications</CardContent></Card>
        ) : (
          notifications.map((notif) => {
            const Icon = typeIcons[notif.type];
            return (
              <Card key={notif.id} className={cn(!notif.read && 'border-primary/30 bg-primary/5')}>
                <CardContent className="flex items-start gap-3 p-4">
                  <div className={cn('flex h-10 w-10 items-center justify-center rounded-lg shrink-0', typeColors[notif.type])}>
                    <Icon className="h-5 w-5" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-2">
                      <div>
                        <p className="font-semibold text-sm">{notif.title}</p>
                        <p className="text-sm text-muted-foreground mt-0.5">{notif.message}</p>
                      </div>
                      {!notif.read && <div className="h-2 w-2 rounded-full bg-primary shrink-0 mt-1.5" />}
                    </div>
                    <div className="mt-2 flex items-center gap-2">
                      <span className="text-xs text-muted-foreground">
                        {new Date(notif.timestamp).toLocaleDateString()} · {new Date(notif.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                      </span>
                      <div className="flex gap-1 ml-auto">
                        {!notif.read && (
                          <Button size="sm" variant="ghost" className="h-7" onClick={() => markAsRead(notif.id)}>
                            <Check className="h-3 w-3" />
                          </Button>
                        )}
                        <Button size="sm" variant="ghost" className="h-7 text-destructive" onClick={() => removeNotification(notif.id)}>
                          <Trash2 className="h-3 w-3" />
                        </Button>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            );
          })
        )}
      </div>
    </div>
  );
}
