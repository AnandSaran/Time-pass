package com.gregantech.timepass.view.topic.model

data class TopicResponse(
	val topic: List<TopicItem?>? = null
) {
	data class TopicItem(
		val topicName: String? = null,
		val Id: String? = null,
		val DateTime: String? = null
	)
}
