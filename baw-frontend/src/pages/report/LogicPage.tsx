import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useLogic } from '../../hooks/useReport';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import { EmptyState } from '../../components/ui/EmptyState';

export const LogicPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const { data, isLoading, isError } = useLogic(id!);
  const [selected, setSelected] = useState(0);

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

  if (!data || data.length === 0) {
    return (
      <div>
        <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>{t('logic.title')}</h1>
        <Card><EmptyState title={t('common.noData')} /></Card>
      </div>
    );
  }

  const section = data[selected];

  return (
    <div>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>{t('logic.title')}</h1>

      <div style={{ display: 'flex', gap: 16 }}>
        {/* Sidebar list */}
        <div
          style={{
            width: 220,
            flexShrink: 0,
            display: 'flex',
            flexDirection: 'column',
            gap: 4,
          }}
        >
          {data.map((s, i) => (
            <button
              key={s.name}
              onClick={() => setSelected(i)}
              style={{
                padding: '8px 12px',
                textAlign: 'left',
                border: 'none',
                borderRadius: 8,
                background: selected === i ? '#e8f0fe' : 'transparent',
                color: selected === i ? 'var(--pv-accent)' : 'var(--pv-text)',
                fontWeight: selected === i ? 600 : 400,
                fontSize: 13,
                cursor: 'pointer',
              }}
            >
              {s.name}
            </button>
          ))}
        </div>

        {/* Code viewer */}
        <div style={{ flex: 1 }}>
          <Card padding={0}>
            <div
              style={{
                padding: '10px 16px',
                borderBottom: '1px solid var(--pv-border)',
                display: 'flex',
                alignItems: 'center',
                gap: 10,
              }}
            >
              <span style={{ fontWeight: 600, fontSize: 14 }}>{section.name}</span>
              <span
                style={{
                  fontSize: 11,
                  background: 'var(--pv-bg)',
                  border: '1px solid var(--pv-border)',
                  padding: '2px 8px',
                  borderRadius: 4,
                  color: 'var(--pv-text-secondary)',
                }}
              >
                {section.language}
              </span>
            </div>
            <pre
              style={{
                margin: 0,
                padding: 20,
                overflowX: 'auto',
                fontFamily: 'IBM Plex Mono, monospace',
                fontSize: 13,
                lineHeight: 1.6,
                background: '#1e1e2e',
                color: '#cdd6f4',
                borderRadius: '0 0 var(--pv-radius) var(--pv-radius)',
              }}
            >
              <code>{section.code}</code>
            </pre>
          </Card>
        </div>
      </div>
    </div>
  );
};
