import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { User } from '../types/api';

interface AuthState {
  token: string | null;
  refreshToken: string | null;
  user: User | null;
  setToken: (token: string, refreshToken?: string) => void;
  setUser: (user: User) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      refreshToken: null,
      user: null,
      setToken: (token, refreshToken) =>
        set({ token, refreshToken: refreshToken ?? null }),
      setUser: (user) => set({ user }),
      logout: () => set({ token: null, refreshToken: null, user: null }),
    }),
    { name: 'pv-auth' }
  )
);
