package com.gregantech.timepass.view.category.viewmodel

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.network.repository.CategoryRepository
import com.gregantech.timepass.network.response.CategoryResponse
import com.gregantech.timepass.util.constant.ErrorMessage
import com.singtel.cast.utils.ANNOTATION_UNCHECKED_CAST
import com.singtel.cast.utils.UNKNOWN_VIEW_MODEL_CLASS
import kotlinx.coroutines.Dispatchers

class CategoryFragmentViewModel(private val categoryRepository: CategoryRepository) :
    ViewModel() {

    fun getCategory() =
        liveData<TimePassBaseResult<CategoryResponse>>(Dispatchers.IO) {
            emit(TimePassBaseResult.loading(null))
            val result = categoryRepository.fetchCategory()
            when (result.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    emit(result)
                }
                TimePassBaseResult.Status.ERROR -> {
                    onFetchPlayBackFail()
                }
                else -> {
                    onFetchPlayBackFail()
                }
            }
        }

    private suspend fun LiveDataScope<TimePassBaseResult<CategoryResponse>>.onFetchPlayBackFail() {
        emit(TimePassBaseResult.error(ErrorMessage.NETWORK.value))
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(private val categoryRepository: CategoryRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryFragmentViewModel::class.java)) {
                return CategoryFragmentViewModel(categoryRepository) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}