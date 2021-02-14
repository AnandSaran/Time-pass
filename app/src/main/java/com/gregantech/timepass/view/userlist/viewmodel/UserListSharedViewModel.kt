package com.gregantech.timepass.view.userlist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeFourModel
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import kotlinx.coroutines.launch

class UserListSharedViewModel : ViewModel() {

    var railList = MutableLiveData<ArrayList<RailBaseItemModel>>()

    fun setRailList(item: ArrayList<RailBaseItemModel>) {
        viewModelScope.launch {
            railList.value = item
        }
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserListSharedViewModel::class.java)) {
                return UserListSharedViewModel() as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }
}