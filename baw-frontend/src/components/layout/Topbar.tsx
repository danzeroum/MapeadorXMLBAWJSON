import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuthStore } from '../../store/authStore';
import { Button } from '../ui/Button';

export const Topbar: React.FC = () => {
  const { i18n, t } = useTranslation();
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();

  const toggleLang = () => {
    const next = i18n.language === 'pt-BR' ? 'en-US' : 'pt-BR';
    i18n.changeLanguage(next);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header
      style={{
        height: 56,
        background: 'var(--pv-surface)',
        borderBottom: '1px solid var(--pv-border)',
        display: 'flex',
        alignItems: 'center',
        padding: '0 20px',
        gap: 12,
        flexShrink: 0,
      }}
    >
      <div style={{ flex: 1 }} />

      <Button variant="ghost" size="sm" onClick={toggleLang}>
        {i18n.language === 'pt-BR' ? '🇧🇷 PT' : '🇺🇸 EN'}
      </Button>

      {user && (
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: 10,
          }}
        >
          <div
            style={{
              width: 32,
              height: 32,
              borderRadius: '50%',
              background: 'var(--pv-accent)',
              color: '#fff',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: 13,
              fontWeight: 700,
            }}
          >
            {user.name?.charAt(0).toUpperCase() ?? 'U'}
          </div>
          <div>
            <div style={{ fontSize: 13, fontWeight: 600, lineHeight: 1.2 }}>{user.name}</div>
            <div style={{ fontSize: 11, color: 'var(--pv-text-secondary)' }}>{user.role}</div>
          </div>
        </div>
      )}

      <Button variant="ghost" size="sm" onClick={handleLogout}>
        {t('nav.logout')}
      </Button>
    </header>
  );
};
