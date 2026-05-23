import type { LlmRequest, LlmResponse, MetricsResponse } from './types';

const API_BASE_URL = 'http://localhost:8080/api';

export const generateResponse = async (request: LlmRequest): Promise<LlmResponse> => {
  const response = await fetch(`${API_BASE_URL}/llm/generate`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-User-Id': request.userId,
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    throw new Error('Failed to generate response');
  }

  return response.json();
};

export const fetchMetrics = async (): Promise<MetricsResponse> => {
  const response = await fetch(`${API_BASE_URL}/metrics`);
  
  if (!response.ok) {
    throw new Error('Failed to fetch metrics');
  }

  return response.json();
};
