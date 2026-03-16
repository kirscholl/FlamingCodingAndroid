package com.example.pffbrowser.utils

import android.util.Log

object CommonLog {

    const val LOG_FLAG = true
    const val TAG = "LifeCycleLog"

    fun logLifeCycle(obj: Any, lifeCycleName: String) {
        if (LOG_FLAG) {
            Log.d(
                TAG,
                "LifCycleName: $lifeCycleName ### className: ${obj.javaClass.simpleName}"
            )
        }
    }
}