import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useRun } from '../../hooks/useRun';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';

export const RunDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { data: run, isLoading, isError } = useRun(id!);

  if (isLoading) {
    return (
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: 300 }}>
        <Spinner size={40} />
      </div>
    );
  }

  if (isError || !run) {
    return (
      <Card>
        <p style={{ color: 'var(--pv-error)' }}>{t('common.error')}</p>
      </Card>
    );
  }

  const isActive = run.status === 'QUEUED' || run.status === 'RUNNING';

  return (
    <div style={{ maxWidth: 600, margin: '0 auto' }}>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>{t('runDetail.title')}</h1>

      <Card>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <InfoRow label={t('runs.processName')} value={run.processName} />
          <InfoRow label={t('runs.processId')} value={run.processId} />
          <InfoRow
            label={t('runDetail.status')}
            value={<Badge variant={run.status}>{run.status}</Badge>}
          />
          {run.startedAt && (
            <InfoRow
              label={t('runs.startedAt')}
              value={new Date(run.startedAt).toLocaleString()}
            />
          )}
          {run.completedAt && (
            <InfoRow
              label="Concluído em"
              value={new Date(run.completedAt).toLocaleString()}
            />
          )}
          {run.twxFilename && (
            <InfoRow label={t('runs.file')} value={run.twxFilename} />
          )}
          {run.errorMessage && (
            <div
              style={{
                background: 'var(--pv-error-bg)',
                color: 'var(--pv-error)',
                padding: '12px 14px',
                borderRadius: 8,
                fontSize: 13,
              }}
            >
              <strong>{t('runDetail.error')}:</strong> {run.errorMessage}
            </div>
          )}
        </div>

        {isActive && (
          <div
            style={{
              marginTop: 24,
              display: 'flex',
              alignItems: 'center',
              gap: 12,
              padding: '12px 16px',
              background: '#dbeafe',
              borderRadius: 8,
            }}
          >
            <Spinner size={18} />
            <span style={{ color: 'var(--pv-accent)', fontSize: 14 }}>
              {t('runDetail.polling')}
            </span>
          </div>
        )}

        {run.status === 'DONE' && (
          <div style={{ marginTop: 24 }}>
            <Button onClick={() => navigate(`/runs/${run.id}/report/ai-score`)}>
              {t('runDetail.viewReport')}
            </Button>
          </div>
        )}
      </Card>
    </div>
  );
};

function InfoRow({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
      <span
        style={{
          color: 'var(--pv-text-secondary)',
          fontSize: 13,
          width: 160,
          flexShrink: 0,
        }}
      >
        {label}
      </span>
      <span style={{ fontSize: 14, fontWeight: 500 }}>{value}</span>
    </div>
  );
}
