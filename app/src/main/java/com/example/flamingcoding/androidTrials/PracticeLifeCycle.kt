package com.example.flamingcoding.androidTrials

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

//class PracticeLifeCycle : LifecycleObserver {
//    // OnLifecycleEvent废弃的原因：需要使用代码生成或反射，会造成很大的性能开销
//    // 使用DefaultLifecycleObserver或LifecycleEventObserver来代替
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun activityStart() {
//        Log.d("MyObserver", "activityStart")
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun activityStop() {
//        Log.d("MyObserver", "activityStop")
//    }
//}

class PracticeLifeCycle(lifecycle: Lifecycle) : DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
    }
}

//class MyLifeCycleObserver : LifecycleEventObserver {
//    override fun onStateChanged(
//        source: LifecycleOwner,
//        event: Lifecycle.Event
//    ) {
//        when (event) {
//            Lifecycle.Event.ON_CREATE -> {
//
//            }
//
//            Lifecycle.Event.ON_DESTROY -> {
//
//            }
//
//            Lifecycle.Event.ON_START -> {
//
//            }
//
//            Lifecycle.Event.ON_PAUSE -> {
//
//            }
//
//            Lifecycle.Event.ON_STOP -> {
//
//            }
//
//            else -> {
//
//            }
//        }
//    }
//}