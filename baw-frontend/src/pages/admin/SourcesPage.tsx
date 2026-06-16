import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { fetchSources, createSource, deactivateSource } from '../../api/admin';
import { Table } from '../../components/ui/Table';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Modal } from '../../components/ui/Modal';
import { EmptyState } from '../../components/ui/EmptyState';
import type { Source } from '../../types/api';
import type { Column } from '../../components/ui/Table';

export const SourcesPage: React.FC = () => {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [page, setPage] = useState(0);
  const [showCreate, setShowCreate] = useState(false);
  const [form, setForm] = useState({ name: '', type: 'TWX_FILE', host: '', credentials: '' });

  const { data, isLoading, isError } = useQuery({
    queryKey: ['sources', page],
    queryFn: () => fetchSources(page, 20),
  });

  const createMutation = useMutation({
    mutationFn: () => createSource({
      name: form.name,
      type: form.type,
      host: form.host || undefined,
      credentials: form.credentials || undefined,
    }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['sources'] });
      setShowCreate(false);
      setForm({ name: '', type: 'TWX_FILE', host: '', credentials: '' });
    },
  });

  const deactivateMutation = useMutation({
    mutationFn: (id: string) => deactivateSource(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['sources'] }),
  });

  const columns: Column<Source>[] = [
    { key: 'name', header: t('admin.sources.name') },
    { key: 'type', header: t('admin.sources.type'), render: (r) => <Badge variant="default">{r.type}</Badge> },
    { key: 'host', header: t('admin.sources.host'), render: (r) => r.host || '—' },
    {
      key: 'status',
      header: t('common.status'),
      render: (r) => (
        <span style={{ color: r.status === 'ACTIVE' ? 'var(--pv-success)' : 'var(--pv-text-secondary)' }}>
          {r.status === 'ACTIVE' ? t('common.active') : t('common.inactive')}
        </span>
      ),
    },
    {
      key: 'createdAt',
      header: t('common.createdAt'),
      render: (r) => new Date(r.createdAt).toLocaleDateString(),
    },
    {
      key: 'actions',
      header: t('common.actions'),
      render: (r) =>
        r.status === 'ACTIVE' ? (
          <Button size="sm" variant="danger" onClick={() => deactivateMutation.mutate(r.id)}>
            {t('admin.sources.deactivate')}
          </Button>
        ) : null,
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <h1 style={{ fontSize: 24, fontWeight: 700 }}>{t('admin.sources.title')}</h1>
        <Button onClick={() => setShowCreate(true)}>{t('admin.sources.createSource')}</Button>
      </div>

      <Card padding={0}>
        {isError && <div style={{ padding: 24, color: 'var(--pv-error)' }}>{t('common.error')}</div>}
        {!isLoading && data?.content.length === 0 ? (
          <EmptyState title={t('admin.sources.empty')} description="" />
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

      <Modal open={showCreate} onClose={() => setShowCreate(false)} title={t('admin.sources.createSource')}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <input style={inputStyle} placeholder={t('admin.sources.name')} value={form.name}
            onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))} />
          <select style={inputStyle} value={form.type}
            onChange={(e) => setForm((f) => ({ ...f, type: e.target.value }))}>
            <option value="TWX_FILE">TWX_FILE</option>
            <option value="BAW_SERVER">BAW_SERVER</option>
          </select>
          {form.type === 'BAW_SERVER' && (
            <input style={inputStyle} placeholder={t('admin.sources.host')} value={form.host}
              onChange={(e) => setForm((f) => ({ ...f, host: e.target.value }))} />
          )}
          <textarea style={{ ...inputStyle, height: 80, resize: 'vertical', fontFamily: 'IBM Plex Mono, monospace' }}
            placeholder={t('admin.sources.credentials')} value={form.credentials}
            onChange={(e) => setForm((f) => ({ ...f, credentials: e.target.value }))} />
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button variant="secondary" onClick={() => setShowCreate(false)}>{t('common.cancel')}</Button>
            <Button onClick={() => createMutation.mutate()} disabled={createMutation.isPending}>
              {createMutation.isPending ? t('common.loading') : t('common.create')}
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

const inputStyle: React.CSSProperties = {
  padding: '8px 12px',
  border: '1px solid var(--pv-border)',
  borderRadius: 8,
  fontSize: 14,
  fontFamily: 'Public Sans, sans-serif',
  color: 'var(--pv-text)',
  width: '100%',
  boxSizing: 'border-box',
};
