import { useEffect, useState } from 'react';
import { taskApi, type TaskInstance } from '../api/taskApi';
import { progressApi, type ProgressData } from '../api/progressApi';
import { Check } from 'lucide-react';
import { m } from 'framer-motion';

export default function Dashboard() {
  const [tasks, setTasks] = useState<TaskInstance[]>([]);
  const [progress, setProgress] = useState<ProgressData | null>(null);
  const [loading, setLoading] = useState(true);

  const loadData = async () => {
    try {
      const [tasksData, progressData] = await Promise.all([
        taskApi.getTodayTasks(),
        progressApi.getProgress()
      ]);
      setTasks(tasksData);
      setProgress(progressData);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleComplete = async (id: number) => {
    setTasks(prev => prev.map(t => t.id === id ? { ...t, status: 'COMPLETED' } : t));
    try {
      await taskApi.completeTask(id);
      await loadData();
    } catch (e) {
      console.error('Failed to complete task', e);
      await loadData(); 
    }
  };

  if (loading) {
    return <div style={{ padding: '40px', color: 'var(--text-secondary)' }}>Loading environment...</div>;
  }

  const xpProgress = progress && progress.nextLevelXp > progress.currentLevelXp
    ? ((progress.totalXp - progress.currentLevelXp) / (progress.nextLevelXp - progress.currentLevelXp)) * 100 
    : 0;

  return (
    <>
      <div className="responsive-grid">
        
        {/* XP & LEVEL WIDGET */}
        <m.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ type: 'spring', stiffness: 300, damping: 30 }}
          className="apple-glass responsive-card col-span-8" 
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: '24px' }}>
            <span style={{ fontSize: '1.2rem', color: 'var(--text-secondary)', fontWeight: 500 }}>LEVEL {progress?.level}</span>
            <span className="vibrant-text" style={{ fontSize: '2.5rem', fontWeight: 600 }}>{progress?.totalXp.toLocaleString()} XP</span>
          </div>
          <div className="liquid-tube">
            <div className="liquid-fill" style={{ width: `${Math.min(xpProgress, 100)}%` }} />
          </div>
          <div style={{ marginTop: '12px', fontSize: '0.9rem', color: 'var(--text-secondary)', textAlign: 'right' }}>
            {progress?.nextLevelXp && progress?.currentLevelXp ? (progress.nextLevelXp - progress.currentLevelXp) : 0} XP to Level {progress ? progress.level + 1 : 0}
          </div>
        </m.div>

        {/* WALLET WIDGET */}
        <m.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ type: 'spring', stiffness: 300, damping: 30, delay: 0.1 }}
          className="apple-glass responsive-card col-span-4" 
          style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center' }}
        >
          <span style={{ color: 'var(--text-secondary)', fontSize: '1.1rem', fontWeight: 500 }}>BALANCE</span>
          <div className="vibrant-text" style={{ fontSize: '3rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '12px' }}>
            <span style={{ color: '#FCD34D' }}>✦</span> {progress?.coinBalance.toLocaleString()}
          </div>
        </m.div>

        {/* TASKS WIDGET */}
        <m.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ type: 'spring', stiffness: 300, damping: 30, delay: 0.2 }}
          className="apple-glass responsive-card col-span-12" 
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
            <h2 className="vibrant-text" style={{ fontSize: '1.8rem', fontWeight: 600 }}>Today's Tasks</h2>
          </div>

          <m.div layoutScroll className="custom-scroll" style={{ display: 'flex', flexDirection: 'column', gap: '12px', maxHeight: '50vh', overflowY: 'auto', paddingRight: '8px' }}>
            {tasks.length === 0 ? (
              <div style={{ color: 'var(--text-secondary)', padding: '20px', textAlign: 'center' }}>No tasks scheduled for today.</div>
            ) : (
              tasks.map(task => {
                const isCompleted = task.status === 'COMPLETED';
                return (
                  <m.div 
                    key={task.id}
                    layout
                    whileHover={!isCompleted ? { scale: 1.01 } : {}}
                    whileTap={!isCompleted ? { scale: 0.98 } : {}}
                    onClick={() => !isCompleted ? handleComplete(task.id) : undefined}
                    style={{ 
                      display: 'flex', 
                      alignItems: 'center', 
                      padding: '20px', 
                      background: isCompleted ? 'rgba(16, 185, 129, 0.2)' : 'rgba(255, 255, 255, 0.15)',
                      border: isCompleted ? '1px solid rgba(16, 185, 129, 0.6)' : '1px solid rgba(255, 255, 255, 0.5)',
                      backdropFilter: 'blur(10px)',
                      borderRadius: '16px',
                      cursor: isCompleted ? 'default' : 'pointer',
                      opacity: isCompleted ? 0.6 : 1,
                      transition: 'background 0.3s, border 0.3s'
                    }}
                  >
                    <m.div 
                      layout
                      initial={false}
                      animate={{
                          backgroundColor: isCompleted ? 'var(--completed-green)' : 'rgba(0,0,0,0)',
                          borderColor: isCompleted ? 'rgba(0,0,0,0)' : 'rgba(255,255,255,0.3)'
                      }}
                      style={{ 
                        width: '28px', height: '28px', 
                        borderRadius: '8px', 
                        borderStyle: 'solid',
                        borderWidth: '1px',
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        marginRight: '20px'
                      }}
                    >
                      {isCompleted && (
                        <m.div
                          initial={{ scale: 0, opacity: 0, rotate: -45 }}
                          animate={{ scale: 1, opacity: 1, rotate: 0 }}
                          transition={{ type: 'spring', stiffness: 400, damping: 25 }}
                        >
                          <Check size={18} color="white" />
                        </m.div>
                      )}
                    </m.div>
                    <div style={{ flex: 1, position: 'relative' }}>
                      <div style={{ fontSize: '1.1rem', fontWeight: 500, position: 'relative', display: 'inline-block', color: isCompleted ? 'var(--text-secondary)' : 'white' }}>
                        {task.title}
                        {isCompleted && (
                          <m.div
                            initial={{ width: 0 }}
                            animate={{ width: '100%' }}
                            transition={{ duration: 0.3, ease: 'easeOut' }}
                            style={{
                              position: 'absolute',
                              left: 0,
                              top: '50%',
                              height: '2px',
                              background: 'var(--text-secondary)'
                            }}
                          />
                        )}
                      </div>
                    </div>
                    <div style={{ display: 'flex', gap: '16px', fontWeight: 600, fontSize: '0.9rem' }}>
                      <span style={{ color: 'var(--accent-purple)' }}>+{task.xpReward} XP</span>
                      <span style={{ color: '#FCD34D' }}>+{task.coinReward} COINS</span>
                    </div>
                  </m.div>
                )
              })
            )}
          </m.div>
        </m.div>
      </div>
    </>
  );
}
