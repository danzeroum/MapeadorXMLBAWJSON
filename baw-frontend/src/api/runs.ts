import client from './client';
import type { Run, Page } from '../types/api';

export interface RunsParams {
  page?: number;
  size?: number;
  status?: string;
}

export async function fetchRuns(params: RunsParams = {}): Promise<Page<Run>> {
  const res = await client.get<Page<Run>>('/api/v1/runs', { params });
  return res.data;
}

export async function fetchRun(id: string): Promise<Run> {
  const res = await client.get<Run>(`/api/v1/runs/${id}`);
  return res.data;
}

export async function createRun(formData: FormData): Promise<Run> {
  const res = await client.post<Run>('/api/v1/runs', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return res.data;
}
