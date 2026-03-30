package com.example.pffbrowser.download.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.pffbrowser.download.manager.DownloadManager
import com.example.pffbrowser.download.notification.DownloadNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

/**
 * 下载前台服务
 * 负责在后台保活下载进程，处理系统杀死后自动恢复
 *
 * 设计原则：
 * - 不抢夺 DownloadManager 的业务控制权
 * - 仅在有活跃下载任务时保持前台状态
 * - 被杀死后通过 START_STICKY 自动恢复，并重新启动未完成的任务
 */
@AndroidEntryPoint
class DownloadForegroundService : Service() {

    companion object {
        private const val TAG = "DownloadForegroundService"
        const val ACTION_RESTORE = "action_restore"
    }

    @Inject
    lateinit var downloadManager: DownloadManager

    @Inject
    lateinit var notificationManager: DownloadNotificationManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    inner class LocalBinder : Binder() {
        fun getService(): DownloadForegroundService = this@DownloadForegroundService
    }

    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        // 立即进入前台状态，满足 Android 8+ 的 5 秒限制
        val notification = notificationManager.getServiceNotification()
        startForeground(DownloadNotificationManager.SERVICE_NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d(TAG, "onStartCommand action=$action, intent=${intent != null}")

        // 以下情况触发恢复：
        // 1. 显式传入了 ACTION_RESTORE
        // 2. Service 被系统杀死后自动恢复（intent 为 null）
//        if (action == ACTION_RESTORE || intent == null) {
//            scope.launch {
//                try {
//                    downloadManager.restoreActiveDownloads()
//                } catch (e: Exception) {
//                    Log.e(TAG, "恢复下载失败", e)
//                }
//            }
//        }

        return START_STICKY
    }

    /**
     * 准备停止服务：先移除前台状态，再允许系统销毁
     */
    fun prepareToStop() {
        Log.d(TAG, "prepareToStop")
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }
}
