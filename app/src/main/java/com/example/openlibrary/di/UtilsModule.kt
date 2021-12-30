package com.example.openlibrary.di

import androidx.fragment.app.FragmentManager
import com.example.openlibrary.data.api.QUERY_KEY
import com.example.openlibrary.data.model.SearchArguments
import com.example.openlibrary.data.repositories.MainRepository
import com.example.openlibrary.utils.navigation.NavigationManager

object UtilsModule {

    fun provideNavigationManager(fragmentManager: FragmentManager, onStackChanged: () -> Unit) : NavigationManager {
        return NavigationManager(fragmentManager, onStackChanged)
    }

    fun provideSearchArgument(key: String = "", query: String = QUERY_KEY) : SearchArguments {
        return SearchArguments(key, query)
    }

}