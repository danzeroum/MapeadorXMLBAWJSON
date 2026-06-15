import { useQuery } from '@tanstack/react-query';
import {
  fetchAiScore,
  fetchProcessGraph,
  fetchDataTypes,
  fetchLogic,
  fetchIssues,
  fetchSecurity,
  fetchIntegrity,
} from '../api/reports';

export const useAiScore = (runId: string) =>
  useQuery({ queryKey: ['report', runId, 'ai-score'], queryFn: () => fetchAiScore(runId) });

export const useProcessGraph = (runId: string) =>
  useQuery({ queryKey: ['report', runId, 'graph'], queryFn: () => fetchProcessGraph(runId) });

export const useDataTypes = (runId: string) =>
  useQuery({ queryKey: ['report', runId, 'data-types'], queryFn: () => fetchDataTypes(runId) });

export const useLogic = (runId: string) =>
  useQuery({ queryKey: ['report', runId, 'logic'], queryFn: () => fetchLogic(runId) });

export const useIssues = (runId: string) =>
  useQuery({ queryKey: ['report', runId, 'issues'], queryFn: () => fetchIssues(runId) });

export const useSecurity = (runId: string) =>
  useQuery({ queryKey: ['report', runId, 'security'], queryFn: () => fetchSecurity(runId) });

export const useIntegrity = (runId: string) =>
  useQuery({ queryKey: ['report', runId, 'integrity'], queryFn: () => fetchIntegrity(runId) });
