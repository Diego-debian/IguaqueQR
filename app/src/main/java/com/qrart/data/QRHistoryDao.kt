package com.qrart.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QRHistoryDao {
    @Query("SELECT * FROM qr_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<QRHistory>>

    @Insert
    suspend fun insert(history: QRHistory)

    @Delete
    suspend fun delete(history: QRHistory)

    @Query("DELETE FROM qr_history")
    suspend fun deleteAll()
}