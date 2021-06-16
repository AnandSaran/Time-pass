package com.gregantech.timepass.view.live.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.firestore.REACTION
import com.gregantech.timepass.model.ChatModel
import com.gregantech.timepass.network.repository.LiveChatRepository
import com.gregantech.timepass.util.constant.ANNOTATION_UNCHECKED_CAST
import com.gregantech.timepass.util.constant.UNKNOWN_VIEW_MODEL_CLASS

class LiveChatViewModel(private val liveChatRepository: LiveChatRepository) : ViewModel() {

    fun obIncomingMessage(docKey: String) = liveChatRepository.geChatHistory(docKey)

    fun obOutgoingMessage(chatModel: ChatModel, docKey: String) =
        liveChatRepository.sendMessage(chatModel, docKey)

    fun obCreateBroadcastDocument() = liveChatRepository.createBroadcastDoc()

    fun obUpdateReactionCount(docKey: String, reaction: REACTION) =
        liveChatRepository.updateReactionCount(docKey, reaction)

    fun obReactionCount(docKey: String) = liveChatRepository.getReactionCount(docKey)

    @Suppress(ANNOTATION_UNCHECKED_CAST)
    class Factory(
        private val liveChatRepository: LiveChatRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LiveChatViewModel::class.java)) {
                return LiveChatViewModel(liveChatRepository) as T
            }
            throw IllegalArgumentException(UNKNOWN_VIEW_MODEL_CLASS)
        }
    }


}