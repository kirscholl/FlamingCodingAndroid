package com.example.pffbrowser.download.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pffbrowser.download.database.DownloadTask
import com.example.pffbrowser.main.PffBrowserMainActivity
import com.example.pffbrowser.utils.FileUtil

/**
 * 下载通知管理器
 * 管理下载通知的显示、更新和删除
 *
 * 通知策略：
 * - 0个下载中任务：不显示通知
 * - 1个下载中任务：显示详细通知（文件名、进度、速度）
 * - 2+个下载中任务：显示汇总通知（"有x条下载任务正在进行中"）
 * - 用户删除通知后，状态不变时不再推送
 */
class DownloadNotificationManager private constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "DownloadNotificationManager"

        // 通知渠道ID
        private const val CHANNEL_ID = "download_channel"
        private const val CHANNEL_NAME = "下载管理"

        // 通知ID
        const val SERVICE_NOTIFICATION_ID = 1001  // 前台服务通知ID（复用为进度通知）
        private const val NOTIFICATION_ID_COMPLETED_BASE = 10000  // 完成通知基础ID

        // 单例
        @Volatile
        private var instance: DownloadNotificationManager? = null

        fun getInstance(context: Context): DownloadNotificationManager {
            return instance ?: synchronized(this) {
                instance ?: DownloadNotificationManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    // 当前显示的通知（用于前台服务）
    private var currentProgressNotification: Notification? = null
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // 用户是否手动删除了通知
    @Volatile
    private var isNotificationDismissed = false

    // 当前显示的通知类型
    private var currentNotificationType: NotificationType = NotificationType.None

    init {
        createNotificationChannel()
    }

    /**
     * 通知类型
     */
    private sealed class NotificationType {
        object None : NotificationType()
        data class Single(val taskId: Long) : NotificationType()
        data class Multiple(val count: Int) : NotificationType()
    }

    /**
     * 创建通知渠道（Android 8.0+）
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW  // 低重要性，不发出声音
            ).apply {
                description = "显示文件下载进度"
                setShowBadge(false)
                enableVibration(false)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 更新通知
     *
     * @param downloadingTasks 正在下载的任务列表
     * @param speeds 下载速度Map<taskId, speed>（字节/秒）
     */
    fun updateNotification(
        downloadingTasks: List<DownloadTask>,
        speeds: Map<Long, Long>
    ) {
        // 如果用户删除了通知，不再主动推送
        if (isNotificationDismissed) {
            Log.d(TAG, "用户已删除通知，跳过更新")
            return
        }

        when {
            downloadingTasks.isEmpty() -> {
                // 没有下载任务，取消通知
                cancelProgressNotification()
            }

            downloadingTasks.size == 1 -> {
                // 单个任务，显示详细通知
                val task = downloadingTasks[0]
                val speed = speeds[task.id] ?: 0L
                showSingleTaskNotification(task, speed)
            }

            else -> {
                // 多个任务，显示汇总通知
                showMultipleTasksNotification(downloadingTasks.size)
            }
        }
    }

    /**
     * 显示单任务详细通知
     */
    private fun showSingleTaskNotification(task: DownloadTask, speed: Long) {
        Log.d(TAG, "显示单任务通知: ${task.fileName}, 进度: ${task.progress}%")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(task.fileName)
            .setContentText(
                "${FileUtil.formatFileSize(task.downloadedBytes)} / ${
                    FileUtil.formatFileSize(
                        task.totalBytes
                    )
                }"
            )
            .setSubText(formatSpeed(speed))
            .setProgress(100, task.progress, false)
            .setOngoing(true)  // 不可滑动删除（下载中）
            .setAutoCancel(false)
            .setDeleteIntent(createDeleteIntent())  // 监听删除事件
            .setContentIntent(createContentIntent())  // 点击跳转到下载列表
            .build()

        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification)
        currentProgressNotification = notification
        currentNotificationType = NotificationType.Single(task.id)
    }

    /**
     * 显示多任务汇总通知
     */
    private fun showMultipleTasksNotification(count: Int) {
        Log.d(TAG, "显示多任务通知: $count 个任务")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("下载管理")
            .setContentText("有${count}条下载任务正在进行中")
            .setOngoing(true)
            .setAutoCancel(false)
            .setDeleteIntent(createDeleteIntent())
            .setContentIntent(createContentIntent())
            .build()

        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification)
        currentProgressNotification = notification
        currentNotificationType = NotificationType.Multiple(count)
    }

    /**
     * 取消进度通知
     */
    fun cancelProgressNotification() {
        Log.d(TAG, "取消进度通知")
        notificationManager.cancel(SERVICE_NOTIFICATION_ID)
        currentProgressNotification = null
        currentNotificationType = NotificationType.None
    }

    /**
     * 获取用于前台服务的通知
     * 如果有活跃任务，返回当前进度通知；否则返回占位通知
     */
    fun getServiceNotification(): Notification {
        return currentProgressNotification ?: createPlaceholderNotification()
    }

    /**
     * 创建占位通知（用于 Service 启动时暂无任务的情况）
     */
    private fun createPlaceholderNotification(): android.app.Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("下载服务")
            .setContentText("正在运行")
            .setOngoing(true)
            .setAutoCancel(false)
            .setSilent(true)
            .setContentIntent(createContentIntent())
            .build()
    }

    /**
     * 显示下载完成通知（独立通知，可删除）
     */
    fun showCompletedNotification(task: DownloadTask) {
        Log.d(TAG, "显示完成通知: ${task.fileName}")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("下载完成")
            .setContentText(task.fileName)
            .setAutoCancel(true)  // 点击后自动消失
            .setContentIntent(createOpenFileIntent(task))  // 点击打开文件
            .build()

        // 使用不同的ID，避免覆盖进度通知
        val notificationId = NOTIFICATION_ID_COMPLETED_BASE + (task.id % 1000).toInt()
        notificationManager.notify(notificationId, notification)
    }

    /**
     * 用户删除了通知
     */
    fun onNotificationDismissed() {
        Log.d(TAG, "用户删除了通知，设置标志位")
        isNotificationDismissed = true
    }

    /**
     * 重置删除标志（状态变化时调用）
     */
    fun resetDismissFlag() {
        if (isNotificationDismissed) {
            Log.d(TAG, "重置删除标志")
            isNotificationDismissed = false
        }
    }

    /**
     * 创建删除Intent（监听用户删除通知）
     */
    private fun createDeleteIntent(): PendingIntent {
        val intent = Intent(context, NotificationDismissReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * 创建点击Intent（跳转到下载列表）
     */
    private fun createContentIntent(): PendingIntent {
        val intent = Intent(context, PffBrowserMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "download_list")  // 导航到下载列表
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * 创建打开文件Intent
     */
    private fun createOpenFileIntent(task: DownloadTask): PendingIntent {
        val intent = Intent(context, FileOpenReceiver::class.java).apply {
            putExtra(FileOpenReceiver.EXTRA_FILE_PATH, task.filePath)
        }
        return PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * 格式化下载速度
     */
    private fun formatSpeed(bytesPerSecond: Long): String {
        if (bytesPerSecond <= 0) return "0 B/s"

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
