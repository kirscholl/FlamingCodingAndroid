package com.example.pffbrowser.download.manager

import kotlinx.coroutines.*

/**
 * 进度节流器
 * 用于控制数据库更新频率，避免频繁写入
 *
 * 使用场景：
 * - 下载进度更新时，每500ms最多更新一次数据库
 * - 下载完成时，强制刷新最后的进度
 *
 * @param interval 节流间隔（毫秒），默认500ms
 * @param scope 协程作用域
 * @param onUpdate 更新回调，参数为(已下载字节数, 进度百分比)
 */
class ProgressThrottler(
    private val interval: Long = 500L,
    private val scope: CoroutineScope,
    private val onUpdate: suspend (downloadedBytes: Long, progress: Int) -> Unit
) {
    // 上次更新时间
    private var lastUpdateTime = 0L

    // 待处理的进度数据
    private var pendingBytes = 0L
    private var pendingProgress = 0

    // 是否有待处理的更新
    private var hasPendingUpdate = false

    // 延迟任务
    private var delayedJob: Job? = null

    /**
     * 更新进度（节流）
     *
     * @param downloadedBytes 已下载字节数
     * @param progress 进度百分比（0-100）
     */
    fun update(downloadedBytes: Long, progress: Int) {
        val now = System.currentTimeMillis()

        // 保存最新的进度数据
        pendingBytes = downloadedBytes
        pendingProgress = progress
        hasPendingUpdate = true

        // 如果距离上次更新超过间隔，立即更新
        if (now - lastUpdateTime >= interval) {
            executeUpdate()
        } else {
            // 否则，调度延迟更新
            scheduleDelayedUpdate(interval - (now - lastUpdateTime))
        }
    }

    /**
     * 强制刷新（立即执行更新）
     * 用于下载完成、暂停等需要立即更新的场景
     */
    suspend fun flush() {
        // 取消待处理的延迟任务
        delayedJob?.cancel()
        delayedJob = null

        // 如果有待处理的更新，立即执行
        if (hasPendingUpdate) {
            executeUpdate()
        }
    }

    /**
     * 执行更新
     */
    private fun executeUpdate() {
        // 取消之前的延迟任务
        delayedJob?.cancel()
        delayedJob = null

        if (!hasPendingUpdate) return

        // 启动协程执行更新
        scope.launch {
            try {
                onUpdate(pendingBytes, pendingProgress)
                lastUpdateTime = System.currentTimeMillis()
                hasPendingUpdate = false
            } catch (e: Exception) {
                // 忽略异常，避免影响下载流程
                e.printStackTrace()
            }
        }
    }

    /**
     * 调度延迟更新
     *
     * @param delay 延迟时间（毫秒）
     */
    private fun scheduleDelayedUpdate(delay: Long) {
        // 如果已经有延迟任务，不重复调度
        if (delayedJob?.isActive == true) {
            return
        }

        delayedJob = scope.launch {
            delay(delay)
            if (hasPendingUpdate) {
                executeUpdate()
            }
        }
    }

    /**
     * 取消所有待处理的更新
     */
    fun cancel() {
        delayedJob?.cancel()
        delayedJob = null
        hasPendingUpdate = false
    }

    /**
     * 重置节流器状态
     */
    fun reset() {
        cancel()
        lastUpdateTime = 0L
        pendingBytes = 0L
        pendingProgress = 0
    }
}
