package com.example.pffbrowser.download.database

import androidx.room.TypeConverter
import com.example.pffbrowser.download.DownloadStatus

/**
 * Room类型转换器
 * 用于将枚举类型转换为数据库可存储的类型
 */
class Converters {

    /**
     * 将DownloadStatus枚举转换为String存储
     */
    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus): String {
        return status.name
    }

    /**
     * 将String转换为DownloadStatus枚举
     */
    @TypeConverter
    fun toDownloadStatus(value: String): DownloadStatus {
        return try {
            DownloadStatus.valueOf(value)
        } catch (e: IllegalArgumentException) {
            // 如果数据库中的值无效，默认返回FAILED
            DownloadStatus.FAILED
        }
    }
}
