package com.example.pffbrowser.mine.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BrowserHistory::class, BrowserHistoryFts::class],
    version = 1,
    exportSchema = true
)
abstract class BrowserHistoryDatabase : RoomDatabase() {

    abstract fun browserHistoryDao(): BrowserHistoryDao

    companion object {
        const val DATABASE_NAME = "browser_history_database"
    }
}
