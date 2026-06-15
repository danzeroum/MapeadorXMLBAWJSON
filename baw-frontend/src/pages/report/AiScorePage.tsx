import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
  RadialBarChart,
  RadialBar,
  PolarAngleAxis,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import { useAiScore } from '../../hooks/useReport';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';

function scoreColor(score: number) {
  if (score >= 70) return 'var(--pv-success)';
  if (score >= 40) return 'var(--pv-warning)';
  return 'var(--pv-error)';
}

function ScoreGauge({ label, value }: { label: string; value: number }) {
  const color = scoreColor(value);
  const data = [{ name: label, value, fill: color }];
  return (
    <div style={{ textAlign: 'center', flex: 1, minWidth: 140 }}>
      <ResponsiveContainer width="100%" height={120}>
        <RadialBarChart
          cx="50%"
          cy="80%"
          innerRadius="60%"
          outerRadius="100%"
          startAngle={180}
          endAngle={0}
          data={data}
          barSize={12}
        >
          <PolarAngleAxis type="number" domain={[0, 100]} tick={false} />
          <RadialBar dataKey="value" cornerRadius={6} />
          <Tooltip formatter={(v: number) => [`${v}`, label]} />
        </RadialBarChart>
      </ResponsiveContainer>
      <div style={{ fontSize: 22, fontWeight: 700, color, marginTop: -16 }}>{value}</div>
      <div style={{ fontSize: 12, color: 'var(--pv-text-secondary)', marginTop: 4 }}>{label}</div>
    </div>
  );
}

export const AiScorePage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const { data, isLoading, isError } = useAiScore(id!);

  if (isLoading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', paddingTop: 80 }}>
        <Spinner size={40} />
      </div>
    );
  }

  if (isError || !data) {
    return (
      <Card>
        <p style={{ color: 'var(--pv-error)' }}>{t('common.error')}</p>
      </Card>
    );
  }

  const subScores = [
    { label: t('aiScore.complexity'), value: data.complexityScore },
    { label: t('aiScore.standardization'), value: data.standardizationScore },
    { label: t('aiScore.automation'), value: data.automationReadinessScore },
    { label: t('aiScore.maintainability'), value: data.maintainabilityScore },
  ];

  return (
    <div>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>{t('aiScore.title')}</h1>

      <div style={{ display: 'grid', gridTemplateColumns: '280px 1fr', gap: 20 }}>
        {/* Overall score */}
        <Card style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
          <div style={{ fontSize: 13, color: 'var(--pv-text-secondary)', marginBottom: 8 }}>
            {t('aiScore.overall')}
          </div>
          <div
            style={{
              width: 120,
              height: 120,
              borderRadius: '50%',
              background: `conic-gradient(${scoreColor(data.overallScore)} ${data.overallScore}%, var(--pv-border) 0%)`,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              position: 'relative',
            }}
          >
            <div
              style={{
                width: 90,
                height: 90,
                borderRadius: '50%',
                background: 'var(--pv-surface)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                flexDirection: 'column',
              }}
            >
              <span style={{ fontSize: 30, fontWeight: 700, color: scoreColor(data.overallScore) }}>
                {data.overallScore}
              </span>
            </div>
          </div>
          <div style={{ marginTop: 16, fontSize: 13, color: scoreColor(data.overallScore), fontWeight: 600 }}>
            {data.overallScore >= 70 ? 'Bom' : data.overallScore >= 40 ? 'Médio' : 'Crítico'}
          </div>
        </Card>

        {/* Sub-scores */}
        <Card title="Dimensões">
          <div style={{ display: 'flex', gap: 8 }}>
            {subScores.map((s) => (
              <ScoreGauge key={s.label} label={s.label} value={s.value} />
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
};
