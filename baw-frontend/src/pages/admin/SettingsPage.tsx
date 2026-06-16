import React, { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { fetchSettings, upsertSetting } from '../../api/admin';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { EmptyState } from '../../components/ui/EmptyState';
import type { Setting } from '../../types/api';

interface EditableRow {
  key: string;
  value: string;
  dirty: boolean;
}

export const SettingsPage: React.FC = () => {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [rows, setRows] = useState<EditableRow[]>([]);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['settings'],
    queryFn: fetchSettings,
  });

  useEffect(() => {
    if (data) {
      setRows(data.map((s) => ({ key: s.key, value: s.value, dirty: false })));
    }
  }, [data]);

  const saveMutation = useMutation({
    mutationFn: ({ key, value }: { key: string; value: string }) => upsertSetting(key, value),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['settings'] }),
  });

  const updateRow = (key: string, value: string) => {
    setRows((rs) => rs.map((r) => r.key === key ? { ...r, value, dirty: true } : r));
  };

  return (
    <div>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 20 }}>{t('admin.settings.title')}</h1>

      {isLoading && <div style={{ padding: 24, color: 'var(--pv-text-secondary)' }}>{t('common.loading')}</div>}
      {isError && <div style={{ padding: 24, color: 'var(--pv-error)' }}>{t('common.error')}</div>}
      {!isLoading && rows.length === 0 && <EmptyState title={t('admin.settings.empty')} description="" />}

      <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
        {rows.map((row) => (
          <Card key={row.key}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              <label style={{ fontSize: 13, fontWeight: 600, color: 'var(--pv-text-secondary)', fontFamily: 'IBM Plex Mono, monospace' }}>
                {row.key}
              </label>
              <textarea
                style={textareaStyle}
                value={row.value}
                rows={3}
                onChange={(e) => updateRow(row.key, e.target.value)}
              />
              <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <Button
                  size="sm"
                  disabled={!row.dirty || saveMutation.isPending}
                  onClick={() => saveMutation.mutate({ key: row.key, value: row.value })}
                >
                  {t('admin.settings.save')}
                </Button>
              </div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
};

const textareaStyle: React.CSSProperties = {
  padding: '8px 12px',
  border: '1px solid var(--pv-border)',
  borderRadius: 8,
  fontSize: 13,
  fontFamily: 'IBM Plex Mono, monospace',
  color: 'var(--pv-text)',
  width: '100%',
  boxSizing: 'border-box',
  resize: 'vertical',
};
