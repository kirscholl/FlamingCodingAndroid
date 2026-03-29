package com.example.pffbrowser.download.manager

import android.util.Log
import com.example.pffbrowser.download.DownloadStatus
import com.example.pffbrowser.download.database.DownloadTask
import com.example.pffbrowser.download.database.DownloadTaskDao
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 下载队列管理器
 * 管理下载任务队列和并发控制
 *
 * 功能：
 * - 维护下载队列（最多2个并发）
 * - 维护等待队列
 * - 自动启动等待任务
 * - 任务优先级管理
 */
class DownloadQueueManager(
    private val downloadTaskDao: DownloadTaskDao
) {
    companion object {
        private const val TAG = "DownloadQueueManager"

        // 最大并发下载数
        const val MAX_CONCURRENT_DOWNLOADS = 2
    }

    // 当前正在下载的任务ID集合
    private val downloadingTaskIds = ConcurrentHashMap.newKeySet<Long>()

    // 等待队列（FIFO）
    private val pendingQueue = ConcurrentLinkedQueue<Long>()

    /**
     * 添加任务到下载队列
     *
     * @param taskId 任务ID
     * @return true表示可以立即开始下载，false表示需要等待
     */
    fun addToQueue(taskId: Long): Boolean {
        Log.d(TAG, "添加任务到队列: taskId=$taskId")

        // 检查是否已在下载队列中
        if (downloadingTaskIds.contains(taskId)) {
            Log.w(TAG, "任务已在下载队列中: taskId=$taskId")
            return false
        }

        // 检查是否已在等待队列中
        if (pendingQueue.contains(taskId)) {
            Log.w(TAG, "任务已在等待队列中: taskId=$taskId")
            return false
        }

        // 检查是否可以立即开始下载
        if (canStartDownload()) {
            // 添加到下载队列
            downloadingTaskIds.add(taskId)
            Log.d(TAG, "任务可以立即开始下载: taskId=$taskId, 当前下载数=${downloadingTaskIds.size}")
            return true
        } else {
            // 添加到等待队列
            pendingQueue.offer(taskId)
            Log.d(TAG, "任务添加到等待队列: taskId=$taskId, 等待队列大小=${pendingQueue.size}")
            return false
        }
    }

    /**
     * 从下载队列移除任务
     *
     * @param taskId 任务ID
     */
    fun removeFromQueue(taskId: Long) {
        Log.d(TAG, "从队列移除任务: taskId=$taskId")

        // 从下载队列移除
        val removed = downloadingTaskIds.remove(taskId)

        if (removed) {
            Log.d(TAG, "任务已从下载队列移除: taskId=$taskId, 当前下载数=${downloadingTaskIds.size}")
        } else {
            // 从等待队列移除
            val removedFromPending = pendingQueue.remove(taskId)
            if (removedFromPending) {
                Log.d(TAG, "任务已从等待队列移除: taskId=$taskId, 等待队列大小=${pendingQueue.size}")
            }
        }
    }

    /**
     * 获取下一个等待中的任务ID
     *
     * @return 任务ID，如果没有等待任务返回null
     */
    fun getNextPendingTask(): Long? {
        val taskId = pendingQueue.poll()
        if (taskId != null) {
            Log.d(TAG, "获取下一个等待任务: taskId=$taskId, 剩余等待任务=${pendingQueue.size}")
        }
        return taskId
    }

    /**
     * 尝试启动等待中的任务
     *
     * @return 可以启动的任务ID列表
     */
    suspend fun tryStartPendingTasks(): List<Long> {
        val tasksToStart = mutableListOf<Long>()

        while (canStartDownload()) {
            val taskId = getNextPendingTask() ?: break

            // 检查任务是否仍然存在且状态为PENDING
            val task = downloadTaskDao.getTaskById(taskId)
            if (task != null && task.status == DownloadStatus.PENDING) {
                // 添加到下载队列
                downloadingTaskIds.add(taskId)
                tasksToStart.add(taskId)
                Log.d(TAG, "准备启动等待任务: taskId=$taskId")
            } else {
                Log.w(TAG, "等待任务已不存在或状态已变化: taskId=$taskId")
            }
        }

        if (tasksToStart.isNotEmpty()) {
            Log.d(TAG, "可以启动${tasksToStart.size}个等待任务")
        }

        return tasksToStart
    }

    /**
     * 检查是否可以开始新的下载
     *
     * @return true表示可以开始
     */
    fun canStartDownload(): Boolean {
        return downloadingTaskIds.size < MAX_CONCURRENT_DOWNLOADS
    }

    /**
     * 获取当前正在下载的任务数量
     */
    fun getDownloadingCount(): Int {
        return downloadingTaskIds.size
    }

    /**
     * 获取等待队列大小
     */
    fun getPendingCount(): Int {
        return pendingQueue.size
    }

    /**
     * 获取当前正在下载的任务ID列表
     */
    fun getDownloadingTaskIds(): List<Long> {
        return downloadingTaskIds.toList()
    }

    /**
     * 获取等待队列中的任务ID列表
     */
    fun getPendingTaskIds(): List<Long> {
        return pendingQueue.toList()
    }

    /**
     * 检查任务是否在下载队列中
     */
    fun isDownloading(taskId: Long): Boolean {
        return downloadingTaskIds.contains(taskId)
    }

    /**
     * 检查任务是否在等待队列中
     */
    fun isPending(taskId: Long): Boolean {
        return pendingQueue.contains(taskId)
    }

    /**
     * 清空所有队列
     */
    fun clearAll() {
        Log.d(TAG, "清空所有队列")
        downloadingTaskIds.clear()
        pendingQueue.clear()
    }

    /**
     * 获取队列状态信息（用于调试）
     */
    fun getQueueStatus(): String {
        return "下载中: ${downloadingTaskIds.size}/$MAX_CONCURRENT_DOWNLOADS, " +
                "等待中: ${pendingQueue.size}, " +
                "下载任务: ${downloadingTaskIds.joinToString()}, " +
                "等待任务: ${pendingQueue.joinToString()}"
    }
}
