package com.gregantech.timepass.firestore.livedata

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.gregantech.timepass.R
import com.gregantech.timepass.model.BroadcastModel
import com.gregantech.timepass.model.ChatModel
import com.gregantech.timepass.util.constant.CHAT_LIMIT
import kotlin.reflect.KFunction1

class IncomingMessageLiveData(
    private val query: Query,
    private val lastVisibleCallback: KFunction1<DocumentSnapshot?, Unit>,
    private val lastProductReachedCallback: KFunction1<Boolean, Unit>
) : LiveData<BroadcastModel>(), EventListener<QuerySnapshot> {

    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        listenerRegistration = query.addSnapshotListener(this)
    }

    override fun onInactive() {
        listenerRegistration?.remove()
    }

    override fun onEvent(snapShot: QuerySnapshot?, error: FirebaseFirestoreException?) {

        error?.let {
            Log.e("ChatLiveData", "onEvent: error ${it.message}")
        }

        snapShot?.documentChanges?.forEach {
            value = when (it.type) {
                DocumentChange.Type.ADDED -> {
                    val addedChat = it.document.toObject(ChatModel::class.java).apply {
                        id = it.document.id
                    }
                    BroadcastModel(addedChat, R.string.added)
                }
                DocumentChange.Type.MODIFIED -> {
                    val modifyChat = it.document.toObject(ChatModel::class.java).apply {
                        id = it.document.id
                    }
                    BroadcastModel(modifyChat, R.string.modified)
                }
                DocumentChange.Type.REMOVED -> {
                    val removeChat = it.document.toObject(ChatModel::class.java).apply {
                        id = it.document.id
                    }
                    BroadcastModel(removeChat, R.string.removed)
                }
            }
        }

        val snapSize = snapShot?.size() ?: 0
        if (snapSize < CHAT_LIMIT)
            lastProductReachedCallback.invoke(true)
        else
            lastVisibleCallback.invoke(snapShot?.documents?.get(snapSize - 1))
    }


}