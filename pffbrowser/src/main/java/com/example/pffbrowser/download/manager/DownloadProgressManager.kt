package com.example.pffbrowser.download.manager

import android.util.Log
import com.example.pffbrowser.download.DownloadStatus
import com.example.pffbrowser.download.database.DownloadTaskDao
import com.example.pffbrowser.download.notification.DownloadNotificationManager
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.ConcurrentHashMap

/**
 * 下载进度管理器
 * 管理下载进度的更新和通知
 *
 * 功能：
 * - 接收OkDownload进度回调
 * - 节流更新数据库（500ms）
 * - 节流更新通知（1000ms）
 * - 缓存下载速度
 */
class DownloadProgressManager(
    private val downloadTaskDao: DownloadTaskDao,
    private val notificationManager: DownloadNotificationManager,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "DownloadProgressManager"
    }

    // 进度节流器Map<taskId, ProgressThrottler>
    private val progressThrottlers = ConcurrentHashMap<Long, ProgressThrottler>()

    // 通知节流器（全局唯一）
    private var notificationThrottler: NotificationThrottler? = null

    // 下载速度缓存Map<taskId, speed>
    private val downloadSpeeds = ConcurrentHashMap<Long, Long>()

    /**
     * 初始化通知节流器
     */
    fun initNotificationThrottler(onUpdate: suspend () -> Unit) {
        notificationThrottler = NotificationThrottler(
            interval = 1000L,
            scope = scope,
            onUpdate = onUpdate
        )
    }

    /**
     * 处理进度更新
     *
     * @param taskId 任务ID
     * @param currentBytes 已下载字节数
     * @param progress 进度百分比
     * @param speed 下载速度（字节/秒）
     */
    fun onProgressUpdate(
        taskId: Long,
        currentBytes: Long,
        progress: Int,
        speed: Long
    ) {
        // 更新速度缓存
        downloadSpeeds[taskId] = speed

        // 获取或创建进度节流器
        val throttler = progressThrottlers.getOrPut(taskId) {
            ProgressThrottler(
                interval = 500L,
                scope = scope,
                onUpdate = { bytes, prog ->
                    // 更新数据库
                    updateDatabase(taskId, bytes, prog)
                }
            )
        }

        // 节流更新数据库
        throttler.update(currentBytes, progress)

        // 节流更新通知
        notificationThrottler?.update()
    }

    /**
     * 更新数据库
     */
    private suspend fun updateDatabase(taskId: Long, bytes: Long, progress: Int) {
        try {
            downloadTaskDao.updateProgress(
                id = taskId,
                bytes = bytes,
                progress = progress,
                time = System.currentTimeMillis()
            )
            Log.d(TAG, "数据库更新成功: taskId=$taskId, progress=$progress%")
        } catch (e: Exception) {
            Log.e(TAG, "数据库更新失败: taskId=$taskId", e)
        }
    }

    /**
     * 强制刷新进度（下载完成/暂停时调用）
     */
    suspend fun flushProgress(taskId: Long) {
        progressThrottlers[taskId]?.flush()
    }

    /**
     * 立即更新通知（状态变化时调用）
     */
    fun updateNotificationImmediately() {
        notificationThrottler?.updateImmediately()
    }

    /**
     * 获取下载速度
     */
    fun getSpeed(taskId: Long): Long {
        return downloadSpeeds[taskId] ?: 0L
    }

    /**
     * 获取所有下载速度
     */
    fun getAllSpeeds(): Map<Long, Long> {
        return downloadSpeeds.toMap()
    }

    /**
     * 移除任务的进度管理
     */
    fun removeTask(taskId: Long) {
        Log.d(TAG, "移除任务进度管理: taskId=$taskId")

        // 取消并移除进度节流器
        progressThrottlers.remove(taskId)?.cancel()

        // 移除速度缓存
        downloadSpeeds.remove(taskId)
    }

    /**
     * 清空所有进度管理
     */
    fun clearAll() {
        Log.d(TAG, "清空所有进度管理")

        // 取消所有进度节流器
        progressThrottlers.values.forEach { it.cancel() }
        progressThrottlers.clear()

        // 取消通知节流器
        notificationThrottler?.cancel()

        // 清空速度缓存
        downloadSpeeds.clear()
    }

    /**
     * 获取进度管理状态（用于调试）
     */
    fun getStatus(): String {
        return "管理中的任务: ${progressThrottlers.size}, " +
                "速度缓存: ${downloadSpeeds.size}"
    }
}
