#!/usr/bin/env bash
echo "--- Testing Rate Limit Burst ---"
echo "Sending 12 requests in a row (Limit is 10)..."
for i in {1..12}; do
  printf "Request %2d: " $i
  curl -s -o /dev/null -w "HTTP Status: %{http_code}\n" -X POST http://localhost:8080/api/llm/generate \
       -H "Content-Type: application/json" \
       -H "X-User-Id: user-burst-test" \
       -d '{"userId": "user-burst-test", "prompt": "Hello"}';
done
echo -e "\n"
