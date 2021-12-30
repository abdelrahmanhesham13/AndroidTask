package com.example.openlibrary.utils

import com.example.openlibrary.data.api.NetworkBuilder
import com.example.openlibrary.data.model.Document
import com.example.openlibrary.data.model.Isbn
import org.json.JSONObject
import java.lang.Exception

private const val BASE_IMAGE_URL = "https://covers.openlibrary.org/b/isbn/"
private const val IMAGE_EXTENSION = ".jpg"
private const val IMAGE_SIZE = "L"

/**
 * map Json object to model array list of documents.
 *
 * @param  jsonObject   jsonObject fetched from api service
 * @return              the post-incremented value
 */
@Throws(Exception::class)
fun mapToDocument(jsonObject: JSONObject?): ArrayList<Document> {
    val documents = ArrayList<Document>()
    val error = jsonObject?.optString("error")
    if (error.isNullOrEmpty().not()) {
        throw Exception(error)
    }
    val documentsJsonArray = jsonObject!!.getJSONArray("docs")
    for (i in 0 until documentsJsonArray.length()) {
        val documentJsonObject = documentsJsonArray.getJSONObject(i)
        val authorsJsonArray = documentJsonObject.optJSONArray("author_name")
        val authors = ArrayList<String>()
        authorsJsonArray?.let {
            for (j in 0 until authorsJsonArray.length()) {
                authors.add(authorsJsonArray[j] as String)
            }
        }
        val isbnJsonArray = documentJsonObject.optJSONArray("isbn")
        val isbns = ArrayList<Isbn>()
        isbnJsonArray?.let {
            for (j in 0 until isbnJsonArray.length()) {
                val image =
                    BASE_IMAGE_URL + isbnJsonArray[j] as String + "-" + IMAGE_SIZE + IMAGE_EXTENSION
                val isbn = Isbn(isbnJsonArray[j] as String, image)
                isbns.add(isbn)
            }
        }
        val document = Document(documentJsonObject.getString("title"), authors, isbns, jsonObject.optInt("numFound"))
        documents.add(document)
    }
    return documents
}