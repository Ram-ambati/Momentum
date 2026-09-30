import { fetchApi, setAuthToken, removeAuthToken } from './client';

export const authApi = {
  login: async (username: string, password: string) => {
    const res = await fetchApi('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    });
    setAuthToken(res.token);
    return res;
  },
  
  register: async (username: string, password: string) => {
    const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    const res = await fetchApi('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, password, timezone })
    });
    setAuthToken(res.token);
    return res;
  },
  
  me: () => fetchApi('/auth/me'),
  
  logout: async () => {
    try { 
      await fetchApi('/auth/logout', { method: 'POST' }); 
    } catch(e) {
      console.error(e);
    }
    removeAuthToken();
  }
}
