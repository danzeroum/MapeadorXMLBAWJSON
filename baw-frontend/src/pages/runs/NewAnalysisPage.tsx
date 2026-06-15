import React, { useState, useRef, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { createRun } from '../../api/runs';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { useToast } from '../../components/ui/Toast';

type Step = 1 | 2 | 3;

export const NewAnalysisPage: React.FC = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [step, setStep] = useState<Step>(1);
  const [file, setFile] = useState<File | null>(null);
  const [dragOver, setDragOver] = useState(false);
  const [processName, setProcessName] = useState('');
  const [processId, setProcessId] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setDragOver(false);
    const f = e.dataTransfer.files[0];
    if (f && f.name.endsWith('.twx')) setFile(f);
  }, []);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const f = e.target.files?.[0];
    if (f) setFile(f);
  };

  const handleSubmit = async () => {
    if (!file) return;
    setSubmitting(true);
    try {
      const fd = new FormData();
      fd.append('file', file);
      fd.append('processName', processName);
      fd.append('processId', processId);
      const run = await createRun(fd);
      showToast('Análise iniciada com sucesso!', 'success');
      navigate(`/runs/${run.id}`);
    } catch {
      showToast('Erro ao criar análise', 'error');
      setSubmitting(false);
    }
  };

  const steps = [
    t('newAnalysis.step1'),
    t('newAnalysis.step2'),
    t('newAnalysis.step3'),
  ];

  return (
    <div style={{ maxWidth: 640, margin: '0 auto' }}>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>{t('newAnalysis.title')}</h1>

      {/* Step indicator */}
      <div style={{ display: 'flex', gap: 0, marginBottom: 32 }}>
        {steps.map((label, i) => {
          const idx = i + 1;
          const active = step === idx;
          const done = step > idx;
          return (
            <React.Fragment key={idx}>
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', flex: 1 }}>
                <div
                  style={{
                    width: 32,
                    height: 32,
                    borderRadius: '50%',
                    background: done ? 'var(--pv-success)' : active ? 'var(--pv-accent)' : 'var(--pv-border)',
                    color: done || active ? '#fff' : 'var(--pv-text-secondary)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontWeight: 700,
                    fontSize: 13,
                  }}
                >
                  {done ? '✓' : idx}
                </div>
                <div style={{ fontSize: 11, marginTop: 6, color: active ? 'var(--pv-accent)' : 'var(--pv-text-secondary)', fontWeight: active ? 600 : 400 }}>
                  {label}
                </div>
              </div>
              {i < steps.length - 1 && (
                <div style={{ flex: 0.5, height: 2, background: done ? 'var(--pv-success)' : 'var(--pv-border)', marginTop: 15 }} />
              )}
            </React.Fragment>
          );
        })}
      </div>

      <Card>
        {step === 1 && (
          <div>
            <div
              onDrop={handleDrop}
              onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
              onDragLeave={() => setDragOver(false)}
              onClick={() => inputRef.current?.click()}
              style={{
                border: `2px dashed ${dragOver ? 'var(--pv-accent)' : 'var(--pv-border)'}`,
                borderRadius: 'var(--pv-radius)',
                padding: 48,
                textAlign: 'center',
                cursor: 'pointer',
                background: dragOver ? '#e8f0fe' : 'var(--pv-bg)',
                transition: 'all 0.15s',
              }}
            >
              <div style={{ fontSize: 40, marginBottom: 12 }}>📁</div>
              <p style={{ color: 'var(--pv-text-secondary)', fontSize: 14 }}>
                {dragOver ? t('newAnalysis.dropzoneActive') : t('newAnalysis.dropzone')}
              </p>
              {file && (
                <p style={{ marginTop: 12, color: 'var(--pv-success)', fontWeight: 600 }}>
                  ✓ {file.name}
                </p>
              )}
            </div>
            <input
              ref={inputRef}
              type="file"
              accept=".twx"
              onChange={handleFileChange}
              style={{ display: 'none' }}
            />
            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: 20 }}>
              <Button onClick={() => setStep(2)} disabled={!file}>
                {t('newAnalysis.next')}
              </Button>
            </div>
          </div>
        )}

        {step === 2 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <div>
              <label style={labelStyle}>{t('newAnalysis.processName')}</label>
              <input
                type="text"
                value={processName}
                onChange={(e) => setProcessName(e.target.value)}
                style={inputStyle}
                placeholder="ex: Processo de Aprovação"
              />
            </div>
            <div>
              <label style={labelStyle}>{t('newAnalysis.processId')}</label>
              <input
                type="text"
                value={processId}
                onChange={(e) => setProcessId(e.target.value)}
                style={inputStyle}
                placeholder="ex: PA-001"
              />
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 8 }}>
              <Button variant="ghost" onClick={() => setStep(1)}>{t('newAnalysis.back')}</Button>
              <Button onClick={() => setStep(3)} disabled={!processName || !processId}>
                {t('newAnalysis.next')}
              </Button>
            </div>
          </div>
        )}

        {step === 3 && (
          <div>
            <p style={{ color: 'var(--pv-text-secondary)', marginBottom: 20 }}>
              {t('newAnalysis.review')}
            </p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              {[
                { label: t('newAnalysis.file'), value: file?.name ?? '' },
                { label: t('newAnalysis.processName'), value: processName },
                { label: t('newAnalysis.processId'), value: processId },
              ].map((item) => (
                <div key={item.label} style={{ display: 'flex', gap: 12 }}>
                  <span style={{ color: 'var(--pv-text-secondary)', fontSize: 13, width: 140, flexShrink: 0 }}>
                    {item.label}
                  </span>
                  <span style={{ fontWeight: 600, fontSize: 14 }}>{item.value}</span>
                </div>
              ))}
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 24 }}>
              <Button variant="ghost" onClick={() => setStep(2)}>{t('newAnalysis.back')}</Button>
              <Button onClick={handleSubmit} loading={submitting}>
                {submitting ? t('newAnalysis.submitting') : t('newAnalysis.submit')}
              </Button>
            </div>
          </div>
        )}
      </Card>
    </div>
  );
};

const labelStyle: React.CSSProperties = {
  display: 'block',
  marginBottom: 6,
  fontSize: 13,
  fontWeight: 500,
  color: 'var(--pv-text)',
};

const inputStyle: React.CSSProperties = {
  width: '100%',
  padding: '9px 12px',
  border: '1px solid var(--pv-border)',
  borderRadius: 8,
  fontSize: 14,
  fontFamily: 'Public Sans, sans-serif',
  color: 'var(--pv-text)',
  background: 'var(--pv-bg)',
  outline: 'none',
  boxSizing: 'border-box',
};
