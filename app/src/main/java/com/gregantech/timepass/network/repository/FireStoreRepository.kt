package com.gregantech.timepass.network.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.gregantech.timepass.firestore.FireStoreConst.Keys.DATE_FIELD
import com.gregantech.timepass.firestore.REACTION
import com.gregantech.timepass.firestore.broadCastCollection
import com.gregantech.timepass.firestore.chatCollection
import com.gregantech.timepass.firestore.livedata.*
import com.gregantech.timepass.model.ChatModel
import com.gregantech.timepass.util.constant.CHAT_LIMIT

class FireStoreRepository {

    private val fireStore = FirebaseFirestore.getInstance()
    private lateinit var query: Query

    private var lastVisibleProduct: DocumentSnapshot? = null
    private var isLastProductReached = false

    fun sendMessage(chatModel: ChatModel, broadDocKey: String): OutgoingMessageLiveData {
        return OutgoingMessageLiveData(fireStore.chatCollection(broadDocKey), chatModel)
    }

    fun createBroadcastDoc() = BroadCastCreateLiveData(fireStore.broadCastCollection())

    fun observeBroadCast() = BroadCastListenerLiveData(fireStore.broadCastCollection())

    fun updateReactionCount(broadDocKey: String, reaction: REACTION) =
        ReactionCountLiveData(fireStore.broadCastCollection().document(broadDocKey), reaction)

    fun getReactionCount(broadDocKey: String) =
        ReactionListenerLiveData(fireStore.broadCastCollection().document(broadDocKey))

    fun updateBroadcastState(broadDocKey: String, state: Boolean) =
        BroadCastStatusLiveData(fireStore.broadCastCollection().document(broadDocKey), state)

    fun geChatHistory(broadDocKey: String): IncomingMessageLiveData? {
        if (isLastProductReached)
            return null

        query = fireStore.chatCollection(broadDocKey).orderBy(DATE_FIELD, Query.Direction.ASCENDING)
            .limit(CHAT_LIMIT)

        lastVisibleProduct?.let {
            query = query.startAfter(it)
        }

        return IncomingMessageLiveData(query, ::setLastVisibleProduct, ::setLastProductReached)
    }

    private fun setLastVisibleProduct(lvP: DocumentSnapshot?) {
        lastVisibleProduct = lvP
    }

    private fun setLastProductReached(iLPR: Boolean) {
        isLastProductReached = iLPR
    }


}