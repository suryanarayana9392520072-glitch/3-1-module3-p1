package com.example.a3_1module3p1.ui

import androidx.lifecycle.ViewModel
import com.example.a3_1module3p1.data.Document
import com.example.a3_1module3p1.data.DocumentRepository
import com.example.a3_1module3p1.data.VectorSearchService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchViewModel(
    private val repository: DocumentRepository,
    private val vectorSearchService: VectorSearchService
) : ViewModel() {
    private val _documents = MutableStateFlow<List<Document>>(emptyList())
    val documents: StateFlow<List<Document>> = _documents

    private val _searchResults = MutableStateFlow<List<Document>>(emptyList())
    val searchResults: StateFlow<List<Document>> = _searchResults

    suspend fun getDocuments(): List<Document> {
        val docs = repository.loadDocuments()
        _documents.value = docs
        return docs
    }

    suspend fun indexDocument(document: Document) {
        repository.addDocumentFromObject(document)
        vectorSearchService.indexDocument(document)
        _documents.value = repository.loadDocuments()
    }

    suspend fun addDocument(uri: android.net.Uri, resolver: android.content.ContentResolver): Document? {
        val document = repository.addDocument(uri, resolver) ?: return null
        vectorSearchService.indexDocument(document)
        _documents.value = repository.loadDocuments()
        return document
    }

    suspend fun searchDocuments(query: String): List<Document> {
        val results = vectorSearchService.searchDocuments(query)
        _searchResults.value = results
        return results
    }
}
