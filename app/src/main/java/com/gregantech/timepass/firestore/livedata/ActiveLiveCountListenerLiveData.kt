package com.gregantech.timepass.firestore.livedata

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.gregantech.timepass.base.TimePassBaseResult

class ActiveLiveCountListenerLiveData(collectionReference: CollectionReference) :
    LiveData<TimePassBaseResult<Boolean>>(), EventListener<QuerySnapshot> {

    init {
        collectionReference.addSnapshotListener(this)
    }

    override fun onEvent(snapShot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        error?.let {
            Log.e("ActiveLiveCountListener", "onEvent: error ${it.message}")
            value = TimePassBaseResult.error(it.message ?: "Error")
        }
        snapShot?.documentChanges?.forEach {
            Log.d("ActiveLiveCountListener", "onEvent: type ${it.type}")
            value = TimePassBaseResult.success(true)
        }
    }


}