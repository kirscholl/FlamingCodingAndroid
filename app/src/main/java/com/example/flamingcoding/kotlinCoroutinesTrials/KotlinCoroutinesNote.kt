package com.example.flamingcoding.kotlinCoroutinesTrials


import android.os.Handler
import android.os.Looper
import com.example.flamingcoding.retrofitOkHttpDev.Repo
import com.example.flamingcoding.retrofitOkHttpDev.RetrofitServerInterface
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.coroutines.ContinuationInterceptor
import kotlin.time.measureTime

class KotlinCoroutinesNote {

    companion object {

    }

    // kotlin协程的好处：用线性代码去处理结构化并发

    // 切线程
    fun createThread() {

        // 直接启动线程
        // 创建线程返回的对象就是该线程
        // 这个对象可以对线程这个抽象概念进行管理 --> 而协程不是
        val thread1 = Thread {

        }
        thread1.start()
        // 等同于
        val thread2 = thread {
            //...
        }
        // 这个启动了的线程是什么

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
    fun createCoroutines() {
        //
        val scope = CoroutineScope(Dispatchers.IO)
        // 启动一个协程
        // 启动协程所返回的对象类型为Job，类型限制是为了更好地区分职责，限制功能
        // 如果启动模式为Default则其内部实际创建了一个StandaloneCoroutine对象
        // StandaloneCoroutine继承自AbstractCoroutine而AbstractCoroutine继承自JobSupport
        // AbstractCoroutine并且实现了Job, Continuation<T>, CoroutineScope接口
        var innerJob: Job? = null
        val outerJob = scope.launch(Dispatchers.Default) {
            // ..
            innerJob = this.coroutineContext[Job]
            val outerContext = scope.coroutineContext
            val innerContext = this.coroutineContext

            // outerContext: [JobImpl{Active}@88f7199, Dispatchers.IO]
            // innerContext: [StandaloneCoroutine{Active}@f062c5e, Dispatchers.Default]
            // outerContext === innerContext: false
            println("outerContext: $outerContext")
            println("innerContext: $innerContext")
            println("outerContext === innerContext: ${outerContext === innerContext}")

            // outerInterceptor: Dispatchers.IO
            // innerInterceptor: Dispatchers.Default
            // outerInterceptor === innerInterceptor: false
            val outerInterceptor = scope.coroutineContext[ContinuationInterceptor]
            val innerInterceptor = this.coroutineContext[ContinuationInterceptor]
            println("outerInterceptor: $outerInterceptor")
            println("innerInterceptor: $innerInterceptor")
            println("outerInterceptor === innerInterceptor: ${outerInterceptor === innerInterceptor}")
        }
//        println("outerJob: $outerJob")
//        println("innerJob: $innerJob")
//        println("outerJob === innerJob: ${outerJob === innerJob}")

        // Job的一系列方法都是协程流程上的方法，这种设计让其并不能干预协程内部的具体运行逻辑
//        println(outerJob.isActive)
//        println(outerJob.isCancelled)
//        println(outerJob.isCompleted)
//        println(outerJob.parent)
//        println(outerJob.children)
//        outerJob.cancelChildren()

        // 每次启用都会复用CoroutineContext 也就是 Dispatchers.Default
//        co.launch {
//            // ..
//        }

        // 如果启动模式为Lazy则其内部创建了 一个LazyStandaloneCoroutine对象、
        // LazyStandaloneCoroutine继承自StandaloneCoroutine
        val lazyJob = scope.launch(Dispatchers.Default, CoroutineStart.LAZY) {
            //
            val interceptor = this.coroutineContext[ContinuationInterceptor]
//            println("Lazy协程开始启动")
        }
        Thread.sleep(2000)
        lazyJob.start()


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

    fun coroutinesWithContext() {
        CoroutineScope(Dispatchers.Default).launch {
            // xxx1
            println("协程执行顺序测试 launch：xxx1")
            CoroutineScope(Dispatchers.IO).launch {
                Thread.sleep(2000)
//                delay(2000)
                // 因为是异步调用，外层协程的代码不会等里层协程的代码执行完毕再执行
                // xxx1执行完毕之后会直接执行xxx3，不会等xxx2
                // xxx2
                println("协程执行顺序测试 launch：xxx2")
            }
            // xxx3
            println("协程执行顺序测试 launch：xxx3")
        }

        CoroutineScope(Dispatchers.Default).launch {
            // xxx1
            println("协程执行顺序测试 withContext：xxx1")
            withContext(Dispatchers.IO) {
                // 使用withContext让外层协程等待里层协程的代码执行完毕再执行
                // 执行顺序xxx1 xxx2 xxx3
                Thread.sleep(2000)
//                delay(2000)
                println("协程执行顺序测试 withContext：xxx2")
            }
            // xxx3
            println("协程执行顺序测试 withContext：xxx3")
        }
    }

    // 挂起函数只能在协程中或者其他挂起函数中调用
    // 当在函数中调用了别的挂起函数时，定义suspend函数
    suspend fun suspendTestFun() {
        val retro = Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retroService = retro.create(RetrofitServerInterface::class.java)
        // 调用了其他挂起函数
        val repos = retroService.listReposCor("octocat")
    }

    // Thread线程切换
    // 客户端主线程是一个循环的线程，所以才有切换回主线程这个需求
    // 本质上是向循环的主线程提交特定的任务，让主线程去处理该任务

    // 在java中，在一个线程中处理完部分任务之后是没有办法再切换到主线程中执行后续任务的，
    // 本质上是没有办法切换到指定的其他线程中去执行后续任务

    // 协程的线程切换
    suspend fun suspendSwitchExp() {
        val retro = Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retroService = retro.create(RetrofitServerInterface::class.java)

        // 连续的两行代码
        val repos = retroService.listReposCor("octocat")
        println(repos.toString())
        // 协程为什么不卡主线程，调用协程之后是怎么切到其他线程的 ，在执行完协程内的内容之后是如何切换回当前线程的 -->
        // 主线程并未等待协程内容执行完毕，在编写协程代码时候看似两行是连续的但是编译器在编译时候会根据协程状态机自动添加回调，
        // 在开始执行协程内容时开启新的线程，在执行完协程内的内容之后使用回调调回当前线程
    }

    // 协程为什么是轻量级线程

    // 父子协程之间的关系
    fun corCorParentTest() {
        val scope = CoroutineScope(Dispatchers.Default)
        var innerJob: Job? = null
        var anotherJob: Job? = null
        var anotherChildJob: Job? = null
        //
        val job = scope.launch {
            // 内部协程是由this也就是外部的CoroutineScope创建的
            // 外部的CoroutineScope包含了外部返回的Job对象
            innerJob = this.launch {

            }
            anotherJob = scope.launch {

            }
            // 通过制定Job的方式来关联父子协程
            anotherChildJob = scope.launch(scope.coroutineContext[Job]!!) {

            }
            // 自定义Job
            val customJob = Job()
            val customJobChildJob = scope.launch(customJob) {

            }
        }
        println("Children Count: ${job.children.count()}")
        println("innerJob === job.children.first() ${innerJob === job.children.first()}")
        println("innerJob.parent === job ${innerJob?.parent === job}")

        // 所有父子协程，兄弟协程的执行顺序都是并列的，并没有前后关系 -- 前后关系只会通过Join()或者await()确定
        // 但是父协程会等待所有子协程执行完毕之后才会关闭
        // 具体的应用场
        // 当程序中有一个initData()方法该方法承担数据库的读取/网络请求/文件读取，可以开一个协程去处理这个initData()方法
        // 在initData()执行的过程中同步做视图的初始化
        // 但是当有一个任务依赖这些数据时，可以调用Join()等待initData()协程的执行完成，
        // initData()执行完成的时候，initData()的子协程也一定执行完成了
        val initJob = scope.launch {
            // initData()
            launch {
                // 子协程1处理 ...
            }
            launch {
                // 子协程2处理 ...
            }
        }
        // 处理不依赖initData()的逻辑
        scope.launch {
            initJob.join()
            // 处理一定依赖initData()执行完毕之后的逻辑
        }
    }

    // 线程的取消
    fun threadCancel() {
        val thread = thread {
            println("thread start")
            // 从线程内部结束线程
//            return@thread
            try {
                Thread.sleep(200)
            } catch (e: InterruptedException) {
                // 需要手动处理结束状态，执行清理操作
                // 在抛出异常之后Thread.currentThread().isInterrupted会重新置为false
                // 在其他等待方法都会抛出InterruptedException异常
                throw e
            }
//            println("thread end")

            var count = 0
            while (true) {
                if (Thread.currentThread().isInterrupted) {
                    // 需要手动处理结束状态，执行清理操作
                    // 给图片加滤镜 --> 恢复图片
                    // 给图片的拷贝加滤镜 --> 删除拷贝
                    println("thread end")
                    return@thread
                }
                count++
                if (count % 100_000_000 == 0) {
                    println(count)
                }
                if (count % 1_000_000_000 == 0) {
                    break
                }
            }
        }
        Thread.sleep(300)
        // 强制杀死线程，不管线程的执行状态
        // 会导致内存中的对象状态不连续，有时这种不连续是没办法恢复的只能依靠重启程序，所以该方法已经废弃
//        thread.stop()

        // 让线程自杀 从外部通知线程该结束了，需不需要结束怎么结束由线程自己判断
        // 当对一个线程调用interrupt，会把线程的中断状态isInterrupted标记为true，在线程中设置检查点主动结束线程
        // 提早结束线程是为了节省资源，一般用法是在耗时任务中检查isInterrupted状态
        // 例如：在给一张图片加滤镜，可以在每一层滤镜添加之前检查，或者添加到特定百分比时候就检查一次
        thread.interrupt()
        // 当线程在sleep过程中调用thread.interrupt()会立即抛出异常
        // 当调用thread.interrupt()之后开始sleep也会立即抛出异常
        // 这就是为什么java在调用Thread.sleep()时都要加try catch，这是对打断的订阅，在catch中应该写线程的中断代码回收清理
    }

    // 协程的取消
    fun coroutinesCancel() {
        runBlocking {
            // 如果在Java中创建的Main函数中调用该方法那runBlocking所处的线程就是主线程
            // 其他线程是守护线程，程序是不会等待守护线程的结束而结束的，主线程结束代表业务逻辑处理完成，程序关闭
            // 如果这样创建协程，该协程并不是runBlocking所启动的协程的子协程
            // runBlocking所启动的协程不会等待该协程结束再结束
//            val scope = CoroutineScope(Dispatchers.Default)
//            val job = scope.launch {
//                var count = 0
//                while (true) {
//                    println("cor count: $count")
//                    delay(500)
//                    count++
//                }
//            }
//            // 如果在这里delay()够久则会一直拖住runBlocking协程不结束而主线程就不会结束，打印就会继续
//            delay(100000)


            val job = this.launch {
                var count = 0
                while (true) {
                    if (!isActive) {
                        // 执行清理逻辑
                        // 协程的取消不使用return@launch
//                        return@launch
                        // 而是使用 throw CancellationException() 抛出异常
                        throw CancellationException()
                    }
                    // 如果不需要做代码清理，则可以直接使用ensureActive()
                    ensureActive()
                    println("cor count: $count")
                    delay(500)
                    count++
                }
            }
            // 协程的取消也是交互式取消
            delay(3000)
            // 当内部不做return@launch操作配合取消，协程是不会真正地取消的
            // 其内部的delay(500)导致了协程的取消 如果外部执行cancel()取消协程内部如果执行delay()方法也会抛出CancellationException异常
            // 而在协程内部只要抛出CancellationException异常就会取消协程
            // 返回如果在delay的外部包裹一层try catch去捕获这个异常，异常没有被抛出则协程反而无法停止，这种情况一定要调用throw将异常抛出
            job.cancel()
        }
    }

    // 协程的结构化取消
    fun coroutinesStructureCancel() {
        runBlocking {
            val scope = CoroutineScope(Dispatchers.Default)
            var childJob: Job? = null
            val parentJob = scope.launch {
                childJob = launch {
                    println("child job begin")
                    // 因为父协程调用了cancel而子协程使用了delay挂起函数，所以会抛出CancellationException子协程取消
//                    delay(3000)
//                    var count = 0
//                    while (true) {
//                        // 虽然调用了父协程的取消，但是没有触发检查和挂起函数，所以print会一直执行下去
//                        println("child job print count: $count")
//                        count++
//                    }
                    // 没有调用挂起函数，而是调用线程休眠
                    Thread.sleep(3000)
                    println("child job end")
                }
            }
            delay(1000)
            // 对于父协程的取消子协程能够拒绝吗
            // 每个job的cancel都包含了其子job的cancel的调用
            // 当一个job调用cancel之后会修改自己内部的isActive标志为false并且在检查点或者挂起函数节点抛出CancellationException()
            // 并且调用子Job的cancel方法，但是子job可能1.没有检查点或者2.没有挂起函数再或者3.子协程捕获了CancellationException异常，
            // 在这些特殊情况下子协程会一直执行直至协程内的逻辑执行完毕，这种情况是需要预防的
            // 所以不管是父子协程的取消本质上还是交互式的取消
            parentJob.cancel()
            println("父协程已经结束了，父协程的isActive状态：${parentJob.isActive}")
            println("父协程已经结束了，子协程的isActive状态：${childJob?.isActive}")
            measureTime { parentJob.join() }.also { println("父协程被拖住了：$it") }
            // 另外如果子协程一直结束不了，会拖住他的父协程让父协程也一直结束不了
            // 另一个层面上当一个协程的isActive为false的时候，该协程也可能是运行状态
        }
    }

    // 拒绝取消协程
    fun nonCancellableCoroutines() {

        suspend fun writeFile() {
            withContext(Dispatchers.IO + NonCancellable) {
                // 写第一段文件
                // 从数据库中读取数据
                // 将数据库中的数据再次写入文件
            }
        }

        runBlocking {
            val scope = CoroutineScope(Dispatchers.Default)
            var childJob: Job? = null
            val parentJob = scope.launch {
                // 当加上NonCancellable之后父协程的取消就无法触发子协程的取消
                // NonCancellable本质上是一个单例Job()也就是通过制定特定的Job()的方式切断了父子关系
                // NonCancellable不仅切断了它创建的协程与它的父协程的关系，它也切断了它创建的协程与它的子协程的父子关系
                // NonCancellable.children也是空实现
                childJob = launch(NonCancellable) {
                    println("child job begin")
                    writeFile()
//                    throw CancellationException()
                    delay(3000)
                    println("child job end")
                }
                println("parent job begin")
                delay(3000)
                println("parent job end")
            }
            delay(1500)

            parentJob.cancel()
            // 使用launch返回的childJob对象去取消内部的子协程，子协程是可以被取消的 ！！！
            childJob?.cancel()
//            NonCancellable.cancel()
            // 此时子协程的isActive还是true
            println("子协程的isActive状态：${childJob?.isActive}")
            // NonCancellable.cancel()内部是空实现
            // NonCancellable.cancel()是空实现，所以在它内部调用挂起函数或者抛出异常？？？
//            NonCancellable.cancel()
//            NonCancellable.children
            // 实际应用
            // 1. 协程退出之后的收尾清理工作 应该配合withContext一起使用
            // 2. 没法收尾的业务代码 -> writeFile() 后台写文件
            // 3. 当前的工作和当前的协程无关 -> 埋点日志工作 这种工作应该用launch来包裹

            // 总结：使用了NonCancellable的子协程
            // 1.当父协程cancel的时候子协程内部delay不会cancel
            // 2.子协程内部抛CancellationException异常会子协程会结束
            // 3.使用launch返回的childJob对象调用cancel如果内部delay会结束子协程 也就是说launch返回的childJob和NonCancellable不是同一个Job
        }
    }

    // 协程的结构化异常
    fun coroutinesException() {
        runBlocking {
            val scope = CoroutineScope(Dispatchers.Default)
            // 错误示范，在协程外部不能捕获到协程内部的异常
//            try {
//                scope.launch {
//                    throw IllegalStateException("Wrong!")
//                }
//            } catch (e: Exception) {
//                // ...
//            }

            // 当一个协程抛出异常，他的父子协程树中的协程都会被取消
            var topParentJob: Job?
            var parentJob: Job? = null
            var childJob: Job? = null
            topParentJob = scope.launch {
                parentJob = launch {
                    childJob = launch {
                        println("child job begin")
                        delay(3000)
                        println("child job end")
                    }
                    println("parent job begin")
                    delay(1000)
//                     throw IllegalStateException("Wrong!")
//                    throw CancellationException()
                    println("parent job end")
                }
                println("top parent job begin")
                delay(5000)
                println("top parent job end")
            }
            delay(500)
            println("父父协程的isActive状态：${topParentJob.isActive}")
            println("父协程的isActive状态：${parentJob?.isActive}")
            println("子协程的isActive状态：${childJob?.isActive}")
//            parentJob?.cancel()
            delay(1000)
            println("父父协程的isActive状态：${topParentJob.isActive}")
            println("抛出异常之后父协程的isActive状态：${parentJob?.isActive}")
            println("抛出异常之后子协程的isActive状态：${childJob?.isActive}")
            CancellationException()

            // 如果子协程关闭，如果父协程下的全部子协程都关闭了，则父协程会关闭，否则子协程的关闭不会传递到父协程
            // 子协程抛出CancellationException异常和关闭逻辑一样
            // 如果子协程抛异常，其父子协程都会被异常传递，从而导致父子协程和兄弟协程都被传递取消

            // 取消流程中的CancellationException异常只是用来结束协程而普通异常会暴露给线程世界
            // 协程可以通过注册CoroutineExceptionHandler来防止异常暴露给线程世界
        }
    }

    // 内存泄漏
    // 1.static变量   2.活跃线程所指向的变量    3.JNI变量
    // 这三种变量和变量所引用的变量会被GC Collector判定为有用的不能回收的变量
    // 开启一个后台活跃线程，该活跃线程通过一步步的操作持有该Activity中的引用或持有该Activity成员的引用，
    // 此时需要关闭该Activity，但是该活跃线程还是间接持有该Activity中的引用，
    // 因此GC Collector无法将该Activity视为垃圾，导致该Activity无法被回收
    // 接下来，如果该后台线程结束执行，该线程会回收，此时的Activity也跟着被回收
    // TODO 变量销毁 和 内容释放的区别

    // 传统方法是通过弱引用在关闭Activity的时刻就释放内容
    // RxJava是通过切断业务链条的方式，也就是当Activity关闭时候如果此时线程中还有未执行完毕的内容直接切断执行的方式来保证内存不泄漏
    // RxJava的这种方式不仅节省了Activity所占用内容的时间，也缩短的线程占用内容的时间，更好的节省了CPU性能

    suspend fun retroRequest(hostName: String, user: String): List<Repo> {
        val retro = Retrofit.Builder()
            .baseUrl(hostName)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retroService = retro.create(RetrofitServerInterface::class.java)
        // 调用了其他挂起函数
        return retroService.listReposCor(user)
    }


    // 并行协程之间的交互
    suspend fun suspendInter() {
        // 实际上这两个请求是串行的？？？
        val repos1 = retroRequest("https://api.github.com", "octocat")
        val repos2 = retroRequest("https://api.github.com", "octocat")
    }

    fun coroutinesRunBlocking() {
        CoroutineScope(Dispatchers.Default)
        runBlocking {
            // 1.不需要CoroutineScope
            // CoroutineScope提供CoroutineContext告诉协程该运行在什么线程中
            // CoroutineScope提供取消功能
            // 2.它是阻塞的 会阻塞当前线程，直到运行完成
            // 因此它的作用是将挂起函数的协程代码转化为阻塞式运行的

            // 阻塞运行
            RetrofitServerInterface.retroRequest("https://api.github.com", "octocat")
            RetrofitServerInterface.retroRequest("https://api.github.com", "octocat")
        }
    }
}