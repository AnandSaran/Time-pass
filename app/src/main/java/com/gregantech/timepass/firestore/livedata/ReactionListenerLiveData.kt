package com.gregantech.timepass.firestore.livedata

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.firestore.FireStoreConst
import com.gregantech.timepass.model.BroadcastModel

class ReactionListenerLiveData(broadDocRef: DocumentReference) :
    LiveData<TimePassBaseResult<BroadcastModel>>(), EventListener<DocumentSnapshot> {

    init {
        broadDocRef.addSnapshotListener(this)
    }

    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        error?.let {
            Log.e("ReactionLiveData", "listen: ${it.message}")
            value = TimePassBaseResult.error(it.message!!)
        }

        snapshot?.run {
            if (exists()) {
                val broadcastModel = BroadcastModel().apply {
                    Likes = get(FireStoreConst.FIELD.LIKES).toString()
                    Loves = get(FireStoreConst.FIELD.LOVES).toString()
                    Smiles = get(FireStoreConst.FIELD.SMILES).toString()
                    Angry = get(FireStoreConst.FIELD.ANGRY).toString()
                }
                value = TimePassBaseResult.success(broadcastModel)
            }
        }
    }


}