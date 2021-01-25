package com.gregantech.timepass.view.home.viewmodel

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.network.repository.CategoryRepository
import com.gregantech.timepass.network.response.CategoryResponse
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.ErrorMessage
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
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
                    onFetchCategoryFail()
                }
                else -> {
                    onFetchCategoryFail()
                }
            }
        }

    private suspend fun LiveDataScope<TimePassBaseResult<CategoryResponse>>.onFetchCategoryFail() {
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