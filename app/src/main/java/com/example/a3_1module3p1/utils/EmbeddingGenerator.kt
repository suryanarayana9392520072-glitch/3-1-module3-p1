package com.example.a3_1module3p1.utils

/**
 * Kept as a small compatibility helper for projects that expect an embedding generator.
 * The app itself uses the on-device vector search service and does not require an API key.
 */
class EmbeddingGenerator {
    fun generateEmbedding(text: String): FloatArray {
        val vector = FloatArray(128)
        text.lowercase().split(Regex("[^a-z0-9]+"))
            .filter { it.isNotBlank() }
            .forEach { token ->
                val index = (token.hashCode() and Int.MAX_VALUE) % vector.size
                vector[index] += 1f
            }
        return vector
    }
}
