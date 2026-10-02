import { fetchApi } from './client';

export type ProgressData = {
  level: number;
  totalXp: number;
  currentLevelXp: number;
  nextLevelXp: number;
  coinBalance: number;
};

export type StreakData = {
  overallStreak: number;
  longestOverallStreak: number;
  perTaskStreaks: Record<number, number>;
};

export type PageResponse<T> = {
  content: T[];
  totalPages: number;
  totalElements: number;
  last: boolean;
  number: number;
};

export const progressApi = {
  getProgress: (): Promise<ProgressData> => fetchApi('/progress'),
  getStreaks: (): Promise<StreakData> => fetchApi('/streaks'),
  getXpLedger: (page = 0, size = 20): Promise<PageResponse<any>> => fetchApi(`/transactions/xp?page=${page}&size=${size}`),
  getCoinLedger: (page = 0, size = 20): Promise<PageResponse<any>> => fetchApi(`/transactions/coins?page=${page}&size=${size}`)
};
