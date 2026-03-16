package com.example.pffbrowser.base

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    companion object {
        const val TAG = "BaseActivity"
    }

    override fun onCreate(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        Log.d(TAG, "LifCycleName: onCreate ### className: ${javaClass.simpleName}")
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onStart() {
        Log.d(TAG, "LifCycleName: onStart ### className: ${javaClass.simpleName}")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "LifCycleName: onResume ### className: ${javaClass.simpleName}")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "LifCycleName: onPause ### className: ${javaClass.simpleName}")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "LifCycleName: onStop ### className: ${javaClass.simpleName}")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "LifCycleName: onDestroy ### className: ${javaClass.simpleName}")
        super.onDestroy()
    }
}