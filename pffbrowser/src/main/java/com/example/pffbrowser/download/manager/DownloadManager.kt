package com.example.pffbrowser.download.manager

import android.content.Context
import android.util.Log
import com.example.pffbrowser.download.DownloadStatus
import com.example.pffbrowser.download.database.DownloadTask
import com.example.pffbrowser.download.database.DownloadTaskDao
import com.example.pffbrowser.download.notification.DownloadNotificationManager
import com.example.pffbrowser.download.okdownload.BaseDownloadListener
import com.example.pffbrowser.download.okdownload.OkDownloadHelper
import com.liulishuo.okdownload.DownloadTask as OkDownloadTask
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 下载管理器（主类）
 * 整合所有子模块，提供统一的下载管理接口
 *
 * 功能：
 * - 开始/暂停/恢复/删除下载
 * - 队列管理（最多2个并发）
 * - 状态管理
 * - 进度管理
 * - 通知管理
 */
@Singleton
class DownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadTaskDao: DownloadTaskDao,
    private val notificationManager: DownloadNotificationManager
) {
    companion object {
        private const val TAG = "DownloadManager"
    }

    // 协程作用域
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // 子模块
    private val queueManager = DownloadQueueManager(downloadTaskDao)
    private val stateManager = DownloadStateManager()
    private val progressManager = DownloadProgressManager(
        downloadTaskDao,
        notificationManager,
        scope
    )

    // OkDownload任务映射Map<taskId, OkDownloadTask>
    private val okDownloadTasks = ConcurrentHashMap<Long, OkDownloadTask>()

    init {
        // 初始化通知节流器
        progressManager.initNotificationThrottler {
            updateNotification()
        }

        Log.d(TAG, "DownloadManager初始化完成")
    }

    /**
     * 开始下载
     *
     * @param taskId 任务ID
     */
    suspend fun startDownload(taskId: Long) {
        Log.d(TAG, "开始下载: taskId=$taskId")

        // 获取任务信息
        val task = downloadTaskDao.getTaskById(taskId)
        if (task == null) {
            Log.e(TAG, "任务不存在: taskId=$taskId")
            return
        }

        // 验证状态转换
        if (!stateManager.isValidTransition(task.status, DownloadStatus.DOWNLOADING)) {
            Log.w(TAG, "无法开始下载，状态转换非法: ${task.status} → DOWNLOADING")
            return
        }

        // 添加到队列
        val canStart = queueManager.addToQueue(taskId)

        if (canStart) {
            // 可以立即开始
            executeDownload(task)
        } else {
            // 需要等待
            Log.d(TAG, "任务添加到等待队列: taskId=$taskId")

            // 更新状态为PENDING
            downloadTaskDao.updateStatus(
                id = taskId,
                status = DownloadStatus.PENDING,
                time = System.currentTimeMillis()
            )
        }

        // 重置通知删除标志
        notificationManager.resetDismissFlag()

        // 立即更新通知
        progressManager.updateNotificationImmediately()
    }

    /**
     * 执行下载
     */
    private suspend fun executeDownload(task: DownloadTask) {
        Log.d(TAG, "执行下载: taskId=${task.id}, fileName=${task.fileName}")

        // 创建下载监听器
        val listener = createDownloadListener(task.id)

        // 创建OkDownload任务
        val okTask = OkDownloadHelper.createDownloadTask(
            url = task.url,
            filePath = task.filePath,
            listener = listener
        )

        // 保存任务映射
        okDownloadTasks[task.id] = okTask

        // 更新数据库状态
        downloadTaskDao.updateStatus(
            id = task.id,
            status = DownloadStatus.DOWNLOADING,
            time = System.currentTimeMillis()
        )

        // 记录状态转换
        stateManager.logTransition(task.id, task.status, DownloadStatus.DOWNLOADING)

        // 开始下载
        OkDownloadHelper.startDownload(okTask)
    }

    /**
     * 创建下载监听器
     */
    private fun createDownloadListener(taskId: Long): BaseDownloadListener {
        return object : BaseDownloadListener() {
            override fun onDownloadProgress(
                currentBytes: Long,
                totalBytes: Long,
                progress: Int,
                speed: Long
            ) {
                // 处理进度更新
                progressManager.onProgressUpdate(taskId, currentBytes, progress, speed)
            }

            override fun onDownloadCompleted(totalBytes: Long) {
                // 下载完成
                scope.launch {
                    handleDownloadCompleted(taskId, totalBytes)
                }
            }

            override fun onDownloadFailed(errorMsg: String, exception: Exception?) {
                // 下载失败
                scope.launch {
                    handleDownloadFailed(taskId, errorMsg)
                }
            }

            override fun onDownloadCanceled() {
                // 下载取消（暂停）
                Log.d(TAG, "下载已取消: taskId=$taskId")
            }
        }
    }

    /**
     * 处理下载完成
     */
    private suspend fun handleDownloadCompleted(taskId: Long, totalBytes: Long) {
        Log.d(TAG, "处理下载完成: taskId=$taskId")

        // 强制刷新进度
        progressManager.flushProgress(taskId)

        // 更新数据库状态
        downloadTaskDao.markAsCompleted(
            id = taskId,
            completeTime = System.currentTimeMillis()
        )

        // 获取任务信息
        val task = downloadTaskDao.getTaskById(taskId)

        // 显示完成通知
        if (task != null) {
            notificationManager.showCompletedNotification(task)
        }

        // 从队列移除
        queueManager.removeFromQueue(taskId)

        // 移除OkDownload任务
        okDownloadTasks.remove(taskId)

        // 移除进度管理
        progressManager.removeTask(taskId)

        // 重置通知删除标志
        notificationManager.resetDismissFlag()

        // 立即更新通知
        progressManager.updateNotificationImmediately()

        // 启动等待中的任务
        checkAndStartPendingTasks()
    }

    /**
     * 处理下载失败
     */
    private suspend fun handleDownloadFailed(taskId: Long, errorMsg: String) {
        Log.e(TAG, "处理下载失败: taskId=$taskId, error=$errorMsg")

        // 强制刷新进度
        progressManager.flushProgress(taskId)

        // 更新数据库状态
        downloadTaskDao.updateStatusWithError(
            id = taskId,
            status = DownloadStatus.FAILED,
            errorMsg = errorMsg,
            time = System.currentTimeMillis()
        )

        // 从队列移除
        queueManager.removeFromQueue(taskId)

        // 移除OkDownload任务
        okDownloadTasks.remove(taskId)

        // 移除进度管理
        progressManager.removeTask(taskId)

        // 重置通知删除标志
        notificationManager.resetDismissFlag()

        // 立即更新通知
        progressManager.updateNotificationImmediately()

        // 启动等待中的任务
        checkAndStartPendingTasks()
    }

    /**
     * 暂停下载
     */
    suspend fun pauseDownload(taskId: Long) {
        Log.d(TAG, "暂停下载: taskId=$taskId")

        // 获取任务信息
        val task = downloadTaskDao.getTaskById(taskId)
        if (task == null) {
            Log.e(TAG, "任务不存在: taskId=$taskId")
            return
        }

        // 验证状态转换
        if (!stateManager.canPause(task.status)) {
            Log.w(TAG, "无法暂停下载，当前状态: ${task.status}")
            return
        }

        // 获取OkDownload任务
        val okTask = okDownloadTasks[taskId]
        if (okTask != null) {
            // 暂停下载
            OkDownloadHelper.pauseDownload(okTask)

            // 强制刷新进度
            progressManager.flushProgress(taskId)
        }

        // 更新数据库状态
        downloadTaskDao.updateStatus(
            id = taskId,
            status = DownloadStatus.PAUSED,
            time = System.currentTimeMillis()
        )

        // 记录状态转换
        stateManager.logTransition(taskId, task.status, DownloadStatus.PAUSED)

        // 从队列移除
        queueManager.removeFromQueue(taskId)

        // 移除OkDownload任务
        okDownloadTasks.remove(taskId)

        // 移除进度管理
        progressManager.removeTask(taskId)

        // 重置通知删除标志
        notificationManager.resetDismissFlag()

        // 立即更新通知
        progressManager.updateNotificationImmediately()

        // 启动等待中的任务
        checkAndStartPendingTasks()
    }

    /**
     * 恢复下载（暂停或失败的任务）
     */
    suspend fun resumeDownload(taskId: Long) {
        Log.d(TAG, "恢复下载: taskId=$taskId")

        // 获取任务信息
        val task = downloadTaskDao.getTaskById(taskId)
        if (task == null) {
            Log.e(TAG, "任务不存在: taskId=$taskId")
            return
        }

        // 验证状态
        if (!stateManager.canResume(task.status)) {
            Log.w(TAG, "无法恢复下载，当前状态: ${task.status}")
            return
        }

        // 调用startDownload（会自动处理队列）
        startDownload(taskId)
    }

    /**
     * 删除任务
     */
    suspend fun deleteTask(taskId: Long, deleteFile: Boolean = true) {
        Log.d(TAG, "删除任务: taskId=$taskId, deleteFile=$deleteFile")

        // 获取任务信息
        val task = downloadTaskDao.getTaskById(taskId)
        if (task == null) {
            Log.e(TAG, "任务不存在: taskId=$taskId")
            return
        }

        // 如果正在下载，先暂停
        val okTask = okDownloadTasks[taskId]
        if (okTask != null) {
            OkDownloadHelper.cancelDownload(okTask)
            okDownloadTasks.remove(taskId)
        }

        // 从队列移除
        queueManager.removeFromQueue(taskId)

        // 移除进度管理
        progressManager.removeTask(taskId)

        // 删除文件
        if (deleteFile) {
            FilePathManager.deleteFile(task.filePath)
        }

        // 删除数据库记录
        downloadTaskDao.deleteById(taskId)

        Log.d(TAG, "任务已删除: taskId=$taskId")

        // 重置通知删除标志
        notificationManager.resetDismissFlag()

        // 立即更新通知
        progressManager.updateNotificationImmediately()

        // 启动等待中的任务
        checkAndStartPendingTasks()
    }

    /**
     * 检查并启动等待中的任务
     */
    private suspend fun checkAndStartPendingTasks() {
        val tasksToStart = queueManager.tryStartPendingTasks()

        for (taskId in tasksToStart) {
            val task = downloadTaskDao.getTaskById(taskId)
            if (task != null) {
                executeDownload(task)
            }
        }
    }

    /**
     * 更新通知
     */
    private suspend fun updateNotification() {
        // 获取正在下载的任务
        val downloadingTaskIds = queueManager.getDownloadingTaskIds()
        val downloadingTasks = downloadingTaskIds.mapNotNull { taskId ->
            downloadTaskDao.getTaskById(taskId)
        }

        // 获取下载速度
        val speeds = progressManager.getAllSpeeds()

        // 更新通知
        notificationManager.updateNotification(downloadingTasks, speeds)
    }

    /**
     * 获取管理器状态（用于调试）
     */
    fun getStatus(): String {
        return """
            队列状态: ${queueManager.getQueueStatus()}
            进度管理: ${progressManager.getStatus()}
            OkDownload任务: ${okDownloadTasks.size}
        """.trimIndent()
    }
}
