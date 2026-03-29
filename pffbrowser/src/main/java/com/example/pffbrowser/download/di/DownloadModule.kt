package com.example.pffbrowser.download.di

import android.content.Context
import com.example.pffbrowser.download.notification.DownloadNotificationManager
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
object DownloadModule {

    /**
     * 提供DownloadNotificationManager单例
     */
    @Provides
    @Singleton
    fun provideDownloadNotificationManager(
        @ApplicationContext context: Context
    ): DownloadNotificationManager {
        return DownloadNotificationManager.getInstance(context)
    }

    /**
     * 提供DownloadManager单例
     * 注意：DownloadManager已经使用@Singleton和@Inject constructor，
     * Hilt会自动创建，这里不需要手动提供
     */

    /**
     * 提供DownloadRepository单例
     * 注意：DownloadRepository已经使用@Singleton和@Inject constructor，
     * Hilt会自动创建，这里不需要手动提供
     */
}
