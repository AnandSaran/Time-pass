package com.gregantech.timepass.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.gregantech.timepass.firestore.FireStoreConst.Keys.ACTIVE_LIVE_COUNT_COLLECTION_KEY
import com.gregantech.timepass.firestore.FireStoreConst.Keys.BROADCAST_COLLECTION_KEY
import com.gregantech.timepass.firestore.FireStoreConst.Keys.CHAT_COLLECTION_KEY
import com.gregantech.timepass.firestore.FireStoreConst.Keys.TOPIC_COLLECTION_KEY

fun FirebaseFirestore.broadCastCollection() = collection(BROADCAST_COLLECTION_KEY) //broadcast

fun FirebaseFirestore.activeLiveCountCollection() =
    collection(ACTIVE_LIVE_COUNT_COLLECTION_KEY) //broadcast

fun FirebaseFirestore.topicCastCollection() = collection(TOPIC_COLLECTION_KEY) //topic

fun FirebaseFirestore.chatCollection(documentKey: String) =
    broadCastCollection().document(documentKey).collection(CHAT_COLLECTION_KEY)

fun FirebaseFirestore.topicChatCollection(documentKey: String) =
    topicCastCollection().document(documentKey).collection(CHAT_COLLECTION_KEY)

object FireStoreConst {
    object Keys {
        const val BROADCAST_COLLECTION_KEY = "Broadcast"
        const val ACTIVE_LIVE_COUNT_COLLECTION_KEY = "ActiveLiveCount"
        const val TOPIC_COLLECTION_KEY = "Topic"
        const val CHAT_COLLECTION_KEY = "Chat"
        const val LIVE_COUNT_DOCUMENT_KEY = "liveCount"
        const val DATE_FIELD = "commentedDate"
        const val DOC_KEY = "docKey"
        const val MODE = "mode"
    }

    object FIELD {
        const val LIKES = "likes"
        const val LOVES = "loves"
        const val SMILES = "smiles"
        const val ANGRY = "angry"
        const val BROADCAST_LIVE = "broadcast_live"
        const val COUNT = "count"
    }
}

enum class REACTION {
    LIKE, LOVE, SMILE, ANGRY
}