package com.qrart.drawing

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QRRendererInstrumentedTest {
    private val renderer = QRRenderer()
    private val black = Color.BLACK
    private val white = Color.WHITE

    @Test
    fun generatesSquareQrWithExpectedDimensions() {
        val bitmap = renderer.generateQR("texto simple", DotStyle.SQUARE, FrameType.NONE, ErrorLevel.H, black, white)

        assertNotNull(bitmap)
        assertEquals(2048, bitmap.width)
        assertEquals(2048, bitmap.height)
    }

    @Test
    fun squareQrCanBeDecodedWithUnicodeContent() {
        val content = "Diseño QR: ñandú ✨"
        val bitmap = renderer.generateQR(content, DotStyle.SQUARE, FrameType.NONE, ErrorLevel.H, black, white)

        assertEquals(content, decode(bitmap))
    }

    @Test
    fun squareQrDecodesAtEveryConfiguredErrorLevel() {
        val content = "https://ejemplo.com/qr"
        ErrorLevel.values().forEach { level ->
            val bitmap = renderer.generateQR(content, DotStyle.SQUARE, FrameType.NONE, level, black, white)
            assertEquals(level.name, content, decode(bitmap))
        }
    }

    @Test
    fun configuredStylesAndFramesProduceBitmaps() {
        DotStyle.values().forEach { style ->
            val bitmap = renderer.generateQR("estilo ${style.name}", style, FrameType.NONE, ErrorLevel.H, black, white)
            assertTrue("style ${style.name}", bitmap.width > 0 && bitmap.height > 0)
        }
        FrameType.values().forEach { frame ->
            val bitmap = renderer.generateQR("marco ${frame.name}", DotStyle.SQUARE, frame, ErrorLevel.H, black, white)
            assertTrue("frame ${frame.name}", bitmap.width > 0 && bitmap.height > 0)
        }
    }

    @Test
    fun supportsLongContentAndRejectsEmptyContent() {
        val longContent = "á".repeat(400)
        val bitmap = renderer.generateQR(longContent, DotStyle.SQUARE, FrameType.NONE, ErrorLevel.H, black, white)

        assertEquals(longContent, decode(bitmap))
        try {
            renderer.generateQR("", DotStyle.SQUARE, FrameType.NONE, ErrorLevel.H, black, white)
            throw AssertionError("Empty content should be rejected by QRCodeWriter")
        } catch (_: Exception) {
            // Expected writer validation for empty content.
        }
    }

    private fun decode(bitmap: Bitmap): String {
        val source = BitmapLuminanceSource(bitmap)
        val hints = mapOf(DecodeHintType.TRY_HARDER to true)
        return MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(source)), hints).text
    }

    private class BitmapLuminanceSource(bitmap: Bitmap) : LuminanceSource(bitmap.width, bitmap.height) {
        private val pixels = IntArray(bitmap.width * bitmap.height)

        init {
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        }

        override fun getRow(y: Int, row: ByteArray?): ByteArray {
            val output = row?.takeIf { it.size >= width } ?: ByteArray(width)
            for (x in 0 until width) output[x] = luminance(pixels[y * width + x]).toByte()
            return output
        }

        override fun getMatrix(): ByteArray {
            val output = ByteArray(pixels.size)
            for (i in pixels.indices) output[i] = luminance(pixels[i]).toByte()
            return output
        }

        private fun luminance(color: Int): Int {
            return (Color.red(color) * 299 + Color.green(color) * 587 + Color.blue(color) * 114) / 1000
        }
    }
}
