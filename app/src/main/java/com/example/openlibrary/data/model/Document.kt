package com.example.openlibrary.data.model

import org.json.JSONObject
import java.lang.StringBuilder

data class Document(
    val title: String,
    val authors: ArrayList<String>,
    val isbn: ArrayList<Isbn>,
    val totalDocuments: Int) {

    fun getAuthorsString() : String {
        val authorsStringBuilder = StringBuilder("")
        for (author in authors) {
            authorsStringBuilder.append("$author, ")
        }
        return authorsStringBuilder.toString()
    }

}