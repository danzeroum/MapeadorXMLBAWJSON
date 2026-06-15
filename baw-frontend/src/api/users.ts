import client from './client';
import type { User, Page } from '../types/api';

export async function fetchUsers(page = 0, size = 20): Promise<Page<User>> {
  const res = await client.get<Page<User>>('/api/v1/users', { params: { page, size } });
  return res.data;
}

export async function createUser(data: Omit<User, 'id' | 'createdAt'>): Promise<User> {
  const res = await client.post<User>('/api/v1/users', data);
  return res.data;
}

export async function updateUser(id: string, data: Partial<User>): Promise<User> {
  const res = await client.patch<User>(`/api/v1/users/${id}`, data);
  return res.data;
}

export async function deleteUser(id: string): Promise<void> {
  await client.delete(`/api/v1/users/${id}`);
}
