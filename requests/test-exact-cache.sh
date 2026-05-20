#!/usr/bin/env bash
echo "--- Testing Exact Cache ---"

echo "Step 1: First request (should be a CACHE MISS)"
curl -s -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: cache-user" \
  -d '{"userId": "cache-user", "prompt": "What is Java?"}' | jq .

echo -e "\nStep 2: Identical request (should be a CACHE HIT - check server logs)"
curl -s -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: cache-user" \
  -d '{"userId": "cache-user", "prompt": "What is Java?"}' | jq .

echo -e "\nStep 3: Different request (should be a CACHE MISS)"
curl -s -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: cache-user" \
  -d '{"userId": "cache-user", "prompt": "What is Python?"}' | jq .
echo -e "\n"
