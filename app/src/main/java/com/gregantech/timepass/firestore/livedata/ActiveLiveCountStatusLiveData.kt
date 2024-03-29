package com.gregantech.timepass.firestore.livedata

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.firestore.FireStoreConst
import com.gregantech.timepass.model.ChatModel

class ActiveLiveCountStatusLiveData(broadRef: DocumentReference, increment: Boolean) :
    LiveData<TimePassBaseResult<ChatModel?>>(), OnSuccessListener<Void>,
    OnFailureListener {

    init {
        if (increment) {
            broadRef.update(FireStoreConst.FIELD.COUNT, FieldValue.increment(1))
                .addOnSuccessListener(this)
                .addOnFailureListener(this)
        } else {
            broadRef.update(FireStoreConst.FIELD.COUNT, FieldValue.increment(-1))
                .addOnSuccessListener(this)
                .addOnFailureListener(this)
        }
    }

    override fun onSuccess(void: Void?) {
        //Log.d("BroadCastStatusLiveData", "onSuccess: ${documentReference?.id}")
        value = TimePassBaseResult.success(ChatModel())
    }

    override fun onFailure(exception: Exception) {
        Log.e("BroadCastStatusLiveData", "error: ${exception.message.toString()}")
        value = TimePassBaseResult.error(exception.message.toString())
    }

}