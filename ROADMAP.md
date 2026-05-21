# LLM Inference Optimization Server - Roadmap

This document tracks the progress of the optimization pipeline implementation.

## 🟢 Stage 0: Architecture
- [x] Define pipeline: Rate Limiter -> Semantic Cache -> Request Batcher -> LLM API
- [x] Document design patterns for each stage

## 🟢 Stage 1: Basic Spring Boot Server
- [x] Build `/hello` and `/health` endpoints.
- [x] Project structure setup.

## 🟢 Stage 2: LLM Request API
- [x] Build `POST /api/llm/generate` with basic request/response models.

## 🟢 Stage 3: Service Layer
- [x] Move logic from controller to `LlmService`.
- [x] Design pattern: Layered Architecture.

## 🟢 Stage 4: Rate Limiter
- [x] Add token bucket per user (using Bucket4j).
- [x] Design pattern: Filter/Interceptor Pattern.

## 🟢 Stage 5: Basic Exact Cache
- [x] Implement Cache-Aside Pattern for exact string matches using Spring Cache + Caffeine.

## 🟢 Stage 6: Semantic Cache
- [x] Implement Python Microservice for vector similarity search.
- [x] Integration: Java Gateway calls Python AI Service.
- [x] Design pattern: Sidecar / Microservice Pattern.

## 🟡 Stage 7: Request Batcher (Next Up)
- [ ] Coalesce compatible requests (20ms window).
- [ ] Design pattern: Producer-Consumer / Batching Pattern.

## ⚪ Stage 8: LLM API Client
- [ ] Implement OpenAI Adapter.
- [ ] Design pattern: Adapter Pattern.

## ⚪ Stage 9: Logging and Metrics
- [ ] Track latency, cost, cache hit %, and batch size.
- [ ] Design pattern: Observer / Metrics Collector.

## ⚪ Stage 10: Tests
- [ ] Unit and Integration tests.

## ⚪ Stage 11: Production Readiness
- [ ] Docker, Config, README.
