import { useEffect, useRef } from 'react';
import { useAuthStore } from '../store/authStore';

interface SSEOptions {
  onMessage?: (data: string) => void;
  onError?: (err: Event) => void;
  enabled?: boolean;
}

export function useSSE(url: string, options: SSEOptions = {}) {
  const { onMessage, onError, enabled = true } = options;
  const token = useAuthStore((s) => s.token);
  const sourceRef = useRef<EventSource | null>(null);

  useEffect(() => {
    if (!enabled || !url) return;

    const fullUrl = token ? `${url}?token=${encodeURIComponent(token)}` : url;
    const source = new EventSource(fullUrl);
    sourceRef.current = source;

    source.onmessage = (e) => onMessage?.(e.data);
    source.onerror = (e) => onError?.(e);

    return () => {
      source.close();
      sourceRef.current = null;
    };
  }, [url, token, enabled, onMessage, onError]);

  return sourceRef;
}
