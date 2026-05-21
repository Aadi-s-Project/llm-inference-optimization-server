from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
import faiss
import numpy as np
import os

app = FastAPI()

# 1. Load the embedding model (runs on CPU)
model = SentenceTransformer('all-MiniLM-L6-v2')

# 2. Initialize FAISS index for vector similarity search
# all-MiniLM-L6-v2 produces vectors of dimension 384
dimension = 384
index = faiss.IndexFlatL2(dimension)

# 3. Simple in-memory storage for the responses (mapped to index IDs)
cache_responses = []
cache_prompts = []

# Threshold for similarity (L2 distance). 
# Note: Lower distance = more similar. 
# For Cosine Similarity we'd normalize, but L2 on normalized vectors works similarly.
SIMILARITY_THRESHOLD = 0.5 

class CacheRequest(BaseModel):
    prompt: str
    response: str = None

@app.post("/search")
async def search_cache(request: CacheRequest):
    if index.ntotal == 0:
        return {"hit": False}

    # Convert prompt to vector
    query_vector = model.encode([request.prompt])
    
    # Search the index
    # D: distances, I: indices
    D, I = index.search(np.array(query_vector).astype('float32'), 1)
    
    distance = float(D[0][0])
    idx = int(I[0][0])

    if idx != -1 and distance < SIMILARITY_THRESHOLD:
        return {
            "hit": True,
            "response": cache_responses[idx],
            "originalPrompt": cache_prompts[idx],
            "distance": distance
        }
    
    return {"hit": False}

@app.post("/add")
async def add_to_cache(request: CacheRequest):
    if not request.response:
        raise HTTPException(status_code=400, detail="Response is required to add to cache")

    # Convert prompt to vector
    vector = model.encode([request.prompt])
    
    # Add to FAISS index
    index.add(np.array(vector).astype('float32'))
    
    # Store the response and prompt
    cache_responses.append(request.response)
    cache_prompts.append(request.prompt)
    
    return {"status": "added", "count": index.ntotal}

@app.get("/health")
async def health():
    return {"status": "healthy", "vectors_cached": index.ntotal}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=5005)
