package com.qrart.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_history")
data class QRHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val type: String, // "generated"
    val dotStyle: String = "square",
    val frameType: String = "none",
    val qrColor: Int = 0xFF0F172A.toInt(),
    val bgColor: Int = 0xFFFFFFFF.toInt(),
    val timestamp: Long = System.currentTimeMillis()
)
