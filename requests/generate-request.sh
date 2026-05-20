#!/usr/bin/env bash

curl -s -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -H "X-User-Id: test-user-1" \
  -d @requests/generate-request.json
