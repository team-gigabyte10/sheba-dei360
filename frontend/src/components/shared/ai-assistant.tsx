import { useState, useRef, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { Card, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { ScrollArea } from '@/components/ui/scroll-area';
import { MessageSquare, X, Send, Sparkles, Brain, Bot, Loader2 } from 'lucide-react';

interface ChatMessage {
  id: string;
  sender: 'user' | 'bot';
  text: string;
  timestamp: string;
}

export function AiAssistant() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: 'welcome',
      sender: 'bot',
      text: 'Hello! I am your ServeNear AI Assistant. Ask me to find services, book rides, or locate emergency responders near you!',
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    },
  ]);
  const [input, setInput] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isTyping]);

  const handleSend = (textToSend?: string) => {
    const text = (textToSend || input).trim();
    if (!text) return;

    const userMsg: ChatMessage = {
      id: `u-${Date.now()}`,
      sender: 'user',
      text,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setMessages((prev) => [...prev, userMsg]);
    setInput('');
    setIsTyping(true);

    setTimeout(() => {
      let reply = 'I can help you with anything on ServeNear! Try asking about: "food delivery", "book a plumber", "request a ride", or "doctor visits".';
      const cleanText = text.toLowerCase();

      if (cleanText.includes('restaurant') || cleanText.includes('food') || cleanText.includes('eat') || cleanText.includes('kacchi')) {
        reply = 'I highly recommend **Sultan\'s Dine**! Their Mutton Kacchi has a 4.9 rating. You can order here: [Order Food](/restaurant/rest1).';
      } else if (cleanText.includes('plumber') || cleanText.includes('repair') || cleanText.includes('leak') || cleanText.includes('pipe')) {
        reply = 'I found **Kamrul Plumber** (4.9 rating) in your area. You can book his home visit here: [Book Plumber](/service/s2).';
      } else if (cleanText.includes('ride') || cleanText.includes('car') || cleanText.includes('bike') || cleanText.includes('fare') || cleanText.includes('taxi')) {
        reply = 'Need to travel? Book a Moto or Car ride with real-time vector map tracking here: [Book Ride](/ride).';
      } else if (cleanText.includes('emergency') || cleanText.includes('sos') || cleanText.includes('accident') || cleanText.includes('police') || cleanText.includes('ambulance') || cleanText.includes('hospital')) {
        reply = 'For emergency hotline dialing or live GPS responder dispatching, go straight to: [Emergency Center](/emergency).';
      } else if (cleanText.includes('doctor') || cleanText.includes('medical') || cleanText.includes('health') || cleanText.includes('nurse') || cleanText.includes('pediatrics')) {
        reply = 'I can help you schedule appointments with verified specialists. Schedule here: [Doctor Appointments](/doctor-appointment).';
      } else if (cleanText.includes('hi') || cleanText.includes('hello') || cleanText.includes('hey')) {
        reply = 'Hello! I am your AI Assistant. How can I help you navigate the ServeNear platform today?';
      } else if (cleanText.includes('thank') || cleanText.includes('thanks') || cleanText.includes('awesome')) {
        reply = 'You\'re welcome! Let me know if you need anything else.';
      }

      const botMsg: ChatMessage = {
        id: `b-${Date.now()}`,
        sender: 'bot',
        text: reply,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      };

      setMessages((prev) => [...prev, botMsg]);
      setIsTyping(false);
    }, 1200);
  };

  const renderMessageText = (text: string) => {
    // Basic parser for mock links [Text](/path)
    const linkRegex = /\[([^\]]+)\]\(([^)]+)\)/g;
    const parts = [];
    let lastIndex = 0;
    let match;

    while ((match = linkRegex.exec(text)) !== null) {
      if (match.index > lastIndex) {
        parts.push(text.substring(lastIndex, match.index));
      }
      parts.push(
        <Link
          key={match.index}
          to={match[2]}
          onClick={() => setIsOpen(false)}
          className="text-primary font-bold hover:underline underline-offset-2"
        >
          {match[1]}
        </Link>
      );
      lastIndex = linkRegex.lastIndex;
    }

    if (lastIndex < text.length) {
      parts.push(text.substring(lastIndex));
    }

    return parts.length > 0 ? parts : text;
  };

  return (
    <>
      {/* Floating Action Button (FAB) */}
      <Button
        onClick={() => setIsOpen(!isOpen)}
        className="fixed bottom-6 right-6 h-14 w-14 rounded-full shadow-2xl z-40 bg-gradient-to-tr from-primary to-sky-600 hover:from-primary/95 hover:to-sky-700 text-white flex items-center justify-center p-0 border-none scale-100 hover:scale-105 active:scale-95 transition-transform"
      >
        <MessageSquare className="h-6 w-6" />
      </Button>

      {/* Chat Window Panel */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: 50, scale: 0.9 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 50, scale: 0.9 }}
            className="fixed bottom-24 right-6 w-full max-w-[360px] sm:max-w-[380px] h-[480px] z-40 shadow-2xl"
          >
            <Card className="h-full flex flex-col overflow-hidden border border-border/40 bg-background/95 backdrop-blur-md rounded-3xl">
              <CardHeader className="bg-gradient-to-r from-primary to-sky-600 text-white p-4 flex flex-row items-center justify-between">
                <div className="flex items-center gap-2">
                  <div className="flex h-9 w-9 items-center justify-center rounded-full bg-white/20">
                    <Brain className="h-5 w-5 text-white animate-pulse" />
                  </div>
                  <div>
                    <CardTitle className="text-sm font-bold flex items-center gap-1.5">
                      ServeNear AI <Sparkles className="h-3 w-3 text-amber-300" />
                    </CardTitle>
                    <div className="flex items-center gap-1">
                      <span className="h-1.5 w-1.5 rounded-full bg-emerald-400 animate-pulse" />
                      <span className="text-[10px] text-white/80 font-medium">Active Assistant</span>
                    </div>
                  </div>
                </div>
                <Button
                  size="icon"
                  variant="ghost"
                  onClick={() => setIsOpen(false)}
                  className="h-8 w-8 text-white/80 hover:text-white hover:bg-white/10 rounded-full"
                >
                  <X className="h-4 w-4" />
                </Button>
              </CardHeader>

              {/* Messages Area */}
              <ScrollArea className="flex-1 p-4 bg-muted/20">
                <div className="space-y-4">
                  {messages.map((msg) => {
                    const isUser = msg.sender === 'user';
                    return (
                      <div key={msg.id} className={`flex gap-2 ${isUser ? 'justify-end' : 'justify-start'}`}>
                        {!isUser && (
                          <div className="h-8 w-8 rounded-full bg-primary/10 text-primary flex items-center justify-center shrink-0">
                            <Bot className="h-4.5 w-4.5" />
                          </div>
                        )}
                        <div className="space-y-1 max-w-[75%]">
                          <div className={`rounded-2xl px-3.5 py-2 text-xs font-medium shadow-sm leading-relaxed ${
                            isUser
                              ? 'bg-primary text-primary-foreground rounded-tr-none'
                              : 'bg-muted border border-border/40 text-foreground rounded-tl-none'
                          }`}>
                            {renderMessageText(msg.text)}
                          </div>
                          <p className="text-[9px] text-muted-foreground text-right">{msg.timestamp}</p>
                        </div>
                      </div>
                    );
                  })}

                  {isTyping && (
                    <div className="flex gap-2 justify-start">
                      <div className="h-8 w-8 rounded-full bg-primary/10 text-primary flex items-center justify-center shrink-0">
                        <Bot className="h-4.5 w-4.5" />
                      </div>
                      <div className="bg-muted border border-border/40 rounded-2xl px-4 py-2 flex items-center gap-1">
                        <Loader2 className="h-3 w-3 animate-spin text-muted-foreground" />
                        <span className="text-[10px] text-muted-foreground font-semibold">AI is typing...</span>
                      </div>
                    </div>
                  )}
                  <div ref={messagesEndRef} />
                </div>
              </ScrollArea>

              {/* Quick Suggestion Tags */}
              <div className="px-4 py-2 border-t flex gap-1.5 overflow-x-auto scrollbar-none shrink-0 bg-muted/10">
                {[
                  { text: 'Order Kacchi', label: 'Order Kacchi' },
                  { text: 'Book Plumber', label: 'Find Plumber' },
                  { text: 'Request Ride', label: 'Check Fare' },
                  { text: 'SOS Medical Help', label: 'SOS Alert' }
                ].map((s) => (
                  <button
                    key={s.text}
                    onClick={() => handleSend(s.text)}
                    className="rounded-full bg-background border border-border px-3 py-1 text-[10px] font-semibold text-muted-foreground hover:border-primary hover:text-primary transition-colors shrink-0"
                  >
                    {s.label}
                  </button>
                ))}
              </div>

              {/* Form Input Bar */}
              <div className="p-3 border-t flex gap-2 items-center bg-background">
                <Input
                  placeholder="Ask ServeNear AI..."
                  value={input}
                  onChange={(e) => setInput(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                  className="h-9 rounded-xl text-xs"
                />
                <Button size="icon" onClick={() => handleSend()} className="h-9 w-9 rounded-xl shrink-0">
                  <Send className="h-4 w-4" />
                </Button>
              </div>
            </Card>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
}
