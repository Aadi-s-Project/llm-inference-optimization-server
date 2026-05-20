#!/usr/bin/env bash
echo "--- Testing Successful Request ---"
echo "Sending request with valid X-User-Id header..."
curl -i -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-success-test" \
  -d '{"userId": "user-success-test", "prompt": "Explain Spring Boot"}'
echo -e "\n"
