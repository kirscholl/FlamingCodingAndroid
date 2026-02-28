package com.example.flamingcoding.kotlinCoroutinesTrials


import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class KotlinCoroutinesNote {

    companion object {

    }

    // kotlin协程的好处：用线性代码去处理结构化并发

    // 切线程
    fun createThread() {
        // 直接创建线程
        thread {
            //...
        }

        // 使用线程池
        val executor = Executors.newCachedThreadPool()
        executor.execute {
            //...
        }

        // 切换到Ui线程
        // 使用Handler切换到主线程
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            //...
        }
        // 或者使用view切换到主线程
//        val view: View = TODO()
//        view.post {
//            //...
//        }
    }
    // 在各个线程的执行中等待线程，线程交互
    // 互斥锁，线程间共享资源的保护


    // 切换协程
    fun coroutinesTest() {
        val co = CoroutineScope(Dispatchers.Default)
        // 启动一个协程
        co.launch {
            // ..
        }
        // 每次启用都会复用CoroutineContext 也就是 Dispatchers.Default
        co.launch {
            // ..
        }

//        val co = CoroutineScope(EmptyCoroutineContext)
//        // 只有该次启动Dispatchers.Default 会覆盖CoroutineScope中所设置的CoroutineContext
//        co.launch(Dispatchers.Default) {
//            // ..
//        }

////         自定义线程池
//        val scopeThreadPool = newFixedThreadPoolContext(4, "FlaMingThreadPool")
////        使用完一定要记得关闭
//        scopeThreadPool.close()
////         单线程线程池
//        val singleThreadPool = newSingleThreadContext("FlamingSingleThreadPool")
//        singleThreadPool.close()


        // 系统提供的线程池都是全局的，所以不用手动关闭
        // 计算密集型任务 线程池的大小: 和CPU核心数量相等
//        @JvmStatic
//        public actual val Default: CoroutineDispatcher = DefaultScheduler
//
        // 协程的代码块会在主线程执行：UI线程
//        @JvmStatic
//        public actual val Main: MainCoroutineDispatcher get() = MainDispatcherLoader.dispatcher
//
        // 不进行限制，使用该线程池执行的协程代码立即就开始执行代码不切线程，在挂起时也不切换线程，直接在挂起的线程中继续执行代码？？？
        // 几乎不用
//        @JvmStatic
//        public actual val Unconfined: CoroutineDispatcher = kotlinx.coroutines.Unconfined
//
        // I/O密集型任务：跟磁盘和网络交互相关的任务 -> 线程池的大小: * <= 64 = 64 || * > 64 = *
//        @JvmStatic
//        public val IO: CoroutineDispatcher get() = DefaultIoScheduler
    }
}