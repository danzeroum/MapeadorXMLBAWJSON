import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { login } from '../../api/auth';
import { useAuthStore } from '../../store/authStore';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';

export const LoginPage: React.FC = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { setToken } = useAuthStore();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await login(email, password);
      setToken(res.accessToken, res.refreshToken);
      navigate('/');
    } catch {
      setError(t('login.error'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        background: 'var(--pv-bg)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
      }}
    >
      <Card style={{ width: 400 }}>
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <h1 style={{ fontSize: 24, fontWeight: 700, color: 'var(--pv-accent)' }}>
            {t('login.title')}
          </h1>
          <p style={{ color: 'var(--pv-text-secondary)', marginTop: 6 }}>
            {t('login.subtitle')}
          </p>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div>
            <label
              style={{
                display: 'block',
                marginBottom: 6,
                fontSize: 13,
                fontWeight: 500,
                color: 'var(--pv-text)',
              }}
            >
              {t('login.email')}
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              style={inputStyle}
              placeholder="usuario@empresa.com"
            />
          </div>

          <div>
            <label
              style={{
                display: 'block',
                marginBottom: 6,
                fontSize: 13,
                fontWeight: 500,
                color: 'var(--pv-text)',
              }}
            >
              {t('login.password')}
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              style={inputStyle}
              placeholder="••••••••"
            />
          </div>

          {error && (
            <div
              style={{
                background: 'var(--pv-error-bg)',
                color: 'var(--pv-error)',
                padding: '10px 14px',
                borderRadius: 8,
                fontSize: 13,
              }}
            >
              {error}
            </div>
          )}

          <Button type="submit" loading={loading} style={{ width: '100%', justifyContent: 'center' }}>
            {t('login.submit')}
          </Button>
        </form>
      </Card>
    </div>
  );
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
