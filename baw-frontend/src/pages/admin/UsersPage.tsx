import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { fetchUsers, createUser, updateUser, deleteUser } from '../../api/users';
import { Table } from '../../components/ui/Table';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Modal } from '../../components/ui/Modal';
import { EmptyState } from '../../components/ui/EmptyState';
import type { User } from '../../types/api';
import type { Column } from '../../components/ui/Table';

const ROLES = ['ADMIN', 'ANALISTA', 'AUDITOR', 'VISUALIZADOR'] as const;

export const UsersPage: React.FC = () => {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [page, setPage] = useState(0);
  const [showCreate, setShowCreate] = useState(false);
  const [form, setForm] = useState({ email: '', name: '', role: 'ANALISTA', password: '' });

  const { data, isLoading, isError } = useQuery({
    queryKey: ['users', page],
    queryFn: () => fetchUsers(page, 20),
  });

  const createMutation = useMutation({
    mutationFn: () => createUser({ email: form.email, name: form.name, role: form.role as User['role'], active: true, password: form.password } as any),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['users'] });
      setShowCreate(false);
      setForm({ email: '', name: '', role: 'ANALISTA', password: '' });
    },
  });

  const deactivateMutation = useMutation({
    mutationFn: (id: string) => deleteUser(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['users'] }),
  });

  const columns: Column<User>[] = [
    { key: 'email', header: t('admin.users.email') },
    { key: 'name', header: t('admin.users.name') },
    { key: 'role', header: t('admin.users.role'), render: (r) => <Badge variant="default">{r.role}</Badge> },
    {
      key: 'active',
      header: t('admin.users.active'),
      render: (r) => (
        <span style={{ color: r.active ? 'var(--pv-success)' : 'var(--pv-text-secondary)' }}>
          {r.active ? t('common.active') : t('common.inactive')}
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
        r.active ? (
          <Button size="sm" variant="danger" onClick={() => deactivateMutation.mutate(r.id)}>
            {t('admin.users.deactivate')}
          </Button>
        ) : null,
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <h1 style={{ fontSize: 24, fontWeight: 700 }}>{t('admin.users.title')}</h1>
        <Button onClick={() => setShowCreate(true)}>{t('admin.users.createUser')}</Button>
      </div>

      <Card padding={0}>
        {isError && <div style={{ padding: 24, color: 'var(--pv-error)' }}>{t('common.error')}</div>}
        {!isLoading && data?.content.length === 0 ? (
          <EmptyState title={t('admin.users.empty')} description="" />
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

      <Modal open={showCreate} onClose={() => setShowCreate(false)} title={t('admin.users.createUser')}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <input style={inputStyle} placeholder={t('admin.users.email')} value={form.email}
            onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))} />
          <input style={inputStyle} placeholder={t('admin.users.name')} value={form.name}
            onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))} />
          <input style={inputStyle} type="password" placeholder="Senha" value={form.password}
            onChange={(e) => setForm((f) => ({ ...f, password: e.target.value }))} />
          <select style={inputStyle} value={form.role} onChange={(e) => setForm((f) => ({ ...f, role: e.target.value }))}>
            {ROLES.map((r) => <option key={r} value={r}>{r}</option>)}
          </select>
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
