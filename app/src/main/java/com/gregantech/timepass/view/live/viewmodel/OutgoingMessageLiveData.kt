package com.gregantech.timepass.view.live.viewmodel

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.model.ChatModel

class OutgoingMessageLiveData(chatRef: CollectionReference, chatModel: ChatModel) :
    LiveData<TimePassBaseResult<String?>>(), OnSuccessListener<DocumentReference>,
    OnFailureListener {

    init {
        chatRef
            .add(chatModel)
            .addOnSuccessListener(this)
            .addOnFailureListener(this)
    }

    override fun onSuccess(documentReference: DocumentReference?) {
        //Log.d("OutgoingMessageLiveData", "onSuccess: ${documentReference?.id}")
        value = TimePassBaseResult.success(documentReference?.id)
    }

    override fun onFailure(exception: Exception) {
        //Log.d("OutgoingMessageLiveData", "error: ${exception.message.toString()}")
        value = TimePassBaseResult.error(exception.message.toString())
    }

}