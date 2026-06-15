import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useDataTypes } from '../../hooks/useReport';
import { Table } from '../../components/ui/Table';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import { EmptyState } from '../../components/ui/EmptyState';
import type { DataType } from '../../types/api';
import type { Column } from '../../components/ui/Table';

export const DataTypesPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const { data, isLoading, isError } = useDataTypes(id!);

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

  const columns: Column<DataType>[] = [
    { key: 'name', header: t('dataTypes.name') },
    { key: 'type', header: t('dataTypes.type') },
    {
      key: 'structure',
      header: t('dataTypes.structure'),
      render: (row) => row.structure ? (
        <code style={{ fontFamily: 'IBM Plex Mono, monospace', fontSize: 12, background: 'var(--pv-bg)', padding: '2px 6px', borderRadius: 4 }}>
          {row.structure}
        </code>
      ) : '—',
    },
    {
      key: 'usedIn',
      header: t('dataTypes.usedIn'),
      render: (row) => row.usedIn?.join(', ') ?? '—',
    },
  ];

  return (
    <div>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>{t('dataTypes.title')}</h1>
      <Card padding={0}>
        {(!data || data.length === 0) ? (
          <EmptyState title={t('common.noData')} />
        ) : (
          <Table
            columns={columns}
            data={data}
            keyExtractor={(r) => r.name}
            loading={isLoading}
          />
        )}
      </Card>
    </div>
  );
};
