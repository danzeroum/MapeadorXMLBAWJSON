import React, { useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
  ReactFlow,
  Background,
  Controls,
  MiniMap,
  useNodesState,
  useEdgesState,
  ReactFlowProvider,
} from '@xyflow/react';
import type { Node, Edge } from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { useProcessGraph } from '../../hooks/useReport';
import { Card } from '../../components/ui/Card';
import { Spinner } from '../../components/ui/Spinner';
import { Button } from '../../components/ui/Button';
import type { GraphNode, ProcessGraph } from '../../types/api';

function nodeStyle(type: GraphNode['type']): React.CSSProperties {
  switch (type) {
    case 'gateway':
      return {
        background: 'var(--pv-warning-bg)',
        border: '2px solid var(--pv-warning)',
        borderRadius: 4,
        width: 48,
        height: 48,
      };
    case 'event':
      return {
        background: 'var(--pv-success-bg)',
        border: '2px solid var(--pv-success)',
        borderRadius: '50%',
        width: 48,
        height: 48,
      };
    default:
      return {
        background: '#e8f0fe',
        border: '2px solid var(--pv-accent)',
        borderRadius: 6,
        minWidth: 120,
        padding: '6px 12px',
      };
  }
}

interface FlowCanvasProps {
  data: ProcessGraph;
  onSelectNode: (node: GraphNode | null) => void;
}

function FlowCanvas({ data, onSelectNode }: FlowCanvasProps) {
  const [nodes, setNodes, onNodesChange] = useNodesState<Node>([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState<Edge>([]);

  useEffect(() => {
    const rfNodes: Node[] = data.nodes.map((n, i) => ({
      id: n.id,
      position: { x: n.x ?? (i % 5) * 200, y: n.y ?? Math.floor(i / 5) * 150 },
      data: { label: n.label },
      style: nodeStyle(n.type),
    }));
    const rfEdges: Edge[] = data.edges.map((e) => ({
      id: e.id,
      source: e.source,
      target: e.target,
      label: e.label,
      style: { stroke: 'var(--pv-text-secondary)' },
    }));
    setNodes(rfNodes);
    setEdges(rfEdges);
  }, [data, setNodes, setEdges]);

  const onNodeClick = useCallback(
    (_: React.MouseEvent, node: Node) => {
      const original = data.nodes.find((n) => n.id === node.id) ?? null;
      onSelectNode(original);
    },
    [data, onSelectNode]
  );

  return (
    <ReactFlow
      nodes={nodes}
      edges={edges}
      onNodesChange={onNodesChange}
      onEdgesChange={onEdgesChange}
      onNodeClick={onNodeClick}
      fitView
    >
      <Background />
      <Controls />
      <MiniMap />
    </ReactFlow>
  );
}

export const GraphPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const { data, isLoading, isError } = useProcessGraph(id!);
  const [selectedNode, setSelectedNode] = useState<GraphNode | null>(null);

  if (isLoading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', paddingTop: 80 }}>
        <Spinner size={40} />
      </div>
    );
  }

  if (isError || !data) {
    return <Card><p style={{ color: 'var(--pv-error)' }}>{t('common.error')}</p></Card>;
  }

  return (
    <div>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 16 }}>{t('graph.title')}</h1>
      <div style={{ display: 'flex', gap: 16, height: 'calc(100vh - 200px)' }}>
        <div
          style={{
            flex: 1,
            background: 'var(--pv-surface)',
            borderRadius: 'var(--pv-radius)',
            border: '1px solid var(--pv-border)',
            overflow: 'hidden',
          }}
        >
          <ReactFlowProvider>
            <FlowCanvas data={data} onSelectNode={setSelectedNode} />
          </ReactFlowProvider>
        </div>

        {selectedNode && (
          <div style={{ width: 280, flexShrink: 0 }}>
            <Card title={t('graph.nodeDetails')}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
                <InfoRow label="ID" value={selectedNode.id} />
                <InfoRow label="Label" value={selectedNode.label} />
                <InfoRow label="Tipo" value={selectedNode.type} />
                {selectedNode.properties && Object.keys(selectedNode.properties).length > 0 && (
                  <div>
                    <div
                      style={{
                        fontSize: 12,
                        fontWeight: 600,
                        color: 'var(--pv-text-secondary)',
                        marginBottom: 8,
                      }}
                    >
                      {t('graph.properties')}
                    </div>
                    {Object.entries(selectedNode.properties).map(([k, v]) => (
                      <InfoRow key={k} label={k} value={v} />
                    ))}
                  </div>
                )}
              </div>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => setSelectedNode(null)}
                style={{ marginTop: 16 }}
              >
                {t('graph.close')}
              </Button>
            </Card>
          </div>
        )}
      </div>
    </div>
  );
};

function InfoRow({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <div style={{ fontSize: 11, color: 'var(--pv-text-secondary)', marginBottom: 2 }}>{label}</div>
      <div style={{ fontSize: 13, fontWeight: 500, wordBreak: 'break-all' }}>{value}</div>
    </div>
  );
}
