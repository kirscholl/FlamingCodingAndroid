package com.example.pffbrowser.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pffbrowser.download.db.DownloadTaskDao
import com.example.pffbrowser.download.db.DownloadTaskEntity

/**
 * PbBrowser 综合数据库
 * 包含搜索历史和下载任务等所有数据表
 */
@Database(
    version = 1,
    entities = [
        SearchHistoryEntity::class,
        DownloadTaskEntity::class
    ]
)
abstract class PbBrowserDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun downloadTaskDao(): DownloadTaskDao

    companion object {

        private const val DATABASE_NAME = "pb_browser.db"
        private var instance: PbBrowserDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): PbBrowserDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                PbBrowserDatabase::class.java,
                DATABASE_NAME
            ).build().also {
                instance = it
            }
        }
    }
}
