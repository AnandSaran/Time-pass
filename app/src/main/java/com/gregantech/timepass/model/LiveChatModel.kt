package com.gregantech.timepass.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp


data class BroadcastModel(
    var likes: String? = "0",
    var loves: String? = "0",
    var smiles: String? = "0",
    var angry: String? = "0",
    var broadcast_live: Boolean = true
)

data class ActiveLiveCountModel(
    var count: Int = 0
)

data class ChatModelWrapper(
    var chatModel: ChatModel,
    var type: Int = -1
)

data class ChatModel(
    @Transient
    var id: String? = null,
    var commentedUserId: String? = null,
    var commentedUserProfileUrl: String? = null,
    var commentedUserName: String? = null,
    var comments: String? = null,
    @ServerTimestamp val commentedDate: Timestamp? = null
)