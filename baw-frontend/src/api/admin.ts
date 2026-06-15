import client from './client';
import type { Source, Methodology, Setting, UsageRecord, AuditLogEntry, Page } from '../types/api';

// Sources
export async function fetchSources(page = 0, size = 20): Promise<Page<Source>> {
  const res = await client.get<Page<Source>>('/api/v1/sources', { params: { page, size } });
  return res.data;
}

export async function createSource(data: { name: string; type: string; host?: string; credentials?: string }): Promise<Source> {
  const res = await client.post<Source>('/api/v1/sources', data);
  return res.data;
}

export async function updateSource(id: string, data: Partial<Source>): Promise<Source> {
  const res = await client.put<Source>(`/api/v1/sources/${id}`, data);
  return res.data;
}

export async function deactivateSource(id: string): Promise<void> {
  await client.delete(`/api/v1/sources/${id}`);
}

// Methodology
export async function fetchMethodologies(page = 0, size = 20): Promise<Page<Methodology>> {
  const res = await client.get<Page<Methodology>>('/api/v1/methodology', { params: { page, size } });
  return res.data;
}

export async function fetchActiveMethodology(): Promise<Methodology> {
  const res = await client.get<Methodology>('/api/v1/methodology/active');
  return res.data;
}

export async function createMethodology(data: { version: string; weights: string }): Promise<Methodology> {
  const res = await client.post<Methodology>('/api/v1/methodology', data);
  return res.data;
}

// Settings
export async function fetchSettings(): Promise<Setting[]> {
  const res = await client.get<Setting[]>('/api/v1/settings');
  return res.data;
}

export async function upsertSetting(key: string, value: string): Promise<Setting> {
  const res = await client.put<Setting>(`/api/v1/settings/${key}`, { value });
  return res.data;
}

// Usage
export async function fetchUsage(params?: { from?: string; to?: string; userId?: string }): Promise<UsageRecord[]> {
  const res = await client.get<UsageRecord[]>('/api/v1/usage', { params });
  return res.data;
}

export async function fetchMyUsage(): Promise<UsageRecord[]> {
  const res = await client.get<UsageRecord[]>('/api/v1/usage/me');
  return res.data;
}

// Audit Log
export async function fetchAuditLog(params?: { actor?: string; page?: number; size?: number }): Promise<Page<AuditLogEntry>> {
  const res = await client.get<Page<AuditLogEntry>>('/api/v1/audit-log', { params });
  return res.data;
}
