import React from 'react';
import type { RunStatus } from '../../types/api';

type BadgeVariant = RunStatus | 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW' | 'default';

interface BadgeProps {
  variant?: BadgeVariant;
  children: React.ReactNode;
}

const variantStyles: Record<BadgeVariant, React.CSSProperties> = {
  QUEUED: {
    background: '#f0f0f0',
    color: '#555',
  },
  RUNNING: {
    background: '#dbeafe',
    color: 'var(--pv-accent)',
  },
  DONE: {
    background: 'var(--pv-success-bg)',
    color: 'var(--pv-success)',
  },
  FAILED: {
    background: 'var(--pv-error-bg)',
    color: 'var(--pv-error)',
  },
  CRITICAL: {
    background: 'var(--pv-error-bg)',
    color: 'var(--pv-error)',
  },
  HIGH: {
    background: '#fff3e0',
    color: '#e65100',
  },
  MEDIUM: {
    background: 'var(--pv-warning-bg)',
    color: 'var(--pv-warning)',
  },
  LOW: {
    background: '#e8f5e9',
    color: '#2e7d32',
  },
  default: {
    background: '#f0f0f0',
    color: '#555',
  },
};

export const Badge: React.FC<BadgeProps> = ({ variant = 'default', children }) => {
  return (
    <span
      style={{
        ...variantStyles[variant],
        padding: '2px 8px',
        borderRadius: 6,
        fontSize: 12,
        fontWeight: 600,
        display: 'inline-block',
        letterSpacing: 0.3,
      }}
    >
      {children}
    </span>
  );
};
