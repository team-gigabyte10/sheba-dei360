import { useState, useMemo } from 'react';
import { useSearchParams, Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  Search, SlidersHorizontal, Star, MapPin, Clock, Shield,
  UtensilsCrossed, Car, Home, HeartPulse, Sparkles, ShoppingCart, Package,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Checkbox } from '@/components/ui/checkbox';
import { Slider } from '@/components/ui/slider';
import { Separator } from '@/components/ui/separator';
import { Sheet, SheetContent, SheetHeader, SheetTitle } from '@/components/ui/sheet';
import { RatingStars } from '@/components/shared/rating-stars';
import { ProviderCardSkeleton } from '@/components/shared/skeletons';
import { providers, restaurants, categories } from '@/lib/mock-data';
import { cn } from '@/lib/utils';

const iconMap: Record<string, React.ComponentType<{ className?: string }>> = {
  UtensilsCrossed, Car, Home, HeartPulse, Sparkles, ShoppingCart, Siren: Sparkles, Package,
};

type SortOption = 'rating' | 'distance' | 'price-low' | 'price-high';

export function SearchPage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const [searchQuery, setSearchQuery] = useState(searchParams.get('q') || '');
  const [showFilters, setShowFilters] = useState(false);
  const [loading] = useState(false);
  const [sortBy, setSortBy] = useState<SortOption>('rating');

  const [filters, setFilters] = useState({
    minRating: 0,
    maxDistance: 10,
    maxPrice: 200,
    openNow: false,
    availableOnly: false,
    categories: [] as string[],
  });

  const activeCategory = searchParams.get('category') || '';

  const filteredProviders = useMemo(() => {
    let result = providers.filter((p) => {
      if (searchQuery && !p.name.toLowerCase().includes(searchQuery.toLowerCase()) && !p.service.toLowerCase().includes(searchQuery.toLowerCase())) return false;
      if (activeCategory && p.category !== activeCategory) return false;
      if (filters.minRating > 0 && p.rating < filters.minRating) return false;
      if (filters.maxDistance < 10 && p.distance > filters.maxDistance) return false;
      if (filters.availableOnly && p.status !== 'available') return false;
      if (filters.categories.length > 0 && !filters.categories.includes(p.category)) return false;
      return true;
    });

    result = [...result].sort((a, b) => {
      switch (sortBy) {
        case 'distance': return a.distance - b.distance;
        case 'price-low': return a.priceFrom - b.priceFrom;
        case 'price-high': return b.priceFrom - a.priceFrom;
        default: return b.rating - a.rating;
      }
    });

    return result;
  }, [searchQuery, activeCategory, filters, sortBy]);

  const filteredRestaurants = useMemo(() => {
    return restaurants.filter((r) => {
      if (searchQuery && !r.name.toLowerCase().includes(searchQuery.toLowerCase()) && !r.cuisine.some((c) => c.toLowerCase().includes(searchQuery.toLowerCase()))) return false;
      if (activeCategory && activeCategory !== 'food') return true;
      if (filters.minRating > 0 && r.rating < filters.minRating) return false;
      if (filters.maxDistance < 10 && r.distance > filters.maxDistance) return false;
      if (filters.openNow && !r.isOpen) return false;
      return true;
    });
  }, [searchQuery, activeCategory, filters]);

  const showRestaurants = !activeCategory || activeCategory === 'food';
  const showProviders = !activeCategory || activeCategory !== 'food';

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    const params = new URLSearchParams(searchParams);
    if (searchQuery) params.set('q', searchQuery);
    else params.delete('q');
    setSearchParams(params);
  };

  const toggleCategory = (catSlug: string) => {
    if (catSlug === 'grocery') {
      navigate('/grocery');
      return;
    }
    if (catSlug === 'ride') {
      navigate('/ride');
      return;
    }
    if (catSlug === 'health') {
      navigate('/doctor-appointment');
      return;
    }
    if (catSlug === 'emergency') {
      navigate('/emergency');
      return;
    }

    const params = new URLSearchParams(searchParams);
    if (activeCategory === catSlug) params.delete('category');
    else params.set('category', catSlug);
    setSearchParams(params);
  };

  const activeFilterCount = [
    filters.minRating > 0,
    filters.maxDistance < 10,
    filters.maxPrice < 200,
    filters.openNow,
    filters.availableOnly,
  ].filter(Boolean).length;

  const clearFilters = () => {
    setFilters({ minRating: 0, maxDistance: 10, maxPrice: 200, openNow: false, availableOnly: false, categories: [] });
  };

  const FilterContent = () => (
    <div className="space-y-6">
      <div>
        <Label className="text-sm font-semibold">Minimum Rating</Label>
        <div className="mt-3 flex items-center gap-2">
          {[0, 3, 4, 4.5].map((r) => (
            <Button
              key={r}
              variant={filters.minRating === r ? 'default' : 'outline'}
              size="sm"
              onClick={() => setFilters({ ...filters, minRating: r })}
              className="flex items-center gap-1"
            >
              {r > 0 && <Star className="h-3 w-3 fill-current" />}
              {r === 0 ? 'Any' : `${r}+`}
            </Button>
          ))}
        </div>
      </div>

      <Separator />

      <div>
        <div className="flex items-center justify-between">
          <Label className="text-sm font-semibold">Max Distance</Label>
          <span className="text-sm text-muted-foreground">{filters.maxDistance} km</span>
        </div>
        <Slider
          value={[filters.maxDistance]}
          min={1}
          max={10}
          step={0.5}
          onValueChange={(v) => setFilters({ ...filters, maxDistance: v[0] })}
          className="mt-3"
        />
      </div>

      <Separator />

      <div>
        <div className="flex items-center justify-between">
          <Label className="text-sm font-semibold">Max Price</Label>
          <span className="text-sm text-muted-foreground">৳{filters.maxPrice}</span>
        </div>
        <Slider
          value={[filters.maxPrice]}
          min={10}
          max={200}
          step={5}
          onValueChange={(v) => setFilters({ ...filters, maxPrice: v[0] })}
          className="mt-3"
        />
      </div>

      <Separator />

      <div className="space-y-3">
        <Label className="text-sm font-semibold">Availability</Label>
        <div className="flex items-center gap-2">
          <Checkbox
            id="openNow"
            checked={filters.openNow}
            onCheckedChange={(c) => setFilters({ ...filters, openNow: c === true })}
          />
          <Label htmlFor="openNow" className="text-sm font-normal cursor-pointer">Open Now</Label>
        </div>
        <div className="flex items-center gap-2">
          <Checkbox
            id="availableOnly"
            checked={filters.availableOnly}
            onCheckedChange={(c) => setFilters({ ...filters, availableOnly: c === true })}
          />
          <Label htmlFor="availableOnly" className="text-sm font-normal cursor-pointer">Available Only</Label>
        </div>
      </div>

      <Separator />

      <div>
        <Label className="text-sm font-semibold">Categories</Label>
        <div className="mt-3 grid grid-cols-2 gap-2">
          {categories.map((cat) => (
            <button
              key={cat.id}
              onClick={() => toggleCategory(cat.slug)}
              className={cn(
                'flex items-center gap-2 rounded-lg border px-3 py-2 text-xs font-medium transition-colors',
                activeCategory === cat.slug
                  ? 'border-primary bg-primary/10 text-primary'
                  : 'border-border hover:bg-muted'
              )}
            >
              {cat.name}
            </button>
          ))}
        </div>
      </div>

      {activeFilterCount > 0 && (
        <Button variant="outline" className="w-full" onClick={clearFilters}>
          Clear All Filters
        </Button>
      )}
    </div>
  );

  return (
    <div className="container mx-auto px-4 py-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold tracking-tight">
          {activeCategory
            ? categories.find((c) => c.slug === activeCategory)?.name || 'Search'
            : 'Search Services'}
        </h1>
        <p className="text-muted-foreground mt-1">
          {filteredProviders.length + (showRestaurants ? filteredRestaurants.length : 0)} results found
        </p>
      </div>

      <form onSubmit={handleSearch} className="mb-4 flex gap-2">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search by service, provider, or restaurant..."
            className="pl-10"
          />
        </div>
        <Button type="submit">Search</Button>
        <Button type="button" variant="outline" onClick={() => setShowFilters(true)} className="lg:hidden">
          <SlidersHorizontal className="h-4 w-4" />
          {activeFilterCount > 0 && <span className="ml-1">{activeFilterCount}</span>}
        </Button>
      </form>

      <div className="mb-4 flex gap-2 overflow-x-auto scrollbar-hide pb-2">
        <Button
          variant={activeCategory ? 'outline' : 'default'}
          size="sm"
          onClick={() => toggleCategory(activeCategory)}
          className="shrink-0"
        >
          All Services
        </Button>
        {categories.map((cat) => {
          const Icon = iconMap[cat.icon] ?? Home;
          return (
            <Button
              key={cat.id}
              variant={activeCategory === cat.slug ? 'default' : 'outline'}
              size="sm"
              onClick={() => toggleCategory(cat.slug)}
              className="shrink-0 flex items-center gap-1.5"
            >
              <Icon className="h-3.5 w-3.5" />
              {cat.name}
            </Button>
          );
        })}
      </div>

      <div className="flex gap-6">
        <aside className="hidden lg:block w-64 shrink-0">
          <div className="sticky top-20">
            <div className="rounded-xl border bg-card p-4">
              <div className="flex items-center justify-between mb-4">
                <h3 className="font-semibold">Filters</h3>
                {activeFilterCount > 0 && (
                  <Badge variant="secondary">{activeFilterCount} active</Badge>
                )}
              </div>
              <FilterContent />
            </div>
          </div>
        </aside>

        <div className="flex-1 min-w-0">
          <div className="mb-4 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="text-sm text-muted-foreground">Sort by:</span>
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value as SortOption)}
                className="rounded-md border border-input bg-background px-3 py-1.5 text-sm"
              >
                <option value="rating">Highest Rated</option>
                <option value="distance">Nearest</option>
                <option value="price-low">Price: Low to High</option>
                <option value="price-high">Price: High to Low</option>
              </select>
            </div>
          </div>

          {loading ? (
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">
              {Array.from({ length: 6 }).map((_, i) => (
                <ProviderCardSkeleton key={i} />
              ))}
            </div>
          ) : (
            <div className="space-y-8">
              {showRestaurants && filteredRestaurants.length > 0 && (
                <section>
                  <h2 className="text-lg font-semibold mb-3">Restaurants</h2>
                  <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">
                    {filteredRestaurants.map((restaurant, i) => (
                      <motion.div
                        key={restaurant.id}
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: i * 0.05 }}
                      >
                        <Link to={`/restaurant/${restaurant.id}`}>
                          <Card className="group h-full overflow-hidden cursor-pointer transition-all hover:-translate-y-1 hover:shadow-lg">
                            <div className="relative aspect-[4/3] overflow-hidden">
                              <img src={restaurant.image} alt={restaurant.name} className="h-full w-full object-cover transition-transform group-hover:scale-105" />
                              {restaurant.promoted && <Badge className="absolute top-2 left-2">Featured</Badge>}
                              <Badge className={cn('absolute top-2 right-2 text-white', restaurant.isOpen ? 'bg-success' : 'bg-destructive')}>
                                {restaurant.isOpen ? 'Open' : 'Closed'}
                              </Badge>
                            </div>
                            <CardContent className="p-4">
                              <div className="flex items-start justify-between gap-2">
                                <h3 className="font-semibold truncate">{restaurant.name}</h3>
                                <div className="flex items-center gap-1 shrink-0">
                                  <Star className="h-3.5 w-3.5 fill-warning text-warning" />
                                  <span className="text-sm font-semibold">{restaurant.rating}</span>
                                </div>
                              </div>
                              <p className="text-xs text-muted-foreground mt-1">{restaurant.cuisine.join(' · ')}</p>
                              <div className="mt-3 flex items-center justify-between text-xs text-muted-foreground">
                                <span className="flex items-center gap-1"><Clock className="h-3 w-3" />{restaurant.deliveryTime}</span>
                                <span className="flex items-center gap-1"><MapPin className="h-3 w-3" />{restaurant.distance} km</span>
                                <span>৳{restaurant.deliveryFee} fee</span>
                              </div>
                            </CardContent>
                          </Card>
                        </Link>
                      </motion.div>
                    ))}
                  </div>
                </section>
              )}

              {showProviders && filteredProviders.length > 0 && (
                <section>
                  <h2 className="text-lg font-semibold mb-3">Service Providers</h2>
                  <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">
                    {filteredProviders.map((provider, i) => (
                      <motion.div
                        key={provider.id}
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: i * 0.05 }}
                      >
                        <Link to={`/service/${provider.id}`}>
                          <Card className="group h-full cursor-pointer transition-all hover:-translate-y-1 hover:shadow-lg">
                            <CardContent className="p-4">
                              <div className="flex items-center gap-3 mb-3">
                                <img src={provider.avatar} alt={provider.name} className="h-14 w-14 rounded-full object-cover" />
                                <div className="flex-1 min-w-0">
                                  <div className="flex items-center gap-1">
                                    <p className="font-semibold truncate">{provider.name}</p>
                                    {provider.verified && <Shield className="h-3.5 w-3.5 text-primary shrink-0" />}
                                  </div>
                                  <p className="text-xs text-muted-foreground truncate">{provider.service}</p>
                                  <div className="mt-1 flex flex-wrap gap-1">
                                    {provider.badges.slice(0, 2).map((badge) => (
                                      <Badge key={badge} variant="secondary" className="text-[10px] py-0">{badge}</Badge>
                                    ))}
                                  </div>
                                </div>
                              </div>
                              <RatingStars rating={provider.rating} reviewCount={provider.reviewCount} />
                              <div className="mt-3 flex items-center justify-between text-xs text-muted-foreground">
                                <span className="flex items-center gap-1"><MapPin className="h-3 w-3" />{provider.distance} km</span>
                                <span className="flex items-center gap-1"><Clock className="h-3 w-3" />{provider.responseTime}</span>
                              </div>
                              <div className="mt-3 flex items-center justify-between">
                                <span className="text-sm font-semibold">From ৳{provider.priceFrom}</span>
                                <Badge variant={provider.status === 'available' ? 'default' : 'secondary'} className={provider.status === 'available' ? 'bg-success text-success-foreground' : ''}>
                                  {provider.status === 'available' ? 'Available' : 'Busy'}
                                </Badge>
                              </div>
                            </CardContent>
                          </Card>
                        </Link>
                      </motion.div>
                    ))}
                  </div>
                </section>
              )}

              {!loading && filteredProviders.length === 0 && !showRestaurants && (
                <div className="text-center py-16">
                  <p className="text-lg font-semibold">No results found</p>
                  <p className="text-muted-foreground mt-1">Try adjusting your filters or search query</p>
                  <Button variant="outline" className="mt-4" onClick={clearFilters}>Clear Filters</Button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      <Sheet open={showFilters} onOpenChange={setShowFilters}>
        <SheetContent side="left" className="w-full sm:max-w-sm overflow-y-auto">
          <SheetHeader>
            <SheetTitle>Filters</SheetTitle>
          </SheetHeader>
          <div className="mt-6">
            <FilterContent />
            <Button className="w-full mt-6" onClick={() => setShowFilters(false)}>
              Show {filteredProviders.length + (showRestaurants ? filteredRestaurants.length : 0)} Results
            </Button>
          </div>
        </SheetContent>
      </Sheet>
    </div>
  );
}
