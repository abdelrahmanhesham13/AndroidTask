package com.example.openlibrary.data.network

import android.util.Log

internal object HttpLogger {
    var isLogsRequired = true
    fun d(tag: String?, message: String?) {
        if (isLogsRequired) Log.d(tag, message!!)
    }
}