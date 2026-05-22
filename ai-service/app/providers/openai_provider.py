from openai import OpenAI
from .base import BaseProvider

class OpenAIProvider(BaseProvider):
    def __init__(self):
        self.client = OpenAI()

    def generate(self, prompt: str) -> str:
        try:
            completion = self.client.chat.completions.create(
                model="gpt-3.5-turbo",
                messages=[{"role": "user", "content": prompt}]
            )
            return completion.choices[0].message.content
        except Exception as e:
            return self.get_fallback_response(prompt, str(e))
