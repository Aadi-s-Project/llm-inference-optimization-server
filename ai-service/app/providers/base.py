from abc import ABC, abstractmethod

class BaseProvider(ABC):
    @abstractmethod
    def generate(self, prompt: str) -> str:
        pass

    def get_fallback_response(self, prompt: str, error: str) -> str:
        import time
        ts = int(time.time())
        return f"[{self.__class__.__name__.replace('Provider','').upper()} RESPONSE {ts}] Fallback for: '{prompt}'. Error: {error}"
