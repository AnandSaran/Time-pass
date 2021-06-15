package com.gregantech.timepass.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp


data class BroadcastModel(var chatModel: ChatModel, var type: Int)


data class ChatModel(
    @Transient
    var id: String? = null,
    var commentedUserId: String? = null,
    var commentedUserProfileUrl: String? = null,
    var commentedUserName: String? = null,
    var comments: String? = null,
    @ServerTimestamp val commentedDate: Timestamp? = null
)