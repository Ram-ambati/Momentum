import { Link, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { LazyMotion, domAnimation, m } from 'framer-motion';
import { LayoutDashboard, CheckSquare, Calendar, Gift, TrendingUp, LogOut } from 'lucide-react';

export default function Layout() {
  const { user, logout } = useAuth();
  const location = useLocation();

  const navItems = [
    { name: 'DASHBOARD', path: '/dashboard', icon: LayoutDashboard },
    { name: 'TASKS', path: '/tasks', icon: CheckSquare },
    { name: 'HISTORY', path: '/history', icon: Calendar },
    { name: 'REWARDS', path: '/rewards', icon: Gift },
    { name: 'PROGRESS', path: '/progress', icon: TrendingUp },
  ];

  return (
    <LazyMotion features={domAnimation}>
      <div className="app-container">
        <header style={{ marginBottom: '40px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h1 className="vibrant-text" style={{ fontSize: '2.5rem', fontWeight: '700', letterSpacing: '-0.02em', marginBottom: '16px' }}>
              MOMENTUM
            </h1>
            <nav className="desktop-only" style={{ display: 'flex', gap: '24px', fontWeight: 'bold', fontSize: '0.9rem' }}>
              {navItems.map(item => {
                const isActive = location.pathname.startsWith(item.path);
                return (
                  <Link 
                    key={item.name} 
                    to={item.path}
                    style={{ 
                      textDecoration: 'none', 
                      color: isActive ? 'var(--text-primary)' : 'var(--text-secondary)',
                      transition: 'color 0.2s',
                      position: 'relative'
                    }}
                  >
                    {item.name}
                    {isActive && (
                      <m.div 
                        layoutId="nav-indicator"
                        style={{ position: 'absolute', bottom: '-4px', left: 0, right: 0, height: '2px', background: 'white', borderRadius: '2px' }}
                      />
                    )}
                  </Link>
                );
              })}
            </nav>
          </div>
          <div className="desktop-only" style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
            <span style={{ color: 'var(--text-secondary)', fontWeight: 500 }}>{user?.username}</span>
            <m.button 
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              className="btn-glass"
              onClick={logout}
            >
              Sign Out
            </m.button>
          </div>
          <div className="mobile-only" style={{ alignItems: 'center', gap: '16px' }}>
             <m.button 
              whileTap={{ scale: 0.95 }}
              onClick={logout}
              style={{ background: 'transparent', border: 'none', color: 'var(--text-secondary)' }}
            >
              <LogOut size={24} />
            </m.button>
          </div>
        </header>

        <main>
          <Outlet />
        </main>
      </div>

      {/* Mobile Bottom Navigation */}
      <div className="mobile-only apple-glass" style={{ 
          position: 'fixed', 
          bottom: '0', 
          left: '0', 
          right: '0', 
          height: '70px', 
          display: 'flex', 
          justifyContent: 'space-around', 
          alignItems: 'center',
          padding: '0 10px',
          zIndex: 1000,
          background: 'rgba(20, 20, 25, 0.95)',
          backdropFilter: 'blur(20px)',
          WebkitBackdropFilter: 'blur(20px)',
          borderRadius: '22px 22px 0 0',
          border: '1px solid rgba(255, 255, 255, 0.05)',
          borderBottom: 'none'
        }}>
          {navItems.map(item => {
            const isActive = location.pathname.startsWith(item.path);
            const Icon = item.icon;
            return (
              <Link 
                key={item.name} 
                to={item.path}
                style={{ 
                  textDecoration: 'none', 
                  color: isActive ? 'var(--text-primary)' : 'var(--text-secondary)',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  gap: '4px',
                  position: 'relative',
                  width: '60px'
                }}
              >
                <Icon size={24} strokeWidth={isActive ? 2.5 : 2} />
                <span style={{ fontSize: '0.65rem', fontWeight: isActive ? 600 : 500 }}>
                  {item.name}
                </span>
              </Link>
            );
          })}
      </div>
    </LazyMotion>
  );
}
