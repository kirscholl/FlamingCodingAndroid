package com.example.flamingcoding.androidTrials

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.flamingcoding.databinding.ActivityWorkManagerPracticeBinding
import java.util.concurrent.TimeUnit

// 大多数的国产手机厂商在进行Android系统定制的时候会增加一个一键关闭的功能，允许用户一键杀死所有非白名单的应用程序。
// 而被杀死的应用程序既无法接收广播，也无法运行WorkManager的后台任务
// WorkManager可以用，但是千万别依赖它去实现什么核心功能，因为它在国产手机上可能会非常不稳定。

// 不同Android系统版本之间后台的功能与API变化频繁, WorkManager保证应用程序所编写的后台代码在不同系统版本上的兼容性

// WorkManager只是一个处理定时任务的工具，它可以保证即使在应用退出甚至手机重启的情况下，之前注册的任务仍然将会得到执行，
// 因此WorkManager很适合用于执行一些定期和服务器进行交互的任务，比如周期性地同步数据，等等
class WorkManagerPracticeActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorkManagerPracticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWorkManagerPracticeBinding.inflate(layoutInflater)
        // 使用binding的时候setContentView一定要传入binding.root
        // setContentView(R.layout.activity_work_manager_practice)如何使用了R.layout按钮点击失效！！！
        setContentView(binding.root)
        binding.doWorkBtn.setOnClickListener { v ->
//            val request = OneTimeWorkRequest.Builder(WorkManagerPractice::class.java).build()
//            WorkManager.getInstance(this).enqueue(request)

            val request = OneTimeWorkRequest.Builder(WorkManagerPractice::class.java)
                // 让后台任务在指定的延迟时间后运行
                .setInitialDelay(5, TimeUnit.MINUTES)
                // 任务请求添加标签
                .addTag("practice")
                .build()

            // 通过标签来取消后台任务请求
            WorkManager.getInstance(this).cancelAllWorkByTag("simple")
            // 通过id来取消后台任务请求
            WorkManager.getInstance(this).cancelWorkById(request.id)
            // 一次性取消所有后台任务请求
            WorkManager.getInstance(this).cancelAllWork()

            // 通过LiveData监听work执行的状态执行对应回调
            WorkManager.getInstance(this)
                .getWorkInfoByIdLiveData(request.id)
                .observe(this) { workInfo ->
                    if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        Log.d("WorkManagerPracticeActivity", "do work succeeded")
                    } else if (workInfo?.state == WorkInfo.State.FAILED) {
                        Log.d("WorkManagerPracticeActivity", "do work failed")
                    }
                }
        }
    }
}