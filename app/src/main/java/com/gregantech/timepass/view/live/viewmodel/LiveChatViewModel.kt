package com.gregantech.timepass.view.live.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.firestore.REACTION
import com.gregantech.timepass.model.ChatModel
import com.gregantech.timepass.network.repository.FireStoreRepository
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveChatViewModel(private val fireStoreRepository: FireStoreRepository) : ViewModel() {

    fun obCreateBroadcastDocument() = fireStoreRepository.createBroadcastDoc()

    //fun obActiveLiveCountDocument() = fireStoreRepository.createActiveLiveCountDoc()

    fun obBroadcastCollection() = fireStoreRepository.observeBroadCast()

    fun obActiveLiveCountCollection() = fireStoreRepository.observeActiveLiveCountChange()

    fun obIncomingMessage(docKey: String) = fireStoreRepository.geChatHistory(docKey)

    fun obTopicIncomingMessage(docKey: String) = fireStoreRepository.getTopicChatHistory(docKey)

    fun obOutgoingMessage(chatModel: ChatModel, docKey: String) =
        fireStoreRepository.sendMessage(chatModel, docKey)

    fun obOutgoingTopicMessage(chatModel: ChatModel, docKey: String) =
        fireStoreRepository.sendTopicMessage(chatModel, docKey)

    fun obUpdateReactionCount(docKey: String, reaction: REACTION) =
        fireStoreRepository.updateReactionCount(docKey, reaction)

    fun obReactionCount(docKey: String) = fireStoreRepository.getReactionCount(docKey)

    fun obDocumentCount(docId: String) = fireStoreRepository.getDocumentCount(docId)

    fun obUpdateBroadcastState(docKey: String, state: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            fireStoreRepository.updateBroadcastState(docKey, state)
        }
    }

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val fireStoreRepository: FireStoreRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LiveChatViewModel::class.java)) {
                return LiveChatViewModel(fireStoreRepository) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }


}