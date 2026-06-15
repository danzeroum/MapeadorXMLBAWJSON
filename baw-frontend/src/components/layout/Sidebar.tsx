import React from 'react';
import { NavLink, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const navLinkStyle = (isActive: boolean): React.CSSProperties => ({
  display: 'flex',
  alignItems: 'center',
  gap: 10,
  padding: '9px 14px',
  borderRadius: 8,
  textDecoration: 'none',
  fontSize: 14,
  fontWeight: isActive ? 600 : 400,
  color: isActive ? 'var(--pv-accent)' : 'var(--pv-text)',
  background: isActive ? '#e8f0fe' : 'transparent',
  transition: 'all 0.12s',
});

interface NavItemProps {
  to: string;
  icon: string;
  label: string;
}

const NavItem: React.FC<NavItemProps> = ({ to, icon, label }) => (
  <NavLink to={to} style={({ isActive }) => navLinkStyle(isActive)}>
    <span style={{ fontSize: 16 }}>{icon}</span>
    <span>{label}</span>
  </NavLink>
);

export const Sidebar: React.FC = () => {
  const { t } = useTranslation();
  const { id } = useParams<{ id: string }>();

  return (
    <aside
      style={{
        width: 240,
        minHeight: '100vh',
        background: 'var(--pv-surface)',
        borderRight: '1px solid var(--pv-border)',
        display: 'flex',
        flexDirection: 'column',
        padding: '16px 12px',
        gap: 4,
        flexShrink: 0,
      }}
    >
      <div
        style={{
          padding: '8px 14px 16px',
          marginBottom: 8,
          borderBottom: '1px solid var(--pv-border)',
        }}
      >
        <div style={{ fontWeight: 700, fontSize: 16, color: 'var(--pv-accent)' }}>
          Process Veritas
        </div>
        <div style={{ fontSize: 11, color: 'var(--pv-text-secondary)', marginTop: 2 }}>
          BAW Analyzer
        </div>
      </div>

      <NavItem to="/" icon="📋" label={t('nav.runs')} />
      <NavItem to="/runs/new" icon="➕" label={t('nav.newAnalysis')} />

      {id && (
        <>
          <div
            style={{
              fontSize: 11,
              fontWeight: 600,
              color: 'var(--pv-text-secondary)',
              textTransform: 'uppercase',
              letterSpacing: 0.8,
              padding: '12px 14px 4px',
            }}
          >
            {t('nav.report')}
          </div>
          <NavItem to={`/runs/${id}/report/ai-score`} icon="🤖" label={t('nav.aiScore')} />
          <NavItem to={`/runs/${id}/report/graph`} icon="🕸️" label={t('nav.graph')} />
          <NavItem to={`/runs/${id}/report/data-types`} icon="🗂️" label={t('nav.dataTypes')} />
          <NavItem to={`/runs/${id}/report/logic`} icon="💡" label={t('nav.logic')} />
          <NavItem to={`/runs/${id}/report/issues`} icon="⚠️" label={t('nav.issues')} />
          <NavItem to={`/runs/${id}/report/security`} icon="🔒" label={t('nav.security')} />
          <NavItem to={`/runs/${id}/report/integrity`} icon="✅" label={t('nav.integrity')} />
        </>
      )}

      <div style={{ flex: 1 }} />

      <div
        style={{
          borderTop: '1px solid var(--pv-border)',
          paddingTop: 12,
          marginTop: 12,
        }}
      >
        <div
          style={{
            fontSize: 11,
            fontWeight: 600,
            color: 'var(--pv-text-secondary)',
            textTransform: 'uppercase',
            letterSpacing: 0.8,
            padding: '0 14px 4px',
          }}
        >
          {t('nav.admin')}
        </div>
        <NavItem to="/admin/users" icon="👥" label={t('nav.users')} />
        <NavItem to="/admin/sources" icon="🔌" label={t('nav.sources')} />
        <NavItem to="/admin/methodology" icon="⚖️" label={t('nav.methodology')} />
        <NavItem to="/admin/settings" icon="⚙️" label={t('nav.settings')} />
        <NavItem to="/admin/system-logs" icon="📡" label={t('nav.systemLogs')} />
        <NavItem to="/admin/audit-log" icon="📜" label={t('nav.auditLog')} />
        <NavItem to="/admin/usage" icon="📊" label={t('nav.usage')} />
      </div>
    </aside>
  );
};
