import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useIntegrity } from '../../hooks/useReport';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';

export const IntegrityPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const { data, isLoading, isError } = useIntegrity(id!);

  if (isLoading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', paddingTop: 80 }}>
        <Spinner size={40} />
      </div>
    );
  }

  if (isError || !data) {
    return <Card><p style={{ color: 'var(--pv-error)' }}>{t('common.error')}</p></Card>;
  }

  return (
    <div>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>{t('integrity.title')}</h1>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
        {/* Checksums */}
        <Card title={t('integrity.checksums')}>
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid var(--pv-border)' }}>
                  <th style={thStyle}>{t('integrity.section')}</th>
                  <th style={thStyle}>{t('integrity.hash')}</th>
                </tr>
              </thead>
              <tbody>
                {data.checksums.map((c) => (
                  <tr key={c.section} style={{ borderBottom: '1px solid var(--pv-border)' }}>
                    <td style={tdStyle}>{c.section}</td>
                    <td style={{ ...tdStyle, fontFamily: 'IBM Plex Mono, monospace', fontSize: 12, wordBreak: 'break-all' }}>
                      {c.sha256}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div
            style={{
              marginTop: 16,
              padding: '12px 16px',
              background: 'var(--pv-bg)',
              borderRadius: 8,
            }}
          >
            <div style={{ fontSize: 12, color: 'var(--pv-text-secondary)', marginBottom: 4 }}>
              {t('integrity.overall')}
            </div>
            <code style={{ fontFamily: 'IBM Plex Mono, monospace', fontSize: 13, wordBreak: 'break-all' }}>
              {data.overallChecksum}
            </code>
          </div>
        </Card>

        {/* Digital signature */}
        <Card title={t('integrity.signature')}>
          {data.digitalSignature ? (
            <pre
              style={{
                fontFamily: 'IBM Plex Mono, monospace',
                fontSize: 12,
                background: '#1e1e2e',
                color: '#cdd6f4',
                padding: 16,
                borderRadius: 8,
                overflow: 'auto',
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-all',
              }}
            >
              {data.digitalSignature}
            </pre>
          ) : (
            <p style={{ color: 'var(--pv-text-secondary)', fontStyle: 'italic' }}>
              {t('integrity.noSignature')}
            </p>
          )}
        </Card>

        {/* Provenance */}
        {data.provenance && data.provenance.length > 0 && (
          <Card title={t('integrity.provenance')}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 0 }}>
              {data.provenance.map((e, i) => (
                <div
                  key={i}
                  style={{
                    display: 'flex',
                    gap: 16,
                    padding: '12px 0',
                    borderBottom: i < data.provenance.length - 1 ? '1px solid var(--pv-border)' : 'none',
                    alignItems: 'flex-start',
                  }}
                >
                  <div
                    style={{
                      width: 10,
                      height: 10,
                      borderRadius: '50%',
                      background: 'var(--pv-accent)',
                      flexShrink: 0,
                      marginTop: 4,
                    }}
                  />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: 14, fontWeight: 500 }}>{e.event}</div>
                    <div style={{ fontSize: 12, color: 'var(--pv-text-secondary)', marginTop: 2 }}>
                      {e.actor && <span>{e.actor} · </span>}
                      {new Date(e.timestamp).toLocaleString()}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </Card>
        )}
      </div>
    </div>
  );
};

const thStyle: React.CSSProperties = {
  padding: '10px 16px',
  textAlign: 'left',
  fontSize: 12,
  fontWeight: 600,
  color: 'var(--pv-text-secondary)',
  textTransform: 'uppercase',
  letterSpacing: 0.5,
};

const tdStyle: React.CSSProperties = {
  padding: '12px 16px',
  fontSize: 14,
};
