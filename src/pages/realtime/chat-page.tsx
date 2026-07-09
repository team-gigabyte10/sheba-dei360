import { useState, useEffect, useRef } from 'react';
import { useParams, Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
  ChevronLeft, Send, Phone, Video, MoreVertical, MapPin, Image as ImageIcon,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { providers, chatMessages } from '@/lib/mock-data';
import { cn } from '@/lib/utils';

export function ChatPage() {
  const { providerId } = useParams();
  const provider = providers.find((p) => p.id === providerId) || providers[0];
  const [messages, setMessages] = useState(chatMessages[providerId || 'p1'] || chatMessages['p1'] || []);
  const [input, setInput] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = () => {
    if (!input.trim()) return;
    const newMsg = {
      id: `c${Date.now()}`,
      senderId: 'u1',
      senderName: 'You',
      text: input,
      timestamp: new Date().toISOString(),
      type: 'text' as const,
    };
    setMessages([...messages, newMsg]);
    setInput('');

    setTimeout(() => {
      const reply = {
        id: `c${Date.now() + 1}`,
        senderId: provider.id,
        senderName: provider.name,
        text: 'Thank you for your message. I will get back to you shortly.',
        timestamp: new Date().toISOString(),
        type: 'text' as const,
      };
      setMessages((prev: typeof messages) => [...prev, reply]);
    }, 2000);
  };

  return (
    <div className="container mx-auto px-4 py-6 max-w-3xl">
      <div className="flex flex-col h-[calc(100vh-8rem)]">
        <Button variant="ghost" size="sm" asChild className="mb-4 self-start">
          <Link to="/dashboard/bookings"><ChevronLeft className="mr-1 h-4 w-4" /> Back</Link>
        </Button>

        <Card className="flex flex-col flex-1 overflow-hidden">
          <div className="flex items-center gap-3 p-4 border-b">
            <Avatar className="h-10 w-10">
              <AvatarImage src={provider.avatar} alt={provider.name} />
              <AvatarFallback>{provider.name.charAt(0)}</AvatarFallback>
            </Avatar>
            <div className="flex-1">
              <p className="font-semibold">{provider.name}</p>
              <div className="flex items-center gap-1">
                <span className="h-2 w-2 rounded-full bg-success animate-pulse" />
                <span className="text-xs text-muted-foreground">Online · {provider.responseTime}</span>
              </div>
            </div>
            <div className="flex gap-1">
              <Button size="icon" variant="ghost"><Phone className="h-4 w-4" /></Button>
              <Button size="icon" variant="ghost"><Video className="h-4 w-4" /></Button>
              <Button size="icon" variant="ghost"><MoreVertical className="h-4 w-4" /></Button>
            </div>
          </div>

          <div className="flex-1 overflow-y-auto p-4 space-y-3 bg-muted/30">
            <div className="text-center">
              <Badge variant="secondary" className="text-xs">Today</Badge>
            </div>
            <AnimatePresence>
              {messages.map((msg: typeof messages[0]) => {
                const isUser = msg.senderId === 'u1';
                return (
                  <motion.div
                    key={msg.id}
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    className={cn('flex', isUser ? 'justify-end' : 'justify-start')}
                  >
                    <div className={cn('flex items-end gap-2 max-w-[75%]', isUser && 'flex-row-reverse')}>
                      {!isUser && (
                        <Avatar className="h-7 w-7 shrink-0">
                          <AvatarImage src={provider.avatar} alt={provider.name} />
                          <AvatarFallback className="text-xs">{provider.name.charAt(0)}</AvatarFallback>
                        </Avatar>
                      )}
                      <div className={cn(
                        'rounded-2xl px-4 py-2 text-sm',
                        isUser
                          ? 'bg-primary text-primary-foreground rounded-br-sm'
                          : 'bg-card border rounded-bl-sm'
                      )}>
                        <p>{msg.text}</p>
                        <p className={cn('text-xs mt-0.5', isUser ? 'text-primary-foreground/70' : 'text-muted-foreground')}>
                          {new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                        </p>
                      </div>
                    </div>
                  </motion.div>
                );
              })}
            </AnimatePresence>
            <div ref={messagesEndRef} />
          </div>

          <div className="p-3 border-t">
            <div className="flex items-center gap-2">
              <Button size="icon" variant="ghost" className="shrink-0"><ImageIcon className="h-5 w-5" /></Button>
              <Button size="icon" variant="ghost" className="shrink-0"><MapPin className="h-5 w-5" /></Button>
              <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                placeholder="Type a message..."
                className="flex-1 rounded-full bg-muted px-4 py-2 text-sm outline-none focus:ring-2 focus:ring-primary"
              />
              <Button size="icon" className="shrink-0 rounded-full" onClick={handleSend} disabled={!input.trim()}>
                <Send className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
}
