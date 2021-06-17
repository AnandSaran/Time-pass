package com.gregantech.timepass.firestore.livedata

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.firestore.FireStoreConst
import com.gregantech.timepass.model.ChatModel

class BroadCastStatusLiveData(broadRef: DocumentReference, state: Boolean) :
    LiveData<TimePassBaseResult<ChatModel?>>(), OnSuccessListener<Void>,
    OnFailureListener {

    init {
        broadRef
            .update(FireStoreConst.FIELD.BROADCAST_LIVE, state)
            .addOnSuccessListener(this)
            .addOnFailureListener(this)
    }

    override fun onSuccess(void: Void?) {
        //Log.d("BroadCastStatusLiveData", "onSuccess: ${documentReference?.id}")
        value = TimePassBaseResult.success(ChatModel())
    }

    override fun onFailure(exception: Exception) {
        //Log.d("BroadCastStatusLiveData", "error: ${exception.message.toString()}")
        value = TimePassBaseResult.error(exception.message.toString())
    }

}