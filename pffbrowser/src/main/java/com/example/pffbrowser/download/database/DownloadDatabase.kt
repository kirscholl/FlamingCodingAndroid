package com.example.pffbrowser.download.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * 下载数据库
 *
 * 版本历史：
 * - v1: 初始版本，包含download_tasks表
 */
@Database(
    entities = [DownloadTask::class],
    version = 1,
    exportSchema = true  // 导出schema用于版本管理
)
@TypeConverters(Converters::class)
abstract class DownloadDatabase : RoomDatabase() {

    /**
     * 获取下载任务DAO
     */
    abstract fun downloadTaskDao(): DownloadTaskDao

    companion object {
        /**
         * 数据库名称
         */
        const val DATABASE_NAME = "download_database"
    }
}
