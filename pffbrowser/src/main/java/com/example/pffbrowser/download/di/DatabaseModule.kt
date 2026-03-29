package com.example.pffbrowser.download.di

import android.content.Context
import androidx.room.Room
import com.example.pffbrowser.download.database.DownloadDatabase
import com.example.pffbrowser.download.database.DownloadTaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 下载模块的Hilt依赖注入配置
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 提供下载数据库实例（单例）
     */
    @Provides
    @Singleton
    fun provideDownloadDatabase(
        @ApplicationContext context: Context
    ): DownloadDatabase {
        return Room.databaseBuilder(
            context,
            DownloadDatabase::class.java,
            DownloadDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()  // 开发阶段使用，生产环境需要提供Migration
            .build()
    }

    /**
     * 提供下载任务DAO（单例）
     */
    @Provides
    @Singleton
    fun provideDownloadTaskDao(database: DownloadDatabase): DownloadTaskDao {
        return database.downloadTaskDao()
    }
}
