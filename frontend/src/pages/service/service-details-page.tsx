import { useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  Star, MapPin, Clock, Shield, CheckCircle, Calendar, MessageCircle,
  ChevronLeft, Share2, Heart, Award, ThumbsUp,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Separator } from '@/components/ui/separator';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { RatingStars } from '@/components/shared/rating-stars';
import { useBookingStore } from '@/store/booking-store';
import { providers, reviews } from '@/lib/mock-data';
import { toast } from 'sonner';

export function ServiceDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { setDraft } = useBookingStore();
  const [selectedSlot, setSelectedSlot] = useState<string | null>(null);
  const [isWishlisted, setIsWishlisted] = useState(false);

  const provider = providers.find((p) => p.id === id);

  if (!provider) {
    return (
      <div className="container mx-auto px-4 py-16 text-center">
        <p className="text-lg font-semibold">Service not found</p>
        <Button asChild className="mt-4"><Link to="/search">Back to Search</Link></Button>
      </div>
    );
  }

  const handleBookNow = () => {
    setDraft({
      serviceId: provider.id,
      serviceName: provider.service,
      providerId: provider.id,
      providerName: provider.name,
      providerAvatar: provider.avatar,
      price: provider.priceFrom,
      type: 'service',
    });
    navigate('/booking/datetime');
  };

  return (
    <div className="container mx-auto px-4 py-6">
      <Button variant="ghost" size="sm" asChild className="mb-4">
        <Link to="/search"><ChevronLeft className="mr-1 h-4 w-4" /> Back</Link>
      </Button>

      <div className="grid gap-6 lg:grid-cols-3">
        <div className="lg:col-span-2 space-y-6">
          <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
            <div className="relative aspect-[16/9] rounded-xl overflow-hidden">
              <img src={provider.gallery[0] || provider.avatar} alt={provider.service} className="h-full w-full object-cover" />
              <div className="absolute top-4 right-4 flex gap-2">
                <Button size="icon" variant="secondary" onClick={() => setIsWishlisted(!isWishlisted)}>
                  <Heart className={isWishlisted ? 'h-4 w-4 fill-destructive text-destructive' : 'h-4 w-4'} />
                </Button>
                <Button size="icon" variant="secondary" onClick={() => toast.success('Link copied!')}>
                  <Share2 className="h-4 w-4" />
                </Button>
              </div>
            </div>
          </motion.div>

          <div>
            <div className="flex items-start justify-between gap-4">
              <div>
                <h1 className="text-2xl font-bold tracking-tight">{provider.service}</h1>
                <div className="mt-2 flex flex-wrap items-center gap-3">
                  <RatingStars rating={provider.rating} reviewCount={provider.reviewCount} size="md" />
                  <Badge variant="secondary" className="flex items-center gap-1">
                    <MapPin className="h-3 w-3" /> {provider.distance} km away
                  </Badge>
                  <Badge variant={provider.status === 'available' ? 'default' : 'secondary'} className={provider.status === 'available' ? 'bg-success text-success-foreground' : ''}>
                    {provider.status === 'available' ? 'Available Now' : 'Currently Busy'}
                  </Badge>
                </div>
              </div>
            </div>
          </div>

          <Tabs defaultValue="about">
            <TabsList className="w-full justify-start">
              <TabsTrigger value="about">About</TabsTrigger>
              <TabsTrigger value="reviews">Reviews</TabsTrigger>
              <TabsTrigger value="gallery">Gallery</TabsTrigger>
            </TabsList>

            <TabsContent value="about" className="space-y-4">
              <Card>
                <CardContent className="p-6">
                  <h3 className="font-semibold mb-2">About this service</h3>
                  <p className="text-sm text-muted-foreground leading-relaxed">{provider.bio}</p>
                  <Separator className="my-4" />
                  <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
                    <div>
                      <p className="text-xs text-muted-foreground">Completed Jobs</p>
                      <p className="text-lg font-semibold">{provider.completedJobs}+</p>
                    </div>
                    <div>
                      <p className="text-xs text-muted-foreground">Response Time</p>
                      <p className="text-lg font-semibold">{provider.responseTime}</p>
                    </div>
                    <div>
                      <p className="text-xs text-muted-foreground">Rating</p>
                      <p className="text-lg font-semibold">{provider.rating} / 5</p>
                    </div>
                    <div>
                      <p className="text-xs text-muted-foreground">Reviews</p>
                      <p className="text-lg font-semibold">{provider.reviewCount}</p>
                    </div>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardContent className="p-6">
                  <h3 className="font-semibold mb-3">What's included</h3>
                  <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
                    {['Professional service', 'Quality guaranteed', 'Eco-friendly products', 'Background verified', 'On-time arrival', 'Satisfaction guaranteed'].map((item) => (
                      <div key={item} className="flex items-center gap-2 text-sm">
                        <CheckCircle className="h-4 w-4 text-success shrink-0" />
                        <span>{item}</span>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="reviews" className="space-y-4">
              <Card>
                <CardContent className="p-6">
                  <div className="flex flex-col items-center gap-4 sm:flex-row sm:justify-between">
                    <div className="text-center sm:text-left">
                      <p className="text-4xl font-bold">{provider.rating}</p>
                      <RatingStars rating={provider.rating} showValue={false} className="mt-1" />
                      <p className="text-sm text-muted-foreground mt-1">{provider.reviewCount} reviews</p>
                    </div>
                    <div className="w-full max-w-xs space-y-1">
                      {[5, 4, 3, 2, 1].map((star) => (
                        <div key={star} className="flex items-center gap-2">
                          <span className="text-xs w-3">{star}</span>
                          <Star className="h-3 w-3 fill-warning text-warning" />
                          <div className="h-2 flex-1 rounded-full bg-muted overflow-hidden">
                            <div className="h-full bg-warning" style={{ width: `${star === 5 ? 78 : star === 4 ? 15 : star === 3 ? 5 : 1}%` }} />
                          </div>
                          <span className="text-xs text-muted-foreground w-8">{star === 5 ? '78%' : star === 4 ? '15%' : star === 3 ? '5%' : '1%'}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                </CardContent>
              </Card>

              {reviews.map((review) => (
                <Card key={review.id}>
                  <CardContent className="p-4">
                    <div className="flex items-start gap-3">
                      <Avatar className="h-10 w-10">
                        <AvatarImage src={review.avatar} alt={review.author} />
                        <AvatarFallback>{review.author.charAt(0)}</AvatarFallback>
                      </Avatar>
                      <div className="flex-1">
                        <div className="flex items-center justify-between">
                          <p className="font-semibold text-sm">{review.author}</p>
                          <span className="text-xs text-muted-foreground">{review.date}</span>
                        </div>
                        <RatingStars rating={review.rating} showValue={false} className="mt-1" />
                        <p className="mt-2 text-sm text-muted-foreground">{review.comment}</p>
                        <div className="mt-2 flex items-center gap-1 text-xs text-muted-foreground">
                          <ThumbsUp className="h-3 w-3" /> Helpful ({review.helpful})
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </TabsContent>

            <TabsContent value="gallery" className="space-y-4">
              {provider.gallery.length > 0 ? (
                <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
                  {provider.gallery.map((img, i) => (
                    <div key={i} className="aspect-square rounded-xl overflow-hidden">
                      <img src={img} alt={`Gallery ${i + 1}`} className="h-full w-full object-cover" />
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-muted-foreground text-center py-8">No gallery images available</p>
              )}
            </TabsContent>
          </Tabs>
        </div>

        <div className="lg:col-span-1">
          <div className="sticky top-20 space-y-4">
            <Card>
              <CardContent className="p-6 space-y-4">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-xs text-muted-foreground">Starting from</p>
                    <p className="text-3xl font-bold">৳{provider.priceFrom}</p>
                    <p className="text-xs text-muted-foreground">per {provider.priceUnit}</p>
                  </div>
                  <Badge variant="secondary" className="flex items-center gap-1">
                    <Award className="h-3 w-3" /> Top Rated
                  </Badge>
                </div>

                <Separator />

                <div>
                  <h4 className="text-sm font-semibold mb-3">Available Time Slots</h4>
                  <div className="grid grid-cols-3 gap-2">
                    {provider.availableSlots.map((slot) => (
                      <Button
                        key={slot}
                        variant={selectedSlot === slot ? 'default' : 'outline'}
                        size="sm"
                        onClick={() => setSelectedSlot(slot)}
                        className="text-xs"
                      >
                        {slot}
                      </Button>
                    ))}
                  </div>
                </div>

                <Separator />

                <div className="flex items-center gap-3">
                  <Avatar className="h-12 w-12">
                    <AvatarImage src={provider.avatar} alt={provider.name} />
                    <AvatarFallback>{provider.name.charAt(0)}</AvatarFallback>
                  </Avatar>
                  <div className="flex-1">
                    <div className="flex items-center gap-1">
                      <p className="font-semibold text-sm">{provider.name}</p>
                      {provider.verified && <Shield className="h-3.5 w-3.5 text-primary" />}
                    </div>
                    <p className="text-xs text-muted-foreground">{provider.completedJobs}+ jobs completed</p>
                  </div>
                </div>

                <div className="flex gap-2">
                  <Button className="flex-1" onClick={handleBookNow}>
                    <Calendar className="mr-2 h-4 w-4" /> Book Now
                  </Button>
                  <Button variant="outline" size="icon" asChild>
                    <Link to={`/chat/${provider.id}`}>
                      <MessageCircle className="h-4 w-4" />
                    </Link>
                  </Button>
                </div>

                <div className="flex items-center justify-center gap-4 text-xs text-muted-foreground">
                  <span className="flex items-center gap-1"><Shield className="h-3 w-3" /> Verified</span>
                  <span className="flex items-center gap-1"><Clock className="h-3 w-3" /> {provider.responseTime}</span>
                  <span className="flex items-center gap-1"><MapPin className="h-3 w-3" /> {provider.distance} km</span>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
}
