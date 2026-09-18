package com.qrart.logic

import org.junit.Assert.assertEquals
import org.junit.Test

class ContentClassifierTest {
    @Test fun classifiesHttpUrl() = assertEquals("Enlace web", classifyContent("http://ejemplo.com"))

    @Test fun classifiesHttpsUrlIgnoringCase() =
        assertEquals("Enlace web", classifyContent("HTTPS://ejemplo.com/ruta"))

    @Test fun classifiesEmailLikeContent() =
        assertEquals("Contacto o correo", classifyContent("persona@ejemplo.com"))

    @Test fun classifiesPlainText() = assertEquals("Texto libre", classifyContent("Hola Iguaque"))

    @Test fun classifiesMultilineText() =
        assertEquals("Texto multilínea", classifyContent("Línea uno\nLínea dos"))

    @Test fun classifiesEmptyAndWhitespaceAsPlainTextLikeCurrentBehavior() {
        assertEquals("Texto libre", classifyContent(""))
        assertEquals("Texto libre", classifyContent("   \t"))
    }

    @Test fun doesNotTreatUrlWithoutSchemeAsWebLink() =
        assertEquals("Texto libre", classifyContent("www.ejemplo.com"))

    @Test fun preservesUnicodeText() =
        assertEquals("Texto libre", classifyContent("Diseño QR: ñandú ✨"))
}
