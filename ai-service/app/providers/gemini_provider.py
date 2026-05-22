import google.generativeai as genai
import os
from .base import BaseProvider

class GeminiProvider(BaseProvider):
    def __init__(self):
        genai.configure(api_key=os.environ.get("GEMINI_API_KEY"))

    def generate(self, prompt: str) -> str:
        try:
            model = genai.GenerativeModel('gemini-1.5-flash')
            response = model.generate_content(prompt)
            return response.text
        except Exception as e:
            return self.get_fallback_response(prompt, str(e))
