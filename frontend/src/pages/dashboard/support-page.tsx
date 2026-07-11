import { useState } from 'react';
import { LifeBuoy, Plus, Send } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Badge } from '@/components/ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { supportTickets } from '@/lib/mock-data';
import { cn } from '@/lib/utils';
import { toast } from 'sonner';

const statusColors: Record<string, string> = {
  open: 'bg-primary text-primary-foreground',
  'in-progress': 'bg-warning text-warning-foreground',
  resolved: 'bg-success text-success-foreground',
  closed: 'bg-muted text-muted-foreground',
};

export function SupportPage() {
  const [tickets, setTickets] = useState(supportTickets);
  const [showNew, setShowNew] = useState(false);
  const [selectedTicket, setSelectedTicket] = useState<typeof supportTickets[0] | null>(null);
  const [newTicket, setNewTicket] = useState({ subject: '', category: '', priority: 'medium', message: '' });
  const [reply, setReply] = useState('');

  const handleCreate = () => {
    if (!newTicket.subject || !newTicket.category || !newTicket.message) {
      toast.error('Please fill all fields');
      return;
    }
    const ticket = {
      id: `s${Date.now()}`,
      subject: newTicket.subject,
      category: newTicket.category,
      status: 'open' as const,
      priority: newTicket.priority as 'low' | 'medium' | 'high',
      createdAt: new Date().toISOString().split('T')[0],
      lastUpdate: new Date().toISOString().split('T')[0],
      messages: [{ id: `m${Date.now()}`, sender: 'user' as const, text: newTicket.message, timestamp: new Date().toISOString() }],
    };
    setTickets([ticket, ...tickets]);
    setNewTicket({ subject: '', category: '', priority: 'medium', message: '' });
    setShowNew(false);
    toast.success('Support ticket created');
  };

  const handleReply = () => {
    if (!reply.trim() || !selectedTicket) return;
    setTickets(tickets.map((t) =>
      t.id === selectedTicket.id
        ? { ...t, messages: [...t.messages, { id: `m${Date.now()}`, sender: 'user' as const, text: reply, timestamp: new Date().toISOString() }], lastUpdate: new Date().toISOString().split('T')[0] }
        : t
    ));
    setSelectedTicket({ ...selectedTicket, messages: [...selectedTicket.messages, { id: `m${Date.now()}`, sender: 'user' as const, text: reply, timestamp: new Date().toISOString() }] });
    setReply('');
    toast.success('Reply sent');
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Support Tickets</h1>
          <p className="text-muted-foreground mt-1">Get help with your bookings and account</p>
        </div>
        <Button onClick={() => setShowNew(true)}>
          <Plus className="mr-1 h-4 w-4" /> New Ticket
        </Button>
      </div>

      <div className="grid gap-4 sm:grid-cols-3">
        <Card><CardContent className="p-4 text-center">
          <p className="text-2xl font-bold">{tickets.filter((t) => t.status === 'open' || t.status === 'in-progress').length}</p>
          <p className="text-xs text-muted-foreground">Active Tickets</p>
        </CardContent></Card>
        <Card><CardContent className="p-4 text-center">
          <p className="text-2xl font-bold">{tickets.filter((t) => t.status === 'resolved').length}</p>
          <p className="text-xs text-muted-foreground">Resolved</p>
        </CardContent></Card>
        <Card><CardContent className="p-4 text-center">
          <p className="text-2xl font-bold">{tickets.length}</p>
          <p className="text-xs text-muted-foreground">Total Tickets</p>
        </CardContent></Card>
      </div>

      <div className="space-y-3">
        {tickets.map((ticket) => (
          <Card key={ticket.id} className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => setSelectedTicket(ticket)}>
            <CardContent className="p-4">
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-start gap-3 flex-1 min-w-0">
                  <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary shrink-0">
                    <LifeBuoy className="h-5 w-5" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="font-semibold truncate">{ticket.subject}</p>
                    <p className="text-xs text-muted-foreground mt-0.5">{ticket.category} · Created {ticket.createdAt}</p>
                  </div>
                </div>
                <div className="flex flex-col items-end gap-1 shrink-0">
                  <Badge className={cn('text-xs capitalize', statusColors[ticket.status])}>{ticket.status.replace('-', ' ')}</Badge>
                  <Badge variant="outline" className="text-xs capitalize">{ticket.priority} priority</Badge>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <Dialog open={!!selectedTicket} onOpenChange={(open) => !open && setSelectedTicket(null)}>
        <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>{selectedTicket?.subject}</DialogTitle>
          </DialogHeader>
          {selectedTicket && (
            <div className="space-y-4">
              <div className="flex gap-2">
                <Badge className={cn('text-xs capitalize', statusColors[selectedTicket.status])}>{selectedTicket.status}</Badge>
                <Badge variant="outline" className="text-xs">{selectedTicket.category}</Badge>
              </div>
              <div className="space-y-3 max-h-64 overflow-y-auto">
                {selectedTicket.messages.map((msg) => (
                  <div key={msg.id} className={cn('flex', msg.sender === 'user' ? 'justify-end' : 'justify-start')}>
                    <div className={cn(
                      'max-w-[80%] rounded-lg p-3 text-sm',
                      msg.sender === 'user' ? 'bg-primary text-primary-foreground' : 'bg-muted'
                    )}>
                      <p>{msg.text}</p>
                      <p className="text-xs opacity-70 mt-1">{new Date(msg.timestamp).toLocaleString()}</p>
                    </div>
                  </div>
                ))}
              </div>
              <div className="flex gap-2">
                <Input placeholder="Type your reply..." value={reply} onChange={(e) => setReply(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && handleReply()} />
                <Button onClick={handleReply} size="icon"><Send className="h-4 w-4" /></Button>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>

      <Dialog open={showNew} onOpenChange={setShowNew}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Create New Ticket</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="subject">Subject</Label>
              <Input id="subject" placeholder="Brief description of your issue" value={newTicket.subject} onChange={(e) => setNewTicket({ ...newTicket, subject: e.target.value })} />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-2">
                <Label>Category</Label>
                <Select value={newTicket.category} onValueChange={(v) => setNewTicket({ ...newTicket, category: v })}>
                  <SelectTrigger><SelectValue placeholder="Select category" /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="Booking">Booking</SelectItem>
                    <SelectItem value="Payment">Payment</SelectItem>
                    <SelectItem value="Provider">Provider</SelectItem>
                    <SelectItem value="Account">Account</SelectItem>
                    <SelectItem value="Other">Other</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label>Priority</Label>
                <Select value={newTicket.priority} onValueChange={(v) => setNewTicket({ ...newTicket, priority: v })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="low">Low</SelectItem>
                    <SelectItem value="medium">Medium</SelectItem>
                    <SelectItem value="high">High</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="message">Message</Label>
              <Textarea id="message" placeholder="Describe your issue in detail..." rows={4} value={newTicket.message} onChange={(e) => setNewTicket({ ...newTicket, message: e.target.value })} />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowNew(false)}>Cancel</Button>
            <Button onClick={handleCreate}>Create Ticket</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
