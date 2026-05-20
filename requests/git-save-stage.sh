#!/usr/bin/env bash

# Commands to save Stage 5 (Exact Cache) to Git

git add .
git commit -m "Stage 5: Implement Basic Exact Cache using Spring Cache and Caffeine"
git tag stage-5-exact-cache
git push
git push origin --tags
