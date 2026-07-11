import { Outlet } from 'react-router-dom';
import { Header } from '@/components/layout/header';
import { Footer } from '@/components/layout/footer';
import { RoleSwitcher } from '@/components/shared/role-switcher';
import { AiAssistant } from '@/components/shared/ai-assistant';

export function RootLayout() {
  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
      <RoleSwitcher />
      <AiAssistant />
    </div>
  );
}
