package com.example.flamingcoding.thread

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R
import java.util.concurrent.CountDownLatch


class HandlerTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_handler_test2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        test()
        testThreadAB()
    }

    fun test() {
        Thread {
            // sThreadLocal.set(new Looper(quitAllowed));
            // Looper.prepare()就是将Thread和Looper进行绑定
            Looper.prepare()
//            mLooper = Looper.myLooper()
//            if (mLooper == null) {
//                throw RuntimeException(
//                    ("Can't create handler inside thread " + Thread.currentThread()
//                            + " that has not called Looper.prepare()")
//                )
//            }
            // 在Looper没有prepare的时候直接创建Handler会报错s

            // 创建Handler的方式1
            val handler1 = Handler()
            val handler2 = Handler(Looper.myLooper()!!)
            // 当一个Handler与一个线程绑定了之后，它就属于这个线程。
            // 使用与哪个线程绑定的Handler调用post post中的代码才是在哪个线程调用！！！
            handler2.post {
                // 这个线程不是主线程！！！是当前创建的线程 Thread[Thread-2, 5, main]
                println("handler2 post 当前的线程是：${Thread.currentThread()}")
            }

            val mianThreadHandler = Handler(Looper.getMainLooper())
            mianThreadHandler.post {
                // 这个线程才是主线程 Thread[main,5,main]
                println("mainThreadHandler post 当前的线程是：${Thread.currentThread()}")
            }
            Looper.loop()
        }.start()

        // 创建Handler的方式2
        val workerThread = HandlerThread("Worker")
        workerThread.start()
        val workerHandler = Handler(workerThread.getLooper())
    }

    fun testThreadAB() {
        // 这样启动并不是使用ThreadB的类，而是启动了一个叫ThreadB的线程
//        val handlerThread = HandlerThread("ThreadB")
//        handlerThread.start()
//        val handlerB = Handler(handlerThread.looper)

        val threadB = ThreadB()
        threadB.start()
        // 不行的 报错并崩溃 handlerB还没准备好
//        val handlerB = threadB.handler
        val handlerB = threadB.awaitHandlerReady()

        val msg = Message.obtain(handlerB)
        msg.what = 100
        val bundle = Bundle()
        bundle.putString("key", "Hello from A")
        msg.data = bundle
        handlerB!!.sendMessage(msg)
        handlerB.post {
            // handlerB.post 当前的线程是：Thread[Thread-3,5,main]
            println("handlerB.post 当前的线程是：${Thread.currentThread()}")
        }
    }
}

// B 线程代码
internal class ThreadB : Thread() {
    var handler: Handler? = null
        private set
    private val latch = CountDownLatch(1)

    override fun run() {
        Looper.prepare()
        // ThreadB run 当前的线程是：Thread[Thread-3,5,main]
        println("ThreadB run 当前的线程是：${currentThread()}")
        // 使用 Callback 拦截所有消息
        handler = object : Handler(Looper.myLooper()!!, object : Callback {
            override fun handleMessage(msg: Message): Boolean {
                if (msg.what == 100) {
                    // 拦截消息 100，处理数据
                    val data = msg.getData()
                    val info = data.getString("key")
                    Log.d("Intercept", "拦截到消息：" + info)
                    // 可以在这里进行额外操作，然后消费消息
                    return true // 不再传递给 handleMessage
                }
                return false // 其他消息继续传递
            }
        }) {
            override fun handleMessage(msg: Message) {
                // 正常业务处理，仅处理未被拦截的消息
                Log.d("Handler", "正常处理：" + msg.what)
            }
        }
        latch.countDown() // 通知主线程 handler 已就绪
        Looper.loop()
    }

    fun awaitHandlerReady(): Handler? {
        latch.await()
        return handler
    }
}