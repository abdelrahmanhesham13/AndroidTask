package com.example.openlibrary.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.openlibrary.data.api.QUERY_KEY
import com.example.openlibrary.data.model.Document
import com.example.openlibrary.data.model.SearchArguments
import com.example.openlibrary.data.repositories.MainRepository
import com.example.openlibrary.di.UtilsModule
import com.example.openlibrary.utils.Resource
import com.example.openlibrary.utils.searchValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _selectedDocumentLiveData = MutableLiveData<Document>()
    val selectedDocumentLiveData: LiveData<Document> = _selectedDocumentLiveData

    private val _searchArgumentsLiveData = MutableLiveData<SearchArguments>()
    val searchArgumentsLiveData: LiveData<SearchArguments> = _searchArgumentsLiveData

    private val _documentsLiveData = MutableLiveData<Resource<ArrayList<Document>>>()
    val documentsLiveData: LiveData<Resource<ArrayList<Document>>> = _documentsLiveData

    private val queryStateFlow = MutableStateFlow("")

    var totalItems = 0
    var currentTotal: Int = 0
    var loadMore: Boolean = false

    //update live data when new arguments set to open details screen
    var selectedDocument: Document? = null
        set(value) {
            field = value
            _selectedDocumentLiveData.value = field
        }

    //update live data when new arguments set to fetch it in search fragment from selected in details screen
    var searchArguments: SearchArguments = UtilsModule.provideSearchArgument()
        set(value) {
            field = value
            _searchArgumentsLiveData.value = field
        }

    init {
        /*Debounce: Here, the debounce operator is used with a time constant. The debounce operator handles the case when the user types “a”, “ab”, “abc”, in a very short time. So, there will be so many network calls. But the user is finally interested in the result of the search “abc”. So, we must discard the results of “a” and “ab”. Ideally, there should be no network calls for “a” and “ab” as the user typed those in a very short time. So, the debounce operator comes to the rescue. The debounce will wait for the provided time for doing anything, if any other search query comes in between that time, it will ignore the previous item and start waiting for that time again with the new search query. If nothing new comes in that given constant time, it will proceed with that search query for further processing. So, debounce only emit an item, if a particular timespan has passed without it emitting another item.
        Filter: The filter operator is used to filter the unwanted string like an empty string in this case to avoid the unnecessary network call.
        DistinctUntilChanged: The distinctUntilChanged operator is used to avoid duplicate network calls. Let say the last on-going search query was “abc” and the user deleted “c” and again typed “c”. So again it’s “abc”. So if the network call is already going on with the search query “abc”, it will not make the duplicate call again with the search query “abc”. So, distinctUntilChanged suppress duplicate consecutive items emitted by the source.
        FlatMapLatest: Here, the flatMapLatest operator is used to avoid the network call results which are not needed more for displaying to the user. Let say the last search query was “ab” and there is an ongoing network call for “ab” and the user typed “abc”. Then, we are no more interested in the result of “ab”. We are only interested in the result of “abc”. So, the flatMapLatest comes to the rescue. It only provides the result for the last search query(most recent) and ignores the rest.*/
        viewModelScope.launch {
            queryStateFlow.debounce(300)
                .map { query -> query.trim() }
                .filter { query ->
                    if (query.searchValidation()) {
                        return@filter true
                    } else {
                        withContext(Dispatchers.Main) {
                            resetQuery()
                            _documentsLiveData.value = Resource.success(ArrayList())
                        }
                        return@filter false
                    }
                }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    setQuery(query)
                    resetPage()
                    mainRepository
                        .searchForDocuments(searchArguments.key, searchArguments.query)
                        .onStart {
                            withContext(Dispatchers.Main) {
                                resetQuery()
                                _documentsLiveData.value = Resource.loading()
                            }
                        }
                }
                .flowOn(Dispatchers.Default)
                .collect {
                    resetQuery()
                    _documentsLiveData.value = it
                }
        }
    }

    //reset default values
    private fun resetPage() {
        searchArguments.page = 1
        totalItems = 0
        currentTotal = 0
    }

    //calculate if there's item to be paginated so load more
    fun incrementPage() {
        if (currentTotal < totalItems) {
            searchArguments.page = searchArguments.page + 1
            loadMore = true
        } else {
            loadMore = false
        }
    }

    fun loadMoreDocuments() {
        if (loadMore)
            viewModelScope.launch {
                mainRepository
                    .searchForDocuments(
                        searchArguments.key,
                        searchArguments.query,
                        searchArguments.page
                    )
                    .onStart { _documentsLiveData.value = Resource.loadMore() }
                    .collect { _documentsLiveData.value = it }
            }

    }

    //reset key to the default query key "q" when new text typed
    private fun resetQuery() {
        searchArguments.key = QUERY_KEY
    }

    private fun setQuery(query: String) {
        searchArguments.query = query
    }

    fun sendQuery(query: String) {
        queryStateFlow.value = query
    }

}