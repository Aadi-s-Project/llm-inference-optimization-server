# LLM Inference Optimization Server

A high-performance Java Spring Boot gateway that sits between users and LLM APIs (OpenAI, Gemini, Claude). It reduces latency and cost using a multi-layer optimization pipeline.

## 🚀 The Optimization Pipeline
1.  **Rate Limiter**: Token Bucket algorithm (Bucket4j) to prevent API abuse.
2.  **Exact Cache**: High-speed string matching (Caffeine) for identical prompts.
3.  **Semantic Cache**: AI-powered vector similarity search (FAISS + Embeddings) to match different wordings with the same meaning.
4.  **Request Batcher**: Producer-Consumer grouping (50ms window) to coalesce multiple requests into a single batch.
5.  **Multi-AI Gateway**: SOLID Strategy pattern to route requests to OpenAI, Gemini, or Claude.
6.  **Observability**: Real-time performance dashboard tracking latency and cost savings.

## 🏗️ Architecture
- **Gateway**: Java 21 / Spring Boot 3.5
- **AI Sidecar**: Python 3.12 / FastAPI / Sentence-Transformers
- **Vector DB**: In-memory FAISS
- **Design Patterns**: Strategy, Adapter, Producer-Consumer, Interceptor, Sidecar Proxy.

## 🛠️ Getting Started

### 1. Prerequisites
- Java 21
- Python 3.12+
- Maven

### 2. Configure Keys
Create a `.env` file in the `ai-service/` directory:
```text
OPENAI_API_KEY=sk-...
GEMINI_API_KEY=...
ANTHROPIC_API_KEY=...
```

### 3. Start the AI Sidecar (Python)
```bash
cd ai-service
pip install -r requirements.txt
python main.py
```

### 4. Start the Gateway (Java)
```bash
# In the root directory
mvn spring-boot:run
```

## 🧪 Verification
Use the provided scripts in the `requests/` folder:
- `./requests/test-multi-provider.sh`: Test routing and caching across OpenAI, Gemini, and Claude.
- `./requests/test-batching.sh`: Simulate simultaneous traffic to see the Request Batcher in action.
- `mvn test`: Run the full automated Unit and Integration test suite.

## 📊 Monitoring
Check the server console after any request to see the **Performance Summary**:
```text
=========================================
      LLM SERVER PERFORMANCE SUMMARY      
=========================================
Total Requests:         25
Overall Cache Hit Rate: 72.00%
Average Latency:        114.25ms
Estimated Savings ($):  0.3600
=========================================
```
