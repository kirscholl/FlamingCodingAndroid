package com.example.pffbrowser.download.database

import com.example.pffbrowser.download.DownloadStatus

/**
 * 下载任务构建器
 * 用于方便地创建DownloadTask对象
 */
class DownloadTaskBuilder {

    private var url: String = ""
    private var fileName: String = ""
    private var filePath: String = ""
    private var mimeType: String? = null
    private var totalBytes: Long = -1L
    private var downloadedBytes: Long = 0L
    private var progress: Int = 0
    private var status: DownloadStatus = DownloadStatus.PENDING
    private var errorMsg: String? = null
    private var createTime: Long = System.currentTimeMillis()
    private var updateTime: Long = System.currentTimeMillis()
    private var completeTime: Long? = null
    private var okDownloadId: Int? = null

    fun url(url: String) = apply { this.url = url }
    fun fileName(fileName: String) = apply { this.fileName = fileName }
    fun filePath(filePath: String) = apply { this.filePath = filePath }
    fun mimeType(mimeType: String?) = apply { this.mimeType = mimeType }
    fun totalBytes(totalBytes: Long) = apply { this.totalBytes = totalBytes }
    fun downloadedBytes(downloadedBytes: Long) = apply { this.downloadedBytes = downloadedBytes }
    fun progress(progress: Int) = apply { this.progress = progress }
    fun status(status: DownloadStatus) = apply { this.status = status }
    fun errorMsg(errorMsg: String?) = apply { this.errorMsg = errorMsg }
    fun createTime(createTime: Long) = apply { this.createTime = createTime }
    fun updateTime(updateTime: Long) = apply { this.updateTime = updateTime }
    fun completeTime(completeTime: Long?) = apply { this.completeTime = completeTime }
    fun okDownloadId(okDownloadId: Int?) = apply { this.okDownloadId = okDownloadId }

    fun build(): DownloadTask {
        require(url.isNotBlank()) { "URL不能为空" }
        require(fileName.isNotBlank()) { "文件名不能为空" }
        require(filePath.isNotBlank()) { "文件路径不能为空" }

        return DownloadTask(
            url = url,
            fileName = fileName,
            filePath = filePath,
            mimeType = mimeType,
            totalBytes = totalBytes,
            downloadedBytes = downloadedBytes,
            progress = progress,
            status = status,
            errorMsg = errorMsg,
            createTime = createTime,
            updateTime = updateTime,
            completeTime = completeTime,
            okDownloadId = okDownloadId
        )
    }

    companion object {
        /**
         * 创建一个新的构建器
         */
        fun create() = DownloadTaskBuilder()

        /**
         * 快速创建一个待下载任务
         */
        fun createPendingTask(
            url: String,
            fileName: String,
            filePath: String,
            totalBytes: Long = -1L,
            mimeType: String? = null
        ): DownloadTask {
            return create()
                .url(url)
                .fileName(fileName)
                .filePath(filePath)
                .totalBytes(totalBytes)
                .mimeType(mimeType)
                .status(DownloadStatus.PENDING)
                .build()
        }
    }
}
