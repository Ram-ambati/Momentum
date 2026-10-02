import { useState, useEffect, useRef } from 'react';
import { taskApi, type TaskInstance } from '../api/taskApi';
import { LazyMotion, domAnimation, m, AnimatePresence } from 'framer-motion';

// Helper to reliably format local dates
const pad = (n: number) => String(n).padStart(2, '0');

export default function HistoryPage() {
  const localToday = new Date();
  const todayStr = `${localToday.getFullYear()}-${pad(localToday.getMonth() + 1)}-${pad(localToday.getDate())}`;

  const [date, setDate] = useState(todayStr);
  const [currentYear, setCurrentYear] = useState(localToday.getFullYear());
  const [currentMonth, setCurrentMonth] = useState(localToday.getMonth() + 1); // 1-12
  
  const [tasks, setTasks] = useState<TaskInstance[]>([]);
  const [monthlyStats, setMonthlyStats] = useState<any>(null);
  const [monthlyHistory, setMonthlyHistory] = useState<any[]>([]);
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const dayCache = useRef<Record<string, TaskInstance[]>>({});
  const monthCache = useRef<Record<string, { stats: any, history: any[] }>>({});

  const loadDayData = async () => {
    if (dayCache.current[date]) {
      setTasks(dayCache.current[date]);
      if (date !== todayStr) return; // Only background refresh if it's today
    } else {
      setLoading(true);
    }
    
    try {
      const historyData = await taskApi.getHistory(date);
      dayCache.current[date] = historyData;
      setTasks(historyData);
    } catch (err: any) {
      setError(err.message || 'Failed to load history');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDayData();
  }, [date]);

  const loadMonthlyData = async () => {
    const monthKey = `${currentYear}-${currentMonth}`;
    if (monthCache.current[monthKey]) {
      const cached = monthCache.current[monthKey];
      setMonthlyStats(cached.stats);
      setMonthlyHistory(cached.history);
    }

    try {
      const startOfMonth = `${currentYear}-${pad(currentMonth)}-01`;
      const endOfMonthDate = new Date(currentYear, currentMonth, 0).getDate();
      const endOfMonth = `${currentYear}-${pad(currentMonth)}-${pad(endOfMonthDate)}`;
      
      const [stats, history] = await Promise.all([
        taskApi.getMonthlyStats(currentYear, currentMonth),
        taskApi.getMonthlyHistory(startOfMonth, endOfMonth)
      ]);
      
      monthCache.current[monthKey] = { stats, history };
      setMonthlyStats(stats);
      setMonthlyHistory(history);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadMonthlyData();
  }, [currentYear, currentMonth]);

  const handleComplete = async (id: number) => {
    // Optimistic UI & Cache update
    setTasks(prev => prev.map(t => t.id === id ? { ...t, status: 'COMPLETED' } : t));
    if (dayCache.current[date]) {
      dayCache.current[date] = dayCache.current[date].map(t => t.id === id ? { ...t, status: 'COMPLETED' } : t);
    }

    try {
      await taskApi.completeTask(id);
      
      // Invalidate current month cache so stats refresh
      const monthKey = `${currentYear}-${currentMonth}`;
      delete monthCache.current[monthKey];
      
      await loadDayData();
      await loadMonthlyData(); // Refresh heatmap & stats!
    } catch (e) {
      console.error('Failed to complete task', e);
      delete dayCache.current[date];
      await loadDayData(); 
    }
  };

  const earnedXp = tasks.filter(t => t.status === 'COMPLETED').reduce((acc, curr) => acc + curr.xpReward, 0);
  const earnedCoins = tasks.filter(t => t.status === 'COMPLETED').reduce((acc, curr) => acc + curr.coinReward, 0);

  // Calendar logic
  const daysInMonth = new Date(currentYear, currentMonth, 0).getDate();
  const firstDayOfWeek = new Date(currentYear, currentMonth - 1, 1).getDay(); // 0 is Sunday
  
  const calendarDays = [];
  for(let i = 0; i < firstDayOfWeek; i++) {
      calendarDays.push(null);
  }
  for(let i = 1; i <= daysInMonth; i++) {
      calendarDays.push(i);
  }

  const getDayColor = (dayNum: number) => {
      const dayStr = `${currentYear}-${pad(currentMonth)}-${pad(dayNum)}`;
      
      if (dayStr > todayStr) {
          return 'rgba(255,255,255,0.05)'; // Future dates neutral
      }
      if (dayStr === todayStr) {
          return 'rgba(59, 130, 246, 0.5)'; // Today in blue
      }

      const dayData = monthlyHistory.find(d => d.date === dayStr);
      if(!dayData) return 'rgba(255,255,255,0.05)';
      
      switch(dayData.productivityLabel) {
          case 'Excellent': return 'rgba(16, 185, 129, 0.5)';
          case 'Good': return 'rgba(16, 185, 129, 0.3)';
          case 'Average': return 'rgba(252, 211, 77, 0.3)';
          case 'Low': return 'rgba(239, 68, 68, 0.3)';
          default: return 'rgba(255,255,255,0.05)';
      }
  };

  const changeMonth = (delta: number) => {
      let newMonth = currentMonth + delta;
      let newYear = currentYear;
      if (newMonth > 12) { newMonth = 1; newYear++; }
      if (newMonth < 1) { newMonth = 12; newYear--; }
      setCurrentMonth(newMonth);
      setCurrentYear(newYear);
  };

  return (
    <LazyMotion features={domAnimation}>
      <div className="responsive-grid" style={{ alignItems: 'stretch' }}>
        
        {/* SIDEBAR: CALENDAR */}
        <div className="col-span-5 mobile-order-2">
          
          <m.div 
            initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }}
            className="apple-glass responsive-card" 
            style={{ overflow: 'hidden' }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
                <button onClick={() => changeMonth(-1)} className="btn-glass" style={{ padding: '8px 16px', borderRadius: '12px' }}>&larr;</button>
                <div className="vibrant-text" style={{ fontSize: '1.2rem', fontWeight: 600 }}>
                    {new Date(currentYear, currentMonth - 1).toLocaleString('default', { month: 'long', year: 'numeric' })}
                </div>
                <button onClick={() => changeMonth(1)} className="btn-glass" style={{ padding: '8px 16px', borderRadius: '12px' }}>&rarr;</button>
            </div>
            
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: '8px', textAlign: 'center', marginBottom: '8px', fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                <div>Sun</div><div>Mon</div><div>Tue</div><div>Wed</div><div>Thu</div><div>Fri</div><div>Sat</div>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: '8px' }}>
                {calendarDays.map((dayNum, idx) => {
                    if (dayNum === null) return <div key={`empty-${idx}`} />;
                    const dayStr = `${currentYear}-${pad(currentMonth)}-${pad(dayNum)}`;
                    const isSelected = date === dayStr;
                    return (
                        <button
                            key={`day-${dayNum}`}
                            onClick={() => setDate(dayStr)}
                            style={{
                                aspectRatio: '1',
                                borderRadius: '8px',
                                border: isSelected ? '2px solid white' : '1px solid rgba(255,255,255,0.1)',
                                background: getDayColor(dayNum),
                                color: 'white',
                                cursor: 'pointer',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                fontWeight: isSelected ? 'bold' : 'normal',
                                transition: 'transform 0.1s',
                                transform: isSelected ? 'scale(1.1)' : 'scale(1)'
                            }}
                        >
                            {dayNum}
                        </button>
                    )
                })}
            </div>

            {/* MONTHLY STATS SUMMARY (Always render to prevent layout jump) */}
            <div style={{ marginTop: '24px', paddingTop: '24px', borderTop: '1px solid rgba(255,255,255,0.1)', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>COMPLETION</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>{monthlyStats ? `${monthlyStats.completionPct}%` : '--%'}</div>
                </div>
                <div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>TASKS DONE</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>{monthlyStats ? `${monthlyStats.completedTasks}/${monthlyStats.totalTasks}` : '--/--'}</div>
                </div>
                <div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>XP EARNED</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'var(--accent-purple)' }}>{monthlyStats ? monthlyStats.xpEarned : '--'}</div>
                </div>
                <div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>COINS EARNED</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#FCD34D' }}>{monthlyStats ? monthlyStats.coinsEarned : '--'}</div>
                </div>
            </div>
          </m.div>

        </div>

        {/* MAIN: TASKS COMPLETED THAT DAY */}
        <div className="col-span-7 mobile-order-1" style={{ minHeight: '600px', position: 'relative' }}>
          <m.div 
            initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}
            className="apple-glass responsive-card" 
            style={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, display: 'flex', flexDirection: 'column' }}
          >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
            <h2 className="vibrant-text" style={{ fontSize: '2rem', fontWeight: 600 }}>
              {date === todayStr ? "Today's Log" : "Time Capsule"}
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
            <div className="custom-scroll" style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: '16px', overflowY: 'auto', paddingRight: '8px', minHeight: 0 }}>
              {tasks.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '60px 0', color: 'var(--text-secondary)' }}>
                  <span style={{ fontSize: '3rem', display: 'block', marginBottom: '16px', opacity: 0.5 }}>🕰️</span>
                  No records found for this date.
                </div>
              ) : (
                <AnimatePresence>
                  {tasks.map((task, i) => {
                    const isCompleted = task.status === 'COMPLETED';
                    const isCompletable = !isCompleted && date === todayStr;
                    return (
                      <m.div 
                        key={task.id}
                        initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.05 }}
                        onClick={() => isCompletable ? handleComplete(task.id) : undefined}
                        style={{
                          background: isCompleted ? 'rgba(16, 185, 129, 0.15)' : 'rgba(255,255,255,0.05)',
                          border: isCompleted ? '1px solid rgba(16, 185, 129, 0.3)' : '1px solid rgba(255,255,255,0.1)',
                          borderRadius: '16px',
                          padding: '20px',
                          display: 'flex',
                          alignItems: 'center',
                          gap: '16px',
                          cursor: isCompletable ? 'pointer' : 'default',
                          opacity: isCompleted ? 0.8 : 1,
                          transition: 'background 0.3s, border 0.3s'
                        }}
                      >
                        {/* STATUS ICON */}
                        <div style={{
                          width: '40px', height: '40px', borderRadius: '50%',
                          display: 'flex', justifyContent: 'center', alignItems: 'center', fontSize: '1.2rem',
                          background: isCompleted ? 'rgba(16, 185, 129, 0.2)' : 
                                    task.status === 'CANCELLED' ? 'rgba(239, 68, 68, 0.1)' : 'rgba(255,255,255,0.05)',
                          color: isCompleted ? '#10B981' : 
                                task.status === 'CANCELLED' ? '#EF4444' : 'var(--text-secondary)',
                          transition: 'background 0.3s'
                        }}>
                          {isCompleted ? '✓' : task.status === 'CANCELLED' ? '✕' : '—'}
                        </div>

                        {/* TASK INFO */}
                        <div style={{ flex: 1, position: 'relative' }}>
                          <div style={{ fontSize: '1.1rem', fontWeight: 600, color: 'white', position: 'relative', display: 'inline-block' }}>
                            {task.title}
                            {isCompleted && (
                              <m.div
                                initial={{ scaleX: 0 }}
                                animate={{ scaleX: 1 }}
                                transition={{ duration: 0.3, ease: 'easeOut' }}
                                style={{
                                  position: 'absolute',
                                  left: 0,
                                  top: '50%',
                                  height: '2px',
                                  width: '100%',
                                  transformOrigin: 'left',
                                  background: 'var(--text-secondary)'
                                }}
                              />
                            )}
                          </div>
                          <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '4px' }}>
                            {task.status}
                          </div>
                        </div>

                        {/* REWARDS */}
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', opacity: isCompleted ? 1 : 0.5 }}>
                          <span style={{ color: 'var(--accent-purple)', fontWeight: 600, fontSize: '0.9rem' }}>+{task.xpReward} XP</span>
                          <span style={{ color: '#FCD34D', fontWeight: 600, fontSize: '0.9rem' }}>+{task.coinReward} COINS</span>
                        </div>
                      </m.div>
                    )
                  })}
                </AnimatePresence>
              )}
            </div>
          )}
        </m.div>
        </div>

      </div>
    </LazyMotion>
  );
}
