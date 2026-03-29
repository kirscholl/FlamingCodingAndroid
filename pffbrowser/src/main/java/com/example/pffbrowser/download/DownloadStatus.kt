package com.example.pffbrowser.download

/**
 * 下载任务状态枚举
 */
enum class DownloadStatus {
    /**
     * 等待中（排队等待下载）
     */
    PENDING,

    /**
     * 下载中
     */
    DOWNLOADING,

    /**
     * 已暂停
     */
    PAUSED,

    /**
     * 已完成
     */
    COMPLETED,

    /**
     * 失败
     */
    FAILED,

    /**
     * 已取消
     */
    CANCELED;

    /**
     * 是否为终止状态（不可恢复）
     */
    fun isTerminal(): Boolean {
        return this == COMPLETED || this == CANCELED
    }

    /**
     * 是否为活跃状态（正在下载或等待）
     */
    fun isActive(): Boolean {
        return this == DOWNLOADING || this == PENDING
    }

    /**
     * 是否可以恢复下载
     */
    fun canResume(): Boolean {
        return this == PAUSED || this == FAILED
    }
}
