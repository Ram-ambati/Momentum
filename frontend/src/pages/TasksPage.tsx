import { useEffect, useState } from 'react';
import { taskApi, type TaskTemplate } from '../api/taskApi';
import { LazyMotion, domAnimation, m } from 'framer-motion';
import TaskCreateModal from '../components/TaskCreateModal';

export default function TasksPage() {
  const [templates, setTemplates] = useState<TaskTemplate[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showTaskModal, setShowTaskModal] = useState(false);

  const loadTemplates = async () => {
    try {
      const data = await taskApi.getTemplates();
      setTemplates(data);
    } catch (err: any) {
      setError(err.message || 'Failed to load templates');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTemplates();
  }, []);

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to deactivate this task? It will not affect past history.')) return;
    try {
      await taskApi.deleteTemplate(id);
      await loadTemplates();
    } catch (err: any) {
      alert('Failed to delete template');
    }
  };

  if (loading) {
    return <div style={{ padding: '40px', color: 'var(--text-secondary)' }}>Loading templates...</div>;
  }

  return (
    <LazyMotion features={domAnimation}>
      <m.div 
        initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ type: 'spring' }}
        className="apple-glass responsive-card" 
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
          <div>
            <h2 className="vibrant-text" style={{ fontSize: '2rem', fontWeight: 600 }}>Task Templates</h2>
            <span style={{ color: 'var(--text-secondary)' }}>{templates.length} Active</span>
          </div>
          <m.button 
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="btn-glass"
            onClick={() => setShowTaskModal(true)}
          >
            + Add Task
          </m.button>
        </div>

        {error && <div style={{ color: '#FCA5A5', marginBottom: '20px' }}>{error}</div>}

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '20px' }}>
          {templates.length === 0 ? (
            <p style={{ color: 'var(--text-secondary)' }}>You don't have any active tasks.</p>
          ) : (
            templates.map(template => (
              <m.div 
                key={template.id}
                layout
                whileHover={{ scale: 1.02 }}
                style={{
                  background: 'rgba(255,255,255,0.03)',
                  border: '1px solid rgba(255,255,255,0.08)',
                  borderRadius: '16px',
                  padding: '24px',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '12px'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <h3 style={{ fontSize: '1.2rem', fontWeight: 600, color: 'white', flex: 1, paddingRight: '12px' }}>{template.title}</h3>
                  <button 
                    onClick={() => handleDelete(template.id)}
                    style={{ background: 'transparent', border: 'none', color: '#EF4444', cursor: 'pointer', fontWeight: 600, fontSize: '0.9rem' }}
                  >
                    Delete
                  </button>
                </div>

                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                  <span style={{ background: 'rgba(255,255,255,0.1)', padding: '4px 8px', borderRadius: '4px', fontSize: '0.8rem', fontWeight: 600 }}>
                    {template.taskType === 'RECURRING' ? template.recurrenceRule : 'ONE TIME'}
                  </span>
                </div>

                <div style={{ display: 'flex', gap: '16px', marginTop: 'auto', paddingTop: '12px', borderTop: '1px solid rgba(255,255,255,0.05)' }}>
                  <span style={{ color: 'var(--accent-purple)', fontWeight: 600, fontSize: '0.9rem' }}>+{template.xpReward} XP</span>
                  <span style={{ color: '#FCD34D', fontWeight: 600, fontSize: '0.9rem' }}>+{template.coinReward} COINS</span>
                </div>
              </m.div>
            ))
          )}
        </div>
      </m.div>

      {showTaskModal && (
        <TaskCreateModal 
          onClose={() => setShowTaskModal(false)}
          onCreated={() => {
            setShowTaskModal(false);
            loadTemplates();
          }}
        />
      )}
    </LazyMotion>
  );
}
