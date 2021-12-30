package com.example.openlibrary.utils

import android.view.View
import android.widget.ProgressBar

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun String.searchValidation(): Boolean {
    return trim().isNotEmpty() && trim().length > 3
}