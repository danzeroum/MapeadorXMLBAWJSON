import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useIssues } from '../../hooks/useReport';
import { Badge } from '../../components/ui/Badge';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import { EmptyState } from '../../components/ui/EmptyState';
import type { Issue } from '../../types/api';

const SEVERITIES: Issue['severity'][] = ['CRITICAL', 'HIGH', 'MEDIUM', 'LOW'];

export const IssuesPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const { data, isLoading, isError } = useIssues(id!);

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

  const grouped = SEVERITIES.reduce<Record<string, Issue[]>>((acc, sev) => {
    const items = (data ?? []).filter((i) => i.severity === sev);
    if (items.length > 0) acc[sev] = items;
    return acc;
  }, {});

  return (
    <div>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>{t('issues.title')}</h1>

      {!data || data.length === 0 ? (
        <Card>
          <EmptyState title={t('issues.empty')} />
        </Card>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          {SEVERITIES.filter((s) => grouped[s]).map((sev) => (
            <div key={sev}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 12 }}>
                <Badge variant={sev}>{sev}</Badge>
                <span style={{ fontSize: 13, color: 'var(--pv-text-secondary)' }}>
                  {grouped[sev].length} {grouped[sev].length === 1 ? 'problema' : 'problemas'}
                </span>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                {grouped[sev].map((issue) => (
                  <IssueCard key={issue.id} issue={issue} t={t} />
                ))}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

function IssueCard({ issue, t }: { issue: Issue; t: (k: string) => string }) {
  return (
    <Card padding={16}>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
        <div style={{ fontWeight: 600, fontSize: 14 }}>{issue.title}</div>
        <div style={{ fontSize: 13, color: 'var(--pv-text-secondary)' }}>{issue.description}</div>
        {issue.location && (
          <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
            <span style={{ fontSize: 11, color: 'var(--pv-text-secondary)', fontWeight: 600 }}>
              {t('issues.location')}:
            </span>
            <code style={{ fontFamily: 'IBM Plex Mono, monospace', fontSize: 12, background: 'var(--pv-bg)', padding: '1px 6px', borderRadius: 4 }}>
              {issue.location}
            </code>
          </div>
        )}
        {issue.recommendation && (
          <div
            style={{
              background: 'var(--pv-success-bg)',
              borderRadius: 6,
              padding: '8px 12px',
              fontSize: 13,
              color: 'var(--pv-success)',
            }}
          >
            <strong>{t('issues.recommendation')}:</strong> {issue.recommendation}
          </div>
        )}
      </div>
    </Card>
  );
}
