import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { fetchAuditLog } from '../../api/admin';
import { Table } from '../../components/ui/Table';
import { Card } from '../../components/ui/Card';
import { EmptyState } from '../../components/ui/EmptyState';
import type { AuditLogEntry } from '../../types/api';
import type { Column } from '../../components/ui/Table';

export const AuditLogPage: React.FC = () => {
  const { t } = useTranslation();
  const [page, setPage] = useState(0);
  const [actorFilter, setActorFilter] = useState('');
  const [debouncedActor, setDebouncedActor] = useState('');

  const handleActorChange = (v: string) => {
    setActorFilter(v);
    clearTimeout((window as any).__auditTimeout);
    (window as any).__auditTimeout = setTimeout(() => {
      setDebouncedActor(v);
      setPage(0);
    }, 400);
  };

  const { data, isLoading, isError } = useQuery({
    queryKey: ['audit-log', page, debouncedActor],
    queryFn: () => fetchAuditLog({ page, size: 20, actor: debouncedActor || undefined }),
  });

  const columns: Column<AuditLogEntry>[] = [
    { key: 'actorEmail', header: t('admin.auditLog.actor') },
    {
      key: 'action',
      header: t('admin.auditLog.action'),
      render: (r) => (
        <code style={{ fontFamily: 'IBM Plex Mono, monospace', fontSize: 12, background: '#f1f3f4', padding: '2px 6px', borderRadius: 4 }}>
          {r.action}
        </code>
      ),
    },
    { key: 'resource', header: t('admin.auditLog.resource'), render: (r) => <span style={{ fontFamily: 'IBM Plex Mono, monospace', fontSize: 12 }}>{r.resource}</span> },
    { key: 'ipAddress', header: t('admin.auditLog.ip'), render: (r) => r.ipAddress || '—' },
    {
      key: 'occurredAt',
      header: t('admin.auditLog.occurredAt'),
      render: (r) => new Date(r.occurredAt).toLocaleString(),
    },
  ];

  return (
    <div>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 20 }}>{t('admin.auditLog.title')}</h1>

      <Card padding={0}>
        <div style={{ padding: '12px 16px', borderBottom: '1px solid var(--pv-border)' }}>
          <input
            style={inputStyle}
            placeholder={t('admin.auditLog.filterActor')}
            value={actorFilter}
            onChange={(e) => handleActorChange(e.target.value)}
          />
        </div>

        {isError && <div style={{ padding: 24, color: 'var(--pv-error)' }}>{t('common.error')}</div>}
        {!isLoading && data?.content.length === 0 ? (
          <EmptyState title={t('admin.auditLog.empty')} description="" />
        ) : (
          <Table
            columns={columns}
            data={data?.content ?? []}
            keyExtractor={(r) => String(r.id)}
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

const inputStyle: React.CSSProperties = {
  padding: '7px 12px',
  border: '1px solid var(--pv-border)',
  borderRadius: 8,
  fontSize: 13,
  fontFamily: 'Public Sans, sans-serif',
  color: 'var(--pv-text)',
  width: 280,
};
