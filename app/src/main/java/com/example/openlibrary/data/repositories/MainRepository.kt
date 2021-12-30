package com.example.openlibrary.data.repositories

import com.example.openlibrary.data.api.ApiService
import com.example.openlibrary.data.api.QUERY_KEY
import com.example.openlibrary.data.model.Document
import com.example.openlibrary.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.nio.channels.spi.AbstractSelectionKey

class MainRepository(private val apiService: ApiService) {

    /**
     * get document by query type and value as flow
     *
     * @param  key   key of the query
     * @param  query   value of the query
     * @param  page   pageNumber
     * @return         flow wrapped with resource result
     */
    fun searchForDocuments(key: String, query: String, page: Int = 1) : Flow<Resource<ArrayList<Document>>> {
        return flow {
            emit(apiService.getDocuments(key, query, page))
        }.flowOn(Dispatchers.IO)
    }

}