import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { FileText, Download, TrendingUp, BarChart2, CheckCircle } from 'lucide-react';
import { toast } from 'sonner';

const mockReportData = [
  { date: '2026-07-01', orders: 12, revenue: 180.50, commission: 27.08, status: 'Settled' },
  { date: '2026-07-02', orders: 15, revenue: 245.00, commission: 36.75, status: 'Settled' },
  { date: '2026-07-03', orders: 8, revenue: 110.00, commission: 16.50, status: 'Settled' },
  { date: '2026-07-04', orders: 22, revenue: 395.20, commission: 59.28, status: 'Settled' },
  { date: '2026-07-05', orders: 19, revenue: 280.00, commission: 42.00, status: 'Settled' },
  { date: '2026-07-06', orders: 31, revenue: 540.80, commission: 81.12, status: 'Pending Settlement' },
  { date: '2026-07-07', orders: 25, revenue: 412.50, commission: 61.88, status: 'Pending Settlement' }
];

export function ReportsPage() {
  const [downloading, setDownloading] = useState(false);

  const handleExportCSV = () => {
    setDownloading(true);
    
    // Construct CSV string content
    const headers = ['Date', 'Orders Completed', 'Gross Revenue (৳)', 'Platform Commission (৳)', 'Status'];
    const rows = mockReportData.map(item => [
      item.date,
      item.orders,
      item.revenue.toFixed(2),
      item.commission.toFixed(2),
      item.status
    ]);
    
    const csvContent = [
      headers.join(','),
      ...rows.map(e => e.join(','))
    ].join('\n');
    
    // Create virtual download anchor
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', `servenear_sales_report_${Date.now()}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    
    // Simulate slight download delay
    setTimeout(() => {
      link.click();
      document.body.removeChild(link);
      setDownloading(false);
      toast.success('Sales report CSV downloaded successfully!');
    }, 1000);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Platform Sales & Analytics Reports</h1>
          <p className="text-muted-foreground mt-1">Review operational performance charts and export transactions</p>
        </div>
        <Button onClick={handleExportCSV} disabled={downloading} className="rounded-xl flex items-center gap-1.5 self-start">
          <Download className="h-4 w-4" /> {downloading ? 'Generating CSV...' : 'Export CSV Report'}
        </Button>
      </div>

      {/* Analytics Summaries Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
        {[
          { label: 'Weekly Gross Sales', value: '৳2,164.00', desc: '+12% from last week', icon: TrendingUp, color: 'text-success' },
          { label: 'Platform Commission Share (15%)', value: '৳324.61', desc: 'Net platform fee shares', icon: BarChart2, color: 'text-primary' },
          { label: 'Weekly Orders Count', value: '132 Orders', desc: '100% fulfillment rate', icon: FileText, color: 'text-rose-500' }
        ].map((stat) => {
          const Icon = stat.icon;
          return (
            <Card key={stat.label}>
              <CardContent className="p-5 flex justify-between items-start">
                <div className="space-y-1">
                  <p className="text-xs text-muted-foreground uppercase font-bold">{stat.label}</p>
                  <p className="text-3xl font-extrabold text-foreground mt-2">{stat.value}</p>
                  <p className="text-xs text-muted-foreground font-semibold mt-1">{stat.desc}</p>
                </div>
                <div className="p-2 bg-muted rounded-xl shrink-0">
                  <Icon className={`h-6 w-6 ${stat.color}`} />
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      {/* SVG Sales Trend Chart Card */}
      <Card>
        <CardHeader>
          <CardTitle>Sales Revenue Trend</CardTitle>
          <CardDescription>Visual sales growth curve for the current week</CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col items-center justify-center p-6 bg-muted/10 rounded-2xl">
          {/* Simulated Line Chart using native SVG */}
          <div className="w-full max-w-2xl h-48 relative">
            <svg viewBox="0 0 700 200" className="w-full h-full text-primary" fill="none" stroke="currentColor">
              {/* Grid Lines */}
              <line x1="50" y1="20" x2="650" y2="20" stroke="rgba(255,255,255,0.05)" />
              <line x1="50" y1="80" x2="650" y2="80" stroke="rgba(255,255,255,0.05)" />
              <line x1="50" y1="140" x2="650" y2="140" stroke="rgba(255,255,255,0.05)" />
              <line x1="50" y1="180" x2="650" y2="180" stroke="rgba(255,255,255,0.1)" strokeWidth="2" />
              
              {/* Sales Curve Line Path */}
              <path
                d="M 50 180 L 150 150 L 250 160 L 350 100 L 450 120 L 550 50 L 650 80"
                strokeWidth="4"
                stroke="currentColor"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="text-primary"
              />
              
              {/* Data points dots */}
              {[
                { x: 50, y: 180 },
                { x: 150, y: 150 },
                { x: 250, y: 160 },
                { x: 350, y: 100 },
                { x: 450, y: 120 },
                { x: 550, y: 50 },
                { x: 650, y: 80 }
              ].map((p, i) => (
                <circle key={i} cx={p.x} cy={p.y} r="5" className="fill-background stroke-primary stroke-[3]" />
              ))}
            </svg>
            <div className="flex justify-between px-6 text-[10px] text-muted-foreground font-semibold mt-2">
              <span>July 1</span>
              <span>July 2</span>
              <span>July 3</span>
              <span>July 4</span>
              <span>July 5</span>
              <span>July 6</span>
              <span>July 7</span>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Reports Transaction Log Table */}
      <Card>
        <CardHeader>
          <CardTitle>Daily Transaction Breakdown</CardTitle>
          <CardDescription>List of weekly transaction ledger items</CardDescription>
        </CardHeader>
        <CardContent className="p-0">
          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left text-muted-foreground">
              <thead className="text-xs uppercase bg-muted/40 text-foreground font-bold border-b border-border">
                <tr>
                  <th scope="col" className="px-6 py-3.5">Date</th>
                  <th scope="col" className="px-6 py-3.5">Orders</th>
                  <th scope="col" className="px-6 py-3.5 text-right">Gross Revenue</th>
                  <th scope="col" className="px-6 py-3.5 text-right">Commission Share</th>
                  <th scope="col" className="px-6 py-3.5 text-center">Status</th>
                </tr>
              </thead>
              <tbody>
                {mockReportData.map((row) => (
                  <tr key={row.date} className="border-b last:border-0 border-border bg-background hover:bg-muted/10 transition-colors">
                    <td className="px-6 py-4 font-medium text-foreground">{row.date}</td>
                    <td className="px-6 py-4">{row.orders} orders</td>
                    <td className="px-6 py-4 text-right font-semibold text-foreground">৳{row.revenue.toFixed(2)}</td>
                    <td className="px-6 py-4 text-right font-semibold text-primary">৳{row.commission.toFixed(2)}</td>
                    <td className="px-6 py-4 text-center">
                      <span className={`inline-flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-full border ${
                        row.status === 'Settled'
                          ? 'bg-emerald-500/10 border-emerald-500/20 text-emerald-600'
                          : 'bg-amber-500/10 border-amber-500/20 text-amber-600'
                      }`}>
                        <CheckCircle className="h-3 w-3 shrink-0" /> {row.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
