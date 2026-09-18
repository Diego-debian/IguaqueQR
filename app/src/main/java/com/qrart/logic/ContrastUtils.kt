package com.qrart.logic

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/** Pure contrast calculations extracted from HomeScreen for reproducible tests. */
object ContrastUtils {
    const val READABLE_RATIO = 4.5f

    fun luminance(color: Int): Float {
        fun linear(channel: Float): Float {
            return if (channel <= 0.03928f) channel / 12.92f
            else ((channel + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
        }

        val red = linear(((color shr 16) and 0xFF) / 255f)
        val green = linear(((color shr 8) and 0xFF) / 255f)
        val blue = linear((color and 0xFF) / 255f)
        return 0.2126f * red + 0.7152f * green + 0.0722f * blue
    }

    fun ratio(foreground: Int, background: Int): Float {
        val lighter = max(luminance(foreground), luminance(background))
        val darker = min(luminance(foreground), luminance(background))
        return (lighter + 0.05f) / (darker + 0.05f)
    }

    fun isReadable(ratio: Float): Boolean = ratio >= READABLE_RATIO
}
