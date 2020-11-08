package com.gregantech.timepass.view.categorydetail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.repository.factory.RailDataFactory
import com.singtel.cast.utils.ANNOTATION_UNCHECKED_CAST
import com.singtel.cast.utils.UNKNOWN_VIEW_MODEL_CLASS

class CategoryDetailFragmentViewModel(private val railDataFactory: RailDataFactory) :
    ViewModel() {
    var categoryVideoList = MutableLiveData(railDataFactory.getCategoryVideoList())

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(private val railDataFactory: RailDataFactory) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryDetailFragmentViewModel::class.java)) {
                return CategoryDetailFragmentViewModel(railDataFactory) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}