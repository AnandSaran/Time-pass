package com.gregantech.timepass.model

/**
 * Base data model for Rail item inside rail
 */
abstract class RailBaseItemModel {
    /**
     * content Id
     */
    abstract val contentId: String

    /**
     * poster url
     * */
    abstract val image: String

    /**
     * title
     */
    abstract val title: String

    /**
     * title
     */
    abstract val railItemType: RailItemTypeEnum
}
