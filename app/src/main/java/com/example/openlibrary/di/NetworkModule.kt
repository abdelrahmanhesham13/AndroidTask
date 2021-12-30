package com.example.openlibrary.di

import com.example.openlibrary.data.api.ApiService

object NetworkModule {

    fun provideApiHelper() : ApiService {
        return ApiService()
    }


}