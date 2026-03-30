package com.example.pffbrowser.download.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.pffbrowser.download.DownloadStatus

/**
 * 下载任务实体类
 *
 * 索引说明：
 * - status索引：用于快速查询特定状态的任务（如正在下载、等待中）
 * - createTime索引：用于按时间排序查询
 */
@Entity(
    tableName = "download_tasks",
    indices = [
        Index(value = ["status"]),
        Index(value = ["createTime"])
    ]
)
data class DownloadTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // ========== 基本信息 ==========
    /**
     * 下载URL
     */
    val url: String,

    /**
     * 文件名
     */
    val fileName: String,

    /**
     * 文件保存路径（完整路径）
     */
    val filePath: String,

    /**
     * MIME类型
     */
    val mimeType: String?,

    // ========== 下载进度 ==========
    /**
     * 文件总大小（字节）
     * -1表示未知大小
     */
    val totalBytes: Long,

    /**
     * 已下载大小（字节）
     */
    val downloadedBytes: Long = 0,

    /**
     * 下载进度（0-100）
     */
    val progress: Int = 0,

    /**
     * 下载速度
     */
    val speed: Long = 0,

    // ========== 状态信息 ==========
    /**
     * 下载状态
     */
    val status: DownloadStatus,

    /**
     * 错误信息（失败时记录）
     */
    val errorMsg: String? = null,

    // ========== 时间信息 ==========
    /**
     * 创建时间（毫秒时间戳）
     */
    val createTime: Long,

    /**
     * 最后更新时间（毫秒时间戳）
     */
    val updateTime: Long,

    /**
     * 完成时间（毫秒时间戳）
     * null表示未完成
     */
    val completeTime: Long? = null,

    // ========== OkDownload相关 ==========
    /**
     * OkDownload任务ID
     * 用于关联OkDownload的下载任务
     */
    val okDownloadId: Int? = null
) {
    /**
     * 获取文件扩展名
     */
    fun getFileExtension(): String {
        return fileName.substringAfterLast('.', "")
    }

    /**
     * 是否可以暂停
     */
    fun canPause(): Boolean {
        return status == DownloadStatus.DOWNLOADING
    }

    /**
     * 是否可以恢复
     */
    fun canResume(): Boolean {
        return status.canResume()
    }

    /**
     * 是否可以删除
     */
    fun canDelete(): Boolean {
        return true  // 所有状态都可以删除
    }

    /**
     * 是否已完成
     */
    fun isCompleted(): Boolean {
        return status == DownloadStatus.COMPLETED
    }

    /**
     * 是否正在下载
     */
    fun isDownloading(): Boolean {
        return status == DownloadStatus.DOWNLOADING
    }
}
