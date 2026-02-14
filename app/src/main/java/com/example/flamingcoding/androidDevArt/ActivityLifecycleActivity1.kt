package com.example.flamingcoding.androidDevArt

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flamingcoding.R

class ActivityLifecycleActivity1 : AppCompatActivity() {

    // onCreate生命周期的第一个方法。在这个方法中，我们可以做一些初始化工作，比如调用setContentView去加载界面布局资源、初始化Activity所需数据
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lifecycle1)
    }

    // 表示Activity正在被启动，即将开始，这时Activity已经可见了，但是还没有出现在前台，还无法和用户交互。
    override fun onStart() {
        super.onStart()
        Log.d("ActivityLifecycleActivity1", "onStart")
    }

    // 表示Activity已经可见了，并且出现在前台并开始活动。要注意这个和onStart的对比，
    // onStart和onResume都表示Activity已经可见，但是onStart的时候Activity还在后台
    override fun onResume() {
        super.onResume()
        Log.d("ActivityLifecycleActivity1", "onResume")
    }

    // 当当前Activity从不可见重新变为可见状态时，onRestart就会被调用
    override fun onRestart() {
        super.onRestart()
        Log.d("ActivityLifecycleActivity1", "onRestart")
    }

    // 表示Activity正在停止，正常情况下，紧接着onStop就会被调用
    // 此时可以做一些存储数据、停止动画等工作，但是注意不能太耗时，因为这会影响到新Activity的显示，onPause必须先执行完，新Activity的onResume才会执行
    override fun onPause() {
        super.onPause()
        Log.d("ActivityLifecycleActivity1", "onPause")
    }

    // Activity是在异常情况下终止的，系统会调用onSaveInstanceState来保存当前Activity的状态。
    // 这个方法的调用时机是在onStop之前，它和onPause没有既定的时序关系，它既可能在onPause之前调用，也可能在onPause之后调用
    override fun onSaveInstanceState(
        outState: Bundle,
        outPersistentState: PersistableBundle
    ) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    // onRestoreInstanceState的调用时机在onStart之后
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    // 表示Activity即将停止，可以做一些稍微重量级的回收工作，同样不能太耗时
    override fun onStop() {
        super.onStop()
        Log.d("ActivityLifecycleActivity1", "onStop")
    }

    // 表示Activity即将被销毁，这是Activity生命周期中的最后一个回调，在这里，我们可以做一些回收工作和最终的资源释放
    override fun onDestroy() {
        super.onDestroy()
        Log.d("ActivityLifecycleActivity1", "onDestroy")
    }
}