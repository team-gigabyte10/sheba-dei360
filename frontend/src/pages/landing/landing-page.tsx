import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  UtensilsCrossed, Car, Home, HeartPulse, Sparkles, ShoppingCart,
  Siren, Package, ArrowRight, Star, MapPin, Clock, Shield, TrendingUp,
  Smartphone, Apple, Play, Search,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { RatingStars } from '@/components/shared/rating-stars';
import { useTranslation } from '@/store/language-store';
import { categories, providers, restaurants, testimonials, emergencyServices, popularServices } from '@/lib/mock-data';
import { cn } from '@/lib/utils';

const iconMap: Record<string, React.ComponentType<{ className?: string }>> = {
  UtensilsCrossed, Car, Home, HeartPulse, Sparkles, ShoppingCart, Siren, Package,
};

const colorMap: Record<string, string> = {
  orange: 'bg-orange-100 text-orange-600 dark:bg-orange-900/30 dark:text-orange-400',
  sky: 'bg-sky-100 text-sky-600 dark:bg-sky-900/30 dark:text-sky-400',
  emerald: 'bg-emerald-100 text-emerald-600 dark:bg-emerald-900/30 dark:text-emerald-400',
  rose: 'bg-rose-100 text-rose-600 dark:bg-rose-900/30 dark:text-rose-400',
  pink: 'bg-pink-100 text-pink-600 dark:bg-pink-900/30 dark:text-pink-400',
  lime: 'bg-lime-100 text-lime-600 dark:bg-lime-900/30 dark:text-lime-400',
  red: 'bg-red-100 text-red-600 dark:bg-red-900/30 dark:text-red-400',
  amber: 'bg-amber-100 text-amber-600 dark:bg-amber-900/30 dark:text-amber-400',
};

export function LandingPage() {
  return (
    <div className="overflow-hidden">
      <HeroSection />
      <StatsBar />
      <CategoriesSection />
      <PopularServicesSection />
      <NearbyProvidersSection />
      <FeaturedRestaurantsSection />
      <EmergencySection />
      <HowItWorksSection />
      <TestimonialsSection />
      <DownloadSection />
    </div>
  );
}

function HeroSection() {
  const t = useTranslation();
  return (
    <section className="relative">
      <div className="absolute inset-0 -z-10 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-sky-50 via-white to-emerald-50 dark:from-slate-900 dark:via-slate-950 dark:to-slate-900" />
        <div className="absolute -top-24 -right-24 h-96 w-96 rounded-full bg-primary/20 blur-3xl animate-pulse-slow" />
        <div className="absolute top-1/2 -left-24 h-96 w-96 rounded-full bg-emerald-400/20 blur-3xl animate-pulse-slow" />
      </div>

      <div className="container mx-auto px-4 py-16 lg:py-24">
        <div className="grid items-center gap-12 lg:grid-cols-2">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="space-y-6"
          >
            <Badge variant="secondary" className="flex w-fit items-center gap-1.5 py-1.5 pl-2 pr-3">
              <span className="flex h-5 w-5 items-center justify-center rounded-full bg-success text-success-foreground">
                <Shield className="h-3 w-3" />
              </span>
              <span className="text-xs font-semibold">Trusted by 500K+ users</span>
            </Badge>

            <h1 className="text-4xl font-bold leading-tight tracking-tight text-balance sm:text-5xl lg:text-6xl">
              {t('hero.title')}
            </h1>

            <p className="text-lg text-muted-foreground text-balance max-w-xl">
              {t('hero.subtitle')}
            </p>

            <div className="flex flex-col gap-3 sm:flex-row">
              <Button size="lg" asChild className="h-12 text-base">
                <Link to="/search">
                  {t('cta.getStarted')}
                  <ArrowRight className="ml-2 h-5 w-5" />
                </Link>
              </Button>
              <Button size="lg" variant="outline" asChild className="h-12 text-base">
                <Link to="/emergency">
                  <Siren className="mr-2 h-5 w-5 text-destructive" />
                  Emergency
                </Link>
              </Button>
            </div>

            <div className="flex items-center gap-6 pt-4">
              <div className="flex -space-x-3">
                {[
                  'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=80',
                  'https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&w=80',
                  'https://images.pexels.com/photos/1043471/pexels-photo-1043471.jpeg?auto=compress&cs=tinysrgb&w=80',
                  'https://images.pexels.com/photos/1181686/pexels-photo-1181686.jpeg?auto=compress&cs=tinysrgb&w=80',
                ].map((src, i) => (
                  <img key={i} src={src} alt="" className="h-10 w-10 rounded-full border-2 border-background object-cover" />
                ))}
              </div>
              <div>
                <RatingStars rating={4.9} size="md" showValue />
                <p className="text-xs text-muted-foreground mt-0.5">From 50,000+ reviews</p>
              </div>
            </div>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.6, delay: 0.2 }}
            className="relative"
          >
            <div className="relative mx-auto max-w-md">
              <div className="absolute -inset-4 rounded-3xl bg-gradient-to-tr from-primary/20 to-emerald-400/20 blur-2xl" />
              <Card className="relative overflow-hidden border-2 border-primary/20 shadow-2xl">
                <div className="aspect-[4/3] relative">
                  <img
                    src="https://images.pexels.com/photos/4239036/pexels-photo-4239036.jpeg?auto=compress&cs=tinysrgb&w=800"
                    alt="Home cleaning service"
                    className="h-full w-full object-cover"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
                  <div className="absolute bottom-4 left-4 right-4 text-white">
                    <Badge className="mb-2 bg-success text-success-foreground">Available Now</Badge>
                    <h3 className="text-lg font-bold">Deep Home Cleaning</h3>
                    <p className="text-sm opacity-90">From ৳35 · 15 min away</p>
                  </div>
                </div>
              </Card>

              <motion.div
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: 0.5 }}
                className="absolute -left-6 top-1/4 hidden sm:block"
              >
                <Card className="w-48 shadow-xl animate-float">
                  <CardContent className="p-3">
                    <div className="flex items-center gap-2">
                      <div className="flex h-10 w-10 items-center justify-center rounded-full bg-sky-100 text-sky-600 dark:bg-sky-900/30 dark:text-sky-400">
                        <Car className="h-5 w-5" />
                      </div>
                      <div>
                        <p className="text-xs font-semibold">Ride Arriving</p>
                        <p className="text-xs text-muted-foreground">3 min away</p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </motion.div>

              <motion.div
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: 0.7 }}
                className="absolute -right-6 bottom-1/4 hidden sm:block"
              >
                <Card className="w-44 shadow-xl animate-float" style={{ animationDelay: '1s' }}>
                  <CardContent className="p-3">
                    <div className="flex items-center gap-2">
                      <div className="flex h-10 w-10 items-center justify-center rounded-full bg-emerald-100 text-emerald-600 dark:bg-emerald-900/30 dark:text-emerald-400">
                        <UtensilsCrossed className="h-5 w-5" />
                      </div>
                      <div>
                        <p className="text-xs font-semibold">Order Delivered</p>
                        <p className="text-xs text-muted-foreground">In 28 min</p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </motion.div>
            </div>
          </motion.div>
        </div>
      </div>
    </section>
  );
}

function StatsBar() {
  const stats = [
    { value: '500K+', label: 'Active Users' },
    { value: '50K+', label: 'Service Providers' },
    { value: '2M+', label: 'Bookings Completed' },
    { value: '4.9', label: 'Average Rating' },
  ];
  return (
    <section className="border-y bg-card">
      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
          {stats.map((stat) => (
            <div key={stat.label} className="text-center">
              <p className="text-3xl font-bold gradient-text">{stat.value}</p>
              <p className="text-sm text-muted-foreground mt-1">{stat.label}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

function CategoriesSection() {
  const t = useTranslation();
  return (
    <section className="py-16">
      <div className="container mx-auto px-4">
        <div className="mb-8 flex items-end justify-between">
          <div>
            <h2 className="text-3xl font-bold tracking-tight">{t('section.categories')}</h2>
            <p className="text-muted-foreground mt-2">Find the right service for any need</p>
          </div>
          <Button variant="ghost" asChild className="hidden sm:flex">
            <Link to="/search">{t('cta.viewAll')} <ArrowRight className="ml-1 h-4 w-4" /></Link>
          </Button>
        </div>

        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-8">
          {categories.map((cat, i) => {
            const Icon = iconMap[cat.icon] ?? Home;
            return (
              <motion.div
                key={cat.id}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.05 }}
              >
                <Link to={
                  cat.slug === 'grocery' ? '/grocery' :
                  cat.slug === 'ride' ? '/ride' :
                  cat.slug === 'health' ? '/doctor-appointment' :
                  cat.slug === 'emergency' ? '/emergency' :
                  `/search?category=${cat.slug}`
                }>
                  <Card className="group h-full cursor-pointer transition-all hover:-translate-y-1 hover:shadow-lg">
                    <CardContent className="flex flex-col items-center gap-3 p-4 text-center">
                      <div className={cn('flex h-14 w-14 items-center justify-center rounded-2xl transition-transform group-hover:scale-110', colorMap[cat.color])}>
                        <Icon className="h-7 w-7" />
                      </div>
                      <div>
                        <p className="text-sm font-semibold">{cat.name}</p>
                        <p className="text-xs text-muted-foreground">{cat.serviceCount}+ services</p>
                      </div>
                    </CardContent>
                  </Card>
                </Link>
              </motion.div>
            );
          })}
        </div>
      </div>
    </section>
  );
}

function PopularServicesSection() {
  return (
    <section className="py-16 bg-muted/30">
      <div className="container mx-auto px-4">
        <div className="mb-8">
          <h2 className="text-3xl font-bold tracking-tight">Popular Services</h2>
          <p className="text-muted-foreground mt-2">Most booked services this week</p>
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {popularServices.map((service, i) => {
            const Icon = iconMap[service.icon] ?? Home;
            return (
              <motion.div
                key={service.id}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.05 }}
              >
                <Link to={`/search?category=${service.category}`}>
                  <Card className="group h-full overflow-hidden cursor-pointer transition-all hover:-translate-y-1 hover:shadow-lg">
                    <div className="relative aspect-[16/9] overflow-hidden">
                      <img src={service.image} alt={service.name} className="h-full w-full object-cover transition-transform group-hover:scale-105" />
                      <div className="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent" />
                      <div className="absolute top-3 left-3">
                        <Badge className="bg-success text-success-foreground">{service.bookings} booked</Badge>
                      </div>
                    </div>
                    <CardContent className="p-4">
                      <div className="flex items-center gap-3">
                        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary">
                          <Icon className="h-5 w-5" />
                        </div>
                        <div className="flex-1">
                          <p className="font-semibold">{service.name}</p>
                          <p className="text-sm text-muted-foreground">From ৳{service.price}</p>
                        </div>
                        <ArrowRight className="h-4 w-4 text-muted-foreground transition-transform group-hover:translate-x-1" />
                      </div>
                    </CardContent>
                  </Card>
                </Link>
              </motion.div>
            );
          })}
        </div>
      </div>
    </section>
  );
}

function NearbyProvidersSection() {
  const t = useTranslation();
  const nearby = providers.slice(0, 4);
  return (
    <section className="py-16">
      <div className="container mx-auto px-4">
        <div className="mb-8 flex items-end justify-between">
          <div>
            <h2 className="text-3xl font-bold tracking-tight">{t('section.providers')}</h2>
            <p className="text-muted-foreground mt-2">Top-rated providers in your area</p>
          </div>
          <Button variant="ghost" asChild className="hidden sm:flex">
            <Link to="/search">{t('cta.viewAll')} <ArrowRight className="ml-1 h-4 w-4" /></Link>
          </Button>
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {nearby.map((provider, i) => (
            <motion.div
              key={provider.id}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: i * 0.05 }}
            >
              <Link to={`/service/${provider.id}`}>
                <Card className="group h-full cursor-pointer transition-all hover:-translate-y-1 hover:shadow-lg">
                  <CardContent className="p-4">
                    <div className="flex items-center gap-3 mb-3">
                      <img src={provider.avatar} alt={provider.name} className="h-12 w-12 rounded-full object-cover" />
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-1">
                          <p className="font-semibold truncate">{provider.name}</p>
                          {provider.verified && <Shield className="h-3.5 w-3.5 text-primary shrink-0" />}
                        </div>
                        <p className="text-xs text-muted-foreground truncate">{provider.service}</p>
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
      </div>
    </section>
  );
}

function FeaturedRestaurantsSection() {
  const t = useTranslation();
  const featured = restaurants.filter((r) => r.promoted || r.rating >= 4.7).slice(0, 4);
  return (
    <section className="py-16 bg-muted/30">
      <div className="container mx-auto px-4">
        <div className="mb-8 flex items-end justify-between">
          <div>
            <h2 className="text-3xl font-bold tracking-tight">{t('section.restaurants')}</h2>
            <p className="text-muted-foreground mt-2">Order from the best restaurants near you</p>
          </div>
          <Button variant="ghost" asChild className="hidden sm:flex">
            <Link to="/search?category=food">{t('cta.viewAll')} <ArrowRight className="ml-1 h-4 w-4" /></Link>
          </Button>
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {featured.map((restaurant, i) => (
            <motion.div
              key={restaurant.id}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: i * 0.05 }}
            >
              <Link to={`/restaurant/${restaurant.id}`}>
                <Card className="group h-full overflow-hidden cursor-pointer transition-all hover:-translate-y-1 hover:shadow-lg">
                  <div className="relative aspect-[4/3] overflow-hidden">
                    <img src={restaurant.image} alt={restaurant.name} className="h-full w-full object-cover transition-transform group-hover:scale-105" />
                    {restaurant.promoted && (
                      <Badge className="absolute top-2 left-2 bg-primary text-primary-foreground">Featured</Badge>
                    )}
                    <div className="absolute top-2 right-2">
                      <Badge className={cn('text-white', restaurant.isOpen ? 'bg-success' : 'bg-destructive')}>
                        {restaurant.isOpen ? 'Open' : 'Closed'}
                      </Badge>
                    </div>
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
                      <span>৳{restaurant.deliveryFee} delivery</span>
                    </div>
                  </CardContent>
                </Card>
              </Link>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}

function EmergencySection() {
  const t = useTranslation();
  return (
    <section className="py-16">
      <div className="container mx-auto px-4">
        <Card className="overflow-hidden border-destructive/20 bg-gradient-to-br from-red-50 to-orange-50 dark:from-red-950/20 dark:to-orange-950/20">
          <CardContent className="p-6 lg:p-8">
            <div className="flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
              <div className="space-y-2">
                <div className="flex items-center gap-2">
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-destructive/10 text-destructive">
                    <Siren className="h-5 w-5" />
                  </div>
                  <h2 className="text-2xl font-bold">{t('section.emergency')}</h2>
                </div>
                <p className="text-muted-foreground max-w-lg">24/7 emergency services at your fingertips. Ambulance, fire, police, and emergency home repairs — one tap away.</p>
              </div>
              <Button size="lg" variant="destructive" asChild>
                <Link to="/emergency">View Emergency Services <ArrowRight className="ml-2 h-4 w-4" /></Link>
              </Button>
            </div>

            <div className="mt-6 grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-6">
              {emergencyServices.map((service) => {
                const Icon = iconMap[service.icon] ?? Siren;
                return (
                  <Link key={service.id} to="/emergency" className="group">
                    <div className="flex flex-col items-center gap-2 rounded-xl border bg-card p-4 text-center transition-all hover:-translate-y-1 hover:shadow-md">
                      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-destructive/10 text-destructive transition-transform group-hover:scale-110">
                        <Icon className="h-6 w-6" />
                      </div>
                      <div>
                        <p className="text-sm font-semibold">{service.name}</p>
                        <p className="text-xs text-muted-foreground">{service.responseTime}</p>
                      </div>
                    </div>
                  </Link>
                );
              })}
            </div>
          </CardContent>
        </Card>
      </div>
    </section>
  );
}

function HowItWorksSection() {
  const steps = [
    { icon: Search, title: 'Search', description: 'Find services, restaurants, or providers near you' },
    { icon: Smartphone, title: 'Book', description: 'Choose your time, address, and payment method' },
    { icon: TrendingUp, title: 'Track', description: 'Live-track your provider and chat in real-time' },
    { icon: Star, title: 'Enjoy', description: 'Rate your experience and earn rewards' },
  ];
  return (
    <section className="py-16 bg-muted/30">
      <div className="container mx-auto px-4">
        <div className="mb-8 text-center">
          <h2 className="text-3xl font-bold tracking-tight">How It Works</h2>
          <p className="text-muted-foreground mt-2">Get started in 4 simple steps</p>
        </div>
        <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-4">
          {steps.map((step, i) => (
            <motion.div
              key={step.title}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: i * 0.1 }}
              className="relative text-center"
            >
              <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-primary text-primary-foreground shadow-lg">
                <step.icon className="h-8 w-8" />
              </div>
              <div className="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-2 flex h-6 w-6 items-center justify-center rounded-full bg-card border-2 border-primary text-xs font-bold text-primary">
                {i + 1}
              </div>
              <h3 className="mt-4 font-semibold">{step.title}</h3>
              <p className="mt-1 text-sm text-muted-foreground">{step.description}</p>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}

function TestimonialsSection() {
  const t = useTranslation();
  return (
    <section className="py-16">
      <div className="container mx-auto px-4">
        <div className="mb-8 text-center">
          <h2 className="text-3xl font-bold tracking-tight">{t('section.testimonials')}</h2>
          <p className="text-muted-foreground mt-2">Don't just take our word for it</p>
        </div>

        <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4">
          {testimonials.map((testimonial, i) => (
            <motion.div
              key={testimonial.id}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: i * 0.05 }}
            >
              <Card className="h-full">
                <CardContent className="p-6">
                  <RatingStars rating={testimonial.rating} showValue={false} />
                  <p className="mt-3 text-sm text-muted-foreground leading-relaxed">"{testimonial.comment}"</p>
                  <div className="mt-4 flex items-center gap-3">
                    <img src={testimonial.avatar} alt={testimonial.name} className="h-10 w-10 rounded-full object-cover" />
                    <div>
                      <p className="text-sm font-semibold">{testimonial.name}</p>
                      <p className="text-xs text-muted-foreground">{testimonial.role}</p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}

function DownloadSection() {
  const t = useTranslation();
  return (
    <section className="py-16">
      <div className="container mx-auto px-4">
        <Card className="overflow-hidden border-0 bg-gradient-to-br from-primary to-sky-600 text-white shadow-2xl">
          <CardContent className="grid items-center gap-8 p-8 lg:grid-cols-2 lg:p-12">
            <div className="space-y-4">
              <h2 className="text-3xl font-bold tracking-tight lg:text-4xl">{t('section.download')}</h2>
              <p className="text-lg text-white/90 max-w-md">Book services, track orders, and chat with providers — all from your phone. Available on iOS and Android.</p>
              <div className="flex flex-col gap-3 sm:flex-row">
                <a href="#" className="flex items-center gap-3 rounded-xl bg-white px-5 py-3 text-foreground transition-transform hover:scale-105">
                  <Apple className="h-7 w-7" />
                  <div className="text-left">
                    <p className="text-xs">Download on the</p>
                    <p className="text-sm font-semibold">App Store</p>
                  </div>
                </a>
                <a href="#" className="flex items-center gap-3 rounded-xl bg-white px-5 py-3 text-foreground transition-transform hover:scale-105">
                  <Play className="h-7 w-7" />
                  <div className="text-left">
                    <p className="text-xs">GET IT ON</p>
                    <p className="text-sm font-semibold">Google Play</p>
                  </div>
                </a>
              </div>
            </div>
            <div className="relative hidden lg:block">
              <div className="mx-auto max-w-xs">
                <div className="rounded-[2rem] border-4 border-white/20 bg-white/10 p-2 backdrop-blur">
                  <div className="rounded-[1.5rem] overflow-hidden">
                    <img
                      src="https://images.pexels.com/photos/4239036/pexels-photo-4239036.jpeg?auto=compress&cs=tinysrgb&w=400"
                      alt="App preview"
                      className="w-full"
                    />
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </section>
  );
}
