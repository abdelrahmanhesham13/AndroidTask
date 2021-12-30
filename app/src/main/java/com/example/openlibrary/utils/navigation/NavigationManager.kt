package com.example.openlibrary.utils.navigation

import android.app.Activity
import androidx.fragment.app.Fragment

import androidx.fragment.app.FragmentManager
import com.example.openlibrary.R


class NavigationManager(
    var fragmentManager: FragmentManager,
    var onBackStackChanged: () -> Unit) {

    init {
        fragmentManager.addOnBackStackChangedListener {
            onBackStackChanged.invoke()
        }
    }

    /**
     * Displays the next fragment
     *
     * @param fragment
     */
    fun open(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, fragment)
            .addToBackStack(fragment.toString())
            .commit()
    }

    fun open(fragment: Fragment, preFragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .hide(preFragment)
            .addToBackStack(fragment.toString())
            .commit()
    }

    fun openX(fragment: Fragment, preFragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .hide(preFragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * pops every fragment and starts the given fragment as a new one.
     *
     * @param fragment
     */
    fun openAsRoot(fragment: Fragment) {
        popEveryFragment()
        openRoot(fragment)
    }

    /**
     * Pops all the queued fragments
     */
    private fun popEveryFragment() {
        // Clear all back stack.
        val backStackCount: Int = fragmentManager.backStackEntryCount
        for (i in 0 until backStackCount) {

            // Get the back stack fragment id.
            val backStackId: Int = fragmentManager.getBackStackEntryAt(i).id
            fragmentManager.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    private fun openRoot(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, fragment)
            .addToBackStack(fragment.toString())
            .commit()
    }

    /**
     * Navigates back by popping teh back stack. If there is no more items left we finish the current activity.
     *
     * @param baseActivity
     */
    fun navigateBack(baseActivity: Activity) {
        if (fragmentManager.backStackEntryCount == 1) {
            // we can finish the base activity since we have no other fragments
            baseActivity.finish()
        } else {
            fragmentManager.popBackStackImmediate()
        }
    }

    fun getBackStackCount() : Int {
        return fragmentManager.backStackEntryCount
    }

}