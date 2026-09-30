import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { LazyMotion, domAnimation, m } from 'framer-motion';

export default function Login() {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  
  const { user, login, register } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (user) {
      navigate('/dashboard');
    }
  }, [user, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      if (isLogin) {
        await login(username, password);
      } else {
        await register(username, password);
      }
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.message || 'Authentication failed');
    }
  };

  return (
    <LazyMotion features={domAnimation}>
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', padding: '20px' }}>
        <m.div 
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ type: 'spring', stiffness: 300, damping: 30 }}
          className="apple-glass" 
          style={{ maxWidth: '400px', width: '100%', padding: '40px' }}
        >
          <h2 className="vibrant-text" style={{ textAlign: 'center', marginBottom: '32px', fontSize: '2rem', fontWeight: 600 }}>
            {isLogin ? 'Sign In' : 'Create Account'}
          </h2>
          
          {error && (
            <div style={{ background: 'rgba(239, 68, 68, 0.2)', color: '#FCA5A5', padding: '12px', border: '1px solid rgba(239, 68, 68, 0.3)', borderRadius: '12px', marginBottom: '24px', textAlign: 'center', fontSize: '0.9rem' }}>
              {error}
            </div>
          )}
          
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '8px', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Username</label>
              <input 
                type="text" 
                value={username}
                onChange={e => setUsername(e.target.value)}
                style={{ 
                  width: '100%', padding: '16px', borderRadius: '12px', 
                  border: '1px solid var(--glass-border)', background: 'rgba(0,0,0,0.3)', 
                  color: 'white', outline: 'none', fontSize: '1rem' 
                }}
                required 
              />
            </div>
            <div>
              <label style={{ display: 'block', marginBottom: '8px', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Password</label>
              <input 
                type="password" 
                value={password}
                onChange={e => setPassword(e.target.value)}
                style={{ 
                  width: '100%', padding: '16px', borderRadius: '12px', 
                  border: '1px solid var(--glass-border)', background: 'rgba(0,0,0,0.3)', 
                  color: 'white', outline: 'none', fontSize: '1rem' 
                }}
                required 
              />
            </div>
            <m.button 
              type="submit" 
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              style={{ 
                marginTop: '16px', padding: '16px', borderRadius: '100px',
                background: 'rgba(255,255,255,0.9)', color: 'black',
                fontWeight: 600, border: 'none', cursor: 'pointer',
                fontSize: '1rem'
              }}
            >
              {isLogin ? 'Sign In' : 'Sign Up'}
            </m.button>
          </form>
          
          <p style={{ textAlign: 'center', marginTop: '32px', color: 'var(--text-secondary)', cursor: 'pointer', fontSize: '0.9rem' }} onClick={() => setIsLogin(!isLogin)}>
            {isLogin ? "Don't have an account? Sign up" : "Already have an account? Sign in"}
          </p>
        </m.div>
      </div>
    </LazyMotion>
  );
}
