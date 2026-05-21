#!/usr/bin/env bash
echo "--- Testing Semantic Cache ---"

echo "Step 1: Ask 'What is Spring Boot?' (Fresh Request)"
curl -s -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: sem-user" \
  -d '{"userId": "sem-user", "prompt": "What is Spring Boot?"}' | jq .

echo -e "\nStep 2: Ask 'Can you explain Spring Boot?' (Slightly different wording - Should be SEMANTIC HIT)"
curl -s -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: sem-user" \
  -d '{"userId": "sem-user", "prompt": "Can you explain Spring Boot?"}' | jq .

echo -e "\nStep 3: Ask 'What is the weather today?' (Totally different - Should be SEMANTIC MISS)"
curl -s -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: sem-user" \
  -d '{"userId": "sem-user", "prompt": "What is the weather today?"}' | jq .
echo -e "\n"
