package com.gregantech.timepass.firestore.livedata

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.firestore.FireStoreConst
import com.gregantech.timepass.firestore.REACTION
import com.gregantech.timepass.model.ChatModel

class ReactionCountLiveData(private val broadDocRef: DocumentReference, reaction: REACTION) :
    LiveData<TimePassBaseResult<ChatModel>>(), OnSuccessListener<Void>,
    OnFailureListener {

    init {
        when (reaction) {
            REACTION.LIKE -> doUpdate(FireStoreConst.FIELD.LIKES)
            REACTION.LOVE -> doUpdate(FireStoreConst.FIELD.LOVES)
            REACTION.SMILE -> doUpdate(FireStoreConst.FIELD.SMILES)
            REACTION.ANGRY -> doUpdate(FireStoreConst.FIELD.ANGRY)
        }
    }

    private fun doUpdate(fieldName: String) {
        broadDocRef.update(fieldName, FieldValue.increment(1))
            .addOnSuccessListener(this)
            .addOnFailureListener(this)
    }

    override fun onSuccess(void: Void?) {
        value = TimePassBaseResult.success(ChatModel())
    }

    override fun onFailure(exception: Exception) {
        value = TimePassBaseResult.error(exception.message.toString())
    }


}