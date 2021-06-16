package com.gregantech.timepass.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.gregantech.timepass.util.constant.BROADCAST_COLLECTION_KEY
import com.gregantech.timepass.util.constant.CHAT_COLLECTION_KEY

fun FirebaseFirestore.broadCastCollection() = collection(BROADCAST_COLLECTION_KEY) //broadcast

fun FirebaseFirestore.chatCollection(documentKey: String) =
    broadCastCollection().document(documentKey).collection(CHAT_COLLECTION_KEY)

object FireStoreConst {

    object Keys {
        const val BROADCAST_COLLECTION_KEY = "Broadcast"
        const val CHAT_COLLECTION_KEY = "Chat"
        const val DATE_FIELD = "commentedDate"
        const val DOC_KEY = "docKey"
        const val MODE = "mode"
    }

}
