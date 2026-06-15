import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useRuns } from '../../hooks/useRun';
import { Table } from '../../components/ui/Table';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { EmptyState } from '../../components/ui/EmptyState';
import type { Run, RunStatus } from '../../types/api';
import type { Column } from '../../components/ui/Table';

function formatDuration(ms?: number) {
  if (!ms) return '—';
  if (ms < 1000) return `${ms}ms`;
  if (ms < 60000) return `${(ms / 1000).toFixed(1)}s`;
  return `${Math.floor(ms / 60000)}m ${Math.floor((ms % 60000) / 1000)}s`;
}

function formatDate(iso?: string) {
  if (!iso) return '—';
  return new Date(iso).toLocaleString();
}

export const RunsPage: React.FC = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [statusFilter, setStatusFilter] = useState<string>('');

  const { data, isLoading, isError } = useRuns({
    page,
    size: 10,
    status: statusFilter || undefined,
  });

  const columns: Column<Run>[] = [
    {
      key: 'processName',
      header: t('runs.processName'),
    },
    {
      key: 'processId',
      header: t('runs.processId'),
    },
    {
      key: 'status',
      header: t('runs.status'),
      render: (row) => <Badge variant={row.status}>{row.status}</Badge>,
    },
    {
      key: 'startedAt',
      header: t('runs.startedAt'),
      render: (row) => formatDate(row.startedAt),
    },
    {
      key: 'durationMs',
      header: t('runs.duration'),
      render: (row) => formatDuration(row.durationMs),
    },
    {
      key: 'authorEmail',
      header: t('runs.author'),
    },
    {
      key: 'actions',
      header: t('runs.actions'),
      render: (row) => (
        <Button
          size="sm"
          variant="secondary"
          onClick={() =>
            navigate(
              row.status === 'DONE'
                ? `/runs/${row.id}/report/ai-score`
                : `/runs/${row.id}`
            )
          }
        >
          {t('runs.viewReport')}
        </Button>
      ),
    },
  ];

  const statuses: RunStatus[] = ['QUEUED', 'RUNNING', 'DONE', 'FAILED'];

  return (
    <div>
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: 20,
        }}
      >
        <h1 style={{ fontSize: 24, fontWeight: 700 }}>{t('runs.title')}</h1>
        <Button onClick={() => navigate('/runs/new')}>{t('runs.newAnalysis')}</Button>
      </div>

      <Card padding={0}>
        <div
          style={{
            padding: '14px 16px',
            borderBottom: '1px solid var(--pv-border)',
            display: 'flex',
            gap: 12,
          }}
        >
          <select
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value);
              setPage(0);
            }}
            style={selectStyle}
          >
            <option value="">{t('runs.allStatuses')}</option>
            {statuses.map((s) => (
              <option key={s} value={s}>
                {s}
              </option>
            ))}
          </select>
        </div>

        {isError && (
          <div style={{ padding: 24, color: 'var(--pv-error)' }}>{t('common.error')}</div>
        )}

        {!isLoading && !isError && data?.content.length === 0 ? (
          <EmptyState
            title={t('runs.empty')}
            description={t('runs.emptyDesc')}
            action={
              <Button onClick={() => navigate('/runs/new')}>{t('runs.newAnalysis')}</Button>
            }
          />
        ) : (
          <Table
            columns={columns}
            data={data?.content ?? []}
            keyExtractor={(r) => r.id}
            page={page}
            totalPages={data?.totalPages ?? 1}
            onPageChange={setPage}
            loading={isLoading}
          />
        )}
      </Card>
    </div>
  );
};

const selectStyle: React.CSSProperties = {
  padding: '7px 12px',
  border: '1px solid var(--pv-border)',
  borderRadius: 8,
  fontSize: 13,
  fontFamily: 'Public Sans, sans-serif',
  color: 'var(--pv-text)',
  background: 'var(--pv-surface)',
  cursor: 'pointer',
};
