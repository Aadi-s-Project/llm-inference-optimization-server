from fastapi import FastAPI
from .models import CacheRequest
from .core.vector_store import VectorStore
from .providers.factory import ProviderFactory

app = FastAPI()
vector_store = VectorStore()

@app.post("/search")
async def search(request: CacheRequest):
    return vector_store.search(request.prompt, request.provider)

@app.post("/add")
async def add(request: CacheRequest):
    vector_store.add(request.prompt, request.response, request.provider)
    return {"status": "added"}

@app.post("/generate")
async def generate(request: CacheRequest):
    provider = ProviderFactory.get_provider(request.provider)
    answer = provider.generate(request.prompt)
    return {"answer": answer}

@app.get("/health")
async def health():
    return {"status": "healthy"}

