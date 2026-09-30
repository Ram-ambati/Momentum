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

export const progressApi = {
  getProgress: (): Promise<ProgressData> => fetchApi('/progress'),
  getStreaks: (): Promise<StreakData> => fetchApi('/streaks'),
  getXpLedger: (): Promise<any[]> => fetchApi('/transactions/xp'),
  getCoinLedger: (): Promise<any[]> => fetchApi('/transactions/coins')
};
