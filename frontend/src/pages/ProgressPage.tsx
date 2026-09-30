import { useEffect, useState } from 'react';
import { progressApi, type ProgressData } from '../api/progressApi';
import { LazyMotion, domAnimation, m } from 'framer-motion';

export default function ProgressPage() {
  const [progress, setProgress] = useState<ProgressData | null>(null);
  const [xpLedger, setXpLedger] = useState<any[]>([]);
  const [coinLedger, setCoinLedger] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState<'XP' | 'COINS'>('XP');

  useEffect(() => {
    const loadData = async () => {
      try {
        const [prog, xp, coins] = await Promise.all([
          progressApi.getProgress(),
          progressApi.getXpLedger(),
          progressApi.getCoinLedger()
        ]);
        setProgress(prog);
        setXpLedger(xp);
        setCoinLedger(coins);
      } catch (err: any) {
        setError(err.message || 'Failed to load progress data');
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  if (loading) return <div style={{ padding: '40px', color: 'var(--text-secondary)' }}>Loading analytics...</div>;

  const currentLedger = activeTab === 'XP' ? xpLedger : coinLedger;

  return (
    <LazyMotion features={domAnimation}>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(12, 1fr)', gap: '24px' }}>
        
        {/* STATS OVERVIEW */}
        <m.div 
          initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ type: 'spring' }}
          className="apple-glass" 
          style={{ gridColumn: 'span 12', padding: '32px', display: 'flex', justifyContent: 'space-around', alignItems: 'center', textAlign: 'center' }}
        >
          <div>
            <div style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', fontWeight: 600, letterSpacing: '0.1em' }}>CURRENT LEVEL</div>
            <div className="vibrant-text" style={{ fontSize: '3rem', fontWeight: 700 }}>{progress?.level}</div>
          </div>
          <div style={{ width: '1px', height: '60px', background: 'rgba(255,255,255,0.1)' }}></div>
          <div>
            <div style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', fontWeight: 600, letterSpacing: '0.1em' }}>TOTAL XP</div>
            <div style={{ fontSize: '3rem', fontWeight: 700, color: 'var(--accent-purple)' }}>{progress?.totalXp.toLocaleString()}</div>
          </div>
          <div style={{ width: '1px', height: '60px', background: 'rgba(255,255,255,0.1)' }}></div>
          <div>
            <div style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', fontWeight: 600, letterSpacing: '0.1em' }}>COIN BALANCE</div>
            <div style={{ fontSize: '3rem', fontWeight: 700, color: '#FCD34D' }}>{progress?.coinBalance.toLocaleString()}</div>
          </div>
        </m.div>

        {/* LEDGER SECTION */}
        <m.div 
          initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ type: 'spring', delay: 0.1 }}
          className="apple-glass" 
          style={{ gridColumn: 'span 12', padding: '32px' }}
        >
          <div style={{ display: 'flex', gap: '16px', marginBottom: '24px', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '16px' }}>
            <button 
              onClick={() => setActiveTab('XP')}
              style={{ background: 'transparent', border: 'none', color: activeTab === 'XP' ? 'white' : 'var(--text-secondary)', fontSize: '1.2rem', fontWeight: 600, cursor: 'pointer' }}
            >
              XP History
            </button>
            <button 
              onClick={() => setActiveTab('COINS')}
              style={{ background: 'transparent', border: 'none', color: activeTab === 'COINS' ? 'white' : 'var(--text-secondary)', fontSize: '1.2rem', fontWeight: 600, cursor: 'pointer' }}
            >
              Coin Transactions
            </button>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {currentLedger.length === 0 ? (
              <p style={{ color: 'var(--text-secondary)' }}>No transactions found.</p>
            ) : (
              currentLedger.map((txn, i) => (
                <m.div 
                  key={txn.id}
                  initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.02 }}
                  style={{ 
                    display: 'flex', justifyContent: 'space-between', alignItems: 'center', 
                    padding: '16px', background: 'rgba(255,255,255,0.02)', borderRadius: '12px',
                    border: '1px solid rgba(255,255,255,0.05)'
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 600, color: 'white', marginBottom: '4px' }}>{txn.reason}</div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                      {new Date(txn.createdAt).toLocaleString()}
                    </div>
                  </div>
                  <div style={{ 
                    fontWeight: 'bold', fontSize: '1.2rem',
                    color: activeTab === 'XP' ? 'var(--accent-purple)' : (txn.amount > 0 ? '#10B981' : '#EF4444') 
                  }}>
                    {txn.amount > 0 ? '+' : ''}{txn.amount} {activeTab === 'XP' ? 'XP' : 'COINS'}
                  </div>
                </m.div>
              ))
            )}
          </div>
        </m.div>

      </div>
    </LazyMotion>
  );
}
