package com.gregantech.timepass.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp


data class BroadcastModel(
    var Likes: String? = "0",
    var Loves: String? = "0",
    var Smiles: String? = "0",
    var Angry: String? = "0"
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