import path from 'path';
import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  optimizeDeps: {
    include: [
      'react-router-dom',
      'framer-motion',
      'zustand',
      'axios',
      'socket.io-client',
      '@tanstack/react-query',
      '@tanstack/react-query-devtools',
      'react-hook-form',
      'zod',
      '@hookform/resolvers',
      'lucide-react',
      'sonner',
    ],
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          'ui-vendor': ['framer-motion', 'lucide-react'],
          'query-vendor': ['@tanstack/react-query', '@tanstack/react-query-devtools'],
          'form-vendor': ['react-hook-form', 'zod', '@hookform/resolvers'],
        },
      },
    },
  },
});
