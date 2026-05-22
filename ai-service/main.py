import uvicorn
import sys
import os
from dotenv import load_dotenv

# Add the current directory to sys.path
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

# Load variables from .env file
load_dotenv()

from app.main import app

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=5005)
