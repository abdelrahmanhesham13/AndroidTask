package com.example.openlibrary.data.api

import com.example.openlibrary.BuildConfig
import com.example.openlibrary.data.network.Http

object NetworkBuilder {

    private const val BASE_URL = "https://openlibrary.org/search.json"

    /**
     * builder for network client
     *
     * @param  queryMap   url query map
     * @param  method   request method [Http.RequestMethod.GET], [Http.RequestMethod.POST]...etc
     * @return         well prepared request object to be executed
     */
    fun getRequest(queryMap: HashMap<String, String>, method: Http.RequestMethod): Http.Request {
        return Http
            .Request(method.name)
            .url(BASE_URL)
            .query(queryMap)
            .enableLog(BuildConfig.DEBUG)
    }
}