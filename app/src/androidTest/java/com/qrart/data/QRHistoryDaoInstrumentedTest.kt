package com.qrart.data

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.Closeable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QRHistoryDaoInstrumentedTest {
    private lateinit var database: QRDatabase
    private lateinit var dao: QRHistoryDao

    @Before
    fun setUp() {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, QRDatabase::class.java).build()
        dao = database.qrHistoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertsAndQueriesGeneratedRecord() = runBlocking {
        val expected = QRHistory(
            content = "https://ejemplo.com/á",
            type = "generated",
            dotStyle = "CIRCLE",
            frameType = "BEAR",
            qrColor = 0xFF112233.toInt(),
            bgColor = 0xFFFFFFFF.toInt(),
            timestamp = 100L
        )

        dao.insert(expected)
        val actual = dao.getAllHistory().first().single()

        assertEquals(expected.content, actual.content)
        assertEquals(expected.type, actual.type)
        assertEquals(expected.dotStyle, actual.dotStyle)
        assertEquals(expected.frameType, actual.frameType)
        assertEquals(expected.qrColor, actual.qrColor)
        assertEquals(expected.bgColor, actual.bgColor)
        assertEquals(expected.timestamp, actual.timestamp)
    }

    @Test
    fun ordersHistoryByTimestampDescending() = runBlocking {
        dao.insert(QRHistory(content = "old", type = "generated", timestamp = 1L))
        dao.insert(QRHistory(content = "new", type = "generated", timestamp = 2L))

        assertEquals(listOf("new", "old"), dao.getAllHistory().first().map { it.content })
    }

    @Test
    fun deletesOneRecordAndClearsAllRecords() = runBlocking {
        val first = QRHistory(content = "first", type = "generated", timestamp = 1L)
        val second = QRHistory(content = "second", type = "generated", timestamp = 2L)
        dao.insert(first)
        dao.insert(second)
        val inserted = dao.getAllHistory().first()

        dao.delete(inserted.first { it.content == "first" })
        assertEquals(listOf("second"), dao.getAllHistory().first().map { it.content })

        dao.deleteAll()
        assertTrue(dao.getAllHistory().first().isEmpty())
    }

    @Test
    fun newDatabaseStartsWithEmptyHistory() = runBlocking {
        assertTrue(dao.getAllHistory().first().isEmpty())
    }
}
