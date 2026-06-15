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
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
