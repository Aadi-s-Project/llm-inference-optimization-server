#!/usr/bin/env bash
echo "--- Testing Request Batcher ---"
echo "Sending 5 requests simultaneously..."

# We use different prompts to ensure they all miss the Exact/Semantic cache 
# and end up in the batcher.

for i in {1..5}; do
  (
    curl -s -X POST http://localhost:8080/api/llm/generate \
      -H "Content-Type: application/json" \
      -H "X-User-Id: batch-user" \
      -d "{\"userId\": \"batch-user\", \"prompt\": \"Batch prompt $i\"}" | jq .
  ) &
done

wait
echo -e "\nCheck the server logs to see the batch processing message!"
echo -e "\n"
