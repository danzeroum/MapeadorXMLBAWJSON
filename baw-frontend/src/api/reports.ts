import client from './client';
import type { AiScore, ProcessGraph, DataType, Issue, SecurityFinding, IntegrityReport, LogicSection } from '../types/api';

export async function fetchAiScore(runId: string): Promise<AiScore> {
  const res = await client.get<AiScore>(`/api/v1/runs/${runId}/report/ai-score`);
  return res.data;
}

export async function fetchProcessGraph(runId: string): Promise<ProcessGraph> {
  const res = await client.get<ProcessGraph>(`/api/v1/runs/${runId}/report/graph`);
  return res.data;
}

export async function fetchDataTypes(runId: string): Promise<DataType[]> {
  const res = await client.get<DataType[]>(`/api/v1/runs/${runId}/report/data-types`);
  return res.data;
}

export async function fetchLogic(runId: string): Promise<LogicSection[]> {
  const res = await client.get<LogicSection[]>(`/api/v1/runs/${runId}/report/logic`);
  return res.data;
}

export async function fetchIssues(runId: string): Promise<Issue[]> {
  const res = await client.get<Issue[]>(`/api/v1/runs/${runId}/report/issues`);
  return res.data;
}

export async function fetchSecurity(runId: string): Promise<SecurityFinding[]> {
  const res = await client.get<SecurityFinding[]>(`/api/v1/runs/${runId}/report/security`);
  return res.data;
}

export async function fetchIntegrity(runId: string): Promise<IntegrityReport> {
  const res = await client.get<IntegrityReport>(`/api/v1/runs/${runId}/report/integrity`);
  return res.data;
}
