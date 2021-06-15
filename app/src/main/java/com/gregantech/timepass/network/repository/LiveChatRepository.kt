package com.gregantech.timepass.network.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.gregantech.timepass.model.ChatModel
import com.gregantech.timepass.util.constant.CHAT_LIMIT
import com.gregantech.timepass.util.constant.DATE_FIELD
import com.gregantech.timepass.view.live.viewmodel.*

class LiveChatRepository(private val broadDocKey: String) {

    private val fireStore = FirebaseFirestore.getInstance()
    private var query =
        fireStore.chatCollection(broadDocKey).orderBy(DATE_FIELD, Query.Direction.ASCENDING)
            .limit(CHAT_LIMIT)

    private var lastVisibleProduct: DocumentSnapshot? = null
    private var isLastProductReached = false

    fun sendMessage(chatModel: ChatModel): OutgoingMessageLiveData {
        return OutgoingMessageLiveData(fireStore.chatCollection(broadDocKey), chatModel)
    }

    fun updateLikeCount() =
        ReactionCountLiveData(fireStore.broadCastCollection().document(broadDocKey), REACTION.LIKE)

    fun geChatHistory(): IncomingMessageLiveData? {
        if (isLastProductReached)
            return null

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