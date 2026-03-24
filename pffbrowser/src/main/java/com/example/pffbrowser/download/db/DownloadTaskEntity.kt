package com.example.pffbrowser.download.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 下载任务状态枚举
 */
enum class DownloadTaskStatus {
    PENDING,      // 等待中
    RUNNING,      // 下载中
    PAUSED,       // 已暂停
    COMPLETED,    // 已完成
    ERROR         // 下载失败
}

/**
 * 下载任务实体
 * 用于 Room 数据库存储
 */
@Entity(
    tableName = "download_tasks",
    indices = [
        Index(value = ["status"]),
        Index(value = ["createTime"])
    ]
)
data class DownloadTaskEntity(
    @PrimaryKey
    val taskId: String,           // 唯一ID: url.hashCode()_filename

    val url: String,              // 下载链接
    val fileName: String,         // 文件名
    val mimeType: String?,        // MIME 类型
    val filePath: String,         // 完整文件路径

    // 文件大小信息
    val totalBytes: Long,         // 总字节数
    val downloadedBytes: Long,    // 已下载字节数

    // 状态
    val status: DownloadTaskStatus,

    // 时间戳
    val createTime: Long,         // 创建时间（用于排序，最新的在上）
    val completeTime: Long?,      // 完成时间
    val lastUpdateTime: Long      // 最后更新时间
)

/**
 * 获取下载进度百分比
 */
fun DownloadTaskEntity.getProgressPercent(): Int {
    return if (totalBytes > 0) {
        ((downloadedBytes * 100) / totalBytes).toInt()
    } else 0
}

/**
 * 检查是否是已完成状态
 */
fun DownloadTaskEntity.isCompleted(): Boolean {
    return status == DownloadTaskStatus.COMPLETED
}

/**
 * 检查是否可以暂停
 */
fun DownloadTaskEntity.canPause(): Boolean {
    return status == DownloadTaskStatus.RUNNING
}

/**
 * 检查是否可以继续
 */
fun DownloadTaskEntity.canResume(): Boolean {
    return status == DownloadTaskStatus.PAUSED || status == DownloadTaskStatus.ERROR
}

/**
 * 检查是否可以重试
 */
fun DownloadTaskEntity.canRetry(): Boolean {
    return status == DownloadTaskStatus.ERROR
}
