package com.example.pffbrowser.pbdownload.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pffbrowser.R
import com.example.pffbrowser.download.DownloadForegroundService
import com.example.pffbrowser.download.OkDownloadManager
import com.example.pffbrowser.main.PffBrowserMainActivity
import com.example.pffbrowser.pbdownload.PbDownloadAction
import com.example.pffbrowser.pbdownload.PbDownloader

class PbDownloadForegroundService : Service() {

    companion object {
        // 服务运行状态标记
        @Volatile
        private var isRunning = false

        const val TAG = "PbDownloadForegroundService"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_NAME = "PbBrowser下载运行"

        fun checkPbDownloadServiceAlive(): Boolean {
            return isRunning
        }
    }

    lateinit var downloaderManager: OkDownloadManager

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DownloadForegroundService.Companion.CHANNEL_ID,
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

    private fun createNotification(): Notification {
        return buildNotificationContent(
            title = "PbBrowser 下载",
            content = "准备下载...",
            showProgress = false,
            progress = 0,
            subText = null
        )
    }

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

        val builder = NotificationCompat.Builder(
            this,
            DownloadForegroundService.Companion.CHANNEL_ID
        )
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
        isRunning = false
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        when (intent?.action) {
            PbDownloadAction.START.toString() -> {
                startDownload(intent)
            }

            PbDownloadAction.CONTINUE.toString() -> {
                continueDownload(intent)
            }

            PbDownloadAction.PAUSE.toString() -> {
                pauseDownload(intent)
                checkStopService()
            }

            else -> {
                Log.e(TAG, "提供了错误的下载Action")
            }
        }
        return START_NOT_STICKY
    }

    fun startDownload(intent: Intent) {
        val downloadInfo = PbDownloader.getDownloadInfoFromIntent(intent)
    }

    fun continueDownload(intent: Intent) {
        val downloadInfo = PbDownloader.getDownloadInfoFromIntent(intent)
    }

    fun pauseDownload(intent: Intent) {
        val downloadInfo = PbDownloader.getDownloadInfoFromIntent(intent)
    }

    fun stopDownload(intent: Intent) {
        val downloadInfo = PbDownloader.getDownloadInfoFromIntent(intent)
    }

    fun checkStopService() {

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
}