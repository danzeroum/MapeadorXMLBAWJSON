import client from './client';
import type { AuthResponse } from '../types/api';

export async function login(email: string, password: string): Promise<AuthResponse> {
  const res = await client.post<AuthResponse>('/api/v1/auth/login', { email, password });
  return res.data;
}

export async function refreshToken(token: string): Promise<AuthResponse> {
  const res = await client.post<AuthResponse>('/api/v1/auth/refresh', { refreshToken: token });
  return res.data;
}
