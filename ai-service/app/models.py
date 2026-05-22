from pydantic import BaseModel
from typing import Optional

class CacheRequest(BaseModel):
    prompt: str
    response: Optional[str] = None
    provider: Optional[str] = "openai"
