import { useState, useEffect } from 'react';
import { taskApi, type TaskInstance } from '../api/taskApi';
import { progressApi, type StreakData } from '../api/progressApi';
import { LazyMotion, domAnimation, m, AnimatePresence } from 'framer-motion';

export default function HistoryPage() {
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [tasks, setTasks] = useState<TaskInstance[]>([]);
  const [streaks, setStreaks] = useState<StreakData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      try {
        const [historyData, streakData] = await Promise.all([
          taskApi.getHistory(date),
          progressApi.getStreaks()
        ]);
        setTasks(historyData);
        setStreaks(streakData);
      } catch (err: any) {
        setError(err.message || 'Failed to load history');
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, [date]);

  const earnedXp = tasks.filter(t => t.status === 'COMPLETED').reduce((acc, curr) => acc + curr.xpReward, 0);
  const earnedCoins = tasks.filter(t => t.status === 'COMPLETED').reduce((acc, curr) => acc + curr.coinReward, 0);

  return (
    <LazyMotion features={domAnimation}>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(12, 1fr)', gap: '24px' }}>
        
        {/* SIDEBAR: STREAKS & DATE PICKER */}
        <div style={{ gridColumn: 'span 4', display: 'flex', flexDirection: 'column', gap: '24px' }}>
          
          {/* OVERALL STREAK */}
          <m.div 
            initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }}
            className="apple-glass" 
            style={{ padding: '32px', textAlign: 'center' }}
          >
            <div style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', fontWeight: 600, letterSpacing: '0.1em', marginBottom: '8px' }}>
              CURRENT STREAK
            </div>
            <div className="vibrant-text" style={{ fontSize: '4rem', fontWeight: 700, lineHeight: 1 }}>
              {streaks?.overallStreak || 0}
            </div>
            <div style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '8px' }}>
              Days in a row
            </div>
          </m.div>

          {/* DATE PICKER */}
          <m.div 
            initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.1 }}
            className="apple-glass" 
            style={{ padding: '32px' }}
          >
            <div style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', fontWeight: 600, letterSpacing: '0.1em', marginBottom: '16px' }}>
              SELECT DATE
            </div>
            <input 
              type="date" 
              value={date} 
              onChange={e => setDate(e.target.value)}
              max={new Date().toISOString().split('T')[0]}
              style={{ 
                width: '100%', padding: '16px', borderRadius: '12px', 
                border: '1px solid rgba(255,255,255,0.1)', background: 'rgba(0,0,0,0.3)', 
                color: 'white', outline: 'none', fontSize: '1.1rem',
                colorScheme: 'dark'
              }}
            />
          </m.div>

        </div>

        {/* MAIN: TASKS COMPLETED THAT DAY */}
        <m.div 
          initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}
          className="apple-glass" 
          style={{ gridColumn: 'span 8', padding: '32px', display: 'flex', flexDirection: 'column' }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
            <h2 className="vibrant-text" style={{ fontSize: '2rem', fontWeight: 600 }}>
              {date === new Date().toISOString().split('T')[0] ? "Today's Log" : "Time Capsule"}
            </h2>
            <div style={{ display: 'flex', gap: '16px' }}>
              <span style={{ color: 'var(--accent-purple)', fontWeight: 600 }}>+{earnedXp} XP</span>
              <span style={{ color: '#FCD34D', fontWeight: 600 }}>+{earnedCoins} COINS</span>
            </div>
          </div>

          {error && <div style={{ color: '#FCA5A5', marginBottom: '16px' }}>{error}</div>}

          {loading ? (
            <div style={{ color: 'var(--text-secondary)' }}>Loading time capsule...</div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              {tasks.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '60px 0', color: 'var(--text-secondary)' }}>
                  <span style={{ fontSize: '3rem', display: 'block', marginBottom: '16px', opacity: 0.5 }}>🕰️</span>
                  No records found for this date.
                </div>
              ) : (
                <AnimatePresence>
                  {tasks.map((task, i) => (
                    <m.div 
                      key={task.id}
                      initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.05 }}
                      style={{
                        background: 'rgba(255,255,255,0.02)',
                        border: '1px solid rgba(255,255,255,0.05)',
                        borderRadius: '16px',
                        padding: '20px',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '16px'
                      }}
                    >
                      {/* STATUS ICON */}
                      <div style={{
                        width: '40px', height: '40px', borderRadius: '50%',
                        display: 'flex', justifyContent: 'center', alignItems: 'center', fontSize: '1.2rem',
                        background: task.status === 'COMPLETED' ? 'rgba(16, 185, 129, 0.1)' : 
                                  task.status === 'CANCELLED' ? 'rgba(239, 68, 68, 0.1)' : 'rgba(255,255,255,0.05)',
                        color: task.status === 'COMPLETED' ? '#10B981' : 
                               task.status === 'CANCELLED' ? '#EF4444' : 'var(--text-secondary)'
                      }}>
                        {task.status === 'COMPLETED' ? '✓' : task.status === 'CANCELLED' ? '✕' : '—'}
                      </div>

                      {/* TASK INFO */}
                      <div style={{ flex: 1 }}>
                        <div style={{ fontSize: '1.1rem', fontWeight: 600, color: 'white', opacity: task.status === 'COMPLETED' ? 1 : 0.6 }}>
                          {task.title}
                        </div>
                        <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '4px' }}>
                          {task.status}
                        </div>
                      </div>

                      {/* REWARDS */}
                      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', opacity: task.status === 'COMPLETED' ? 1 : 0.3 }}>
                        <span style={{ color: 'var(--accent-purple)', fontWeight: 600, fontSize: '0.9rem' }}>+{task.xpReward} XP</span>
                        <span style={{ color: '#FCD34D', fontWeight: 600, fontSize: '0.9rem' }}>+{task.coinReward} COINS</span>
                      </div>
                    </m.div>
                  ))}
                </AnimatePresence>
              )}
            </div>
          )}
        </m.div>

      </div>
    </LazyMotion>
  );
}
