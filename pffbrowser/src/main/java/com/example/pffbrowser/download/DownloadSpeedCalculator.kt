package com.example.pffbrowser.download

import android.util.Log
import java.util.concurrent.ConcurrentHashMap

/**
 * 下载速度计算器
 * 使用滑动窗口计算实时下载速度
 */
class DownloadSpeedCalculator {

    companion object {
        private const val TAG = "DownloadSpeed"
        private const val MIN_CALC_INTERVAL_MS = 500L  // 最小计算间隔
        private const val SPEED_CACHE_DURATION_MS = 1000L  // 速度缓存有效期
    }

    private var lastCalcTime: Long = 0
    private var lastBytes: Long = 0
    private var currentSpeed: Long = 0
    private var lastSpeedUpdateTime: Long = 0

    /**
     * 添加新的下载进度样本
     * 使用节流策略，避免过于频繁计算
     */
    fun addSample(downloadedBytes: Long): Boolean {
        val now = System.currentTimeMillis()

        if (lastCalcTime == 0L) {
            lastCalcTime = now
            lastBytes = downloadedBytes
            return false  // 第一个样本，不计算速度
        }

        val timeDiff = now - lastCalcTime
        val bytesDiff = downloadedBytes - lastBytes

        // 至少间隔 500ms 且字节有变化才计算
        if (timeDiff >= MIN_CALC_INTERVAL_MS && bytesDiff > 0) {
            currentSpeed = (bytesDiff * 1000) / timeDiff
            lastCalcTime = now
            lastBytes = downloadedBytes
            lastSpeedUpdateTime = now
            Log.d(TAG, "addSample: bytesDiff=$bytesDiff, timeDiff=$timeDiff, speed=$currentSpeed")
            return true  // 速度已更新
        }

        return false  // 速度未更新
    }

    /**
     * 获取当前下载速度
     */
    fun getSpeed(): Long {
        // 如果超过1秒没有新样本，速度归零
        val now = System.currentTimeMillis()
        if (now - lastSpeedUpdateTime > SPEED_CACHE_DURATION_MS) {
            currentSpeed = 0
        }
        return currentSpeed
    }

    /**
     * 获取格式化的速度字符串
     */
    fun getFormattedSpeed(): String {
        return formatSpeed(getSpeed())
    }

    /**
     * 重置计算器
     */
    fun reset() {
        lastCalcTime = 0
        lastBytes = 0
        currentSpeed = 0
        lastSpeedUpdateTime = 0
    }

    /**
     * 格式化速度为可读的字符串
     */
    private fun formatSpeed(bytesPerSecond: Long): String {
        return when {
            bytesPerSecond < 1024 -> "$bytesPerSecond B/s"
            bytesPerSecond < 1024 * 1024 -> "${bytesPerSecond / 1024} KB/s"
            else -> String.format("%.1f MB/s", bytesPerSecond / (1024.0 * 1024.0))
        }
    }
}

/**
 * 全局速度计算器管理器
 * 使用 ConcurrentHashMap 避免同步锁竞争
 */
object DownloadSpeedManager {
    private const val TAG = "DownloadSpeedManager"
    private val calculators = ConcurrentHashMap<String, DownloadSpeedCalculator>()

    /**
     * 获取或创建计算器
     */
    fun getCalculator(taskId: String): DownloadSpeedCalculator {
        return calculators.computeIfAbsent(taskId) { DownloadSpeedCalculator() }
    }

    /**
     * 移除计算器
     */
    fun removeCalculator(taskId: String) {
        calculators.remove(taskId)
    }

    /**
     * 更新进度并返回速度
     * 只在速度实际更新时返回新值
     */
    fun updateProgress(taskId: String, downloadedBytes: Long): String? {
        val calculator = getCalculator(taskId)
        val speedUpdated = calculator.addSample(downloadedBytes)
        return if (speedUpdated) {
            val speed = calculator.getFormattedSpeed()
            Log.d(TAG, "updateProgress: taskId=$taskId, bytes=$downloadedBytes, speed=$speed")
            speed
        } else {
            null  // 速度未更新，返回 null
        }
    }

    /**
     * 获取当前速度
     */
    fun getSpeed(taskId: String): String {
        return calculators[taskId]?.getFormattedSpeed() ?: "0 B/s"
    }

    /**
     * 清理所有计算器
     */
    fun clearAll() {
        calculators.clear()
    }
}
