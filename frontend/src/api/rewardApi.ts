import { fetchApi } from './client';

export type Reward = {
  id: number;
  name: string;
  description: string;
  coinCost: number;
  active: boolean;
};

export type Redemption = {
  id: number;
  rewardId: number;
  coinCost: number;
  redeemedAt: string;
};

export const rewardApi = {
  getRewards: (): Promise<Reward[]> => fetchApi('/rewards'),
  
  createReward: (data: { name: string; description: string; coinCost: number }): Promise<Reward> => 
    fetchApi('/rewards', {
      method: 'POST',
      body: JSON.stringify(data)
    }),
    
  redeemReward: (id: number): Promise<Redemption> => 
    fetchApi(`/rewards/${id}/redeem`, {
      method: 'POST'
    }),
    
  getRedemptions: (): Promise<Redemption[]> => fetchApi('/rewards/redemptions'),
  
  deleteReward: (id: number): Promise<void> => fetchApi(`/rewards/${id}`, {
    method: 'DELETE'
  })
};
