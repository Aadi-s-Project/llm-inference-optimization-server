import anthropic
import os
from .base import BaseProvider

class ClaudeProvider(BaseProvider):
    def __init__(self):
        self.client = anthropic.Anthropic(api_key=os.environ.get("ANTHROPIC_API_KEY", "mock-key"))

    def generate(self, prompt: str) -> str:
        try:
            message = self.client.messages.create(
                model="claude-3-opus-20240229",
                max_tokens=1000,
                messages=[{"role": "user", "content": prompt}]
            )
            return message.content[0].text
        except Exception as e:
            return self.get_fallback_response(prompt, str(e))
