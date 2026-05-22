from .openai_provider import OpenAIProvider
from .gemini_provider import GeminiProvider
from .claude_provider import ClaudeProvider
from .base import BaseProvider

class ProviderFactory:
    _providers = {
        "openai": OpenAIProvider(),
        "gemini": GeminiProvider(),
        "claude": ClaudeProvider()
    }

    @staticmethod
    def get_provider(name: str) -> BaseProvider:
        # Default to OpenAI if not found
        return ProviderFactory._providers.get(name.lower(), ProviderFactory._providers["openai"])
