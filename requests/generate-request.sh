#!/usr/bin/env bash

curl -s -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -d @requests/generate-request.json
