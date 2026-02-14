package com.example.flamingcoding.androidDevArt

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flamingcoding.R

class ActivityLifecycleActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lifecycle2)
    }

    // 旧的Activity的onPause()先调用，然后新的Activity才启动 onCreate onStart onResume

    override fun onStart() {
        super.onStart()
        Log.d("ActivityLifecycleActivity2", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ActivityLifecycleActivity2", "onResume")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("ActivityLifecycleActivity2", "onRestart")
    }

    override fun onPause() {
        super.onPause()
        Log.d("ActivityLifecycleActivity2", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("ActivityLifecycleActivity2", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ActivityLifecycleActivity2", "onDestroy")
    }
}