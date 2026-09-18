package com.qrart.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContrastUtilsTest {
    @Test fun blackOnWhiteHasKnownRatio() {
        assertEquals(21.0f, ContrastUtils.ratio(0xFF000000.toInt(), 0xFFFFFFFF.toInt()), 0.01f)
    }

    @Test fun whiteOnBlackHasSameRatio() {
        val black = 0xFF000000.toInt()
        val white = 0xFFFFFFFF.toInt()
        assertEquals(ContrastUtils.ratio(black, white), ContrastUtils.ratio(white, black), 0.0001f)
    }

    @Test fun identicalColorsHaveRatioOne() {
        assertEquals(1.0f, ContrastUtils.ratio(0xFF336699.toInt(), 0xFF336699.toInt()), 0.0001f)
    }

    @Test fun thresholdClassifiesKnownBoundaryColors() {
        val white = 0xFFFFFFFF.toInt()
        val below = ContrastUtils.ratio(0xFF777777.toInt(), white)
        val above = ContrastUtils.ratio(0xFF767676.toInt(), white)

        assertFalse(ContrastUtils.isReadable(below))
        assertTrue(ContrastUtils.isReadable(above))
    }

    @Test fun luminanceValuesAreOrderedForBlackGrayWhite() {
        val black = ContrastUtils.luminance(0xFF000000.toInt())
        val gray = ContrastUtils.luminance(0xFF777777.toInt())
        val white = ContrastUtils.luminance(0xFFFFFFFF.toInt())

        assertEquals(0.0f, black, 0.0001f)
        assertTrue(black < gray)
        assertTrue(gray < white)
    }
}
