import { useState } from 'react';
import { taskApi } from '../api/taskApi';
import { m } from 'framer-motion';

interface TaskCreateModalProps {
  onClose: () => void;
  onCreated: () => void;
}

export default function TaskCreateModal({ onClose, onCreated }: TaskCreateModalProps) {
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('General');
  const [priority, setPriority] = useState<'LOW' | 'MEDIUM' | 'HIGH'>('MEDIUM');
  const [taskType, setTaskType] = useState<'ONE_TIME' | 'RECURRING'>('ONE_TIME');
  const [scheduledDate, setScheduledDate] = useState(new Date().toISOString().split('T')[0]);
  const [recurrenceRule, setRecurrenceRule] = useState<'DAILY' | 'WEEKLY'>('DAILY');
  const [xpReward, setXpReward] = useState(10);
  const [coinReward, setCoinReward] = useState(5);
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      await taskApi.createTask({
        title,
        description: '',
        category,
        priority,
        taskType,
        recurrenceRule: taskType === 'RECURRING' ? recurrenceRule : undefined,
        scheduledDate: taskType === 'ONE_TIME' ? scheduledDate : undefined,
        xpReward,
        coinReward
      });
      onCreated();
    } catch (err: any) {
      setError(err.message || 'Failed to create task');
      setLoading(false);
    }
  };

  const inputStyle = {
    width: '100%', padding: '12px 16px', borderRadius: '12px', 
    border: '1px solid rgba(255,255,255,0.1)', background: 'rgba(0,0,0,0.3)', 
    color: 'white', outline: 'none', fontSize: '0.95rem' 
  };
  
  const labelStyle = { display: 'block', marginBottom: '8px', color: 'var(--text-secondary)', fontSize: '0.85rem', fontWeight: 600, letterSpacing: '0.05em' };

  return (
    <div className="modal-overlay">
      <m.div 
        initial={{ opacity: 0, scale: 0.95, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 20 }}
        className="apple-glass responsive-modal" 
        style={{ width: '100%', maxWidth: '500px', maxHeight: '90vh', overflowY: 'auto' }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
          <h2 className="vibrant-text" style={{ fontSize: '1.8rem', fontWeight: 600 }}>Create Task</h2>
          <button onClick={onClose} style={{ background: 'transparent', border: 'none', cursor: 'pointer', fontSize: '2rem', color: 'var(--text-secondary)', lineHeight: 1 }}>×</button>
        </div>

        {error && (
          <div style={{ background: 'rgba(239, 68, 68, 0.2)', color: '#FCA5A5', padding: '12px', border: '1px solid rgba(239, 68, 68, 0.3)', borderRadius: '12px', marginBottom: '24px', textAlign: 'center', fontSize: '0.9rem' }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div>
            <label style={labelStyle}>TITLE</label>
            <input type="text" style={inputStyle} value={title} onChange={e => setTitle(e.target.value)} required placeholder="e.g. Meditate for 10 minutes" />
          </div>
          
          <div style={{ display: 'flex', gap: '16px' }}>
            <div style={{ flex: 1 }}>
              <label style={labelStyle}>CATEGORY</label>
              <input type="text" style={inputStyle} value={category} onChange={e => setCategory(e.target.value)} required />
            </div>
            <div style={{ flex: 1 }}>
              <label style={labelStyle}>PRIORITY</label>
              <select style={inputStyle} value={priority} onChange={e => setPriority(e.target.value as any)}>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '16px' }}>
            <div style={{ flex: 1 }}>
              <label style={labelStyle}>TYPE</label>
              <select style={inputStyle} value={taskType} onChange={e => setTaskType(e.target.value as any)}>
                <option value="ONE_TIME">One Time</option>
                <option value="RECURRING">Recurring</option>
              </select>
            </div>
            
            {taskType === 'ONE_TIME' ? (
              <div style={{ flex: 1 }}>
                <label style={labelStyle}>DATE</label>
                <input type="date" style={inputStyle} value={scheduledDate} onChange={e => setScheduledDate(e.target.value)} required />
              </div>
            ) : (
              <div style={{ flex: 1 }}>
                <label style={labelStyle}>RECURRENCE</label>
                <select style={inputStyle} value={recurrenceRule} onChange={e => setRecurrenceRule(e.target.value as any)}>
                  <option value="DAILY">Daily</option>
                  <option value="WEEKLY">Weekly</option>
                </select>
              </div>
            )}
          </div>

          <div style={{ display: 'flex', gap: '16px', background: 'rgba(0,0,0,0.2)', padding: '20px', borderRadius: '16px', border: '1px solid rgba(255,255,255,0.05)', marginTop: '8px' }}>
            <div style={{ flex: 1 }}>
              <label style={{ ...labelStyle, color: 'var(--accent-purple)' }}>XP REWARD</label>
              <input type="number" style={inputStyle} value={xpReward} onChange={e => setXpReward(Number(e.target.value))} min={0} required />
            </div>
            <div style={{ flex: 1 }}>
              <label style={{ ...labelStyle, color: '#FCD34D' }}>COIN REWARD</label>
              <input type="number" style={inputStyle} value={coinReward} onChange={e => setCoinReward(Number(e.target.value))} min={0} required />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '16px' }}>
            <m.button 
              type="button" 
              whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}
              className="btn-glass" onClick={onClose} disabled={loading}
              style={{ padding: '12px 24px', fontSize: '0.95rem' }}
            >
              Cancel
            </m.button>
            <m.button 
              type="submit" 
              whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}
              className="btn-glass-primary" disabled={loading}
              style={{ padding: '12px 24px', fontSize: '0.95rem' }}
            >
              {loading ? 'Saving...' : 'Create Task'}
            </m.button>
          </div>
        </form>
      </m.div>
    </div>
  );
}
