package com.example.openlibrary.ui.main.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.openlibrary.R
import com.example.openlibrary.di.UtilsModule
import com.example.openlibrary.ui.base.ViewModelFactory
import com.example.openlibrary.ui.details.view.DetailsFragment
import com.example.openlibrary.ui.search.view.SearchFragment
import com.example.openlibrary.ui.main.viewmodel.MainViewModel
import com.example.openlibrary.utils.navigation.NavigationManager


class MainActivity : AppCompatActivity() {

    private lateinit var navigationManager: NavigationManager
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
        initNavigationManager()
        addObservers()
    }

    private fun addObservers() {
        viewModel.selectedDocumentLiveData.observe(this) {
            navigationManager.open(DetailsFragment.newInstance())
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    private fun initNavigationManager() {
        navigationManager = UtilsModule.provideNavigationManager(supportFragmentManager) {
            if (navigationManager.getBackStackCount() <= 1) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                title = "Search"
            }
        }
        navigationManager.openAsRoot(SearchFragment.newInstance())
    }

    override fun onBackPressed() {
        navigationManager.navigateBack(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}