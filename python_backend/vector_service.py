from __future__ import annotations

import json
from pathlib import Path
from threading import Lock
from typing import Any

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity


class VectorSearch:
    """Persistent TF-IDF vector search service.

    This version is self-contained and does not require an external API key.
    """

    def __init__(self, data_file: Path):
        self.data_file = Path(data_file)
        self.data_file.parent.mkdir(parents=True, exist_ok=True)
        self.lock = Lock()
        self.documents: list[dict[str, Any]] = []
        self._load()

    def _load(self) -> None:
        if self.data_file.exists():
            try:
                self.documents = json.loads(self.data_file.read_text(encoding="utf-8"))
            except (json.JSONDecodeError, OSError):
                self.documents = []

    def _save(self) -> None:
        self.data_file.write_text(
            json.dumps(self.documents, ensure_ascii=False, indent=2), encoding="utf-8"
        )

    def add_document(self, doc_id: str, content: str, metadata: dict | None = None) -> dict:
        if not doc_id or not content.strip():
            return {"success": False, "error": "id and content are required"}
        item = {"id": doc_id, "content": content[:50000], "metadata": metadata or {}}
        with self.lock:
            self.documents = [d for d in self.documents if d.get("id") != doc_id]
            self.documents.append(item)
            self._save()
        return {"success": True, "doc_id": doc_id, "count": len(self.documents)}

    def search(self, query: str, top_k: int = 10) -> list[dict]:
        if not query.strip() or not self.documents:
            return []
        corpus = [d.get("content", "") for d in self.documents]
        vectorizer = TfidfVectorizer(stop_words="english")
        try:
            matrix = vectorizer.fit_transform(corpus + [query])
        except ValueError:
            return []
        scores = cosine_similarity(matrix[-1], matrix[:-1]).ravel()
        order = scores.argsort()[::-1][: max(1, min(top_k, 50))]
        return [
            {"document": self.documents[i], "similarity": round(float(scores[i]), 6)}
            for i in order
            if scores[i] > 0
        ]

    def get_documents(self) -> list[dict]:
        return list(self.documents)

    def clear_all(self) -> None:
        with self.lock:
            self.documents.clear()
            self._save()
