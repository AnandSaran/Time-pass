package com.gregantech.timepass.view.live.viewmodel

import com.google.firebase.firestore.FirebaseFirestore
import com.gregantech.timepass.util.constant.BROADCAST_COLLECTION_KEY
import com.gregantech.timepass.util.constant.CHAT_COLLECTION_KEY

fun FirebaseFirestore.broadCastCollection() = collection(BROADCAST_COLLECTION_KEY) //broadcast

fun FirebaseFirestore.chatCollection(documentKey: String) =
    broadCastCollection().document(documentKey).collection(CHAT_COLLECTION_KEY)
