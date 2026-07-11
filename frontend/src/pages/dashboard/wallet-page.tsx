import { useState } from 'react';
import { Wallet, Plus, ArrowDownLeft, ArrowUpRight, Loader2 } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { useAuthStore } from '@/store/auth-store';
import { walletTransactions } from '@/lib/mock-data';
import { cn } from '@/lib/utils';
import { toast } from 'sonner';

export function WalletPage() {
  const { user, updateProfile } = useAuthStore();
  const [topUpOpen, setTopUpOpen] = useState(false);
  const [topUpAmount, setTopUpAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const [transactions, setTransactions] = useState(walletTransactions);

  const handleTopUp = async () => {
    const amount = parseFloat(topUpAmount);
    if (!amount || amount <= 0) {
      toast.error('Please enter a valid amount');
      return;
    }
    setLoading(true);
    await new Promise((r) => setTimeout(r, 1000));
    updateProfile({ walletBalance: (user?.walletBalance ?? 0) + amount });
    
    const newTx = {
      id: `w-${Date.now()}`,
      type: 'credit' as const,
      amount,
      description: 'Wallet Top-up via Card',
      date: new Date().toISOString().split('T')[0],
      method: 'card' as const,
      status: 'success' as const
    };
    setTransactions((prev) => [newTx, ...prev]);

    setLoading(false);
    setTopUpOpen(false);
    setTopUpAmount('');
    toast.success(`৳${amount.toFixed(2)} added to your wallet`);
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">My Wallet</h1>
        <p className="text-muted-foreground mt-1">Manage your balance and transactions</p>
      </div>

      <Card className="overflow-hidden border-0 bg-gradient-to-br from-primary to-sky-600 text-white shadow-xl">
        <CardContent className="p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-white/80">Available Balance</p>
              <p className="text-4xl font-bold mt-1">৳{user?.walletBalance.toFixed(2)}</p>
            </div>
            <Wallet className="h-12 w-12 text-white/80" />
          </div>
          <div className="mt-6 flex gap-2">
            <Button variant="secondary" onClick={() => setTopUpOpen(true)}>
              <Plus className="mr-1 h-4 w-4" /> Add Money
            </Button>
            <Button variant="secondary" onClick={() => toast.info('Withdrawal feature coming soon')}>
              Withdraw
            </Button>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Transaction History</CardTitle>
        </CardHeader>
        <CardContent>
          <Tabs defaultValue="all">
            <TabsList>
              <TabsTrigger value="all">All</TabsTrigger>
              <TabsTrigger value="credit">Credit</TabsTrigger>
              <TabsTrigger value="debit">Debit</TabsTrigger>
            </TabsList>

            <TabsContent value="all" className="space-y-2 mt-4">
              {transactions.map((tx) => (
                <div key={tx.id} className="flex items-center justify-between py-3 border-b last:border-0">
                  <div className="flex items-center gap-3">
                    <div className={cn(
                      'flex h-10 w-10 items-center justify-center rounded-full',
                      tx.type === 'credit' ? 'bg-success/10 text-success' : 'bg-destructive/10 text-destructive'
                    )}>
                      {tx.type === 'credit' ? <ArrowDownLeft className="h-5 w-5" /> : <ArrowUpRight className="h-5 w-5" />}
                    </div>
                    <div>
                      <p className="text-sm font-medium">{tx.description}</p>
                      <p className="text-xs text-muted-foreground">{tx.date} · <span className="capitalize">{tx.method}</span></p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className={cn('font-semibold', tx.type === 'credit' ? 'text-success' : 'text-foreground')}>
                      {tx.type === 'credit' ? '+' : '-'}৳{tx.amount.toFixed(2)}
                    </p>
                    <Badge variant={tx.status === 'success' ? 'default' : tx.status === 'pending' ? 'secondary' : 'destructive'} className="text-[10px] capitalize">
                      {tx.status}
                    </Badge>
                  </div>
                </div>
              ))}
            </TabsContent>
            <TabsContent value="credit" className="space-y-2 mt-4">
              {transactions.filter((t) => t.type === 'credit').map((tx) => (
                <div key={tx.id} className="flex items-center justify-between py-3 border-b last:border-0">
                  <div className="flex items-center gap-3">
                    <div className="flex h-10 w-10 items-center justify-center rounded-full bg-success/10 text-success">
                      <ArrowDownLeft className="h-5 w-5" />
                    </div>
                    <div>
                      <p className="text-sm font-medium">{tx.description}</p>
                      <p className="text-xs text-muted-foreground">{tx.date}</p>
                    </div>
                  </div>
                  <p className="font-semibold text-success">+৳{tx.amount.toFixed(2)}</p>
                </div>
              ))}
            </TabsContent>
            <TabsContent value="debit" className="space-y-2 mt-4">
              {transactions.filter((t) => t.type === 'debit').map((tx) => (
                <div key={tx.id} className="flex items-center justify-between py-3 border-b last:border-0">
                  <div className="flex items-center gap-3">
                    <div className="flex h-10 w-10 items-center justify-center rounded-full bg-destructive/10 text-destructive">
                      <ArrowUpRight className="h-5 w-5" />
                    </div>
                    <div>
                      <p className="text-sm font-medium">{tx.description}</p>
                      <p className="text-xs text-muted-foreground">{tx.date}</p>
                    </div>
                  </div>
                  <p className="font-semibold">-৳{tx.amount.toFixed(2)}</p>
                </div>
              ))}
            </TabsContent>
          </Tabs>
        </CardContent>
      </Card>

      <Dialog open={topUpOpen} onOpenChange={setTopUpOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add Money to Wallet</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="amount">Amount</Label>
              <Input id="amount" type="number" placeholder="0.00" value={topUpAmount} onChange={(e) => setTopUpAmount(e.target.value)} />
            </div>
            <div className="flex gap-2">
              {[10, 25, 50, 100].map((amt) => (
                <Button key={amt} variant="outline" size="sm" onClick={() => setTopUpAmount(String(amt))}>
                  ৳{amt}
                </Button>
              ))}
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setTopUpOpen(false)}>Cancel</Button>
            <Button onClick={handleTopUp} disabled={loading}>
              {loading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : null}
              Add Money
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
