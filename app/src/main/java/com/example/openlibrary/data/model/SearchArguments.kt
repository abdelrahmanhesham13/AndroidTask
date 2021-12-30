package com.example.openlibrary.data.model

import com.example.openlibrary.data.api.QUERY_KEY

data class SearchArguments(
    var key: String,
    var query: String,
    var page: Int = 1
)