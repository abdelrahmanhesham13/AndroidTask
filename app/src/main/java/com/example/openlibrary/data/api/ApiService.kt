package com.example.openlibrary.data.api

import com.example.openlibrary.data.model.Document
import com.example.openlibrary.data.network.Http
import com.example.openlibrary.utils.*
import java.lang.Exception

const val QUERY_KEY = "q"
const val TITLE_KEY = "title"
const val AUTHOR_KEY = "author"
const val PAGE_KEY = "page"

class ApiService {
    /**
     * get document by query type and value as resource
     *
     * @param  key   key of the query
     * @param  query   value of the query
     * @param  page   pageNumber
     * @return         resource wrapped with status and data or error
     */
    fun getDocuments(key: String, query: String, page: Int = 1) : Resource<ArrayList<Document>> {
        val map = HashMap<String, String>()
        map[key] = query
        map[PAGE_KEY] = page.toString()
        return responseHelper(NetworkBuilder
            .getRequest(map, Http.RequestMethod.GET)
            .execute())
    }

}