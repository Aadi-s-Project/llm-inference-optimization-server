#!/usr/bin/env bash
echo "--- Testing Missing Header ---"
echo "Sending request without X-User-Id header..."
curl -i -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -d '{"userId": "user-no-header", "prompt": "This should fail"}'
echo -e "\n"
