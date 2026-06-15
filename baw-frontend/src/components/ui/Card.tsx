import React from 'react';

interface CardProps {
  children: React.ReactNode;
  style?: React.CSSProperties;
  title?: string;
  padding?: number | string;
}

export const Card: React.FC<CardProps> = ({ children, style, title, padding = 20 }) => {
  return (
    <div
      style={{
        background: 'var(--pv-surface)',
        border: '1px solid var(--pv-border)',
        borderRadius: 'var(--pv-radius)',
        padding,
        ...style,
      }}
    >
      {title && (
        <h3
          style={{
            fontSize: 16,
            fontWeight: 600,
            color: 'var(--pv-text)',
            marginBottom: 16,
          }}
        >
          {title}
        </h3>
      )}
      {children}
    </div>
  );
};
