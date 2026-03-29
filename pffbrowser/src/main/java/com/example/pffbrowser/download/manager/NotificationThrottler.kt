package com.example.pffbrowser.download.manager

import kotlinx.coroutines.*

/**
 * 通知节流器
 * 用于控制通知更新频率，避免频繁刷新通知栏
 *
 * 使用场景：
 * - 下载进度更新时，每1000ms最多更新一次通知
 * - 状态变化时，立即更新通知
 *
 * @param interval 节流间隔（毫秒），默认1000ms
 * @param scope 协程作用域
 * @param onUpdate 更新回调
 */
class NotificationThrottler(
    private val interval: Long = 1000L,
    private val scope: CoroutineScope,
    private val onUpdate: suspend () -> Unit
) {
    // 上次更新时间
    private var lastUpdateTime = 0L

    // 是否有待处理的更新
    private var hasPendingUpdate = false

    // 延迟任务
    private var delayedJob: Job? = null

    /**
     * 请求更新通知（节流）
     */
    fun update() {
        val now = System.currentTimeMillis()

        // 标记有待处理的更新
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
     * 强制立即更新（不受节流限制）
     * 用于状态变化等需要立即反馈的场景
     */
    fun updateImmediately() {
        // 取消待处理的延迟任务
        delayedJob?.cancel()
        delayedJob = null

        // 立即执行更新
        executeUpdate()
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
                onUpdate()
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
    }
}
