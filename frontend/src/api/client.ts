const BASE_URL = 'http://localhost:8080/api';

export const getAuthToken = () => localStorage.getItem('token');
export const setAuthToken = (token: string) => localStorage.setItem('token', token);
export const removeAuthToken = () => localStorage.removeItem('token');

export async function fetchApi(endpoint: string, options: RequestInit = {}) {
  const token = getAuthToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...options.headers,
  };

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    let errorMsg = 'An error occurred';
    try {
      const text = await response.text();
      try {
        const errorData = JSON.parse(text);
        errorMsg = errorData.message || errorMsg;
      } catch (e) {
        errorMsg = text || errorMsg;
      }
    } catch (e) {
      console.error(e);
    }
    throw new Error(errorMsg);
  }

  if (response.status === 204) return null;
  return response.json();
}
