import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { fetchUsage } from '../../api/admin';
import { Table } from '../../components/ui/Table';
import { Card } from '../../components/ui/Card';
import { EmptyState } from '../../components/ui/EmptyState';
import type { UsageRecord } from '../../types/api';
import type { Column } from '../../components/ui/Table';

function monthsAgo(n: number): string {
  const d = new Date();
  d.setMonth(d.getMonth() - n);
  d.setDate(1);
  return d.toISOString().slice(0, 10);
}

export const UsagePage: React.FC = () => {
  const { t } = useTranslation();
  const [from] = useState(() => monthsAgo(12));
  const [to] = useState(() => new Date().toISOString().slice(0, 10));

  const { data, isLoading, isError } = useQuery({
    queryKey: ['usage', from, to],
    queryFn: () => fetchUsage({ from, to }),
  });

  const totalRuns = data?.reduce((s, r) => s + r.runCount, 0) ?? 0;
  const totalStorage = data?.reduce((s, r) => s + (r.storageMb ?? 0), 0) ?? 0;

  const columns: Column<UsageRecord>[] = [
    { key: 'userId', header: t('admin.usage.userId'), render: (r) => <span style={{ fontFamily: 'IBM Plex Mono, monospace', fontSize: 12 }}>{r.userId}</span> },
    { key: 'period', header: t('admin.usage.period') },
    { key: 'runCount', header: t('admin.usage.runCount') },
    { key: 'storageMb', header: t('admin.usage.storageMb'), render: (r) => r.storageMb != null ? `${r.storageMb} MB` : '—' },
  ];

  return (
    <div>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 20 }}>{t('admin.usage.title')}</h1>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 16, marginBottom: 20 }}>
        <Card>
          <div style={{ fontSize: 13, color: 'var(--pv-text-secondary)', marginBottom: 4 }}>{t('admin.usage.runCount')} (12 meses)</div>
          <div style={{ fontSize: 36, fontWeight: 700, color: 'var(--pv-accent)' }}>{totalRuns}</div>
        </Card>
        <Card>
          <div style={{ fontSize: 13, color: 'var(--pv-text-secondary)', marginBottom: 4 }}>{t('admin.usage.storageMb')} (12 meses)</div>
          <div style={{ fontSize: 36, fontWeight: 700, color: 'var(--pv-accent)' }}>{totalStorage.toFixed(1)} MB</div>
        </Card>
      </div>

      <Card padding={0}>
        {isError && <div style={{ padding: 24, color: 'var(--pv-error)' }}>{t('common.error')}</div>}
        {!isLoading && (data?.length ?? 0) === 0 ? (
          <EmptyState title={t('admin.usage.empty')} description="" />
        ) : (
          <Table
            columns={columns}
            data={data ?? []}
            keyExtractor={(r) => r.id}
            page={0}
            totalPages={1}
            onPageChange={() => {}}
            loading={isLoading}
          />
        )}
      </Card>
    </div>
  );
};
