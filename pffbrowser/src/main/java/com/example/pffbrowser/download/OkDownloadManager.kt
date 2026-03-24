package com.example.pffbrowser.download

import android.content.Context
import android.os.Environment
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkDownload 下载管理器
 * 作为下载功能的统一入口，协调前台服务和任务仓库
 */
@Singleton
class OkDownloadManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val taskRepository: DownloadTaskRepository
) {
    companion object {
        const val DOWNLOAD_DIR = "PbDownloads"
    }

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * 下载状态流（用于通知栏等）
     */
    private val _downloadStatusFlow = MutableStateFlow<DownloadStatus>(DownloadStatus.Idle)
    val downloadStatusFlow: Flow<DownloadStatus> = _downloadStatusFlow.asStateFlow()

    /**
     * 初始化 OkDownload
     * 在 Application 的 onCreate 中调用
     */
    fun init() {
        // 如果已经初始化过，则跳过
        if (OkDownload.with() != null) {
            return
        }

        // 设置同时下载的最大任务数（由 Repository 控制，这里设为较大值）
        DownloadDispatcher.setMaxParallelRunningCount(10)

        // 配置 OkDownload
        val okDownloadBuilder = OkDownload.Builder(context)
            .connectionFactory(DownloadOkHttp3Connection.Factory())

        OkDownload.setSingletonInstance(okDownloadBuilder.build())

        // 设置 Repository 的回调
        taskRepository.downloadCallback = object : DownloadTaskRepository.DownloadTaskCallback {
            override fun onTaskStarted(taskId: String) {
                _downloadStatusFlow.value = DownloadStatus.Started(taskId)
            }

            override fun onTaskPaused(taskId: String) {
                _downloadStatusFlow.value = DownloadStatus.Paused(taskId)
            }

            override fun onTaskCompleted(taskId: String) {
                _downloadStatusFlow.value = DownloadStatus.Completed(taskId)
            }

            override fun onTaskError(taskId: String, errorMessage: String) {
                _downloadStatusFlow.value = DownloadStatus.Error(taskId, errorMessage)
            }

            override fun onProgress(taskId: String, downloadedBytes: Long, totalBytes: Long) {
                val progress = if (totalBytes > 0) {
                    ((downloadedBytes * 100) / totalBytes).toInt()
                } else 0

                _downloadStatusFlow.value = DownloadStatus.Progress(
                    taskId = taskId,
                    progress = progress,
                    downloadedBytes = downloadedBytes,
                    totalBytes = totalBytes
                )
            }
        }

        // 恢复之前未完成的下载任务
        resumeUnfinishedTasks()
    }

    /**
     * 开始下载文件
     * 启动前台服务来确保后台下载不被杀死
     *
     * @param url 下载链接
     * @param fileName 文件名
     * @param mimeType MIME 类型
     * @param contentLength 文件大小（可能为 -1）
     */
    fun startDownload(
        url: String,
        fileName: String,
        mimeType: String? = null,
        contentLength: Long = -1
    ) {
        // 启动前台服务
        DownloadForegroundService.startDownload(context, url, fileName, mimeType, contentLength)
    }

    /**
     * 实际执行下载（供 DownloadForegroundService 调用）
     * 不要直接调用此方法，应使用 startDownload()
     */
    suspend fun executeDownload(
        url: String,
        fileName: String,
        mimeType: String? = null,
        contentLength: Long = -1
    ) {
        taskRepository.addDownloadTask(url, fileName, mimeType, contentLength)
    }

    /**
     * 暂停下载任务
     */
    suspend fun pauseTask(taskId: String) {
        taskRepository.pauseTask(taskId)
    }

    /**
     * 继续下载任务
     * 先确保前台服务在运行，然后恢复下载
     */
    suspend fun resumeTask(taskId: String) {
        // 先确保前台服务在运行（后台保活）
        DownloadForegroundService.ensureServiceRunning(context)
        // 然后恢复下载任务
        taskRepository.resumeTask(taskId)
    }

    /**
     * 重试下载任务
     * 先确保前台服务在运行，然后重试下载
     */
    suspend fun retryTask(taskId: String) {
        // 先确保前台服务在运行（后台保活）
        DownloadForegroundService.ensureServiceRunning(context)
        // 然后重试下载任务
        taskRepository.retryTask(taskId)
    }

    /**
     * 删除下载任务
     */
    suspend fun deleteTask(taskId: String) {
        taskRepository.deleteTask(taskId)
    }

    /**
     * 获取所有下载任务
     */
    fun getAllTasks(): Flow<List<com.example.pffbrowser.download.db.DownloadTaskEntity>> {
        return taskRepository.getAllTasks()
    }

    /**
     * 获取活跃任务流（正在运行或等待中）
     * 用于前台服务监听，当所有任务完成时关闭服务
     */
    fun getActiveTasks(): Flow<List<com.example.pffbrowser.download.db.DownloadTaskEntity>> {
        return taskRepository.getActiveTasks()
    }

    /**
     * 获取当前活跃任务数量
     */
    suspend fun getActiveTaskCount(): Int {
        return taskRepository.getActiveTaskCount()
    }

    /**
     * 获取下载速度流
     * 用于 UI 实时显示下载速度，避免轮询
     */
    val downloadSpeedFlow = taskRepository.speedFlow

    /**
     * 获取下载目录
     */
    fun getDownloadDir(): File {
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            DOWNLOAD_DIR
        )
    }

    /**
     * 恢复未完成的下载任务（App 启动时调用）
     */
    private fun resumeUnfinishedTasks() {
        managerScope.launch {
            // 将之前正在运行的任务状态重置为暂停
            // 等待用户手动继续或新的下载触发队列检查
            // 这里不需要做特殊处理，Repository 会管理状态
        }
    }

    /**
     * 释放资源
     */
    fun shutdown() {
        OkDownload.with().downloadDispatcher().cancelAll()
    }

    /**
     * 下载状态密封类
     */
    sealed class DownloadStatus {
        object Idle : DownloadStatus()
        data class Started(val taskId: String) : DownloadStatus()
        data class Paused(val taskId: String) : DownloadStatus()
        data class Progress(
            val taskId: String,
            val progress: Int,
            val downloadedBytes: Long,
            val totalBytes: Long
        ) : DownloadStatus()

        data class Completed(val taskId: String) : DownloadStatus()
        data class Error(val taskId: String, val errorMessage: String) : DownloadStatus()
    }
}
