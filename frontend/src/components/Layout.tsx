import { Link, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { LazyMotion, domAnimation, m } from 'framer-motion';

export default function Layout() {
  const { user, logout } = useAuth();
  const location = useLocation();

  const navItems = [
    { name: 'DASHBOARD', path: '/dashboard' },
    { name: 'TASKS', path: '/tasks' },
    { name: 'HISTORY', path: '/history' },
    { name: 'REWARDS', path: '/rewards' },
    { name: 'PROGRESS', path: '/progress' },
  ];

  return (
    <LazyMotion features={domAnimation}>
      <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '40px 20px' }}>
        <header style={{ marginBottom: '40px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h1 className="vibrant-text" style={{ fontSize: '2.5rem', fontWeight: '700', letterSpacing: '-0.02em', marginBottom: '16px' }}>
              MOMENTUM
            </h1>
            <nav style={{ display: 'flex', gap: '24px', fontWeight: 'bold', fontSize: '0.9rem' }}>
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
          <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
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
        </header>

        <main>
          <Outlet />
        </main>
      </div>
    </LazyMotion>
  );
}
