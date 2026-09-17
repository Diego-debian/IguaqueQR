package com.qrart.drawing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.BitmapFactory
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.common.BitMatrix

enum class DotStyle(val displayName: String, val emoji: String) {
    SQUARE("Normal", "⬛"),
    CIRCLE("Redondo", "🔵"),
    HEART("Corazón", "❤️"),
    STAR("Estrella", "⭐"),
    PAW("Huellita", "🐾"),
    CAT("Gatito", "🐱"),
    DIAMOND("Rombo", "🔹"),
    FLOWER("Flor", "🌸"),
    LEAF("Hoja", "🍃"),
    TRIANGLE("Triángulo", "🔺"),
    HEXAGON("Hexágono", "⬡"),
    SPARKLE("Destello", "✨"),
    MOON("Luna", "🌙"),
    GHOST("Fantasma", "👻")
}

enum class FrameType(val displayName: String, val emoji: String) {
    NONE("Ninguno", "⏹️"),
    BEAR("Osito", "🐻"),
    CAT_FRAME("Gatito", "🐱"),
    TV("Tele", "📺"),
    PHONE("Móvil", "📱"),
    CLOUD("Nube", "☁️"),
    HOUSE("Casa", "🏠"),
    CROWN("Corona", "👑"),
    FROG("Rana", "🐸")
}

enum class ErrorLevel(val displayName: String, val description: String, val zxingLevel: ErrorCorrectionLevel) {
    L("Bajo", "~7% daño", ErrorCorrectionLevel.L),
    M("Medio", "~15% daño", ErrorCorrectionLevel.M),
    Q("Alto", "~25% daño", ErrorCorrectionLevel.Q),
    H("Máximo", "~30% daño", ErrorCorrectionLevel.H)
}

class QRRenderer {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }

    fun generateQR(
        text: String,
        dotStyle: DotStyle,
        frameType: FrameType,
        errorLevel: ErrorLevel,
        qrColor: Int,
        bgColor: Int,
        logoBitmap: Bitmap? = null
    ): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix: BitMatrix = writer.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, 0, 0, mapOf(
            com.google.zxing.EncodeHintType.ERROR_CORRECTION to errorLevel.zxingLevel,
            com.google.zxing.EncodeHintType.MARGIN to 0
        ))

        val moduleCount = bitMatrix.width
        val baseSize = 2048
        val quietZoneModules = 4
        val totalModules = moduleCount + quietZoneModules * 2
        val cellSize = baseSize.toFloat() / totalModules.toFloat()

        // Create internal QR canvas
        val qrBitmap = Bitmap.createBitmap(baseSize, baseSize, Bitmap.Config.ARGB_8888)
        val qrCanvas = Canvas(qrBitmap)

        // Draw background
        fillPaint.color = bgColor
        qrCanvas.drawRect(0f, 0f, baseSize.toFloat(), baseSize.toFloat(), fillPaint)

        fillPaint.color = qrColor

        // Draw modules
        for (r in 0 until moduleCount) {
            for (c in 0 until moduleCount) {
                if (bitMatrix.get(c, r)) {
                    val x = (c + quietZoneModules).toFloat() * cellSize
                    val y = (r + quietZoneModules).toFloat() * cellSize

                    if (isFinderPattern(r, c, moduleCount)) {
                        // Finder patterns stay untouched; readers depend on their exact geometry.
                        qrCanvas.drawRect(x, y, x + cellSize + 0.5f, y + cellSize + 0.5f, fillPaint)
                    } else if (dotStyle == DotStyle.SQUARE) {
                        qrCanvas.drawRect(x, y, x + cellSize + 0.5f, y + cellSize + 0.5f, fillPaint)
                    } else {
                        // Leave a controlled gap between decorative modules so shapes do not merge.
                        val dotSize = cellSize * 0.82f
                        val inset = (cellSize - dotSize) / 2f
                        drawDot(qrCanvas, x + inset, y + inset, dotSize, dotStyle)
                    }
                }
            }
        }

        // Draw logo if provided
        if (logoBitmap != null) {
            val logoSize = baseSize * 0.25f
            val center = baseSize / 2f
            val logoX = center - logoSize / 2f
            val logoY = center - logoSize / 2f

            // White background for logo
            fillPaint.color = bgColor
            drawRoundRect(qrCanvas, logoX - 30f, logoY - 30f, logoSize + 60f, logoSize + 60f, 40f)

            // Draw logo
            val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, logoSize.toInt(), logoSize.toInt(), true)
            qrCanvas.drawBitmap(scaledLogo, logoX, logoY, null)
        }

        // Apply frame if needed
        if (frameType == FrameType.NONE) {
            return qrBitmap
        }

        return applyFrame(qrBitmap, frameType, qrColor, bgColor)
    }

    private fun isFinderPattern(r: Int, c: Int, count: Int): Boolean {
        return (r < 7 && c < 7) || (r < 7 && c >= count - 7) || (r >= count - 7 && c < 7)
    }

    private fun drawDot(canvas: Canvas, x: Float, y: Float, size: Float, style: DotStyle) {
        val cx = x + size / 2f
        val cy = y + size / 2f
        val half = size / 2f

        when (style) {
            DotStyle.SQUARE -> canvas.drawRect(x, y, x + size + 0.5f, y + size + 0.5f, fillPaint)
            DotStyle.CIRCLE -> canvas.drawCircle(cx, cy, half * 0.9f, fillPaint)
            DotStyle.HEART -> drawHeart(canvas, x, y, size)
            DotStyle.STAR -> drawStar(canvas, cx, cy, half * 0.95f)
            DotStyle.PAW -> drawPaw(canvas, x, y, size)
            DotStyle.CAT -> drawCatDot(canvas, x, y, size)
            DotStyle.DIAMOND -> drawDiamond(canvas, x, y, size)
            DotStyle.FLOWER -> drawFlower(canvas, cx, cy, size / 3.5f)
            DotStyle.LEAF -> drawLeaf(canvas, x, y, size)
            DotStyle.TRIANGLE -> drawTriangle(canvas, x, y, size)
            DotStyle.HEXAGON -> drawHexagon(canvas, x, y, size)
            DotStyle.SPARKLE -> drawSparkle(canvas, x, y, size)
            DotStyle.MOON -> drawMoon(canvas, cx, cy, size / 2.2f)
            DotStyle.GHOST -> drawGhost(canvas, x, y, size)
        }
    }

    // ===== DOT DRAWING FUNCTIONS =====

    private fun drawHeart(canvas: Canvas, x: Float, y: Float, w: Float) {
        val path = Path()
        val topY = y + w * 0.25f
        path.moveTo(x + w / 2f, topY)
        path.cubicTo(x + w / 2f, y, x, y, x, topY)
        path.cubicTo(x, y + w * 0.55f, x + w / 2f, y + w * 0.85f, x + w / 2f, y + w)
        path.cubicTo(x + w / 2f, y + w * 0.85f, x + w, y + w * 0.55f, x + w, topY)
        path.cubicTo(x + w, y, x + w / 2f, y, x + w / 2f, topY)
        canvas.drawPath(path, fillPaint)
    }

    private fun drawStar(canvas: Canvas, cx: Float, cy: Float, outerRadius: Float) {
        val innerRadius = outerRadius * 0.4f
        val spikes = 5
        val path = Path()
        var rot = Math.PI.toFloat() / 2f * 3f
        val step = Math.PI.toFloat() / spikes.toFloat()

        path.moveTo(cx, cy - outerRadius)
        for (i in 0 until spikes) {
            path.lineTo(cx + kotlin.math.cos(rot.toDouble()).toFloat() * outerRadius,
                cy + kotlin.math.sin(rot.toDouble()).toFloat() * outerRadius)
            rot += step
            path.lineTo(cx + kotlin.math.cos(rot.toDouble()).toFloat() * innerRadius,
                cy + kotlin.math.sin(rot.toDouble()).toFloat() * innerRadius)
            rot += step
        }
        path.close()
        canvas.drawPath(path, fillPaint)
    }

    private fun drawPaw(canvas: Canvas, x: Float, y: Float, w: Float) {
        canvas.drawCircle(x + w * 0.5f, y + w * 0.65f, w * 0.25f, fillPaint)
        canvas.drawCircle(x + w * 0.25f, y + w * 0.35f, w * 0.12f, fillPaint)
        canvas.drawCircle(x + w * 0.5f, y + w * 0.15f, w * 0.15f, fillPaint)
        canvas.drawCircle(x + w * 0.75f, y + w * 0.35f, w * 0.12f, fillPaint)
    }

    private fun drawCatDot(canvas: Canvas, x: Float, y: Float, w: Float) {
        canvas.drawCircle(x + w * 0.5f, y + w * 0.6f, w * 0.35f, fillPaint)
        val path = Path()
        path.moveTo(x + w * 0.15f, y + w * 0.5f)
        path.lineTo(x + w * 0.1f, y + w * 0.1f)
        path.lineTo(x + w * 0.4f, y + w * 0.3f)
        canvas.drawPath(path, fillPaint)
        path.rewind()
        path.moveTo(x + w * 0.85f, y + w * 0.5f)
        path.lineTo(x + w * 0.9f, y + w * 0.1f)
        path.lineTo(x + w * 0.6f, y + w * 0.3f)
        canvas.drawPath(path, fillPaint)
    }

    private fun drawDiamond(canvas: Canvas, x: Float, y: Float, w: Float) {
        val path = Path()
        path.moveTo(x + w / 2f, y)
        path.lineTo(x + w, y + w / 2f)
        path.lineTo(x + w / 2f, y + w)
        path.lineTo(x, y + w / 2f)
        path.close()
        canvas.drawPath(path, fillPaint)
    }

    private fun drawFlower(canvas: Canvas, cx: Float, cy: Float, r: Float) {
        canvas.drawCircle(cx - r * 0.8f, cy - r * 0.8f, r, fillPaint)
        canvas.drawCircle(cx + r * 0.8f, cy - r * 0.8f, r, fillPaint)
        canvas.drawCircle(cx - r * 0.8f, cy + r * 0.8f, r, fillPaint)
        canvas.drawCircle(cx + r * 0.8f, cy + r * 0.8f, r, fillPaint)
        canvas.drawCircle(cx, cy, r * 1.2f, fillPaint)
    }

    private fun drawLeaf(canvas: Canvas, x: Float, y: Float, w: Float) {
        val path = Path()
        path.moveTo(x + w * 0.1f, y + w * 0.9f)
        path.cubicTo(x + w * 0.1f, y + w * 0.3f, x + w * 0.5f, y + w * 0.1f, x + w * 0.9f, y + w * 0.1f)
        path.cubicTo(x + w * 0.7f, y + w * 0.5f, x + w * 0.9f, y + w * 0.9f, x + w * 0.1f, y + w * 0.9f)
        canvas.drawPath(path, fillPaint)
    }

    private fun drawTriangle(canvas: Canvas, x: Float, y: Float, w: Float) {
        val path = Path()
        path.moveTo(x + w / 2f, y)
        path.lineTo(x + w, y + w)
        path.lineTo(x, y + w)
        path.close()
        canvas.drawPath(path, fillPaint)
    }

    private fun drawHexagon(canvas: Canvas, x: Float, y: Float, w: Float) {
        val path = Path()
        for (i in 0 until 6) {
            val angle = 2f * Math.PI.toFloat() / 6f * i.toFloat()
            val px = x + w / 2f + (w / 2.2f) * kotlin.math.cos(angle.toDouble()).toFloat()
            val py = y + w / 2f + (w / 2.2f) * kotlin.math.sin(angle.toDouble()).toFloat()
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        path.close()
        canvas.drawPath(path, fillPaint)
    }

    private fun drawSparkle(canvas: Canvas, x: Float, y: Float, w: Float) {
        val path = Path()
        path.moveTo(x + w / 2f, y)
        path.quadTo(x + w / 2f, y + w / 2f, x + w, y + w / 2f)
        path.quadTo(x + w / 2f, y + w / 2f, x + w / 2f, y + w)
        path.quadTo(x + w / 2f, y + w / 2f, x, y + w / 2f)
        path.quadTo(x + w / 2f, y + w / 2f, x + w / 2f, y)
        canvas.drawPath(path, fillPaint)
    }

    private fun drawMoon(canvas: Canvas, cx: Float, cy: Float, r: Float) {
        val path = Path()
        path.addCircle(cx, cy, r, Path.Direction.CW)
        path.addCircle(cx - r / 3f, cy - r / 3f, r * 0.8f, Path.Direction.CCW)
        canvas.drawPath(path, fillPaint)
    }

    private fun drawGhost(canvas: Canvas, x: Float, y: Float, w: Float) {
        val path = Path()
        path.moveTo(x, y + w)
        path.lineTo(x, y + w / 2f)
        path.addCircle(x + w / 2f, y + w / 2f, w / 2f, Path.Direction.CW)
        path.moveTo(x + w, y + w / 2f)
        path.lineTo(x + w, y + w)
        path.lineTo(x + w * 0.8f, y + w * 0.8f)
        path.lineTo(x + w * 0.5f, y + w)
        path.lineTo(x + w * 0.2f, y + w * 0.8f)
        path.close()
        canvas.drawPath(path, fillPaint)
    }

    // ===== FRAME DRAWING FUNCTIONS =====

    private fun applyFrame(qrBitmap: Bitmap, frameType: FrameType, qrColor: Int, bgColor: Int): Bitmap {
        val qrSize = qrBitmap.width
        val pad = (qrSize * 0.15f).toInt()
        val topPad = (pad * 2.5f).toInt()

        val frameBitmap = Bitmap.createBitmap(qrSize + pad * 2, qrSize + pad * 2 + topPad, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(frameBitmap)

        // Draw transparent background
        canvas.drawColor(android.graphics.Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)

        val xOffset = pad.toFloat()
        val yOffset = topPad.toFloat()

        fillPaint.color = qrColor
        strokePaint.color = qrColor

        // Draw frame decorations
        when (frameType) {
            FrameType.BEAR -> {
                canvas.drawCircle(xOffset + pad * 1.2f, yOffset, pad * 1.5f, fillPaint)
                canvas.drawCircle(frameBitmap.width - xOffset - pad * 1.2f, yOffset, pad * 1.5f, fillPaint)
            }
            FrameType.CAT_FRAME -> {
                val path = Path()
                path.moveTo(xOffset, yOffset + pad)
                path.lineTo(xOffset + pad, yOffset - pad * 1.5f)
                path.lineTo(xOffset + pad * 2.5f, yOffset + pad)
                canvas.drawPath(path, fillPaint)
                path.rewind()
                path.moveTo(frameBitmap.width - xOffset, yOffset + pad)
                path.lineTo(frameBitmap.width - xOffset - pad, yOffset - pad * 1.5f)
                path.lineTo(frameBitmap.width - xOffset - pad * 2.5f, yOffset + pad)
                canvas.drawPath(path, fillPaint)
            }
            FrameType.TV -> {
                strokePaint.strokeWidth = pad * 0.3f
                strokePaint.strokeCap = Paint.Cap.ROUND
                canvas.drawLine(frameBitmap.width / 2f, yOffset, frameBitmap.width / 2f - pad * 1.5f, yOffset - pad * 1.5f, strokePaint)
                canvas.drawLine(frameBitmap.width / 2f, yOffset, frameBitmap.width / 2f + pad * 1.5f, yOffset - pad * 1.5f, strokePaint)
                canvas.drawLine(xOffset + pad, frameBitmap.height - pad.toFloat(),
                    xOffset + pad * 0.5f, frameBitmap.height - pad * 0.2f, strokePaint)
                canvas.drawLine(frameBitmap.width - xOffset - pad, frameBitmap.height - pad.toFloat(),
                    frameBitmap.width - xOffset - pad * 0.5f, frameBitmap.height - pad * 0.2f, strokePaint)
            }
            FrameType.PHONE -> {
                strokePaint.strokeWidth = pad * 0.5f
                strokePaint.strokeCap = Paint.Cap.ROUND
                canvas.drawLine(frameBitmap.width / 2f - pad * 1.5f, yOffset - pad * 1.2f,
                    frameBitmap.width / 2f + pad * 1.5f, yOffset - pad * 1.2f, strokePaint)
            }
            FrameType.CLOUD -> {
                for (i in 0..8) {
                    val step = qrSize / 8f * i
                    canvas.drawCircle(xOffset + step, yOffset - pad / 2f, pad * 1.2f, fillPaint)
                    canvas.drawCircle(xOffset + step, yOffset + qrSize + pad / 2f, pad * 1.2f, fillPaint)
                    canvas.drawCircle(xOffset - pad / 2f, yOffset + step, pad * 1.2f, fillPaint)
                    canvas.drawCircle(xOffset + qrSize + pad / 2f, yOffset + step, pad * 1.2f, fillPaint)
                }
            }
            FrameType.HOUSE -> {
                val path = Path()
                path.moveTo(frameBitmap.width / 2f, yOffset - pad * 2.5f)
                path.lineTo(xOffset - pad * 1.5f, yOffset)
                path.lineTo(frameBitmap.width - xOffset + pad * 1.5f, yOffset)
                path.close()
                canvas.drawPath(path, fillPaint)
            }
            FrameType.CROWN -> {
                val path = Path()
                path.moveTo(xOffset - pad, yOffset)
                path.lineTo(xOffset, yOffset - pad * 2.5f)
                path.lineTo(xOffset + qrSize / 3f, yOffset - pad)
                path.lineTo(frameBitmap.width / 2f, yOffset - pad * 3f)
                path.lineTo(frameBitmap.width - xOffset - qrSize / 3f, yOffset - pad)
                path.lineTo(frameBitmap.width - xOffset, yOffset - pad * 2.5f)
                path.lineTo(frameBitmap.width - xOffset + pad, yOffset)
                path.close()
                canvas.drawPath(path, fillPaint)
            }
            FrameType.FROG -> {
                canvas.drawCircle(xOffset + pad * 1.5f, yOffset - pad * 0.8f, pad * 1.8f, fillPaint)
                canvas.drawCircle(frameBitmap.width - xOffset - pad * 1.5f, yOffset - pad * 0.8f, pad * 1.8f, fillPaint)
                fillPaint.color = bgColor
                canvas.drawCircle(xOffset + pad * 1.5f, yOffset - pad * 0.8f, pad * 0.8f, fillPaint)
                canvas.drawCircle(frameBitmap.width - xOffset - pad * 1.5f, yOffset - pad * 0.8f, pad * 0.8f, fillPaint)
                fillPaint.color = qrColor
            }
            else -> {}
        }

        // Draw frame background
        fillPaint.color = qrColor
        drawRoundRect(canvas, pad / 2f, yOffset - pad / 2f, qrSize + pad.toFloat(), qrSize + pad.toFloat(), pad.toFloat())

        // Draw inner white area
        fillPaint.color = bgColor
        drawRoundRect(canvas, xOffset - 15f, yOffset - 15f, qrSize + 30f, qrSize + 30f, 15f)

        // Paste QR
        canvas.drawBitmap(qrBitmap, xOffset, yOffset, null)

        return frameBitmap
    }

    private fun drawRoundRect(canvas: Canvas, x: Float, y: Float, w: Float, h: Float, r: Float) {
        val rect = RectF(x, y, x + w, y + h)
        canvas.drawRoundRect(rect, r, r, fillPaint)
    }
}
