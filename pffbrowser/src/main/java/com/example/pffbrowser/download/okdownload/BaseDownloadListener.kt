package com.example.pffbrowser.download.okdownload

import android.util.Log
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.SpeedCalculator
import com.liulishuo.okdownload.core.breakpoint.BlockInfo
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend

/**
 * 下载监听器基类
 * 封装OkDownload的DownloadListener4WithSpeed，提供简化的回调接口
 *
 * 使用方式：
 * ```
 * val listener = object : BaseDownloadListener() {
 *     override fun onDownloadProgress(
 *         currentBytes: Long,
 *         totalBytes: Long,
 *         progress: Int,
 *         speed: Long
 *     ) {
 *         // 处理进度更新
 *     }
 *
 *     override fun onDownloadCompleted(totalBytes: Long) {
 *         // 处理下载完成
 *     }
 * }
 * ```
 */
abstract class BaseDownloadListener : DownloadListener4WithSpeed() {

    companion object {
        private const val TAG = "BaseDownloadListener"
    }

    // 文件总大小（字节）
    private var totalBytes: Long = 0L

    // 是否已获取文件信息
    private var isInfoReady = false

    /**
     * 任务开始
     */
    override fun taskStart(task: DownloadTask) {
        Log.d(TAG, "任务开始: ${task.url}")
        isInfoReady = false
        totalBytes = 0L
        onDownloadStart()
    }

    /**
     * 连接开始
     */
    override fun connectStart(
        task: DownloadTask,
        blockIndex: Int,
        requestHeaderFields: MutableMap<String, MutableList<String>>
    ) {
        Log.d(TAG, "连接开始: blockIndex=$blockIndex")
    }

    /**
     * 连接结束
     */
    override fun connectEnd(
        task: DownloadTask,
        blockIndex: Int,
        responseCode: Int,
        responseHeaderFields: MutableMap<String, MutableList<String>>
    ) {
        Log.d(TAG, "连接结束: blockIndex=$blockIndex, responseCode=$responseCode")
    }

    /**
     * 文件信息就绪
     * 此时可以获取文件总大小
     */
    override fun infoReady(
        task: DownloadTask,
        info: BreakpointInfo,
        fromBreakpoint: Boolean,
        model: Listener4SpeedAssistExtend.Listener4SpeedModel
    ) {
        totalBytes = info.totalLength
        isInfoReady = true

        Log.d(TAG, "文件信息就绪: totalBytes=$totalBytes, fromBreakpoint=$fromBreakpoint")

        onDownloadInfoReady(totalBytes, fromBreakpoint)
    }

    /**
     * 分块进度更新（内部使用，不对外暴露）
     */
    override fun progressBlock(
        task: DownloadTask,
        blockIndex: Int,
        currentBlockOffset: Long,
        blockSpeed: SpeedCalculator
    ) {
        // 分块进度，通常不需要处理
    }

    /**
     * 整体进度更新
     * OkDownload已经做了节流处理（默认500ms回调一次）
     */
    override fun progress(
        task: DownloadTask,
        currentOffset: Long,
        taskSpeed: SpeedCalculator
    ) {
        if (!isInfoReady || totalBytes <= 0) {
            // 文件信息未就绪，无法计算进度
            return
        }

        // 计算进度百分比
        val progress = ((currentOffset * 100) / totalBytes).toInt().coerceIn(0, 100)

        // 获取下载速度（字节/秒）
        val speed = taskSpeed.getBytesPerSecondAndFlush()

        Log.d(TAG, "进度更新: $currentOffset/$totalBytes ($progress%), 速度: ${formatSpeed(speed)}")

        onDownloadProgress(currentOffset, totalBytes, progress, speed)
    }

    /**
     * 分块下载结束（内部使用，不对外暴露）
     */
    override fun blockEnd(
        task: DownloadTask,
        blockIndex: Int,
        info: BlockInfo,
        blockSpeed: SpeedCalculator
    ) {
        Log.d(TAG, "分块结束: blockIndex=$blockIndex")
    }

    /**
     * 任务结束
     * 根据EndCause判断是成功、失败还是取消
     */
    override fun taskEnd(
        task: DownloadTask,
        cause: EndCause,
        realCause: Exception?,
        taskSpeed: SpeedCalculator
    ) {
        Log.d(TAG, "任务结束: cause=$cause, exception=${realCause?.message}")

        when (cause) {
            EndCause.COMPLETED -> {
                // 下载完成
                onDownloadCompleted(totalBytes)
            }
            EndCause.CANCELED -> {
                // 用户取消
                onDownloadCanceled()
            }
            EndCause.ERROR -> {
                // 下载失败
                val errorMsg = realCause?.message ?: "未知错误"
                onDownloadFailed(errorMsg, realCause)
            }
            EndCause.PRE_ALLOCATE_FAILED -> {
                // 预分配空间失败（磁盘空间不足）
                onDownloadFailed("磁盘空间不足", realCause)
            }
            EndCause.FILE_BUSY -> {
                // 文件被占用
                onDownloadFailed("文件被占用", realCause)
            }
            EndCause.SAME_TASK_BUSY -> {
                // 相同任务正在下载
                onDownloadFailed("相同任务正在下载", realCause)
            }
            else -> {
                // 其他未知原因
                onDownloadFailed("下载失败: $cause", realCause)
            }
        }
    }

    // ========== 抽象方法（子类实现） ==========

    /**
     * 下载开始
     */
    protected open fun onDownloadStart() {}

    /**
     * 文件信息就绪
     *
     * @param totalBytes 文件总大小（字节）
     * @param fromBreakpoint 是否从断点恢复
     */
    protected open fun onDownloadInfoReady(totalBytes: Long, fromBreakpoint: Boolean) {}

    /**
     * 下载进度更新
     *
     * @param currentBytes 已下载字节数
     * @param totalBytes 文件总大小
     * @param progress 进度百分比（0-100）
     * @param speed 下载速度（字节/秒）
     */
    protected abstract fun onDownloadProgress(
        currentBytes: Long,
        totalBytes: Long,
        progress: Int,
        speed: Long
    )

    /**
     * 下载完成
     *
     * @param totalBytes 文件总大小
     */
    protected abstract fun onDownloadCompleted(totalBytes: Long)

    /**
     * 下载失败
     *
     * @param errorMsg 错误信息
     * @param exception 异常对象
     */
    protected open fun onDownloadFailed(errorMsg: String, exception: Exception?) {
        Log.e(TAG, "下载失败: $errorMsg", exception)
    }

    /**
     * 下载取消
     */
    protected open fun onDownloadCanceled() {
        Log.d(TAG, "下载取消")
    }

    // ========== 工具方法 ==========

    /**
     * 格式化速度
     */
    private fun formatSpeed(bytesPerSecond: Long): String {
        return when {
            bytesPerSecond < 1024 -> "${bytesPerSecond} B/s"
            bytesPerSecond < 1024 * 1024 -> String.format("%.1f KB/s", bytesPerSecond / 1024.0)
            bytesPerSecond < 1024 * 1024 * 1024 -> String.format(
                "%.1f MB/s",
                bytesPerSecond / (1024.0 * 1024)
            )
            else -> String.format("%.2f GB/s", bytesPerSecond / (1024.0 * 1024 * 1024))
        }
    }
}
