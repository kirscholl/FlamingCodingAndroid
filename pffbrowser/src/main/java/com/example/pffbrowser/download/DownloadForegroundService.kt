package com.example.pffbrowser.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.pffbrowser.R
import com.example.pffbrowser.main.PffBrowserMainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 下载前台服务
 * 确保 App 在后台时下载任务不被系统杀死
 */
@AndroidEntryPoint
class DownloadForegroundService : Service() {

    @Inject
    lateinit var downloadManager: OkDownloadManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val CHANNEL_ID = "pb_download_channel"
        const val CHANNEL_NAME = "文件下载"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START_DOWNLOAD = "action_start_download"
        const val ACTION_STOP_SERVICE = "action_stop_service"

        const val EXTRA_URL = "extra_url"
        const val EXTRA_FILE_NAME = "extra_file_name"

        fun startDownload(context: Context, url: String, fileName: String) {
            val intent = Intent(context, DownloadForegroundService::class.java).apply {
                action = ACTION_START_DOWNLOAD
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_FILE_NAME, fileName)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, DownloadForegroundService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("准备下载...", 0, 0))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_DOWNLOAD -> {
                val url = intent.getStringExtra(EXTRA_URL) ?: return START_NOT_STICKY
                val fileName = intent.getStringExtra(EXTRA_FILE_NAME) ?: return START_NOT_STICKY

                startDownload(url, fileName)
            }

            ACTION_STOP_SERVICE -> {
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * 开始下载
     */
    private fun startDownload(url: String, fileName: String) {
        // 启动下载
        downloadManager.enqueueDownload(url, fileName)

        // 监听下载状态并更新通知
        serviceScope.launch {
            downloadManager.downloadStatusFlow.collectLatest { status ->
                when (status) {
                    is OkDownloadManager.DownloadStatus.Started -> {
                        updateNotification("开始下载: $fileName", 0, 0)
                    }

                    is OkDownloadManager.DownloadStatus.Progress -> {
                        updateNotification(
                            "正在下载: ${status.fileName}",
                            status.progress,
                            status.downloadedBytes
                        )
                    }

                    is OkDownloadManager.DownloadStatus.Completed -> {
                        updateNotification("下载完成: $fileName", 100, 0, true)
                        // 延迟停止服务
                        kotlinx.coroutines.delay(3000)
                        if (!hasActiveDownloads()) {
                            stopSelf()
                        }
                    }

                    is OkDownloadManager.DownloadStatus.Error -> {
                        updateNotification("下载失败: $fileName", 0, 0, true)
                        if (!hasActiveDownloads()) {
                            stopSelf()
                        }
                    }

                    is OkDownloadManager.DownloadStatus.Cancelled -> {
                        if (!hasActiveDownloads()) {
                            stopSelf()
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    /**
     * 检查是否还有活动中的下载
     */
    private fun hasActiveDownloads(): Boolean {
        return downloadManager.hasRunningTasks()
    }

    /**
     * 创建通知渠道
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "显示文件下载进度"
                setShowBadge(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 构建通知
     */
    private fun buildNotification(
        content: String,
        progress: Int,
        downloadedBytes: Long,
        isComplete: Boolean = false
    ): Notification {
        val intent = Intent(this, PffBrowserMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("PbBrowser 下载")
            .setContentText(content)
            .setSmallIcon(R.drawable.pb_default_file_img)
            .setContentIntent(pendingIntent)
            .setOngoing(!isComplete)
            .setAutoCancel(isComplete)

        // 显示进度条
        if (!isComplete && progress >= 0) {
            builder.setProgress(100, progress, progress == 0)
        }

        return builder.build()
    }

    /**
     * 更新通知
     */
    private fun updateNotification(
        content: String,
        progress: Int,
        downloadedBytes: Long,
        isComplete: Boolean = false
    ) {
        val notification = buildNotification(content, progress, downloadedBytes, isComplete)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
