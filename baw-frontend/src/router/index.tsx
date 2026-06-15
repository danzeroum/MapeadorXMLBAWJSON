import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { AppShell } from '../components/layout/AppShell';
import { LoginPage } from '../pages/login/LoginPage';
import { RunsPage } from '../pages/runs/RunsPage';
import { NewAnalysisPage } from '../pages/runs/NewAnalysisPage';
import { RunDetailPage } from '../pages/runs/RunDetailPage';
import { AiScorePage } from '../pages/report/AiScorePage';
import { GraphPage } from '../pages/report/GraphPage';
import { DataTypesPage } from '../pages/report/DataTypesPage';
import { LogicPage } from '../pages/report/LogicPage';
import { IssuesPage } from '../pages/report/IssuesPage';
import { SecurityPage } from '../pages/report/SecurityPage';
import { IntegrityPage } from '../pages/report/IntegrityPage';
import { UsersPage } from '../pages/admin/UsersPage';
import { SourcesPage } from '../pages/admin/SourcesPage';
import { MethodologyPage } from '../pages/admin/MethodologyPage';
import { SettingsPage } from '../pages/admin/SettingsPage';
import { SystemLogsPage } from '../pages/admin/SystemLogsPage';
import { AuditLogPage } from '../pages/admin/AuditLogPage';
import { UsagePage } from '../pages/admin/UsagePage';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const token = useAuthStore((s) => s.token);
  if (!token) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

export function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        element={
          <ProtectedRoute>
            <AppShell />
          </ProtectedRoute>
        }
      >
        <Route path="/" element={<RunsPage />} />
        <Route path="/runs/new" element={<NewAnalysisPage />} />
        <Route path="/runs/:id" element={<RunDetailPage />} />
        <Route path="/runs/:id/report/ai-score" element={<AiScorePage />} />
        <Route path="/runs/:id/report/graph" element={<GraphPage />} />
        <Route path="/runs/:id/report/issues" element={<IssuesPage />} />
        <Route path="/runs/:id/report/integrity" element={<IntegrityPage />} />
        <Route path="/runs/:id/report/data-types" element={<DataTypesPage />} />
        <Route path="/runs/:id/report/logic" element={<LogicPage />} />
        <Route path="/runs/:id/report/security" element={<SecurityPage />} />
        <Route path="/admin/users" element={<UsersPage />} />
        <Route path="/admin/sources" element={<SourcesPage />} />
        <Route path="/admin/methodology" element={<MethodologyPage />} />
        <Route path="/admin/settings" element={<SettingsPage />} />
        <Route path="/admin/system-logs" element={<SystemLogsPage />} />
        <Route path="/admin/audit-log" element={<AuditLogPage />} />
        <Route path="/admin/usage" element={<UsagePage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
