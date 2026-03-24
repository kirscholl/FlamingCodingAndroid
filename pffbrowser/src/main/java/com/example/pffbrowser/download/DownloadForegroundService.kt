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
import android.text.format.Formatter
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pffbrowser.R
import com.example.pffbrowser.download.db.DownloadTaskStatus
import com.example.pffbrowser.download.db.getProgressPercent
import com.example.pffbrowser.main.PffBrowserMainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 下载前台服务
 * 确保 App 在后台时下载任务不被系统杀死
 *
 * 设计原则：
 * 1. 服务管理所有活跃下载任务（RUNNING 或 PENDING 状态）
 * 2. 只有当所有任务都完成/失败/暂停时，服务才会关闭
 * 3. 通知栏显示总体下载进度
 */
@AndroidEntryPoint
class DownloadForegroundService : Service() {

    @Inject
    lateinit var downloadManager: OkDownloadManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "DownloadFgService"
        const val CHANNEL_ID = "pb_download_channel"
        const val CHANNEL_NAME = "文件下载"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START_DOWNLOAD = "action_start_download"
        const val ACTION_ENSURE_SERVICE = "action_ensure_service"
        const val ACTION_STOP_SERVICE = "action_stop_service"

        const val EXTRA_URL = "extra_url"
        const val EXTRA_FILE_NAME = "extra_file_name"
        const val EXTRA_MIME_TYPE = "extra_mime_type"
        const val EXTRA_CONTENT_LENGTH = "extra_content_length"

        // 服务运行状态标记
        @Volatile
        private var isServiceRunning = false

        /**
         * 启动下载并确保前台服务正在运行
         */
        fun startDownload(
            context: Context,
            url: String,
            fileName: String,
            mimeType: String? = null,
            contentLength: Long = -1
        ) {
            val intent = Intent(context, DownloadForegroundService::class.java).apply {
                action = ACTION_START_DOWNLOAD
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_FILE_NAME, fileName)
                putExtra(EXTRA_MIME_TYPE, mimeType)
                putExtra(EXTRA_CONTENT_LENGTH, contentLength)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        /**
         * 确保前台服务正在运行（用于恢复下载等场景）
         * 如果服务已运行，则不做任何操作
         */
        fun ensureServiceRunning(context: Context) {
            if (isServiceRunning) {
                return
            }

            val intent = Intent(context, DownloadForegroundService::class.java).apply {
                action = ACTION_ENSURE_SERVICE
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        /**
         * 停止前台服务
         */
        fun stopService(context: Context) {
            val intent = Intent(context, DownloadForegroundService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            context.stopService(intent)
        }

        /**
         * 标记服务运行状态
         */
        internal fun setServiceRunning(running: Boolean) {
            isServiceRunning = running
        }

        /**
         * 检查服务是否在运行
         */
        fun isRunning(): Boolean = isServiceRunning
    }

    override fun onCreate() {
        super.onCreate()
        setServiceRunning(true)
        createNotificationChannel()
        // 立即启动为前台服务（Android 要求前台服务必须在创建后5秒内调用 startForeground）
        startForeground(NOTIFICATION_ID, buildInitialNotification())

        // 开始监听活跃任务变化
        startActiveTasksMonitoring()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_DOWNLOAD -> {
                val url = intent.getStringExtra(EXTRA_URL) ?: return START_NOT_STICKY
                val fileName = intent.getStringExtra(EXTRA_FILE_NAME) ?: return START_NOT_STICKY
                val mimeType = intent.getStringExtra(EXTRA_MIME_TYPE)
                val contentLength = intent.getLongExtra(EXTRA_CONTENT_LENGTH, -1)

                // 启动下载任务（添加到队列）
                serviceScope.launch {
                    downloadManager.executeDownload(url, fileName, mimeType, contentLength)
                }
            }

            ACTION_ENSURE_SERVICE -> {
                // 只确保服务在运行，不执行任何操作
                // 服务已在 onCreate 中启动，这里只需要记录日志
                Log.d(TAG, "Service ensured by ACTION_ENSURE_SERVICE")
            }

            ACTION_STOP_SERVICE -> {
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * 监听活跃任务变化
     * 使用 debounce 避免频繁更新通知
     * 当没有活跃任务时自动关闭服务
     */
    private fun startActiveTasksMonitoring() {
        serviceScope.launch {
            downloadManager.getActiveTasks()
                .debounce(500)  // 防抖：500ms 内的多次更新合并为一次
                .collectLatest { activeTasks ->
                    updateNotification(activeTasks)

                    // 当没有活跃任务时，延迟一段时间后关闭服务
                    if (activeTasks.isEmpty()) {
                        delay(3000)
                        // 再次检查，防止在延迟期间有新任务加入
                        checkAndStopService()
                    }
                }
        }
    }

    /**
     * 检查是否可以关闭服务
     */
    private suspend fun checkAndStopService() {
        // 使用 try-catch 避免异常导致服务无法关闭
        try {
            val activeCount = downloadManager.getActiveTaskCount()
            if (activeCount == 0) {
                Log.d(TAG, "No active tasks, stopping service")
                stopSelf()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking active tasks", e)
            // 发生异常时也尝试关闭服务
            stopSelf()
        }
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
     * 构建初始通知（服务创建时显示）
     */
    private fun buildInitialNotification(): Notification {
        return buildNotificationContent(
            title = "PbBrowser 下载",
            content = "准备下载...",
            showProgress = false,
            progress = 0,
            subText = null
        )
    }

    /**
     * 根据活跃任务更新通知
     */
    private fun updateNotification(activeTasks: List<com.example.pffbrowser.download.db.DownloadTaskEntity>) {
        val notification = when {
            activeTasks.isEmpty() -> {
                // 没有活跃任务，显示完成状态
                buildNotificationContent(
                    title = "PbBrowser 下载",
                    content = "所有下载任务已完成",
                    showProgress = false,
                    progress = 0,
                    subText = null,
                    isComplete = true
                )
            }

            activeTasks.size == 1 -> {
                // 单个任务
                val task = activeTasks[0]
                val progress = task.getProgressPercent()
                buildNotificationContent(
                    title = "正在下载",
                    content = task.fileName,
                    showProgress = true,
                    progress = progress,
                    subText = "${
                        Formatter.formatFileSize(
                            this,
                            task.downloadedBytes
                        )
                    } / ${Formatter.formatFileSize(this, task.totalBytes)}"
                )
            }

            else -> {
                // 多个任务
                val runningTasks = activeTasks.filter { it.status == DownloadTaskStatus.RUNNING }
                val pendingTasks = activeTasks.filter { it.status == DownloadTaskStatus.PENDING }

                val totalBytes = activeTasks.sumOf { it.totalBytes }
                val downloadedBytes = activeTasks.sumOf { it.downloadedBytes }
                val overallProgress = if (totalBytes > 0) {
                    ((downloadedBytes * 100) / totalBytes).toInt()
                } else 0

                val content = buildString {
                    append("${runningTasks.size} 个任务下载中")
                    if (pendingTasks.isNotEmpty()) {
                        append("，${pendingTasks.size} 个等待中")
                    }
                }

                buildNotificationContent(
                    title = "PbBrowser 下载",
                    content = content,
                    showProgress = true,
                    progress = overallProgress,
                    subText = "${
                        Formatter.formatFileSize(
                            this,
                            downloadedBytes
                        )
                    } / ${Formatter.formatFileSize(this, totalBytes)}"
                )
            }
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * 构建通知内容
     */
    private fun buildNotificationContent(
        title: String,
        content: String,
        showProgress: Boolean,
        progress: Int,
        subText: String?,
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
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.pb_default_file_img)
            .setContentIntent(pendingIntent)
            .setOngoing(!isComplete)
            .setAutoCancel(isComplete)

        // 显示进度条
        if (showProgress) {
            builder.setProgress(100, progress, progress == 0)
        }

        // 显示子文本（大小信息）
        subText?.let {
            builder.setSubText(it)
        }

        return builder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        setServiceRunning(false)
        serviceScope.cancel()
    }
}
