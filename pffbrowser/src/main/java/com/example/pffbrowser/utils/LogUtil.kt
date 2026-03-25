package com.example.pffbrowser.utils

import android.util.Log

object LogUtil {

    const val LIFE_CYCLE_LOG_FLAG = true
    const val LIFE_CYCLE_TAG = "LifeCycleLog"

    const val WEB_VIEW_LOG_FLAG = false
    const val WEB_VIEW_CLIENT_TRACK_TAG = "WebViewClientTrack"
    const val WEB_CHROME_CLIENT_TRACK_TAG = "WebChromeClientTrack"

    const val DOWN_LOAD_FLAG = true

    const val DOWN_LOAD_STATE_TAG = "DownloadState"


    fun logLifeCycle(obj: Any, lifeCycleName: String) {
        if (LIFE_CYCLE_LOG_FLAG) {
            Log.d(
                LIFE_CYCLE_TAG,
                "LifCycleName: $lifeCycleName ### className: ${obj.javaClass.simpleName}"
            )
        }
    }

    fun logWebViewClient(logStr: String) {
        if (WEB_VIEW_LOG_FLAG) {
            Log.d(
                WEB_VIEW_CLIENT_TRACK_TAG,
                logStr
            )
        }
    }

    fun logWebChromeClient(logStr: String) {
        if (WEB_VIEW_LOG_FLAG) {
            Log.d(
                WEB_CHROME_CLIENT_TRACK_TAG,
                logStr
            )
        }
    }

    fun logDownloadState(logStr: String) {
        if (DOWN_LOAD_FLAG) {
            Log.d(
                DOWN_LOAD_STATE_TAG,
                logStr
            )
        }
    }
}