#!/usr/bin/env bash
echo "--- Testing Multi-Provider Support ---"

echo "Step 1: Request via OpenAI"
curl -s -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: multi-user" \
  -d '{"userId": "multi-user", "prompt": "What is OpenAI?", "provider": "openai"}' | jq .

echo -e "\nStep 2: Request via Gemini"
curl -s -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: multi-user" \
  -d '{"userId": "multi-user", "prompt": "What is Google Gemini?", "provider": "gemini"}' | jq .

echo -e "\nStep 3: Request via Claude"
curl -s -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: multi-user" \
  -d '{"userId": "multi-user", "prompt": "What is Anthropic Claude?", "provider": "claude"}' | jq .
echo -e "\n"
