package com.hnhy.socketdemokotlin.ui

/**
 * Emoji回调
 */
interface EmojiCallback {
    /**
     * 选中Emoji
     */
    fun checkedEmoji(charSequence: CharSequence)
}