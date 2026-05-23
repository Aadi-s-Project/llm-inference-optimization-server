export interface LlmRequest {
  userId: string;
  prompt: string;
  provider: string;
}

export interface LlmResponse {
  answer: string;
}

export interface MetricsResponse {
  totalRequests: number;
  exactCacheHits: number;
  semanticCacheHits: number;
  averageLatency: number;
  cacheHitRate: number;
  estimatedSavings: number;
}
