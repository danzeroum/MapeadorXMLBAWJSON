import { useQuery } from '@tanstack/react-query';
import { fetchRun, fetchRuns } from '../api/runs';
import type { RunsParams } from '../api/runs';
import type { RunStatus } from '../types/api';

export function useRuns(params: RunsParams = {}) {
  return useQuery({
    queryKey: ['runs', params],
    queryFn: () => fetchRuns(params),
  });
}

export function useRun(id: string) {
  return useQuery({
    queryKey: ['run', id],
    queryFn: () => fetchRun(id),
    refetchInterval: (query) => {
      const status = query.state.data?.status as RunStatus | undefined;
      return status === 'QUEUED' || status === 'RUNNING' ? 2000 : false;
    },
  });
}
