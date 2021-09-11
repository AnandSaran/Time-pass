package com.gregantech.timepass.firestore.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.CollectionReference
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.util.constant.CHAT_LIMIT


class DocumentCountLiveData(private val topicChatCollectionRef: CollectionReference) :
    LiveData<TimePassBaseResult<Long>>() {

    init {
        getCount()
    }

    private fun getCount() {
        topicChatCollectionRef.get().addOnCompleteListener {
            value = if (it.isSuccessful) {
                TimePassBaseResult.success(
                    if (it.result.size() == 0) CHAT_LIMIT else it.result.size().toLong()
                )
            } else
                TimePassBaseResult.error(it.toString())
        }
    }

}