package com.example.openlibrary.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.openlibrary.di.RepositoryModule
import com.example.openlibrary.ui.main.viewmodel.MainViewModel

class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(RepositoryModule.provideMainRepository()) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}