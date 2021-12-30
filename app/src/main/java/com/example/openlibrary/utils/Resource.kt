package com.example.openlibrary.utils

const val INTERNET_CONNECTION_ERROR = "Internet connection error"
const val SERVER_ERROR = "Server error"


/**
 * resource class to wrap data with status and message for different statues.
 *
 * @param  status   the status of the current data
 * @param  message   message in case of error happened
 * @param  data   the requested api data
 * @return              resource object appended with status
 */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Resource<T> = Resource(status = Status.SUCCESS, data = data, message = null)

        fun <T> internetError(message: String): Resource<T> =
            Resource(status = Status.INTERNET_ERROR, data = null, message = message)

        fun <T> serverError(message: String): Resource<T> =
            Resource(status = Status.SERVER_ERROR, data = null, message = message)

        fun <T> loading(): Resource<T> =
            Resource(status = Status.LOADING, data = null, message = null)

        fun <T> loadMore(): Resource<T> =
            Resource(status = Status.LOAD_MORE, data = null, message = null)
    }
}