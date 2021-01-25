package com.singtel.cast.utils.navigation

import androidx.fragment.app.FragmentManager
import com.gregantech.timepass.base.TimePassBaseFragment

object FragmentNavigationUtil {

    fun commitFragment(
        fragment: TimePassBaseFragment,
        fragmentManager: FragmentManager,
        container: Int,
        isToAdd: Boolean = false,
        isAddToBackStack: Boolean = false
    ) {
        val fragmentTransaction = fragmentManager.beginTransaction()

        if (isToAdd) {
            fragmentTransaction.add(container, fragment)
        } else {
            fragmentTransaction.replace(container, fragment)
        }
        if (isAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
        }

        fragmentTransaction.commit()
    }

    fun removeFragment(
        container: Int,
        fragmentManager: FragmentManager
    ) {
        fragmentManager.findFragmentById(container)?.let { fragment ->
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.remove(fragment).commit()
        }
    }
}