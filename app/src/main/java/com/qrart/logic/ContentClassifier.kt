package com.qrart.logic

/**
 * Classifies the content label shown in the destination step.
 * The rules match the former private HomeScreen function.
 */
fun classifyContent(value: String): String {
    return when {
        value.startsWith("http://", ignoreCase = true) ||
            value.startsWith("https://", ignoreCase = true) -> "Enlace web"
        value.contains("@") && value.contains(".") -> "Contacto o correo"
        value.lines().size > 1 -> "Texto multilínea"
        else -> "Texto libre"
    }
}
