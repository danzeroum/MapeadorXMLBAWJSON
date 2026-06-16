import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { fetchMethodologies, createMethodology } from '../../api/admin';
import { Table } from '../../components/ui/Table';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Modal } from '../../components/ui/Modal';
import { EmptyState } from '../../components/ui/EmptyState';
import type { Methodology } from '../../types/api';
import type { Column } from '../../components/ui/Table';

export const MethodologyPage: React.FC = () => {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [page, setPage] = useState(0);
  const [showCreate, setShowCreate] = useState(false);
  const [form, setForm] = useState({
    version: '',
    weights: JSON.stringify({
      complexity: 0.3,
      standardization: 0.25,
      automationReadiness: 0.3,
      maintainability: 0.15,
    }, null, 2),
  });

  const { data, isLoading, isError } = useQuery({
    queryKey: ['methodologies', page],
    queryFn: () => fetchMethodologies(page, 20),
  });

  const createMutation = useMutation({
    mutationFn: () => createMethodology({ version: form.version, weights: form.weights }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['methodologies'] });
      setShowCreate(false);
      setForm({ version: '', weights: JSON.stringify({ complexity: 0.3, standardization: 0.25, automationReadiness: 0.3, maintainability: 0.15 }, null, 2) });
    },
  });

  const columns: Column<Methodology>[] = [
    { key: 'version', header: t('admin.methodology.version') },
    {
      key: 'active',
      header: t('admin.methodology.active'),
      render: (r) => r.active ? <Badge variant="DONE">Active</Badge> : <span style={{ color: 'var(--pv-text-secondary)' }}>—</span>,
    },
    {
      key: 'weights',
      header: t('admin.methodology.weights'),
      render: (r) => (
        <span style={{ fontFamily: 'IBM Plex Mono, monospace', fontSize: 11, color: 'var(--pv-text-secondary)' }}>
          {r.weights.substring(0, 60)}…
        </span>
      ),
    },
    {
      key: 'createdAt',
      header: t('common.createdAt'),
      render: (r) => new Date(r.createdAt).toLocaleDateString(),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <h1 style={{ fontSize: 24, fontWeight: 700 }}>{t('admin.methodology.title')}</h1>
        <Button onClick={() => setShowCreate(true)}>{t('admin.methodology.createVersion')}</Button>
      </div>

      <Card padding={0}>
        {isError && <div style={{ padding: 24, color: 'var(--pv-error)' }}>{t('common.error')}</div>}
        {!isLoading && data?.content.length === 0 ? (
          <EmptyState title={t('admin.methodology.empty')} description="" />
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

      <Modal open={showCreate} onClose={() => setShowCreate(false)} title={t('admin.methodology.createVersion')}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <div style={{ padding: 12, background: 'var(--pv-warning-bg)', borderRadius: 8, fontSize: 13, color: 'var(--pv-warning)' }}>
            ⚠️ {t('admin.methodology.warning')}
          </div>
          <input style={inputStyle} placeholder={t('admin.methodology.version')} value={form.version}
            onChange={(e) => setForm((f) => ({ ...f, version: e.target.value }))} />
          <textarea
            style={{ ...inputStyle, height: 140, resize: 'vertical', fontFamily: 'IBM Plex Mono, monospace', fontSize: 12 }}
            placeholder={t('admin.methodology.weights')}
            value={form.weights}
            onChange={(e) => setForm((f) => ({ ...f, weights: e.target.value }))}
          />
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
