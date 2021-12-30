package com.example.openlibrary.di

import com.example.openlibrary.data.repositories.MainRepository

object RepositoryModule {

    fun provideMainRepository() : MainRepository {
        return MainRepository(NetworkModule.provideApiHelper())
    }

}