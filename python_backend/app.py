from flask import Flask, jsonify, request
from flask_cors import CORS

from config import Config
from vector_service import VectorSearch

app = Flask(__name__)
CORS(app)
search_engine = VectorSearch(Config.DATA_FILE)


@app.get("/")
def root():
    return jsonify({
        "name": "Document Assistant API",
        "status": "running",
        "endpoints": ["GET /api/health", "GET /api/documents", "POST /api/index", "POST /api/search", "POST /api/clear"],
    })


@app.get("/api/health")
def health():
    return jsonify({"status": "healthy", "documents_count": len(search_engine.documents)})


@app.get("/api/documents")
def documents():
    docs = search_engine.get_documents()
    return jsonify({"documents": docs, "count": len(docs)})


@app.post("/api/index")
def index_document():
    data = request.get_json(silent=True) or {}
    result = search_engine.add_document(
        str(data.get("id", "")).strip(),
        str(data.get("content", "")),
        data.get("metadata") or {},
    )
    return jsonify(result), (200 if result["success"] else 400)


@app.post("/api/search")
def search_documents():
    data = request.get_json(silent=True) or {}
    query = str(data.get("query", "")).strip()
    if not query:
        return jsonify({"error": "query is required"}), 400
    top_k = int(data.get("top_k", Config.TOP_K))
    results = search_engine.search(query, top_k)
    return jsonify({"results": results, "count": len(results)})


@app.post("/api/clear")
def clear_documents():
    search_engine.clear_all()
    return jsonify({"success": True, "message": "All documents cleared"})


if __name__ == "__main__":
    print(f"Document Assistant API running on http://127.0.0.1:{Config.PORT}")
    app.run(host=Config.HOST, port=Config.PORT, debug=Config.DEBUG)
