import { useState } from 'react';
import { useBookingStore } from '@/store/booking-store';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Search, ShoppingBag, Star, Apple, Fish, Coffee, Sparkles } from 'lucide-react';
import { toast } from 'sonner';

const groceryItems = [
  { id: 'g1', name: 'Organic Red Apples', price: 3.49, desc: 'Fresh crunchy red Gala apples (1kg)', image: 'https://images.pexels.com/photos/102104/apples-home-decoration-fresh-fruit-102104.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'fruits' },
  { id: 'g2', name: 'Fresh Organic Spinach', price: 1.99, desc: 'Pre-washed leafy greens (250g)', image: 'https://images.pexels.com/photos/2325843/pexels-photo-2325843.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'vegetables' },
  { id: 'g3', name: 'Farm Fresh Brown Eggs', price: 4.29, desc: 'Free-range organic large eggs (12pcs)', image: 'https://images.pexels.com/photos/162712/egg-salad-salad-egg-shell-162712.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'dairy' },
  { id: 'g4', name: 'Fresh Salmon Fillet', price: 15.99, desc: 'Wild-caught boneless skin-on fillet (400g)', image: 'https://images.pexels.com/photos/3763816/pexels-photo-3763816.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'meat' },
  { id: 'g5', name: 'Full Cream Milk', price: 1.89, desc: 'Pasteurized whole milk jug (1L)', image: 'https://images.pexels.com/photos/248412/pexels-photo-248412.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'dairy' },
  { id: 'g6', name: 'Premium Arabica Coffee Beans', price: 8.99, desc: 'Medium roast whole bean coffee (500g)', image: 'https://images.pexels.com/photos/3850624/pexels-photo-3850624.jpeg?auto=compress&cs=tinysrgb&w=400', category: 'pantry' },
];

export function GroceryPage() {
  const { addToCart } = useBookingStore();
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');

  const handleAddToCart = (item: typeof groceryItems[0]) => {
    addToCart({
      id: item.id,
      name: item.name,
      price: item.price,
      image: item.image,
      restaurantId: 'grocery-store-1' // Mock store ID
    });
    toast.success(`${item.name} added to cart!`);
  };

  const filteredItems = groceryItems.filter((item) => {
    const matchesCat = selectedCategory === 'all' || item.category === selectedCategory;
    const matchesSearch = item.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          item.desc.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCat && matchesSearch;
  });

  return (
    <div className="container mx-auto px-4 py-6 space-y-6">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-extrabold tracking-tight flex items-center gap-2">
            <Apple className="h-8 w-8 text-primary" /> Daily Supermart
          </h1>
          <p className="text-muted-foreground mt-1">Fresh groceries delivered to your door in 30 minutes</p>
        </div>
        
        {/* Search bar */}
        <div className="relative w-full md:w-80">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Search groceries..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10 rounded-xl"
          />
        </div>
      </div>

      {/* Categories Horizontal Banner Selector */}
      <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-thin">
        {[
          { id: 'all', label: 'All Items', icon: Sparkles },
          { id: 'fruits', label: 'Fruits', icon: Apple },
          { id: 'vegetables', label: 'Vegetables', icon: Apple },
          { id: 'dairy', label: 'Dairy & Eggs', icon: Coffee },
          { id: 'meat', label: 'Meat & Seafood', icon: Fish },
          { id: 'pantry', label: 'Pantry Staples', icon: Coffee },
        ].map((cat) => {
          const Icon = cat.icon;
          const active = selectedCategory === cat.id;
          return (
            <Button
              key={cat.id}
              variant={active ? 'default' : 'outline'}
              className="rounded-full px-5 py-1.5 shrink-0 flex items-center gap-2 text-xs font-semibold"
              onClick={() => setSelectedCategory(cat.id)}
            >
              <Icon className="h-3.5 w-3.5" /> {cat.label}
            </Button>
          );
        })}
      </div>

      {/* Grocery Items Catalog Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {filteredItems.map((item) => (
          <Card key={item.id} className="group overflow-hidden rounded-2xl border border-border/50 hover:shadow-xl hover:border-primary/20 transition-all duration-300 flex flex-col justify-between">
            <div className="relative aspect-square overflow-hidden bg-muted">
              <img
                src={item.image}
                alt={item.name}
                className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
              />
              <Badge className="absolute left-3 top-3 bg-background/80 backdrop-blur-md text-foreground capitalize text-[10px]">
                {item.category}
              </Badge>
            </div>
            
            <CardContent className="p-4 space-y-3 flex-1 flex flex-col justify-between">
              <div className="space-y-1">
                <h3 className="font-bold text-base truncate group-hover:text-primary transition-colors">{item.name}</h3>
                <p className="text-xs text-muted-foreground line-clamp-2 h-8">{item.desc}</p>
                <div className="flex items-center gap-1 text-amber-500 text-xs font-semibold">
                  <Star className="h-3 w-3 fill-current" /> 4.8 <span className="text-muted-foreground font-normal">(42 reviews)</span>
                </div>
              </div>

              <div className="flex items-center justify-between mt-4">
                <span className="text-lg font-extrabold text-foreground">৳{item.price.toFixed(2)}</span>
                <Button size="sm" onClick={() => handleAddToCart(item)} className="rounded-xl flex items-center gap-1 px-3">
                  <ShoppingBag className="h-3.5 w-3.5" /> Add
                </Button>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {filteredItems.length === 0 && (
        <div className="text-center py-12 text-muted-foreground">
          No groceries found matching your criteria.
        </div>
      )}
    </div>
  );
}
