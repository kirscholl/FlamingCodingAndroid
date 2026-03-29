package com.example.pffbrowser.download.okdownload

import android.content.Context
import android.util.Log
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed
import java.io.File

/**
 * OkDownload辅助类
 * 封装OkDownload的创建和配置，提供统一的下载任务管理接口
 *
 * 功能：
 * - 创建下载任务
 * - 配置下载参数（线程数、超时、回调间隔等）
 * - 暂停/恢复/取消下载
 * - 查询任务状态
 */
object OkDownloadHelper {

    private const val TAG = "OkDownloadHelper"

    // 默认配置
    private const val DEFAULT_CONNECTION_COUNT = 3  // 默认3个线程下载
    private const val DEFAULT_READ_TIMEOUT = 30000  // 读取超时30秒
    private const val DEFAULT_CONNECT_TIMEOUT = 15000  // 连接超时15秒
    private const val DEFAULT_CALLBACK_INTERVAL = 500  // 进度回调间隔500ms

    /**
     * 创建下载任务
     *
     * @param url 下载URL
     * @param filePath 保存路径（完整路径）
     * @param listener 下载监听器
     * @param connectionCount 连接数（线程数），默认3
     * @return DownloadTask
     */
    fun createDownloadTask(
        url: String,
        filePath: String,
        listener: DownloadListener4WithSpeed,
        connectionCount: Int = DEFAULT_CONNECTION_COUNT
    ): DownloadTask {
        Log.d(TAG, "创建下载任务: url=$url, filePath=$filePath, connectionCount=$connectionCount")

        val file = File(filePath)

        // 确保父目录存在
        file.parentFile?.let { parentDir ->
            if (!parentDir.exists()) {
                parentDir.mkdirs()
            }
        }

        return DownloadTask.Builder(url, file)
            .setConnectionCount(connectionCount)  // 设置线程数
            .setReadBufferSize(8192)  // 读取缓冲区大小8KB
            .setFlushBufferSize(16384)  // 刷新缓冲区大小16KB
            .setMinIntervalMillisCallbackProcess(DEFAULT_CALLBACK_INTERVAL)  // 进度回调间隔
            .setPassIfAlreadyCompleted(false)  // 如果已完成，不跳过
            .setAutoCallbackToUIThread(true)  // 自动切换到UI线程回调
            .setWifiRequired(false)  // 不要求WiFi
            .build()
            .also {
                // 设置监听器
                it.enqueue(listener)
            }
    }

    /**
     * 开始下载
     *
     * @param task 下载任务
     */
    fun startDownload(task: DownloadTask) {
        Log.d(TAG, "开始下载: ${task.url}")
        task.execute(task.listener)
    }

    /**
     * 暂停下载
     *
     * @param task 下载任务
     */
    fun pauseDownload(task: DownloadTask) {
        Log.d(TAG, "暂停下载: ${task.url}")
        task.cancel()
    }

    /**
     * 取消下载
     *
     * @param task 下载任务
     */
    fun cancelDownload(task: DownloadTask) {
        Log.d(TAG, "取消下载: ${task.url}")
        task.cancel()
    }

    /**
     * 检查任务是否正在运行
     *
     * @param task 下载任务
     * @return true表示正在运行
     */
    fun isTaskRunning(task: DownloadTask): Boolean {
        // OkDownload没有直接的API判断任务是否运行
        // 可以通过检查任务状态来判断
        return try {
            val info = task.info
            info != null && !info.isChunked
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取任务的下载进度
     *
     * @param task 下载任务
     * @return Pair<已下载字节数, 总字节数>，如果无法获取返回null
     */
    fun getTaskProgress(task: DownloadTask): Pair<Long, Long>? {
        return try {
            val info = task.info
            if (info != null) {
                val currentBytes = info.totalOffset
                val totalBytes = info.totalLength
                Pair(currentBytes, totalBytes)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取任务进度失败", e)
            null
        }
    }

    /**
     * 删除任务的断点信息
     * 用于重新下载或清理任务
     *
     * @param context 上下文
     * @param task 下载任务
     */
    fun removeTaskBreakpoint(context: Context, task: DownloadTask) {
        try {
            Log.d(TAG, "删除断点信息: ${task.url}")
            // OkDownload会自动管理断点信息
            // 如果需要删除，可以删除文件
            task.file?.delete()
        } catch (e: Exception) {
            Log.e(TAG, "删除断点信息失败", e)
        }
    }

    /**
     * 检查是否可以恢复下载
     *
     * @param task 下载任务
     * @return true表示可以恢复
     */
    fun canResumeDownload(task: DownloadTask): Boolean {
        return try {
            val info = task.info
            info != null && info.totalOffset > 0 && info.totalOffset < info.totalLength
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取任务的文件路径
     *
     * @param task 下载任务
     * @return 文件路径
     */
    fun getTaskFilePath(task: DownloadTask): String? {
        return task.file?.absolutePath
    }

    /**
     * 检查文件是否已下载完成
     *
     * @param task 下载任务
     * @return true表示已完成
     */
    fun isTaskCompleted(task: DownloadTask): Boolean {
        return try {
            val file = task.file
            if (file == null || !file.exists()) {
                return false
            }

            val info = task.info
            if (info == null) {
                return false
            }

            // 检查文件大小是否匹配
            file.length() == info.totalLength
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 格式化恢复失败原因
     *
     * @param cause 恢复失败原因
     * @return 可读的错误信息
     */
    fun formatResumeFailedCause(cause: ResumeFailedCause): String {
        return when (cause) {
            ResumeFailedCause.RESPONSE_CREATED_RANGE_NOT_FROM_0 -> "服务器不支持断点续传"
            ResumeFailedCause.RESPONSE_PRECONDITION_FAILED -> "文件已被修改，无法续传"
            ResumeFailedCause.RESPONSE_RESET_RANGE_NOT_FROM_0 -> "服务器重置了下载范围"
            ResumeFailedCause.RESPONSE_ETAG_CHANGED -> "文件ETag已变化，无法续传"
            ResumeFailedCause.FILE_NOT_EXIST -> "本地文件不存在"
            ResumeFailedCause.INFO_DIRTY -> "断点信息损坏"
//            ResumeFailedCause.OUTPUT_STREAM_NOT_SUPPORT_SEEK -> "输出流不支持定位"
            else -> "无法恢复下载: $cause"
        }
    }

    /**
     * 创建简单的下载任务（使用默认配置）
     *
     * @param url 下载URL
     * @param filePath 保存路径
     * @param onProgress 进度回调
     * @param onCompleted 完成回调
     * @param onFailed 失败回调
     * @return DownloadTask
     */
    fun createSimpleDownloadTask(
        url: String,
        filePath: String,
        onProgress: (currentBytes: Long, totalBytes: Long, progress: Int, speed: Long) -> Unit,
        onCompleted: (totalBytes: Long) -> Unit,
        onFailed: (errorMsg: String, exception: Exception?) -> Unit
    ): DownloadTask {
        val listener = object : BaseDownloadListener() {
            override fun onDownloadProgress(
                currentBytes: Long,
                totalBytes: Long,
                progress: Int,
                speed: Long
            ) {
                onProgress(currentBytes, totalBytes, progress, speed)
            }

            override fun onDownloadCompleted(totalBytes: Long) {
                onCompleted(totalBytes)
            }

            override fun onDownloadFailed(errorMsg: String, exception: Exception?) {
                onFailed(errorMsg, exception)
            }
        }

        return createDownloadTask(url, filePath, listener)
    }
}
