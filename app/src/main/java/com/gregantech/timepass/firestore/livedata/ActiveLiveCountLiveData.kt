package com.gregantech.timepass.firestore.livedata

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.firestore.FireStoreConst.FIELD.COUNT
import com.gregantech.timepass.firestore.FireStoreConst.Keys.LIVE_COUNT_DOCUMENT_KEY

class ActiveLiveCountLiveData(broadRef: CollectionReference) :
    LiveData<TimePassBaseResult<String?>>(), OnSuccessListener<DocumentReference>,
    OnFailureListener {

    init {
        broadRef.document(LIVE_COUNT_DOCUMENT_KEY).update(COUNT, FieldValue.increment(1))
    }

    override fun onSuccess(documentReference: DocumentReference?) {
        //Log.d("BroadcastLiveData", "onSuccess: ${documentReference?.id}")
        value = TimePassBaseResult.success(documentReference?.id)
    }

    override fun onFailure(exception: Exception) {
        //Log.d("BroadcastLiveData", "error: ${exception.message.toString()}")
        value = TimePassBaseResult.error(exception.message.toString())
    }

}