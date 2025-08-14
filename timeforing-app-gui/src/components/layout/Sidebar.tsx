import { Home, Clock, FolderOpen, User, BarChart3 } from 'lucide-react';
import { NavLink } from 'react-router-dom';
import { cn } from '@/utils/cn';

const navigation = [
  { name: 'Dashboard', href: '/', icon: Home },
  { name: 'Timeføring', href: '/time-entries', icon: Clock },
  { name: 'Prosjekter', href: '/projects', icon: FolderOpen },
  { name: 'Rapporter', href: '/reports', icon: BarChart3 },
  { name: 'Profil', href: '/profile', icon: User },
] as const;

export const Sidebar = () => {
  return (
    <aside className="w-64 border-r bg-card/50 hidden lg:block">
      <nav className="p-6">
        <ul className="space-y-2">
          {navigation.map((item) => (
            <li key={item.name}>
              <NavLink
                to={item.href}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors',
                    isActive
                      ? 'bg-primary text-primary-foreground'
                      : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
                  )
                }
              >
                <item.icon className="h-4 w-4" />
                {item.name}
              </NavLink>
            </li>
          ))}
        </ul>
      </nav>
    </aside>
  );
};
