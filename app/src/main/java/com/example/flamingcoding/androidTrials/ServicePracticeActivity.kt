package com.example.flamingcoding.androidTrials

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.flamingcoding.R

class ServicePracticeActivity : AppCompatActivity() {

    lateinit var downloaderBinder: PracticeService.DownloadBinder
    private var connection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            downloaderBinder = service as PracticeService.DownloadBinder
            downloaderBinder.startDownload()
            downloaderBinder.getProgress()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_practice)
        val startServiceBtn = findViewById<Button>(R.id.startServiceBtn)
        val stopServiceBtn = findViewById<Button>(R.id.stopServiceBtn)

        startServiceBtn.setOnClickListener {
            val intent = Intent(this, PracticeService::class.java)
            startService(intent) // 启动Service
        }

        stopServiceBtn.setOnClickListener {
            val intent = Intent(this, PracticeService::class.java)
            stopService(intent) // 停止Service
        }

        val bindServiceBtn = findViewById<Button>(R.id.bindServiceBtn)
        val unbindServiceBtn = findViewById<Button>(R.id.unbindServiceBtn)
        bindServiceBtn.setOnClickListener { v ->
            val intent = Intent(this, PracticeService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        unbindServiceBtn.setOnClickListener { v ->
            unbindService(connection)
        }
    }
}