package com.example.openlibrary.utils

import android.content.Context
import android.widget.Toast
import com.example.openlibrary.data.model.Document
import com.example.openlibrary.data.network.Http
import java.lang.Exception


fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

/**
 * map Json object to model array list of documents
 * wrapped with resource to handle different states.
 * @param  response   response from api service contains request info like response code...etc
 * @return              resource object of the current status
 */
fun responseHelper(response: Http.Response) : Resource<ArrayList<Document>> {
    return when (response.status) {
        200 -> {
            return try {
                Resource.success(mapToDocument(response.asJSONObject()))
            } catch (e: Exception) {
                Resource.serverError(SERVER_ERROR)
            }
        }
        null -> Resource.internetError(response.exception?.message ?: INTERNET_CONNECTION_ERROR)
        else -> Resource.serverError(response.message ?: SERVER_ERROR)
    }
}