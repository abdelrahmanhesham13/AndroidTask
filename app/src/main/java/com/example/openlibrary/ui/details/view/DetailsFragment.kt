package com.example.openlibrary.ui.details.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.openlibrary.data.api.AUTHOR_KEY
import com.example.openlibrary.data.api.TITLE_KEY
import com.example.openlibrary.data.model.Document
import com.example.openlibrary.databinding.FragmentDetailsBinding
import com.example.openlibrary.di.UtilsModule
import com.example.openlibrary.ui.base.ViewModelFactory
import com.example.openlibrary.ui.details.adapter.AuthorAdapter
import com.example.openlibrary.ui.details.adapter.IsbnAdapter
import com.example.openlibrary.ui.main.viewmodel.MainViewModel

class DetailsFragment : Fragment() {

    private lateinit var fragmentMainBinding: FragmentDetailsBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var isbnAdapter: IsbnAdapter
    private lateinit var authorAdapter: AuthorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMainBinding = FragmentDetailsBinding.inflate(inflater, container, false)
        return fragmentMainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBack()
        setupViewMode()
        observeDocument()
    }

    private fun attachListeners(document: Document) {
        fragmentMainBinding.titleTextView.setOnClickListener {
            //send selected title to search screen that will fetch query
            viewModel.searchArguments = UtilsModule.provideSearchArgument(TITLE_KEY, document.title)
            requireActivity().onBackPressed()
        }
    }

    private fun setupBack() {
        (activity as AppCompatActivity?)?.let {
            it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun observeDocument() {
        //bind selected document data
        viewModel.selectedDocumentLiveData.observe(viewLifecycleOwner) {
            requireActivity().title = it.title
            fragmentMainBinding.titleTextView.text = it.title
            initIsbnAdapter(it)
            initAuthorAdapter(it)
            attachListeners(it)
        }
    }

    private fun initAuthorAdapter(document: Document) {
        authorAdapter = AuthorAdapter(document.authors) {
            //send selected author to search screen that will fetch query
            viewModel.searchArguments = UtilsModule.provideSearchArgument(AUTHOR_KEY, it)
            requireActivity().onBackPressed()
        }
        fragmentMainBinding.authorsRecyclerView.adapter = authorAdapter
        if  (document.authors.isEmpty()) {
            fragmentMainBinding.authorsRecyclerView.visibility = View.GONE
            fragmentMainBinding.authorsCardView.visibility = View.GONE
            fragmentMainBinding.authorsTextView.visibility = View.GONE
        }
    }

    private fun initIsbnAdapter(document: Document) {
        isbnAdapter = IsbnAdapter(document.isbn)
        fragmentMainBinding.isbnRecyclerView.adapter = isbnAdapter
        if  (document.isbn.isEmpty()) {
            fragmentMainBinding.isbnRecyclerView.visibility = View.GONE
            fragmentMainBinding.isbnTextView.visibility = View.GONE
        }
    }

    private fun setupViewMode() {
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory())[MainViewModel::class.java]
    }

    companion object {
        @JvmStatic
        fun newInstance() = DetailsFragment()
    }
}