package com.example.pffbrowser.download.manager

import android.util.Log
import com.example.pffbrowser.download.DownloadStatus

/**
 * 下载状态管理器
 * 管理下载任务的状态转换，验证状态转换规则
 *
 * 状态转换规则：
 * PENDING → DOWNLOADING → COMPLETED
 *            ↓
 *         PAUSED → DOWNLOADING
 *            ↓
 *         FAILED → DOWNLOADING (重试)
 *            ↓
 *         CANCELED (终止)
 */
class DownloadStateManager {

    companion object {
        private const val TAG = "DownloadStateManager"
    }

    /**
     * 验证状态转换是否合法
     *
     * @param from 当前状态
     * @param to 目标状态
     * @return true表示转换合法
     */
    fun isValidTransition(from: DownloadStatus, to: DownloadStatus): Boolean {
        // 相同状态，不需要转换
        if (from == to) {
            return false
        }

        val isValid = when (from) {
            DownloadStatus.PENDING -> {
                // PENDING只能转换到DOWNLOADING或CANCELED
                to == DownloadStatus.DOWNLOADING || to == DownloadStatus.CANCELED
            }

            DownloadStatus.DOWNLOADING -> {
                // DOWNLOADING可以转换到任何状态
                true
            }

            DownloadStatus.PAUSED -> {
                // PAUSED可以恢复下载、取消或删除
                to == DownloadStatus.DOWNLOADING || to == DownloadStatus.CANCELED
            }

            DownloadStatus.COMPLETED -> {
                // COMPLETED是终止状态，只能删除（通过删除任务实现）
                false
            }

            DownloadStatus.FAILED -> {
                // FAILED可以重试或取消
                to == DownloadStatus.DOWNLOADING || to == DownloadStatus.CANCELED
            }

            DownloadStatus.CANCELED -> {
                // CANCELED是终止状态，只能删除
                false
            }
        }

        if (!isValid) {
            Log.w(TAG, "非法的状态转换: $from → $to")
        }

        return isValid
    }

    /**
     * 获取状态的可读名称
     */
    fun getStatusName(status: DownloadStatus): String {
        return when (status) {
            DownloadStatus.PENDING -> "等待中"
            DownloadStatus.DOWNLOADING -> "下载中"
            DownloadStatus.PAUSED -> "已暂停"
            DownloadStatus.COMPLETED -> "已完成"
            DownloadStatus.FAILED -> "失败"
            DownloadStatus.CANCELED -> "已取消"
        }
    }

    /**
     * 检查状态是否为活跃状态（正在下载或等待）
     */
    fun isActiveStatus(status: DownloadStatus): Boolean {
        return status.isActive()
    }

    /**
     * 检查状态是否为终止状态（已完成或已取消）
     */
    fun isTerminalStatus(status: DownloadStatus): Boolean {
        return status.isTerminal()
    }

    /**
     * 检查状态是否可以恢复下载
     */
    fun canResume(status: DownloadStatus): Boolean {
        return status.canResume()
    }

    /**
     * 检查状态是否可以暂停
     */
    fun canPause(status: DownloadStatus): Boolean {
        return status == DownloadStatus.DOWNLOADING
    }

    /**
     * 检查状态是否可以删除
     */
    fun canDelete(status: DownloadStatus): Boolean {
        // 所有状态都可以删除
        return true
    }

    /**
     * 获取所有可能的状态转换
     *
     * @param from 当前状态
     * @return 可以转换到的状态列表
     */
    fun getPossibleTransitions(from: DownloadStatus): List<DownloadStatus> {
        return when (from) {
            DownloadStatus.PENDING -> listOf(
                DownloadStatus.DOWNLOADING,
                DownloadStatus.CANCELED
            )

            DownloadStatus.DOWNLOADING -> listOf(
                DownloadStatus.PAUSED,
                DownloadStatus.COMPLETED,
                DownloadStatus.FAILED,
                DownloadStatus.CANCELED
            )

            DownloadStatus.PAUSED -> listOf(
                DownloadStatus.DOWNLOADING,
                DownloadStatus.CANCELED
            )

            DownloadStatus.COMPLETED -> emptyList()

            DownloadStatus.FAILED -> listOf(
                DownloadStatus.DOWNLOADING,
                DownloadStatus.CANCELED
            )

            DownloadStatus.CANCELED -> emptyList()
        }
    }

    /**
     * 获取状态转换的描述
     */
    fun getTransitionDescription(from: DownloadStatus, to: DownloadStatus): String {
        return when {
            from == DownloadStatus.PENDING && to == DownloadStatus.DOWNLOADING ->
                "开始下载"

            from == DownloadStatus.DOWNLOADING && to == DownloadStatus.PAUSED ->
                "暂停下载"

            from == DownloadStatus.PAUSED && to == DownloadStatus.DOWNLOADING ->
                "恢复下载"

            from == DownloadStatus.FAILED && to == DownloadStatus.DOWNLOADING ->
                "重试下载"

            from == DownloadStatus.DOWNLOADING && to == DownloadStatus.COMPLETED ->
                "下载完成"

            from == DownloadStatus.DOWNLOADING && to == DownloadStatus.FAILED ->
                "下载失败"

            to == DownloadStatus.CANCELED ->
                "取消下载"

            else ->
                "$from → $to"
        }
    }

    /**
     * 记录状态转换日志
     */
    fun logTransition(taskId: Long, from: DownloadStatus, to: DownloadStatus) {
        val description = getTransitionDescription(from, to)
        Log.d(TAG, "任务状态转换: taskId=$taskId, $description ($from → $to)")
    }
}
