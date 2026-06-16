import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { useSSE } from '../../hooks/useSSE';

type ConnStatus = 'connecting' | 'connected' | 'disconnected';

export const SystemLogsPage: React.FC = () => {
  const { t } = useTranslation();
  const [paused, setPaused] = useState(false);
  const [lines, setLines] = useState<string[]>([]);
  const [status, setStatus] = useState<ConnStatus>('connecting');
  const bottomRef = useRef<HTMLDivElement>(null);
  const pausedRef = useRef(paused);
  pausedRef.current = paused;

  const onMessage = useCallback((data: string) => {
    setStatus('connected');
    if (!pausedRef.current) {
      setLines((prev) => {
        const next = [...prev, data];
        return next.length > 500 ? next.slice(-500) : next;
      });
    }
  }, []);

  const onError = useCallback(() => setStatus('disconnected'), []);

  useSSE('/api/v1/system/logs', { onMessage, onError });

  useEffect(() => {
    if (!paused) {
      bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
    }
  }, [lines, paused]);

  const levelColor = (line: string): string => {
    if (line.includes('[ERROR]')) return 'var(--pv-error)';
    if (line.includes('[WARN]')) return 'var(--pv-warning)';
    if (line.includes('[INFO]')) return 'var(--pv-success)';
    return '#8b949e';
  };

  const statusLabel = status === 'connected'
    ? t('admin.systemLogs.connected')
    : status === 'connecting'
    ? t('admin.systemLogs.connecting')
    : t('admin.systemLogs.disconnected');

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: 'calc(100vh - 80px)' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
        <div>
          <h1 style={{ fontSize: 24, fontWeight: 700 }}>{t('admin.systemLogs.title')}</h1>
          <div style={{ fontSize: 12, marginTop: 4, color: status === 'connected' ? 'var(--pv-success)' : 'var(--pv-text-secondary)' }}>
            {statusLabel}
          </div>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <Button variant="secondary" size="sm" onClick={() => setPaused((p) => !p)}>
            {paused ? t('admin.systemLogs.resume') : t('admin.systemLogs.pause')}
          </Button>
          <Button variant="secondary" size="sm" onClick={() => setLines([])}>
            {t('admin.systemLogs.clear')}
          </Button>
        </div>
      </div>

      <Card padding={0} style={{ flex: 1, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
        <div
          style={{
            flex: 1,
            overflowY: 'auto',
            padding: '12px 16px',
            background: '#0d1117',
            fontFamily: 'IBM Plex Mono, monospace',
            fontSize: 12,
            lineHeight: 1.6,
          }}
        >
          {lines.length === 0 && (
            <div style={{ color: '#6e7681' }}>{t('admin.systemLogs.empty')}</div>
          )}
          {lines.map((line, i) => (
            <div key={i} style={{ color: levelColor(line), whiteSpace: 'pre-wrap', wordBreak: 'break-all' }}>
              {line}
            </div>
          ))}
          <div ref={bottomRef} />
        </div>
      </Card>
    </div>
  );
};
