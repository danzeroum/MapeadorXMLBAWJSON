import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useSecurity } from '../../hooks/useReport';
import { Badge } from '../../components/ui/Badge';
import { Card } from '../../components/ui/Card';
import { Table } from '../../components/ui/Table';
import { Spinner } from '../../components/ui/Spinner';
import { EmptyState } from '../../components/ui/EmptyState';
import type { SecurityFinding } from '../../types/api';
import type { Column } from '../../components/ui/Table';

export const SecurityPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const { data, isLoading, isError } = useSecurity(id!);

  if (isLoading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', paddingTop: 80 }}>
        <Spinner size={40} />
      </div>
    );
  }

  if (isError) {
    return <Card><p style={{ color: 'var(--pv-error)' }}>{t('common.error')}</p></Card>;
  }

  const riskVariant = (r: SecurityFinding['risk']) => {
    if (r === 'HIGH') return 'CRITICAL' as const;
    if (r === 'MEDIUM') return 'MEDIUM' as const;
    return 'LOW' as const;
  };

  const columns: Column<SecurityFinding>[] = [
    { key: 'category', header: t('security.category') },
    {
      key: 'risk',
      header: t('security.risk'),
      render: (row) => <Badge variant={riskVariant(row.risk)}>{row.risk}</Badge>,
    },
    { key: 'description', header: t('security.description') },
    {
      key: 'location',
      header: t('security.location'),
      render: (row) =>
        row.location ? (
          <code style={{ fontFamily: 'IBM Plex Mono, monospace', fontSize: 12 }}>{row.location}</code>
        ) : (
          '—'
        ),
    },
  ];

  return (
    <div>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>{t('security.title')}</h1>
      <Card padding={0}>
        {!data || data.length === 0 ? (
          <EmptyState title={t('security.empty')} />
        ) : (
          <Table
            columns={columns}
            data={data}
            keyExtractor={(r) => r.id}
            loading={isLoading}
          />
        )}
      </Card>
    </div>
  );
};
