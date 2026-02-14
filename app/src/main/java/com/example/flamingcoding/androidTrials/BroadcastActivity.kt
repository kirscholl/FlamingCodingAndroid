package com.example.flamingcoding.androidTrials

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flamingcoding.R

class BroadcastActivity : AppCompatActivity() {

    inner class TimeChangeReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                Toast.makeText(context, "Time tick", Toast.LENGTH_SHORT).show()
            } else if (action.equals(Intent.ACTION_TIME_CHANGED)) {
                Toast.makeText(context, "Time has changed", Toast.LENGTH_SHORT).show()
            } else if (action.equals("TEST_BROADCAST")) {
                Toast.makeText(context, "Test board cast", Toast.LENGTH_SHORT).show()
            }
        }
    }

    lateinit var timeChangeReceiver: TimeChangeReceiver

    // BroadcastReceiver注册方式：在代码中注册(动态注册)和在AndroidManifest.xml中注册(静态注册)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_broadcast)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        val intentFilter = IntentFilter()
        // 每分钟变化
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction("TEST_BROAD_CAST")
        timeChangeReceiver = TimeChangeReceiver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(timeChangeReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        }

        initView()
    }

    private fun initView() {
        val button = findViewById<Button>(R.id.broadcastSendButton)
        button.setOnClickListener { v ->
            // 通过intent发送注册好的消息
            val intent = Intent("com.example.flamingcoding.androidTrials.TEST_BROADCAST")
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timeChangeReceiver)
    }
}