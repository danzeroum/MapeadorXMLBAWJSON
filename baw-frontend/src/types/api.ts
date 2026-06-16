export type RunStatus = 'QUEUED' | 'RUNNING' | 'DONE' | 'FAILED';

export interface Run {
  id: string;
  processName: string;
  processId: string;
  status: RunStatus;
  startedAt?: string;
  completedAt?: string;
  durationMs?: number;
  errorMessage?: string;
  createdAt: string;
  authorEmail?: string;
  twxFilename?: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface User {
  id: string;
  email: string;
  name: string;
  role: 'ADMIN' | 'ANALISTA' | 'AUDITOR' | 'VISUALIZADOR';
  active: boolean;
  createdAt: string;
}

export interface AiScore {
  overallScore: number;
  complexityScore: number;
  standardizationScore: number;
  automationReadinessScore: number;
  maintainabilityScore: number;
}

export interface GraphNode {
  id: string;
  label: string;
  type: 'task' | 'gateway' | 'event';
  properties?: Record<string, string>;
  x?: number;
  y?: number;
}

export interface GraphEdge {
  id: string;
  source: string;
  target: string;
  label?: string;
}

export interface ProcessGraph {
  nodes: GraphNode[];
  edges: GraphEdge[];
}

export interface DataType {
  name: string;
  type: string;
  structure?: string;
  usedIn?: string[];
}

export interface Issue {
  id: string;
  severity: 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW';
  title: string;
  description: string;
  location?: string;
  recommendation?: string;
}

export interface SecurityFinding {
  id: string;
  category: string;
  description: string;
  risk: 'HIGH' | 'MEDIUM' | 'LOW';
  location?: string;
}

export interface IntegrityChecksum {
  section: string;
  sha256: string;
}

export interface IntegrityReport {
  checksums: IntegrityChecksum[];
  overallChecksum: string;
  digitalSignature?: string;
  generatedAt: string;
  provenance: ProvenanceEvent[];
}

export interface ProvenanceEvent {
  timestamp: string;
  event: string;
  actor?: string;
}

export interface LogicSection {
  name: string;
  language: string;
  code: string;
}

export interface Source {
  id: string;
  name: string;
  type: 'TWX_FILE' | 'BAW_SERVER';
  host?: string;
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
}

export interface Methodology {
  id: string;
  version: string;
  weights: string;
  active: boolean;
  createdAt: string;
}

export interface Setting {
  key: string;
  value: string;
  updatedAt: string;
}

export interface UsageRecord {
  id: string;
  userId: string;
  period: string;
  runCount: number;
  storageMb?: number;
}

export interface AuditLogEntry {
  id: number;
  actorId?: string;
  actorEmail: string;
  action: string;
  resource: string;
  detail?: string;
  ipAddress?: string;
  occurredAt: string;
}
