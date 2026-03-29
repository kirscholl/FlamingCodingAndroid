package com.example.pffbrowser.download.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.pffbrowser.download.utils.FileOpenHelper

/**
 * 文件打开监听器
 * 从通知点击打开文件
 */
class FileOpenReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "FileOpenReceiver"
        const val EXTRA_FILE_PATH = "file_path"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val filePath = intent.getStringExtra(EXTRA_FILE_PATH)

        if (filePath.isNullOrBlank()) {
            Log.d(TAG, "文件路径为空")
            return
        }

        Log.d(TAG, "打开文件: $filePath")

        // 使用FileOpenHelper打开文件
        FileOpenHelper.openFile(context, filePath)
    }
}
