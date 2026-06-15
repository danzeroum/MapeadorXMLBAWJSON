import React from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Topbar } from './Topbar';

export const AppShell: React.FC = () => {
  return (
    <div style={{ display: 'flex', height: '100vh', overflow: 'hidden' }}>
      <Sidebar />
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
        <Topbar />
        <main
          style={{
            flex: 1,
            overflow: 'auto',
            background: 'var(--pv-bg)',
            padding: 24,
          }}
        >
          <Outlet />
        </main>
      </div>
    </div>
  );
};
