import React from 'react';
import { Button } from './Button';

interface ModalProps {
  open: boolean;
  onClose: () => void;
  title?: string;
  children: React.ReactNode;
  footer?: React.ReactNode;
}

export const Modal: React.FC<ModalProps> = ({ open, onClose, title, children, footer }) => {
  if (!open) return null;

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        background: 'rgba(22, 26, 32, 0.5)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 1000,
      }}
      onClick={onClose}
    >
      <div
        style={{
          background: 'var(--pv-surface)',
          borderRadius: 'var(--pv-radius)',
          padding: 24,
          minWidth: 400,
          maxWidth: 600,
          width: '90vw',
          boxShadow: '0 20px 60px rgba(0,0,0,0.2)',
        }}
        onClick={(e) => e.stopPropagation()}
      >
        {title && (
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              marginBottom: 16,
            }}
          >
            <h2 style={{ fontSize: 18, fontWeight: 600 }}>{title}</h2>
            <Button variant="ghost" size="sm" onClick={onClose}>
              ✕
            </Button>
          </div>
        )}
        <div>{children}</div>
        {footer && (
          <div
            style={{
              marginTop: 24,
              display: 'flex',
              gap: 8,
              justifyContent: 'flex-end',
            }}
          >
            {footer}
          </div>
        )}
      </div>
    </div>
  );
};
