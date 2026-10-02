import os
from pathlib import Path
from dotenv import load_dotenv

BASE_DIR = Path(__file__).resolve().parent
load_dotenv(BASE_DIR / ".env")

class Config:
    HOST = os.getenv("HOST", "0.0.0.0")
    PORT = int(os.getenv("PORT", "5000"))
    DEBUG = os.getenv("DEBUG", "false").lower() == "true"
    DATA_FILE = Path(os.getenv("DATA_FILE", str(BASE_DIR / "data" / "documents.json")))
    TOP_K = int(os.getenv("TOP_K", "10"))
