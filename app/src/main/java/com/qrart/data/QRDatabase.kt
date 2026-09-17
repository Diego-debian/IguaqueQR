package com.qrart.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QRHistory::class], version = 1, exportSchema = false)
abstract class QRDatabase : RoomDatabase() {
    abstract fun qrHistoryDao(): QRHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: QRDatabase? = null

        fun getDatabase(context: Context): QRDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QRDatabase::class.java,
                    "qr_art_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}