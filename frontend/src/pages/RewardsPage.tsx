import { useState, useEffect } from 'react';
import { rewardApi, type Reward } from '../api/rewardApi';
import { progressApi, type ProgressData } from '../api/progressApi';
import { LazyMotion, domAnimation, m } from 'framer-motion';

export default function RewardsPage() {
  const [rewards, setRewards] = useState<Reward[]>([]);
  const [progress, setProgress] = useState<ProgressData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // New reward form state
  const [showCreate, setShowCreate] = useState(false);
  const [newName, setNewName] = useState('');
  const [newDesc, setNewDesc] = useState('');
  const [newCost, setNewCost] = useState(100);

  const loadData = async () => {
    try {
      const [rewardData, progData] = await Promise.all([
        rewardApi.getRewards(),
        progressApi.getProgress()
      ]);
      setRewards(rewardData);
      setProgress(progData);
    } catch (err: any) {
      setError(err.message || 'Failed to load rewards');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleRedeem = async (reward: Reward) => {
    if (!progress || progress.coinBalance < reward.coinCost) {
      alert("You don't have enough coins for this reward!");
      return;
    }
    if (!window.confirm(`Spend ${reward.coinCost} coins to redeem ${reward.name}?`)) return;
    
    try {
      await rewardApi.redeemReward(reward.id);
      await loadData();
    } catch (err: any) {
      alert(err.message || 'Failed to redeem reward');
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await rewardApi.createReward({ name: newName, description: newDesc, coinCost: newCost });
      setShowCreate(false);
      setNewName('');
      setNewDesc('');
      setNewCost(100);
      await loadData();
    } catch (err: any) {
      alert(err.message || 'Failed to create reward');
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Delete this reward?')) return;
    try {
      await rewardApi.deleteReward(id);
      await loadData();
    } catch (err: any) {
      alert('Failed to delete reward');
    }
  };

  if (loading) return <div style={{ padding: '40px', color: 'var(--text-secondary)' }}>Loading shop...</div>;

  return (
    <LazyMotion features={domAnimation}>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(12, 1fr)', gap: '24px' }}>
        
        {/* WALLET HEADER */}
        <m.div 
          initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ type: 'spring' }}
          className="apple-glass" 
          style={{ gridColumn: 'span 12', padding: '32px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
        >
          <div>
            <span style={{ color: 'var(--text-secondary)', fontSize: '1.1rem', fontWeight: 500 }}>YOUR BALANCE</span>
            <div className="vibrant-text" style={{ fontSize: '3.5rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '12px' }}>
              <span style={{ color: '#FCD34D' }}>✦</span> {progress?.coinBalance.toLocaleString()}
            </div>
          </div>
          <m.button 
            whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}
            className="btn-glass"
            onClick={() => setShowCreate(!showCreate)}
          >
            {showCreate ? 'Cancel' : '+ New Reward'}
          </m.button>
        </m.div>

        {error && <div style={{ gridColumn: 'span 12', color: '#FCA5A5' }}>{error}</div>}

        {/* CREATE REWARD FORM */}
        {showCreate && (
          <m.div 
            initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: 'auto' }}
            className="apple-glass"
            style={{ gridColumn: 'span 12', padding: '32px' }}
          >
            <h3 className="vibrant-text" style={{ fontSize: '1.5rem', marginBottom: '20px' }}>Create Custom Reward</h3>
            <form onSubmit={handleCreate} style={{ display: 'flex', gap: '16px', alignItems: 'flex-end' }}>
              <div style={{ flex: 2 }}>
                <label style={{ display: 'block', marginBottom: '8px', color: 'var(--text-secondary)', fontSize: '0.85rem' }}>NAME</label>
                <input 
                  type="text" value={newName} onChange={e => setNewName(e.target.value)} required placeholder="e.g. Sushi Dinner"
                  style={{ width: '100%', padding: '12px', borderRadius: '12px', border: '1px solid rgba(255,255,255,0.1)', background: 'rgba(0,0,0,0.3)', color: 'white', outline: 'none' }}
                />
              </div>
              <div style={{ flex: 3 }}>
                <label style={{ display: 'block', marginBottom: '8px', color: 'var(--text-secondary)', fontSize: '0.85rem' }}>DESCRIPTION (OPTIONAL)</label>
                <input 
                  type="text" value={newDesc} onChange={e => setNewDesc(e.target.value)}
                  style={{ width: '100%', padding: '12px', borderRadius: '12px', border: '1px solid rgba(255,255,255,0.1)', background: 'rgba(0,0,0,0.3)', color: 'white', outline: 'none' }}
                />
              </div>
              <div style={{ flex: 1 }}>
                <label style={{ display: 'block', marginBottom: '8px', color: '#FCD34D', fontSize: '0.85rem', fontWeight: 600 }}>COST</label>
                <input 
                  type="number" value={newCost} onChange={e => setNewCost(Number(e.target.value))} min={1} required
                  style={{ width: '100%', padding: '12px', borderRadius: '12px', border: '1px solid rgba(255,255,255,0.1)', background: 'rgba(0,0,0,0.3)', color: 'white', outline: 'none' }}
                />
              </div>
              <m.button type="submit" whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} className="btn-glass-primary">Save</m.button>
            </form>
          </m.div>
        )}

        {/* REWARDS GRID */}
        {rewards.filter(r => r.active).map((reward, i) => {
          const canAfford = progress && progress.coinBalance >= reward.coinCost;
          return (
            <m.div 
              key={reward.id}
              layout
              initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.05 }}
              className="apple-glass"
              style={{ 
                gridColumn: 'span 4', padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px',
                opacity: canAfford ? 1 : 0.5
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <h3 style={{ fontSize: '1.2rem', fontWeight: 600 }}>{reward.name}</h3>
                <button 
                  onClick={() => handleDelete(reward.id)}
                  style={{ background: 'transparent', border: 'none', color: 'rgba(255,255,255,0.3)', cursor: 'pointer', fontSize: '0.9rem' }}
                >
                  ✕
                </button>
              </div>
              {reward.description && <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', flex: 1 }}>{reward.description}</p>}
              
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 'auto', paddingTop: '16px', borderTop: '1px solid rgba(255,255,255,0.05)' }}>
                <span style={{ color: '#FCD34D', fontWeight: 600, fontSize: '1.1rem' }}>✦ {reward.coinCost}</span>
                <m.button 
                  whileHover={canAfford ? { scale: 1.05 } : {}} whileTap={canAfford ? { scale: 0.95 } : {}}
                  onClick={() => handleRedeem(reward)}
                  disabled={!canAfford}
                  style={{
                    background: canAfford ? 'rgba(255,255,255,0.9)' : 'rgba(255,255,255,0.1)',
                    color: canAfford ? 'black' : 'rgba(255,255,255,0.3)',
                    border: 'none', padding: '8px 16px', borderRadius: '100px', fontWeight: 600, cursor: canAfford ? 'pointer' : 'not-allowed'
                  }}
                >
                  Redeem
                </m.button>
              </div>
            </m.div>
          )
        })}
      </div>
    </LazyMotion>
  );
}
