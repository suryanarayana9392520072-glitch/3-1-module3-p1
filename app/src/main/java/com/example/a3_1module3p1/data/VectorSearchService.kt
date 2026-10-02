package com.example.a3_1module3p1.data

import kotlin.math.sqrt

/**
 * Lightweight on-device vector search. Text is converted into a hashed word vector,
 * so the app can search without an API key or internet connection.
 */
class VectorSearchService(private val repository: DocumentRepository) {
    private val indexedDocuments = linkedMapOf<String, Document>()
    private val dimension = 512

    suspend fun indexDocument(document: Document): Document {
        val embedding = vectorize(document.content)
        val indexed = document.copy(embedding = embedding)
        indexedDocuments[document.id] = indexed
        return indexed
    }

    suspend fun searchDocuments(query: String, topK: Int = 10): List<Document> {
        if (indexedDocuments.isEmpty()) {
            repository.loadDocuments().forEach { indexDocument(it) }
        }
        if (query.isBlank()) return indexedDocuments.values.toList()

        val queryVector = vectorize(query)
        return indexedDocuments.values
            .map { it to cosineSimilarity(queryVector, it.embedding ?: FloatArray(0)) }
            .filter { it.second > 0f }
            .sortedByDescending { it.second }
            .take(topK)
            .map { it.first }
    }

    fun clearIndex() {
        indexedDocuments.clear()
    }

    private fun vectorize(text: String): FloatArray {
        val vector = FloatArray(dimension)
        tokenize(text).forEach { token ->
            val index = (token.hashCode() and Int.MAX_VALUE) % dimension
            vector[index] += 1f
        }
        val norm = sqrt(vector.sumOf { (it * it).toDouble() }).toFloat()
        if (norm > 0f) for (i in vector.indices) vector[i] /= norm
        return vector
    }

    private fun tokenize(text: String): List<String> =
        text.lowercase()
            .split(Regex("[^a-z0-9]+"))
            .filter { it.length >= 2 }

    private fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        if (a.isEmpty() || b.isEmpty()) return 0f
        var dot = 0f
        var na = 0f
        var nb = 0f
        for (i in 0 until minOf(a.size, b.size)) {
            dot += a[i] * b[i]
            na += a[i] * a[i]
            nb += b[i] * b[i]
        }
        return if (na > 0f && nb > 0f) dot / (sqrt(na) * sqrt(nb)) else 0f
    }
}
