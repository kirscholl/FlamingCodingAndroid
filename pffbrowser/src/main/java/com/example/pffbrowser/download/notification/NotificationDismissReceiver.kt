package com.example.pffbrowser.download.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 通知删除监听器
 * 监听用户删除下载通知的事件
 */
class NotificationDismissReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NotificationDismissReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "用户删除了下载通知")

        // 通知DownloadNotificationManager用户删除了通知
        DownloadNotificationManager.getInstance(context).onNotificationDismissed()
    }
}
