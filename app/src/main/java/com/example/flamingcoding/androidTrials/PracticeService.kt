package com.example.flamingcoding.androidTrials

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.flamingcoding.R
import kotlin.concurrent.thread

class PracticeService : Service() {

    private val mBinder = DownloadBinder()

    class DownloadBinder : Binder() {
        fun startDownload() {
            Log.d("DownloadBinder", "startDownload")
        }

        fun getProgress() {
            Log.d("DownloadBinder", "getProgress")
        }
    }

    // 调用Context的bindService()来获取一个Service的持久连接，这时就会回调Service中的onBind()方法。
    // 类似地，如果这个Service之前还没有创建过，onCreate()方法会先于onBind()方法执行
    override fun onBind(intent: Intent): IBinder {
        Log.d("PracticeService", "Service life cycle: onBind")
        return mBinder
    }

    // Service创建的时候调用
    override fun onCreate() {
        Log.d("PracticeService", "Service life cycle: onCreate")
        super.onCreate()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "practice_service", "前台Service通知",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, HomeActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, "practice_service")
            .setContentTitle("This is content title")
            .setContentText("This is content text")
            .setSmallIcon(R.drawable.board_icon)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.board_icon_nine))
            .setContentIntent(pi)
            .build()
        startForeground(1, notification)
    }

    // 在每次Service启动的时候调用
    // 虽然每调用一次startService()方法，onStartCommand()就会执行一次，但实际上每个Service只会存在一个实例。
    // 所以不管调用了多少次startService()方法，只需调用一次stopService()或stopSelf()方法，Service就会停止
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d("PracticeService", "Service life cycle: onStartCommand")
        thread {
            // 耗时操作
        }
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }


    // 对一个Service既调用了startService()方法，又调用了bindService()方法的，
    // 根据Android系统的机制，一个Service只要被启动或者被绑定了之后，就会处于运行状态，必须要让以上两种条件同时不满足，Service才能被销毁。
    // 所以，这种情况下要同时调用stopService()和unbindService()方法，onDestroy()方法才会执行
    // 在Service销毁的时候调用
    override fun onDestroy() {
        Log.d("PracticeService", "Service life cycle: onDestroy")
        super.onDestroy()
    }

    // class MyService : Service() {
    //    ...
    //    override fun onCreate() {

    //    }
    //    ...
    //}
}