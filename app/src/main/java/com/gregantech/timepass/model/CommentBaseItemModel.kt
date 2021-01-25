package com.gregantech.timepass.model

/**
 * Base data model for comment  item inside rail
 */
abstract class CommentBaseItemModel {
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
     * time
     */
    abstract val time: String

    /**
     * type
     */
    abstract val commentTypeEnum: CommentTypeEnum

}
