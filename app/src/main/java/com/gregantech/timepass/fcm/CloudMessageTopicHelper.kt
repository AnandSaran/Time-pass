package com.gregantech.timepass.fcm

import com.google.firebase.messaging.FirebaseMessaging


class CloudMessageTopicHelper {
    fun subscribeDefaultTopics(isSubscribe: Boolean = true) {
        subscribeTopic("Live", isSubscribe)
        subscribeTopic("Post", isSubscribe)
    }

    private fun subscribeTopic(topicName: String, isSubscribe: Boolean = true) {
        when {
            isSubscribe -> {
                FirebaseMessaging.getInstance().subscribeToTopic(topicName)
            }
            else -> {
                FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(topicName)
            }
        }
    }
}