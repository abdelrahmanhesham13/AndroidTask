package com.example.openlibrary.ui.search.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.openlibrary.data.model.Document
import com.example.openlibrary.databinding.FragmentSearchBinding
import com.example.openlibrary.ui.base.ViewModelFactory
import com.example.openlibrary.ui.search.adapter.DocumentAdapter
import com.example.openlibrary.ui.main.viewmodel.MainViewModel
import com.example.openlibrary.utils.*

class SearchFragment : Fragment(), TextWatcher {

    private lateinit var viewModel: MainViewModel
    private lateinit var fragmentSearchBinding: FragmentSearchBinding
    private lateinit var documentAdapter: DocumentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return fragmentSearchBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachTextWatcher()
        setupViewModel()
        initAdapterAndRecycler()
        observe()
    }

    private fun attachTextWatcher() {
        fragmentSearchBinding.searchEditText.addTextChangedListener(this)
    }

    private fun observe() {
        /* observe changes of change arguments to
        *load new search query from the details screen*/
        viewModel.searchArgumentsLiveData.observe(viewLifecycleOwner) {
            it?.let { searchArguments ->
                fragmentSearchBinding.searchEditText.setText(searchArguments.query, TextView.BufferType.EDITABLE)
            }
        }

        //observe documents api response
        viewModel.documentsLiveData.observe(viewLifecycleOwner) {
            handleStatus(it)
        }
    }

    private fun initAdapterAndRecycler() {
        documentAdapter = DocumentAdapter ({
            viewModel.selectedDocument = it
        }, {
            viewModel.loadMoreDocuments()
        })
        fragmentSearchBinding.documentsRecyclerView.adapter = documentAdapter
    }

    private fun handleStatus(it: Resource<ArrayList<Document>>) {
        when(it.status) {
            Status.SERVER_ERROR -> {
                fragmentSearchBinding.progressBar.hide()
                showToast(requireContext(), it.message!!)
            }
            Status.SUCCESS -> {
                fragmentSearchBinding.progressBar.hide()
                //skip all data when search query is empty and clear adapter
                //else add data to adapter
                if (fragmentSearchBinding.searchEditText.text.toString().isNotEmpty()) {
                    documentAdapter.addDocuments(it.data!!)
                    if (it.data.isNotEmpty()) {
                        //send total items to viewModel for pagination
                        viewModel.totalItems = it.data[0].totalDocuments
                    }
                    //send current items to viewModel for pagination
                    viewModel.currentTotal = documentAdapter.mDocuments.size
                } else {
                    documentAdapter.clearDocuments()
                }
                viewModel.incrementPage()
            }
            Status.INTERNET_ERROR -> {
                fragmentSearchBinding.progressBar.hide()
                showToast(requireContext(), it.message!!)
            }
            Status.LOADING -> {
                fragmentSearchBinding.progressBar.show()
                documentAdapter.clearDocuments()
            }
            Status.LOAD_MORE -> {
                fragmentSearchBinding.progressBar.show()
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory())[MainViewModel::class.java]
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        //show progress bar according string isEmpty
        if (s.toString().searchValidation()) {
            fragmentSearchBinding.progressBar.show()
        } else {
            fragmentSearchBinding.progressBar.hide()
        }
        //send query to state flow
        viewModel.sendQuery(s.toString())
    }
}