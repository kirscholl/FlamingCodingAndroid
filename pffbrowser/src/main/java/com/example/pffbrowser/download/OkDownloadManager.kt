package com.example.pffbrowser.download

import android.content.Context
import android.os.Environment
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkDownload 下载管理器
 * 支持后台下载和断点续传
 */
@Singleton
class OkDownloadManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        const val DOWNLOAD_DIR = "PbDownloads"
        const val MAX_PARALLEL_DOWNLOADS = 3
    }

    private val _downloadStatusFlow = MutableStateFlow<DownloadStatus>(DownloadStatus.Idle)
    val downloadStatusFlow: Flow<DownloadStatus> = _downloadStatusFlow.asStateFlow()

    /**
     * 跟踪正在进行的下载任务
     */
    private val activeTasks = ConcurrentHashMap<String, DownloadTask>()

    /**
     * 初始化 OkDownload
     * 在 Application 的 onCreate 中调用
     */
    fun init() {
        // 如果已经初始化过，则跳过
        if (OkDownload.with() != null) {
            return
        }

        // 设置同时下载的最大任务数
        DownloadDispatcher.setMaxParallelRunningCount(MAX_PARALLEL_DOWNLOADS)

        // 配置 OkDownload
        val okDownloadBuilder = OkDownload.Builder(context)
            .connectionFactory(DownloadOkHttp3Connection.Factory())

        OkDownload.setSingletonInstance(okDownloadBuilder.build())
    }

    /**
     * 开始下载文件
     * 使用前台服务确保后台下载不被杀死
     *
     * @param url 下载链接
     * @param fileName 文件名
     * @return DownloadTask
     */
    fun enqueueDownload(url: String, fileName: String): DownloadTask {
        // 启动前台服务
        DownloadForegroundService.startDownload(context, url, fileName)

        val downloadDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            DOWNLOAD_DIR
        )

        // 确保目录存在
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }

        val task = DownloadTask.Builder(url, downloadDir)
            .setFilename(fileName)
            .setPassIfAlreadyCompleted(false)
            .setConnectionCount(1)
            .setPreAllocateLength(false)
            .build()

        // 添加到活跃任务列表
        activeTasks[task.id.toString()] = task

        // 设置下载监听器
        task.enqueue(object : DownloadListener {
            override fun taskStart(task: DownloadTask) {
                _downloadStatusFlow.value = DownloadStatus.Started(task.id.toString())
            }

            override fun connectTrialStart(
                task: DownloadTask,
                requestHeaderFields: MutableMap<String, MutableList<String>>
            ) {
            }

            override fun connectTrialEnd(
                task: DownloadTask,
                responseCode: Int,
                responseHeaderFields: MutableMap<String, MutableList<String>>
            ) {
            }

            override fun downloadFromBeginning(
                task: DownloadTask,
                info: BreakpointInfo,
                cause: ResumeFailedCause
            ) {
            }

            override fun downloadFromBreakpoint(
                task: DownloadTask,
                info: BreakpointInfo
            ) {
            }

            override fun connectStart(
                task: DownloadTask,
                blockIndex: Int,
                requestHeaderFields: MutableMap<String, MutableList<String>>
            ) {
            }

            override fun connectEnd(
                task: DownloadTask,
                blockIndex: Int,
                responseCode: Int,
                responseHeaderFields: MutableMap<String, MutableList<String>>
            ) {
            }

            override fun fetchStart(task: DownloadTask, blockIndex: Int, contentLength: Long) {
            }

            override fun fetchProgress(task: DownloadTask, blockIndex: Int, increaseBytes: Long) {
                val totalBytes = task.info?.totalLength ?: 0
                val downloadedBytes = task.info?.totalOffset ?: 0
                val progress = if (totalBytes > 0) {
                    ((downloadedBytes * 100) / totalBytes).toInt()
                } else 0

                _downloadStatusFlow.value = DownloadStatus.Progress(
                    taskId = task.id.toString(),
                    progress = progress,
                    downloadedBytes = downloadedBytes,
                    totalBytes = totalBytes,
                    fileName = task.filename ?: ""
                )
            }

            override fun fetchEnd(task: DownloadTask, blockIndex: Int, contentLength: Long) {
            }

            override fun taskEnd(
                task: DownloadTask,
                cause: com.liulishuo.okdownload.core.cause.EndCause,
                realCause: Exception?
            ) {
                // 从活跃任务列表中移除
                activeTasks.remove(task.id.toString())

                when (cause) {
                    com.liulishuo.okdownload.core.cause.EndCause.COMPLETED -> {
                        _downloadStatusFlow.value = DownloadStatus.Completed(task.id.toString())
                    }

                    com.liulishuo.okdownload.core.cause.EndCause.ERROR -> {
                        _downloadStatusFlow.value = DownloadStatus.Error(
                            taskId = task.id.toString(),
                            errorMessage = realCause?.message ?: "Unknown error"
                        )
                    }

                    com.liulishuo.okdownload.core.cause.EndCause.CANCELED -> {
                        _downloadStatusFlow.value = DownloadStatus.Cancelled(task.id.toString())
                    }

                    else -> {}
                }
            }
        })

        return task
    }

    /**
     * 暂停下载
     */
    fun pauseTask(task: DownloadTask) {
        task.cancel()
    }

    /**
     * 取消下载
     */
    fun cancelTask(task: DownloadTask) {
        task.cancel()
        activeTasks.remove(task.id.toString())
    }

    /**
     * 检查是否有正在运行的下载任务
     */
    fun hasRunningTasks(): Boolean {
        return activeTasks.isNotEmpty()
    }

    /**
     * 获取活跃任务数量
     */
    fun getActiveTaskCount(): Int {
        return activeTasks.size
    }

    /**
     * 释放资源
     */
    fun shutdown() {
        OkDownload.with().downloadDispatcher().cancelAll()
        activeTasks.clear()
    }

    /**
     * 下载状态密封类
     */
    sealed class DownloadStatus {
        object Idle : DownloadStatus()
        data class Started(val taskId: String) : DownloadStatus()
        data class Progress(
            val taskId: String,
            val progress: Int,
            val downloadedBytes: Long,
            val totalBytes: Long,
            val fileName: String
        ) : DownloadStatus()

        data class Completed(val taskId: String) : DownloadStatus()
        data class Cancelled(val taskId: String) : DownloadStatus()
        data class Error(val taskId: String, val errorMessage: String) : DownloadStatus()
    }
}
