package com.gregantech.timepass.view.home.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gregantech.timepass.model.navigation.HomeNavigationState
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import kotlinx.coroutines.launch

class HomeSharedViewModel : ViewModel() {

    var displayFragment = MutableLiveData<Pair<HomeNavigationState, Bundle>>()
    private fun showFragment(item: Pair<HomeNavigationState, Bundle>) {
        viewModelScope.launch {
            displayFragment.value = item
        }
    }

    fun displayCategoryDetailPage(bundle: Bundle = Bundle()) {
        showFragment(Pair(HomeNavigationState.DISPLAY_CATEGORY_DETAIL_FRAGMENT, bundle))
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeSharedViewModel::class.java)) {
                return HomeSharedViewModel() as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}