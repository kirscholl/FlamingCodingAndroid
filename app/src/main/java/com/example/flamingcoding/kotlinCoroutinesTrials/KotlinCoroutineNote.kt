package com.example.flamingcoding.kotlinCoroutinesTrials

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.flamingcoding.retrofitOkHttpTrials.Repo
import com.example.flamingcoding.retrofitOkHttpTrials.TestServerInterface
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class CustomCoroutineContext : AbstractCoroutineContextElement(CustomCoroutineContext) {
    companion object Key : CoroutineContext.Key<CustomCoroutineContext>

    public fun contextLog() {
        println("CustomCoroutineContext contextLog")
    }
}

class KotlinCoroutinesNote {
    // kotlin协程编码优势：用线性代码去处理结构化并发

    // 创建线程，启动线程，线程切换
    fun createThreadTest(context: Context) {
        // 直接启动线程
        // 创建线程返回的对象就是该线程
        // 这个对象可以对线程这个抽象概念进行管理，而协程不是
        val thread1 = Thread {
            println("线程1启动")
        }
        thread1.start()
        // 等同于
        val thread2 = thread {
            //...
            println("线程2启动")
        }

        // 使用线程池
        val executor = Executors.newCachedThreadPool()
        executor.execute {
            //...
            println("线程3启动")
        }

        // 切换到Ui线程
        // 使用Handler切换到主线程
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            //...
            println("handler主线程处理任务")
        }
        // 或者使用view切换到主线程
//        val view = View(context)
//        view.post {
//            //...
//        }
    }

    // 创建协程，启动协程，协程切换
    fun createCoroutinesTest() {
        runBlocking {
            val outScope = CoroutineScope(Dispatchers.IO)
            // 启动一个协程
            // 启动协程所返回的对象类型为Job，类型限制是为了更好地区分职责，限制功能
            var innerJob: Job? = null
            val outerJob = outScope.launch(Dispatchers.Default) {
                // 启动协程的scope和协程启动后内部的scope不是同一个scope
                // outScope: CoroutineScope(coroutineContext=[JobImpl{Active}@fe7eae0, Dispatchers.IO])
                // innerScope: StandaloneCoroutine{Active}@88f7199
                // outScope === innerScope: false
                val innerScope = this
//                println("outScope: $outScope")
//                println("innerScope: $innerScope")
//                println("outScope === innerScope: ${outScope === innerScope}")

                innerJob = coroutineContext[Job]

                // 启动协程的scope的coroutineContext和协程启动后内部的scope的coroutineContext的context也不同
                // outerContext: [JobImpl{Active}@88f7199, Dispatchers.IO]
                // innerContext: [StandaloneCoroutine{Active}@f062c5e, Dispatchers.Default]
                // outerContext === innerContext: false
                val outerContext = outScope.coroutineContext
                val innerContext = coroutineContext
//                println("outerContext: $outerContext")
//                println("innerContext: $innerContext")
//                println("outerContext === innerContext: ${outerContext === innerContext}")

                // ContinuationInterceptor启动前设置的被启动时候在launch中设置的所覆盖了
                // outerInterceptor: Dispatchers.IO
                // innerInterceptor: Dispatchers.Default
                // outerInterceptor === innerInterceptor: false
                val outerInterceptor = outScope.coroutineContext[ContinuationInterceptor]
                val innerInterceptor = coroutineContext[ContinuationInterceptor]
//                println("outerInterceptor: $outerInterceptor")
//                println("innerInterceptor: $innerInterceptor")
//                println("outerInterceptor === innerInterceptor: ${outerInterceptor === innerInterceptor}")
                delay(1000)
            }
            delay(500)
            // 内外Job没有变化
            // outerJob: StandaloneCoroutine{Active}@8dc8e55
            // innerJob: StandaloneCoroutine{Active}@8dc8e55
            // outerJob === innerJob: true
//            println("outerJob: $outerJob")
//            println("innerJob: $innerJob")
//            println("outerJob === innerJob: ${outerJob === innerJob}")

            // Job的一系列方法都是协程流程上的方法，这种设计让其并不能干预协程内部的具体运行逻辑
//        println(outerJob.isActive)
//        println(outerJob.isCancelled)
//        println(outerJob.isCompleted)
//        println(outerJob.parent)
//        println(outerJob.children)
//        outerJob.cancelChildren()

            // 如果启动模式为Lazy则其内部创建了 一个LazyStandaloneCoroutine对象、
            // LazyStandaloneCoroutine继承自StandaloneCoroutine
            val lazyJob = outScope.launch(Dispatchers.Default, CoroutineStart.LAZY) {
                //
                val interceptor = this.coroutineContext[ContinuationInterceptor]
                println("Lazy协程开始启动")
            }
            Thread.sleep(2000)
            lazyJob.start()

////        在启动协程时自定义线程池
//        val scopeThreadPool = newFixedThreadPoolContext(4, "FlaMingThreadPool")
////        使用完一定要记得关闭
//        scopeThreadPool.close()
////        单线程线程池
//        val singleThreadPool = newSingleThreadContext("FlamingSingleThreadPool")
//        singleThreadPool.close()

            // 系统提供的线程池都是全局的，所以不用手动关闭
            // 在计算密集型任务的线程池设置中，线程池的大小应与CPU核心数量相等
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
            // I/O密集型任务：跟磁盘和网络交互相关的任务
            // 在I/O密集型任务的线程池设置中，线程池的大小: * <= 64 = 64 || * > 64 = *
//        @JvmStatic
//        public val IO: CoroutineDispatcher get() = DefaultIoScheduler
        }
    }

    // 为协程制定Name
    fun coroutineNameTest() {
        runBlocking {
            val name = CoroutineName("TestCoroutine")
            val scope = CoroutineScope(name)
            scope.launch {
                println("CoroutineName: ${coroutineContext[CoroutineName]?.name}")
                withContext(name) {

                }
            }
            launch(name) {

            }
        }
    }

    // CoroutineContext的get获取
    fun coroutineContextValueGetTest() {
        runBlocking {
            // + 重写了plus操作符方法，该方法返回一个CombinedContext
            // 会对重复元素进行剔除
            val scope = CoroutineScope(Dispatchers.Default + Job() + CoroutineName("TestCoroutine"))
            val job1 = Job()
            val job2 = Job()
            // Job中的plus操作符也被重写了，level Error
//            job1 + job2
            // CoroutineDispatcher中的plus操作符也被重写了，level Error
//            Dispatchers.Default + Dispatchers.IO

            // CoroutineContext重写了get方法，get方法接受的是一个Key类型
            // 其中Job参数其实是public companion object Key : CoroutineContext.Key<Job>
            coroutineContext[Job.Key]
            // public companion object Key : CoroutineContext.Key<CoroutineName>
            coroutineContext[CoroutineName.Key]
            // CoroutineContext.Key<ContinuationInterceptor>
            coroutineContext[ContinuationInterceptor.Key]
            // 从coroutineContext中取CoroutineDispatcher而不是ContinuationInterceptor
            val dispatcher = coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
            // 从原有的CoroutineDispatcher创建一个线程数为参数值的线程池
            dispatcher.limitedParallelism(3)

            // 删除CoroutineContext中的key
            coroutineContext.minusKey(CoroutineName)
        }
    }

    // 自定义CoroutineContext
    fun customCoroutineContextTest() {
        // 自定义CoroutineContext创建一个新的CoroutineContext类型
        val customCoroutineContext = CustomCoroutineContext()
        val scope = CoroutineScope(customCoroutineContext)
        runBlocking {
            scope.launch {
                // 调用自定义CoroutineContext的自定义函数
                coroutineContext[CustomCoroutineContext]?.contextLog()
            }
        }
    }

    // withContext
    fun withContextTest() {
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
            // 执行顺序 xxx1 xxx3 xxx2
        }

        // 对比coroutineScope和withContext唯一区别withContext可以传参
        CoroutineScope(Dispatchers.Default).launch {
            // xxx1
            println("协程执行顺序测试 withContext：xxx1")
            withContext(Dispatchers.IO) {
                // 使用withContext让外层协程等待里层协程的代码执行完毕再执行
                Thread.sleep(2000)
//                delay(2000)
                println("协程执行顺序测试 withContext：xxx2")
            }
            // xxx3
            println("协程执行顺序测试 withContext：xxx3")
            // 执行顺序xxx1 xxx2 xxx3
        }
    }

    // 挂起函数只能在协程中或者其他挂起函数中调用
    // 当在函数中调用了别的挂起函数时需要使用suspend关键字
    suspend fun suspendTestFun() {
        val retro = Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retroService = retro.create(TestServerInterface::class.java)
        // 调用了其他挂起函数
        val repos = retroService.listReposCor("octocat")
    }

    // Thread线程切换
    // 在java中，在一个线程中处理完部分任务之后是没有办法再切换到主线程中执行后续任务的，
    // 本质上是没有办法切换到指定的其他线程中去执行后续任务，而Android客户端中有切回主线程这个诉求是因为Android客户端特殊的线程设计
    // Android客户端主线程是一个循环的线程，所以才有切换回主线程这个需求
    // 切回主线程这个动作本质上是向循环的主线程提交特定的任务，让主线程去处理该任务

    // 协程的线程切换
    suspend fun suspendSwitchTest() {
        val retro = Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retroService = retro.create(TestServerInterface::class.java)

        // 连续的两行代码
        val repos = retroService.listReposCor("octocat")
        println(repos.toString())
        // 协程为什么不卡主线程，调用协程之后是怎么切到其他线程的 ，在执行完协程内的内容之后是如何切换回当前线程的 -->
        // 主线程并未等待协程内容执行完毕，在编写协程代码时候看似两行是连续的但是编译器在编译时候会根据协程状态机自动添加回调，
        // 在开始执行协程内容时开启新的线程，在执行完协程内的内容之后使用回调调回当前线程
    }

    // 父子协程之间的关系
    fun corCorParentTest() {
        runBlocking {
            val scope = CoroutineScope(EmptyCoroutineContext)
            var innerJob1: Job? = null
            var innerJob2: Job? = null
            var innerJob3: Job? = null
            var parentJob: Job? = null
            // 通过指定Job的方式来关联父子协程
            innerJob3 = scope.launch(scope.coroutineContext[Job]!!) {
                delay(2000)
                println("innerJob3: $innerJob3")
            }

            innerJob2 = scope.launch {
                delay(2000)
                println("innerJob2: $innerJob2")
            }

            // 自定义Job
            val innerJob4 = Job()
            val innerLaunchJob4 = scope.launch(innerJob4) {
                delay(2000)
            }

            parentJob = scope.launch {
                // 内部协程是由this也就是外部的CoroutineScope创建的
                // 外部的CoroutineScope包含了外部返回的Job对象
                innerJob1 = this.launch {
                    delay(2000)
                    println("innerJob1: $innerJob1")
                }

                println("里层Context获取的Job与返回Job对比: ${scope.coroutineContext[Job] === parentJob}")
                // 父协程内部Context获取的Job children count 3 -> innerJob1 innerJob2 innerJob3
                println("父协程内部Context获取的Job children count: ${scope.coroutineContext[Job]!!.children.count()}")
                // 父协程返回的Job children count 1 -> innerJob1
                println("父协程返回的Job children count: ${coroutineContext[Job]?.children?.count()}")
            }
            delay(1000)
            // parentJob children count: 1 -> innerJob1
            println("parentJob children count: ${parentJob.children.count()}")

            // 所有父子协程，兄弟协程的执行顺序都是并列的，并没有前后关系 -- 前后关系只会通过Join()或者await()确定
            // 但是父协程会等待所有子协程执行完毕之后才会关闭
            // 具体的应用场景：
            // 当程序中有一个initData()方法该方法承担数据库的读取/网络请求/文件读取，可以开一个协程去处理这个initData()方法
            // 在initData()执行的过程中同步做视图的初始化
            // 但是当有一个任务依赖这些数据时，可以调用Join()等待initData()协程的执行完成，
            // initData()执行完成的时候，initData()的子协程也一定执行完成了
//            val initJob = scope.launch {
//                // initData()
//                launch {
//                    // 子协程1处理 ...
//                    println("子协程1处理")
//                }
//                launch {
//                    // 子协程2处理 ...
//                    println("子协程2处理")
//                }
//            }
//            // 处理不依赖initData()的逻辑
//            scope.launch {
//                initJob.join()
//                // 处理一定依赖initData()执行完毕之后的逻辑
//                println("initData()执行完毕之后的逻辑")
//            }
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
            println("父协程已经结束了，父协程的isActive状态：${parentJob.isActive}")    // false
            println("父协程已经结束了，子协程的isActive状态：${childJob?.isActive}")    // false
            println("父协程已经结束了，父协程的isCancelled状态：${parentJob.isCancelled}")  // true
            println("父协程已经结束了，子协程的isCancelled状态：${childJob?.isCancelled}")  // true
            println("父协程已经结束了，父协程的isCompleted状态：${parentJob.isCompleted}")  // false
            println("父协程已经结束了，子协程的isCompleted状态：${childJob?.isCompleted}")  // false
            measureTime { parentJob.join() }.also { println("父协程被拖住了：$it") }
            // 另外如果子协程一直结束不了，会拖住他的父协程让父协程也一直结束不了
            // 另一个层面上当一个协程的isActive为false的时候，该协程也可能是运行状态 -> 用isCompleted判断
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
            // 使用launch返回的childJob对象去取消内部的子协程，子协程是可以被取消的
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
//            var topParentJob: Job?
//            var parentJob: Job? = null
//            var childJob: Job? = null
//            topParentJob = scope.launch {
//                parentJob = launch {
//                    childJob = launch {
//                        println("child job begin")
//                        delay(3000)
//                        println("child job end")
//                    }
//                    println("parent job begin")
//                    delay(1000)
////                     throw IllegalStateException("Wrong!")
////                    throw CancellationException()
//                    println("parent job end")
//                }
//                println("top parent job begin")
//                delay(5000)
//                println("top parent job end")
//            }
//            delay(500)
//            println("父父协程的isActive状态：${topParentJob.isActive}")
//            println("父协程的isActive状态：${parentJob?.isActive}")
//            println("子协程的isActive状态：${childJob?.isActive}")
////            parentJob?.cancel()
//            delay(1000)
//            println("父父协程的isActive状态：${topParentJob.isActive}")
//            println("抛出异常之后父协程的isActive状态：${parentJob?.isActive}")
//            println("抛出异常之后子协程的isActive状态：${childJob?.isActive}")
//            CancellationException()

            // 如果子协程关闭，如果父协程下的全部子协程都关闭了，则父协程会关闭，否则子协程的关闭不会传递到父协程
            // 子协程抛出CancellationException异常和关闭逻辑一样
            // 如果子协程抛异常，其父子协程都会被异常传递，从而导致父子协程和兄弟协程都被传递取消

            // 取消流程中的CancellationException异常只是用来结束协程而普通异常会暴露给线程世界
            // 协程可以通过注册CoroutineExceptionHandler来防止异常暴露给线程世界
        }
    }

    // CoroutineExceptionHandler
    fun coroutineExceptionHandlerTest() {
        // 为所有线程添加UncaughtExceptionHandler，通常都会使用全局设置
        // 拦截线程内所抛出的异常
        // 使用场景：在线程中抛出异常之后清理收尾、日志记录，让线程自杀，或重启线程
//        Thread.setDefaultUncaughtExceptionHandler { t, e ->
//            println("Exception: $e")
//            exitProcess(1)
//        }
//        thread {
//            // 为当前线程添加UncaughtExceptionHandler
////            Thread.currentThread().setUncaughtExceptionHandler { t, e ->
////                println("Exception: $e")
////                exitProcess(1)
////            }
//            throw RuntimeException("Error !")
//        }

        runBlocking {
            // 这种写法只能监听协程启动过程中的异常，并不能监听到协程启动之后运行内部的异常
//            try {
//                scope.launch {
//                    throw IllegalStateException("Wrong!")
//                }
//            } catch (e: Exception) {
//                // ...
//            }
            val scope = CoroutineScope(Dispatchers.Default)
            val handler = CoroutineExceptionHandler(
                fun(context: CoroutineContext, exception: Throwable): Unit {
                    println("Exception: $exception")
                }
            )
            // 使用CoroutineExceptionHandler捕获所有子协程抛出的异常，它是结构化的
            // CoroutineExceptionHandler只有设置在最外层协程中才能生效，协程的异常是结构化的
            // 应用场景
            var parentJob: Job?
            var childJob: Job? = null
            parentJob = scope.launch(Dispatchers.Default + handler) {
                launch {
//                    while (true) {
//                        println("childJob work")
//                    }
                }
                childJob = launch {
                    throw RuntimeException("Error!")
                }
            }
            // 抛出异常之后父子协程的isActive状态都已经置为false
            println("父协程的isActive状态：${parentJob.isActive}")
            println("子协程的isActive状态：${childJob?.isActive}")
        }
    }

    // async的异常结构化管理
    fun asyncFunException() {

        suspend fun testAsync(): String? {
            val scope = CoroutineScope(Dispatchers.Default)
            var asyncParent: Job
            var asyncRes: Deferred<String>? = null
            val handler = CoroutineExceptionHandler { context, throwable ->
                println("父协程的handler捕获到异常: $throwable")
            }
            asyncParent = scope.launch(handler) {
                // 这里要async启动，因为只有async启动的协程才会把异常抛给调用它的协程
                // 如果这里使用launch则调用它的协程变为了该launch启动的协程，外层是捕获不到异常的，会直接crash
                asyncRes = async {
                    delay(1000)
                    println("调用 async协程 输出")
//                    throw CancellationException()
                    throw RuntimeException("async协程抛出Runtime异常")
                }
            }
            delay(500)
            println("async协程的父协程的isActive状态：${asyncParent.isActive}")
            println("async协程的isActive状态：${asyncRes?.isActive}")
            delay(2000)
            println("抛出异常之后async协程的父协程的isActive状态：${asyncParent.isActive}")
            println("抛出异常之后async协程的isActive状态：${asyncRes?.isActive}")
//            var asyncStr: String? = ""
//            try {
//                asyncStr = asyncRes?.await()
//            } catch (e: Exception) {
//                println("抛出异常: $e")
//            }
            return asyncRes?.await()
        }

        runBlocking {
            val scope = CoroutineScope(Dispatchers.Default)
//            scope.launch(Dispatchers.Default) {
//                deferred = async {
//                    delay(2000)
//                    println("###1 async 方法")
//                    ""
//                }
//                launch {
//                    delay(1000)
//                    throw CancellationException()
//                    println("###3 normal launch 方法")
//                }
//            }
            val invokeAsyncJob = scope.launch {
                var asyncStr: String? = ""
                try {
                    asyncStr = testAsync()
                } catch (e: Exception) {
                    println("外层调用协程捕获到异常: $e")
                }
                println("asyncStr $asyncStr")
            }
            println("调用async协程的isActive状态: ${invokeAsyncJob.isActive}")
            delay(3000)
            println("抛出异常之后调用async协程的isActive状态: ${invokeAsyncJob.isActive}")

            // async协程在抛出异常之后不仅触发结构化异常流程，还会传递给调用async协程的协程导致该协程的关闭
            // async协程的异常并不会直接抛出到线程世界而是先抛到调用它的协程
            // 因为async的异常不往线程世界抛所以为async协程设置CoroutineExceptionHandler也是没有意义和效果的
//            scope.async(/*handler*/) {
//                val deferred = async {
//                    delay(1000)
//                    throw RuntimeException("Error!")
//                }
//                launch(Job()) {
//                    try {
//                        deferred.await()
//                    } catch (e: Exception) {
//                        println("抛出异常: $e")
//                    }
//                }
//            }
        }
    }

    // SupervisorJob
    fun supervisorJobTest() {
        // 使用SupervisorJob创建的协程，子协程抛异常父协程不会连带性地被取消子协程的兄弟协程也不会被连带取消
        // 对于普通协程：子协程异常->子协程取消->传递到父协程->父协程取消->传递到子协程的兄弟协程->兄弟协程取消
        // SupervisorJob协程 子协程异常->子协程取消->传递到父协程->父协程拦截异常但是不取消 子协程的兄弟协程也就不会取消
        // SupervisorJob协程的childCancelled方法返回false
        // 但是其异常是可以在SupervisorJob协程所捕获到的
        // 使用SupervisorJob创建的协程，他自身触发的异常和取消同样还是结构化的
        runBlocking {
            val scope = CoroutineScope(Dispatchers.Default)
            val handler = CoroutineExceptionHandler { context, throwable ->
                println("supervisorJob协程的handler捕获到异常: $throwable")
            }
            var supervisorJob: Job? = null
            var childJob: Job? = null
            scope.launch {
                // 即使supervisorJob不是最外层的父协程，但是它会像一个最外层的父协程一样工作
                // 既不会取消supervisorJob所在的协程，也会把异常抛到外层的线程世界
                // 给其添加CoroutineExceptionHandler是能捕获到内部异常的
                supervisorJob = launch(SupervisorJob() + handler) {
                    childJob = launch {
                        throw RuntimeException("SupervisorJob的子协程抛出异常")
//                    throw CancellationException("SupervisorJob的子协程取消")
                    }
                }
            }
            delay(1000)
            println("父supervisorJob协程的isCancelled状态：${supervisorJob?.isCancelled}")
            println("子协程的isCancelled状态：${childJob?.isCancelled}")
        }
    }

    fun coroutineScopeAndCoroutineContext() {
        runBlocking {
            // CoroutineScope
            // 1. CoroutineScope是CoroutineContext的容器
            // 2. 提供启动协程的功能
            // 对于手动创建scope它的CoroutineContext没有实际意义，只有当启动具体协程时才有意义
            val scope = CoroutineScope(Dispatchers.Default)
            val job1 = scope.launch {
                this.coroutineContext
                val job2 = this.coroutineContext[Job]
                this.coroutineContext[ContinuationInterceptor]
            }
        }
        // 管理流程 Job()
        // 管理线程 ContinuationInterceptor
        // 上下文信息 CoroutineContext
    }

    // GlobalScope
    fun globalScopeTest() {
        runBlocking {
            // 单例对象 它没有内置的Job 它的coroutineContext是空的 -> 目的：使其不符合协程的结构化设计！！！
            // 没有context就没有job没有job就无法设置父协程，所以它不符合协程的结构化
            // 应用场景：当启动的协程不跟任何界面、应用组件绑定需要一直存活到应用结束
            var job1 = GlobalScope.launch {
                var job2 = coroutineContext[Job] // 可能为空
                var job3 = coroutineContext.job
                println("coroutineContext: $coroutineContext")
                println("coroutineContext job: ${coroutineContext.job}")
            }
        }
    }

    // coroutineScope
    fun coroutineScopeTest() {
        runBlocking {
            //            launch {
//                println("xxx1")
//                coroutineScope {
//                    delay(1000)
//                    println("xxx2")
//                }
//                println("xxx3")
//            }
            // 执行顺序xxx1 xxx2 xxx3 和withContext一样

            val startTime = System.currentTimeMillis()
            coroutineScope {
                // coroutineScope是有返回值的 -> 最后一行代码
                // 串行执行，会等内部的逻辑全部执行完毕才返回，包括它的子协程的逻辑
                // 实际用途：在挂起函数中提供一个coroutineScope的上下文
                // 将多个async协程组合成结构化的协程
                val res = try {
                    coroutineScope {
                        val deferred1 = async { "test1" }
                        val deferred2 = async {
                            "test2"
                            throw RuntimeException("coroutineScope")
                        }
                        deferred1.await() + deferred2.await()
                    }
                } catch (e: Exception) {
                    println("coroutineScope 异常捕获：$e")
                }
                println("coroutineScope 执行结果：$res")
                delay(1000)
                launch {
                    delay(1000)
                }
            }
            // 2s
            println("coroutineScope执行耗时：${System.currentTimeMillis() - startTime}")
        }
    }

    // 内存泄漏
    // 1.static变量   2.活跃线程所指向的变量    3.JNI变量
    // 这三种变量和变量所引用的变量会被GC Collector判定为有用的不能回收的变量
    // 开启一个后台活跃线程，该活跃线程通过一步步的操作持有该Activity中的引用或持有该Activity成员的引用，
    // 此时需要关闭该Activity，但是该活跃线程还是间接持有该Activity中的引用，
    // 因此GC Collector无法将该Activity视为垃圾，导致该Activity无法被回收
    // 接下来，如果该后台线程结束执行，该线程会回收，此时的Activity也跟着被回收
    // TODO 变量销毁 和 内存释放的区别

    // 传统方法是通过弱引用在关闭Activity的时刻就释放内容
    // RxJava是通过切断业务链条的方式，也就是当Activity关闭时候如果此时线程中还有未执行完毕的内容直接切断执行的方式来保证内存不泄漏
    // RxJava的这种方式不仅节省了Activity所占用内容的时间，也缩短的线程占用内容的时间，更好的节省了CPU性能

    suspend fun retroRequest(hostName: String, user: String): List<Repo> {
        val retro = Retrofit.Builder()
            .baseUrl(hostName)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retroService = retro.create(TestServerInterface::class.java)
        // 调用了其他挂起函数
        return retroService.listReposCor(user)
    }

    // 并行协程之间的交互
    suspend fun suspendInter() {
        // 实际上这两个请求是串行的
        println("1")
        val repos1 = retroRequest("https://api.github.com", "octocat")
        println("2")
        val repos2 = retroRequest("https://api.github.com", "octocat")
        println("3")
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
            TestServerInterface.retroRequest("https://api.github.com", "octocat")
            TestServerInterface.retroRequest("https://api.github.com", "octocat")
        }
    }

    fun completedInvoke() {
        runBlocking {
            val scope = CoroutineScope(EmptyCoroutineContext)
            val parentJob = scope.launch(Dispatchers.Default) {
                println("parentJob运行的coroutineContext： ${this.coroutineContext}")
                withContext(Dispatchers.IO) {
                    println("withContext运行的线程coroutineContext： ${this.coroutineContext}")
                }
            }

            parentJob.invokeOnCompletion {
                println("invokeOnCompletion 运行的线程： ${Thread.currentThread()}")
            }
        }
    }

    fun withTimeoutOrNull() {
        runBlocking {
            withTimeoutOrNull(1.seconds) {

            }
        }
    }

    fun awaitAllTest() {
        fun asyncGetData1(): String {
            return "2"
        }

        fun asyncGetData2(): Int {
            return 1
        }

        fun asyncGetData3(): Float {
            return 3f
        }

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val deferredList = listOf(
                async { asyncGetData1() },
                async { asyncGetData2() },
                async { asyncGetData3() }
            )
            val resList = deferredList.awaitAll()
            val a = resList[0]
            val b = resList[1]
            val c = resList[2]
        }
    }
}