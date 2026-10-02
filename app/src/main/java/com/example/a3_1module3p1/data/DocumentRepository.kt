package com.example.a3_1module3p1.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class Document(
    val id: String,
    val title: String,
    val content: String,
    val filePath: String,
    val embedding: FloatArray? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean =
        other is Document && id == other.id && title == other.title &&
            content == other.content && filePath == other.filePath && timestamp == other.timestamp

    override fun hashCode(): Int = id.hashCode()
}

class DocumentRepository(private val context: Context) {
    private val documents = mutableListOf<Document>()
    private val supported = setOf("txt", "md", "csv", "json", "xml", "kt", "java", "py")

    suspend fun loadDocuments(): List<Document> = withContext(Dispatchers.IO) {
        documents.clear()
        val dir = File(context.filesDir, "documents")
        if (dir.exists()) {
            dir.listFiles()?.filter { it.isFile && supported.contains(it.extension.lowercase()) }?.forEach { file ->
                runCatching {
                    documents.add(Document(file.name, file.nameWithoutExtension, file.readText(), file.absolutePath, timestamp = file.lastModified()))
                }
            }
        }
        documents.toList()
    }

    suspend fun addDocument(uri: Uri, resolver: ContentResolver): Document? = withContext(Dispatchers.IO) {
        runCatching {
            val originalName = getFileName(uri, resolver)
            val safeName = originalName.replace(Regex("[^A-Za-z0-9._-]"), "_")
            val dir = File(context.filesDir, "documents").apply { mkdirs() }
            val file = File(dir, safeName)
            resolver.openInputStream(uri)?.use { input -> FileOutputStream(file).use { output -> input.copyTo(output) } }
                ?: return@withContext null
            val content = file.readText()
            val document = Document(file.name, file.nameWithoutExtension, content, file.absolutePath, timestamp = file.lastModified())
            documents.removeAll { it.id == document.id }
            documents.add(document)
            document
        }.getOrNull()
    }

    suspend fun addDocumentFromObject(document: Document) = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, "documents").apply { mkdirs() }
        val file = File(dir, document.id)
        file.writeText(document.content)
        documents.removeAll { it.id == document.id }
        documents.add(document.copy(filePath = file.absolutePath))
    }

    private fun getFileName(uri: Uri, resolver: ContentResolver): String {
        resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && cursor.moveToFirst()) return cursor.getString(index) ?: defaultName()
        }
        return defaultName()
    }

    private fun defaultName() = "document_${System.currentTimeMillis()}.txt"
}
