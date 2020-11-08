package com.gregantech.timepass.view.category.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.singtel.cast.utils.ANNOTATION_UNCHECKED_CAST
import com.singtel.cast.utils.UNKNOWN_VIEW_MODEL_CLASS

class CategoryActivitySharedViewModel :
    ViewModel() {

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryActivitySharedViewModel::class.java)) {
                return CategoryActivitySharedViewModel() as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}