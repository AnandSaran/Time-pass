package com.gregantech.timepass.model


data class AppConfigResponse(
	val App: List<AppItem?>? = null
) {
	data class AppItem(
		val appVersion: String? = null,
		val title: String? = null,
		val message: String? = null
	)
}
