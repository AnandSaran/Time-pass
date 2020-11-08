package com.gregantech.timepass.model

/**
 * Enum class for rail item type
 */
enum class RailItemTypeEnum(val value: Int) {
    /**
     * with poster, title
     */
    TYPE_RAIL_ITEM_ONE(0),

    /**
     * with poster, title, like, share, player, sound, loading
     */
    TYPE_RAIL_ITEM_TWO(1),
}