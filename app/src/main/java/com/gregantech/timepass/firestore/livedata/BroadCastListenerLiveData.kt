package com.gregantech.timepass.firestore.livedata

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.gregantech.timepass.base.TimePassBaseResult

class BroadCastListenerLiveData(collectionReference: CollectionReference) :
    LiveData<TimePassBaseResult<Boolean>>(), EventListener<QuerySnapshot> {

    init {
        collectionReference.addSnapshotListener(this)
    }

    override fun onEvent(snapShot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        error?.let {
            Log.e("BroadCastListener", "onEvent: error ${it.message}")
            value = TimePassBaseResult.error(it.message ?: "Error")
        }
        snapShot?.documentChanges?.forEach {
            Log.d("BroadCastListener", "onEvent: ")
            when (it.type) {
                DocumentChange.Type.ADDED -> value = TimePassBaseResult.success(true)
                else -> {
                }
            }
        }
    }


}