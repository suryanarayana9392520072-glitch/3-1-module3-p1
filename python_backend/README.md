# Python Backend

A small Flask API for indexing and searching documents with TF-IDF + cosine similarity.
No OpenAI/Grok API key is required.

## Windows

```powershell
cd python_backend
py -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
copy .env.example .env
python app.py
```

## Test

Open `http://127.0.0.1:5000/api/health` in a browser.

Index:

```json
POST /api/index
{
  "id": "notes1",
  "content": "Operating systems manage processes and memory.",
  "metadata": {"title": "OS Notes"}
}
```

Search:

```json
POST /api/search
{"query":"process memory", "top_k":5}
```
