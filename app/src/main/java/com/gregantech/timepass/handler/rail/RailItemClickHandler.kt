package com.gregantech.timepass.adapter.handler.rail

import com.gregantech.timepass.model.RailBaseItemModel


/**
 * clicking event handler for rail item
 * @property clickPoster
 */
class RailItemClickHandler {
    /**
     * clicking event handler for poster
     */
    lateinit var clickPoster: (railItem: RailBaseItemModel) -> Unit

    /**
     * checking if poster clicking handler initialized
     * @return Boolean
     */
    fun isInitializedForPosterClicking(): Boolean {
        return ::clickPoster.isInitialized
    }
}