import { useEffect, useState } from 'react';
import { taskApi, type TaskInstance } from '../api/taskApi';
import { progressApi, type ProgressData, type StreakData } from '../api/progressApi';
import { Check } from 'lucide-react';
import TaskCreateModal from '../components/TaskCreateModal';
import { m } from 'framer-motion';

export default function Dashboard() {
  const [tasks, setTasks] = useState<TaskInstance[]>([]);
  const [progress, setProgress] = useState<ProgressData | null>(null);
  const [streaks, setStreaks] = useState<StreakData | null>(null);
  const [loading, setLoading] = useState(true);
  const [showTaskModal, setShowTaskModal] = useState(false);

  const loadData = async () => {
    try {
      const [tasksData, progressData, streakData] = await Promise.all([
        taskApi.getTodayTasks(),
        progressApi.getProgress(),
        progressApi.getStreaks()
      ]);
      setTasks(tasksData);
      setProgress(progressData);
      setStreaks(streakData);
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
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(12, 1fr)', gap: '24px' }}>
        
        {/* XP & LEVEL WIDGET */}
        <m.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ type: 'spring', stiffness: 300, damping: 30 }}
          className="apple-glass" 
          style={{ gridColumn: 'span 8', padding: '32px' }}
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
          className="apple-glass" 
          style={{ gridColumn: 'span 4', padding: '32px', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}
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
          className="apple-glass" 
          style={{ gridColumn: 'span 12', padding: '32px' }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
            <h2 className="vibrant-text" style={{ fontSize: '1.8rem', fontWeight: 600 }}>Today's Tasks</h2>
            <m.button 
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              className="btn-glass"
              onClick={() => setShowTaskModal(true)}
            >
              + Add Task
            </m.button>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
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
                      background: isCompleted ? 'rgba(16, 185, 129, 0.1)' : 'rgba(255, 255, 255, 0.05)',
                      border: isCompleted ? '1px solid rgba(16, 185, 129, 0.3)' : '1px solid transparent',
                      borderRadius: '16px',
                      cursor: isCompleted ? 'default' : 'pointer',
                      opacity: isCompleted ? 0.6 : 1,
                      transition: 'background 0.3s, border 0.3s'
                    }}
                  >
                    <div style={{ 
                      width: '28px', height: '28px', 
                      borderRadius: '8px', 
                      border: isCompleted ? 'none' : '1px solid rgba(255,255,255,0.3)',
                      background: isCompleted ? 'var(--completed-green)' : 'transparent',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                      marginRight: '20px'
                    }}>
                      {isCompleted && <Check size={18} color="white" />}
                    </div>
                    <div style={{ flex: 1 }}>
                      <div style={{ fontSize: '1.1rem', fontWeight: 500, textDecoration: isCompleted ? 'line-through' : 'none' }}>
                        {task.title}
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
          </div>
        </m.div>
      </div>

      {showTaskModal && (
        <TaskCreateModal 
          onClose={() => setShowTaskModal(false)}
          onCreated={() => {
            setShowTaskModal(false);
            loadData();
          }}
        />
      )}
    </>
  );
}
