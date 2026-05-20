#!/usr/bin/env bash

# Commands to save Stage 4 (Rate Limiter) to Git
# We skip Stage 5 as it was reverted.

git add .
git commit -m "Stage 4: Implement Token Bucket Rate Limiter using Bucket4j and Interceptor pattern"
git tag stage-4-rate-limiter
git push
git push origin --tags
