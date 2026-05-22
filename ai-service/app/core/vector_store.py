import faiss
import numpy as np
from sentence_transformers import SentenceTransformer

class VectorStore:
    def __init__(self, dimension=384):
        self.model = SentenceTransformer('all-MiniLM-L6-v2')
        self.index = faiss.IndexFlatL2(dimension)
        self.cache_responses = []
        self.cache_prompts = []
        self.cache_providers = [] # NEW: Store provider for each entry
        self.similarity_threshold = 0.5

    def search(self, prompt: str, provider: str):
        if self.index.ntotal == 0:
            return {"hit": False}
        
        vector = self.model.encode([prompt])
        
        # Search for the top 5 nearest neighbors
        D, I = self.index.search(np.array(vector).astype('float32'), 5)
        
        # Look through results to find the first one that matches the PROVIDER
        for distance, idx in zip(D[0], I[0]):
            idx = int(idx)
            if idx == -1: continue
            
            # Check if similarity is good AND provider matches
            if distance < self.similarity_threshold and self.cache_providers[idx] == provider:
                return {
                    "hit": True,
                    "response": self.cache_responses[idx],
                    "originalPrompt": self.cache_prompts[idx]
                }
        
        return {"hit": False}

    def add(self, prompt: str, response: str, provider: str):
        vector = self.model.encode([prompt])
        self.index.add(np.array(vector).astype('float32'))
        self.cache_responses.append(response)
        self.cache_prompts.append(prompt)
        self.cache_providers.append(provider) # NEW: Store provider
