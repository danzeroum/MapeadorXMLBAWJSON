import React from 'react';

interface EmptyStateProps {
  title: string;
  description?: string;
  action?: React.ReactNode;
}

export const EmptyState: React.FC<EmptyStateProps> = ({ title, description, action }) => {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 64,
        color: 'var(--pv-text-secondary)',
        textAlign: 'center',
        gap: 12,
      }}
    >
      <div style={{ fontSize: 48 }}>📂</div>
      <h3 style={{ fontSize: 18, fontWeight: 600, color: 'var(--pv-text)' }}>{title}</h3>
      {description && <p style={{ fontSize: 14, maxWidth: 400 }}>{description}</p>}
      {action && <div style={{ marginTop: 8 }}>{action}</div>}
    </div>
  );
};
