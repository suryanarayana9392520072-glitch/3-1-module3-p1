package project.handson1.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import project.handson1.MainActivity
import project.handson1.data.Document
import project.handson1.databinding.FragmentSearchBinding
import project.handson1.utils.FilePickerHelper

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DocumentListAdapter
    private lateinit var picker: FilePickerHelper
    private var viewModel: SearchViewModel? = null

    fun setViewModel(vm: SearchViewModel) { viewModel = vm }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupPicker()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { search(query); return true }
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isBlank()) loadDocuments() else if (newText.length >= 2) search(newText)
                return true
            }
        })
        loadDocuments()
    }

    private fun setupRecyclerView() {
        adapter = DocumentListAdapter { showDetails(it) }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupPicker() {
        val activity = requireActivity() as MainActivity
        picker = FilePickerHelper(activity) { uri -> if (uri != null) upload(uri) }
        activity.setOnUploadClickListener { picker.pickDocument() }
    }

    private fun upload(uri: Uri) {
        lifecycleScope.launch {
            setLoading(true)
            val result = viewModel?.addDocument(uri, requireContext().contentResolver)
            setLoading(false)
            if (result != null) {
                Toast.makeText(requireContext(), "Added: ${result.title}", Toast.LENGTH_SHORT).show()
                loadDocuments()
            } else Toast.makeText(requireContext(), "Could not read this file. Use TXT, MD, CSV, JSON, XML, KT, JAVA or PY.", Toast.LENGTH_LONG).show()
        }
    }

    private fun search(query: String) {
        lifecycleScope.launch {
            setLoading(true)
            val results = viewModel?.searchDocuments(query).orEmpty()
            updateUI(results)
            setLoading(false)
        }
    }

    private fun loadDocuments() {
        lifecycleScope.launch {
            setLoading(true)
            updateUI(viewModel?.getDocuments().orEmpty())
            setLoading(false)
        }
    }

    private fun updateUI(items: List<Document>) {
        adapter.submitList(items)
        binding.tvResultsCount.text = getString(project.handson1.R.string.results_count, items.size)
        binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showDetails(document: Document) {
        val preview = document.content.take(400).replace("\n", " ")
        Toast.makeText(requireContext(), preview.ifBlank { document.title }, Toast.LENGTH_LONG).show()
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
