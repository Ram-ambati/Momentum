import { fetchApi } from './client';

export type Priority = 'LOW' | 'MEDIUM' | 'HIGH';
export type TaskType = 'ONE_TIME' | 'RECURRING';
export type RecurrenceRule = 'NONE' | 'DAILY' | 'WEEKLY';
export type TaskStatus = 'PENDING' | 'COMPLETED' | 'MISSED' | 'CANCELLED';

export type TaskTemplate = {
  id: number;
  title: string;
  taskType: TaskType;
  recurrenceRule: RecurrenceRule;
  scheduledDate: string | null;
  xpReward: number;
  coinReward: number;
};

export type TaskInstance = {
  id: number;
  templateId: number;
  title: string;
  taskDate: string;
  status: TaskStatus;
  xpReward: number;
  coinReward: number;
};

export const taskApi = {
  getTodayTasks: (): Promise<TaskInstance[]> => fetchApi('/tasks/today'),
  
  getTemplates: (): Promise<TaskTemplate[]> => fetchApi('/tasks/templates'),
  
  getHistory: (date: string): Promise<TaskInstance[]> => fetchApi(`/tasks/history/${date}`),
  
  createTask: (data: any): Promise<TaskTemplate> => fetchApi('/tasks', {
    method: 'POST',
    body: JSON.stringify(data)
  }),
  
  completeTask: (id: number): Promise<TaskInstance> => fetchApi(`/tasks/${id}/complete`, {
    method: 'POST'
  }),
  
  updateStatus: (id: number, status: TaskStatus): Promise<TaskInstance> => fetchApi(`/tasks/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status })
  }),
  
  deleteTemplate: (id: number): Promise<void> => fetchApi(`/tasks/template/${id}`, {
    method: 'DELETE'
  }),
  
  updateTemplate: (id: number, data: any): Promise<TaskTemplate> => fetchApi(`/tasks/template/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(data)
  })
};
