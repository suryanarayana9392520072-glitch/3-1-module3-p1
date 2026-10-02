package com.example.a3_1module3p1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a3_1module3p1.data.DocumentRepository
import com.example.a3_1module3p1.data.VectorSearchService
import com.example.a3_1module3p1.databinding.ActivityMainBinding
import com.example.a3_1module3p1.ui.SearchFragment
import com.example.a3_1module3p1.ui.SearchViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: SearchViewModel
    private var onUploadClickListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val repository = DocumentRepository(this)
        val searchService = VectorSearchService(repository)
        viewModel = SearchViewModel(repository, searchService)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SearchFragment().apply { setViewModel(viewModel) })
            .commit()

        binding.fabUpload.setOnClickListener { onUploadClickListener?.invoke() }
    }

    fun setOnUploadClickListener(listener: () -> Unit) {
        onUploadClickListener = listener
    }

    fun getViewModel(): SearchViewModel = viewModel
}
