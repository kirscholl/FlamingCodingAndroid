package com.example.flamingcoding.androidTrials

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class WorkManagerPractice(context: Context, params: WorkerParameters) : Worker(context, params) {

    // 不会运行在主线程当中，可以放心地在这里执行耗时逻辑
    override fun doWork(): Result {
        Log.d("WorkManagerPractice", "do work in WorkManagerPractice")
        return Result.success()
    }
}