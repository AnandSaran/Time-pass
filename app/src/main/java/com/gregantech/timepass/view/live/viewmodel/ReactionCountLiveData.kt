package com.gregantech.timepass.view.live.viewmodel

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.model.ChatModel

class ReactionCountLiveData(countRef: DocumentReference, reaction: REACTION) :
    LiveData<TimePassBaseResult<ChatModel>>(), OnSuccessListener<Void>,
    OnFailureListener {

    init {
        when (reaction) {
            REACTION.LIKE -> countRef.update("like", FieldValue.increment(1))
            REACTION.LOVE -> countRef.update("love", FieldValue.increment(1))
            REACTION.SMILE -> countRef.update("smile", FieldValue.increment(1))
            REACTION.ANGRY -> countRef.update("angry", FieldValue.increment(1))
        }
    }

    override fun onSuccess(void: Void?) {
        value = TimePassBaseResult.success(ChatModel())
    }

    override fun onFailure(exception: Exception) {
        value = TimePassBaseResult.error(exception.message.toString())
    }


}

enum class REACTION {
    LIKE, LOVE, SMILE, ANGRY
}