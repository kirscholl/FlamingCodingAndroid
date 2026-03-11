package com.example.flamingcoding.kotlinCoroutinesTrials

import com.example.flamingcoding.retrofitOkHttpTrials.Repo
import com.example.flamingcoding.retrofitOkHttpTrials.TestServerInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.chunked
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.FileWriter
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeoutException
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class KotlinFlowNote {

    fun tesFlow() {
        runBlocking {
            // 状态流
            // StateFlow
            // 事件流
            // ShareFlow
            // 数据流
            // Flow
            // 协程间协作工具
            // Channel
        }
    }

    fun channelProduceTest() {
        val scope = CoroutineScope(EmptyCoroutineContext)
        // 在一个协程中多次进行网络请求，而在另一个协程中不断地获取上一个协程中网络请求的结果
        // 返回一个ReceiveChannel
        //  produce创建一个协程返回一个ReceiveChannel，并且在内部暴露一个SendChannel以ProducerCoroutine的形式
        val receiveChannel = scope.produce {
            // scope调用produce的前后其实返回的都是Channel对象，出于Api暴露的考虑
            // scope.produce返回一个ProducerCoroutine : ChannelCoroutine : Channel : SendChannel<E>, ReceiveChannel<E>
            var count = 0
            while (count <= 10) {
                count++
                val data = TestServerInterface.retroRequest("https://api.github.com", "octocat")
                // 调用内部ProducerScope的send方法，ProducerScope继承自SendChannel
                send(data)
            }
        }
        scope.launch {
            delay(5000)
            while (!receiveChannel.isClosedForReceive) {
                println("Get produce send data: ${receiveChannel.receive()}")
            }
        }
    }

    fun channelTest() {
        runBlocking {
            val scope = CoroutineScope(EmptyCoroutineContext)

            // Channel的本质上是一个挂起队列，在队列满了会挂起等待
            val channel = Channel<Int>()
            scope.launch {
                var count = 1
                while (count < 1000) {
//                    val data = TestServerInterface.retroRequest("https://api.github.com", "octocat")
                    channel.send(count)
                    count++
                }
            }
            // Channel是一个下层支持的组件，不适合直接用于业务
            // 可以在独立的模块实现单订阅的业务逻辑
            // *** 每个send只能被一个receive收到 ！！！
            // 创建时候如果使用默认参数Channel缓冲容量为0，超出缓冲策略为挂起
            // 如果未能及时收到数据，数据不会被丢弃，channel send()所在协程会挂起
            scope.launch {
                delay(1000)
                while (!channel.isClosedForReceive) {
                    println("channel receive data 1: ${channel.receive()}")
                }
            }
            scope.launch {
                while (!channel.isClosedForReceive) {
                    delay(1000)
                    println("channel receive data 2: ${channel.receive()}")
                }
            }
        }
    }

    fun channelApiTest() {
        runBlocking {
            // 通过指定 capacity定制缓冲区，通过onBufferOverflow参数定制缓冲溢出策略
            val channel = Channel<List<Repo>>(10, BufferOverflow.SUSPEND)
            // 使用CONFLATED缓冲区会设置为1，而缓冲策略为总是丢弃最后一个元素
//        val channel2 = Channel<List<Repo>>(CONFLATED)
            // 等同于
//        val channel = Channel<List<Repo>>(1, BufferOverflow.DROP_OLDEST)
            val scope = CoroutineScope(EmptyCoroutineContext)
            scope.launch {
                val data = TestServerInterface.retroRequest("https://api.github.com", "octocat")
                // send是个挂起函数，如果没有及时读取数据导致channel队列满了，send会挂起协程直到队列中有位置了再把数据写入channel队列中
                // channel队列的大小默认为0，没有缓冲区
                // 两个协程使用channel进行数据交互，至少有一个协程是先出于等待状态的
                channel.send(data)
//            channel.close()
            }
            scope.launch {
                // channel的循环遍历是挂起式的，当没有元素获取时
                // public operator fun iterator(): ChannelIterator<E>
                for (data in channel) {
                    println("channel data: $data")
                }
//            channel.cancel()
            }
            // channel的close和cancel，close对应SendChannel，cancel对应ReceiveChannel，close和cancel都是针对channel而言的
            // 在SendChannel调用了close()之后会标记isClosedForSend再调用send会抛出ClosedChannelException异常
            // SendChannel调用了close()是不会对调用close()之前就已经挂起的协程造成异常的
            // SendChannel的关闭不影响接收，包括获取SendChannel中缓冲区中的数据
            // 当channel队列中的数据全部以及send完毕并且也全部已经接收完毕再调用channel.receive()会抛出ClosedReceiveChannelException异常
            // ReceiveChannel在调用了cancel()之后会把isClosedForSend和isClosedForReceive标记都置为true，接收端已经关闭发送也没有任何意义了
            // ReceiveChannel在调用了cancel()之后再调用send()或者receive()CancellationException

            val fileChannel = Channel<FileWriter> { it.close() }
            // channel的cancel可能会造成资源数据的泄漏，当文件读到一半channel就被关闭了
            // 此时使用channel的第三个参数，定制策略
            fileChannel.send(FileWriter("test.txt"))
            fileChannel.cancel()

//            val channel2 = Channel<List<Repo>>(10, BufferOverflow.SUSPEND)
//            // 采用不挂起的方式发送，不会抛出异常，瞬间返回
//            channel2.trySend(TestServerInterface.retroRequest("https://api.github.com", "octocat"))
//            channel2.tryReceive()
//            channel2.receiveCatching()

            // produce创建一个协程返回一个ReceiveChannel，并且在内部暴露一个SendChannel以ProducerCoroutine的形式
//            val receiveChannel = scope.produce {
//                for (num in 1..100) {
//                    send(num)
//                }
//            }
//            launch {
//                for (num in receiveChannel) {
//                    println("receive number: $num")
//                }
//            }

            // actor{ }创建一个协程返回一个SendChannel，并且在内部暴露一个ReceiveChannel以ActorScope的形式
//            val sendChannel = actor<Int> {
//                for (num in this) {
//                    println("receive number: $num")
//                }
//            }
//            launch {
//                for (num in 1..100) {
//                    sendChannel.send(num)
//                }
//            }
        }
    }

    fun sequenceTest() {
        runBlocking {
            // 立即生产数据
            val numList = buildList {
                add(1)
                add(2)
            }

            // 提供一个边生产边消费的数据序列
            // sequence是惰性的，使用的时候才生产，用一条才生产一条
            // 返回的Sequence对象是空的，只生成了一块代码块逻辑，在实际遍历时才生产具体数据
            // SequenceScope 是 RestrictsSuspension sequence{ }内部只能调用自己的挂起函数，例如yield()
            // 所以sequence{ }内部是不支持挂起函数的 这时候就需要使用flow{ }
            val sq = sequence {
                // 报错！
//                delay(5000)
                // 多次持续的网络请求
                for (num in 1..10) {
                    // 报错！
//                    yield(TestServerInterface.retroRequest("https://api.github.com", "octocat"))
                    yield(num)
                }
            }

            launch {
                var count = 0
                // 生产数据的时机在这里
                for (e in sq) {
                    count++
                    println(e)
                    // sequence的第二条数据不会生产
                    if (count == 5) break
                }
            }
        }
    }

    fun flowTest() {
        runBlocking {
            // 提供一个支持协程的边生产边消费的数据列
            val repoFlow = flow {
                for (num in 1..10) {
                    emit(TestServerInterface.retroRequest("https://api.github.com", "octocat"))
                }
            }
            launch {
                repoFlow.collect {
                    println(it)
                }
            }
        }
    }

    fun flowWork() {
        val repoFlow = flow {
            emit("flow第一次发送")
            delay(2000)
            emit("flow第二次发送")
        }

        runBlocking {
            // 两次接收都可以收到已经发送了的数据
            // flow同Sequence一样只有在collect时才生产数据，并且是每次都互不干扰地生产数据
            launch {
                repoFlow.collect {
                    println("第一次接收：$it")
                }
            }
            launch {
                delay(1000)
                repoFlow.collect {
                    println("第二次接收：$it")
                }
            }
        }
    }

    fun createFlow() {
        //创建一个flow { } 对每个函数调用emit()
//        val flow1 = flowOf("123", "123", "123")
//        val flow2 = listOf("123", "123", "123").asFlow()
//        val flow3 = setOf("123", "123", "123").asFlow()
//        val flow4 = sequenceOf("123", "123", "123").asFlow()
//
//        // 使用Channel创建的flow 在不同的协程中进行多次collect时不能拿到全部的数据，会被瓜分数据
//        val channel = Channel<Int>()
//        // consumeAsFlow创建出来的flow只能被消费一次，在调用一次collect之后会在内部标记为已消费，再调用collect会抛出异常
//        val flow5 = channel.consumeAsFlow()
//        val flow6 = channel.receiveAsFlow()


        runBlocking {
//            launch {
//                flow6.collect {
//                    println(" 1 - : $it")
//                }
//            }
//            launch {
//                flow6.collect {
//                    println(" 2 - : $it")
//                }
//            }
//            // 1 - : 1
//            // 1 - : 2
//            // 1 - : 4
//            // 2 - : 3
//            // 1 - : 5
//            // 2 - : 6
//            channel.send(1)
//            channel.send(2)
//            channel.send(3)
//            channel.send(4)
//            channel.send(5)
//            channel.send(6)


            // 使用channelFlow创建出来的flow { }，直到collect时才会创建channel开始生产，多次collect会创建多个Channel
            // flow { }内部是创建了Flow的生产流程，其内部并不具备其他协程的运营环境
            // channelFlow { }内部因为创建了ProducerScope所以在内部可以开启协程去调用send()发送数据，可以支持跨协程生产
//        channelFlow {
//            send(1)
//            send(2)
//            send(3)
//            send(4)
//            send(5)
//            send(6)
//        }

            val flow7 = callbackFlow {
                TestServerInterface.retroRequestWithCallback(
                    "https://api.github.com",
                    "octocat",
                    fun(response): Unit {
                        //channelFlow内部运行的是一个协程，在还没有调用回调的时候协程可能就已经关闭了
                        trySend(response)
                        close()
                    }, fun(throwable): Unit {
                        println("运行异常：$throwable")
                        cancel(CancellationException(throwable))
                    }
                )
                // 调用awaitClose等待协程主动关闭
                // 或者直接使用callbackFlow { } 他是强制调用 awaitClose()
                awaitClose()
            }

            val scope = CoroutineScope(EmptyCoroutineContext)
            scope.launch {
                flow7.collect {
                    println("Get channelFlow response body: $it")
                }
            }
        }
    }

    fun flowCollectTest() {
        val flow7 = flow {
            // ### 报错
//            val scope = CoroutineScope(EmptyCoroutineContext)
//            scope.launch(Dispatchers.IO) {
//                emit(1)
//            }
            emit(1)
        }
        val scope = CoroutineScope(EmptyCoroutineContext)
        scope.launch(Dispatchers.Main) {
            flow7.collect {
                // 如果该协程是在主线程中调用，该方法是一个更新UI的方法
                println("Refresh view in main thread: $it")
                // 如果flow { }中开启的子协程并在IO线程处理数据提交，则此处代码等价于 ###
                // 这就是为什么flow { }中不允许开启子协程的原因
                // flow { }中返回的只是提交代码的逻辑，相当于在collect时会替换这段逻辑
                // ### 报错
//                val scope = CoroutineScope(EmptyCoroutineContext)
//                scope.launch(Dispatchers.IO) {
//                    println("Refresh view in main thread: $it")
//                }
            }
        }
    }

    fun flowOperator() {
        val flowTest1 = flowOf(1, 1, 2, 3, 4, null)
        val flowTest2 = flowOf(1, 2, 3, 4, "xxx1", "xxx2")
        val flowTest3 = flowOf(1, 2, 3, 4, "xxx1", "xxx2", listOf(1, 2, 3), listOf("flaming"))
        val flowTest4 = flowOf("xxx", "XXX", "FlaMing", "flaming")
        runBlocking {
            // 留下符合条件的
            flowTest1.filterNotNull().filter { it % 2 == 0 }.collect {
                println("filter flow 1: $it")
            }
            // 留下不符合条件的
            flowTest1.filterNotNull().filterNot { it % 2 == 0 }.collect {
                println("filter flow 2: $it")
            }
            flowTest2.filterIsInstance<String>().collect {
                println("filter flow 3: $it")
            }
            flowTest3.filter {
                it is List<*> && it.firstOrNull()?.let { item -> item is String } == true
            }.collect {
                println("filter flow 4: $it")
            }
            // 去除重复元素
            flowTest1.distinctUntilChanged().collect {
                println("filter flow 5: $it")
            }
            // 定制去除逻辑，忽略大小写比较
            flowTest4.distinctUntilChanged { a, b ->
                a.equals(b, ignoreCase = true)
            }.collect {
                println("filter flow 6: $it")
            }
            // 先生成比较key再对key进行判断处理
            flowTest4.distinctUntilChangedBy {
                it.uppercase()
            }.collect {
                println("filter flow 7: $it")
            }
        }

        // 自定义扩展函数
        fun <T> Flow<T>.customOperatorTest(): Flow<T> {
            return flow {
                // 创建 flow { }或者channelFlow{ } 再进行逻辑定制
                // ...
                this@customOperatorTest.collect {
                    // 将所有数据发送两次
                    emit(it)
                    emit(it)
                }
            }
        }

        // 自定义在提交一个数据之后冻结一定时间，抛弃冻结时间内的数据
        fun <T> Flow<T>.emitByLockTimeTest(time: Duration): Flow<T> {
            return flow {
                var lastTime = 0L
                collect {
                    if (System.currentTimeMillis() - lastTime >= time.inWholeSeconds) {
                        emit(it)
                        lastTime = System.currentTimeMillis()
                    }
                }
            }
        }

        runBlocking {
            launch {
                flowTest1.customOperatorTest().collect {

                }
            }
        }
    }

    fun flowTimeOutOperator() {
        val flow = flow {
            emit(1)
            emit(1)
            emit(2)
            emit(2)
            delay(2000)
            emit(3)
            emit(2)
            emit(2)
            delay(2000)
            emit(3)
        }
        runBlocking {
            launch {
                // 在发送一条数据之后在特定时间之后还没有发送下一条数据则抛出TimeoutCancellationException
                try {
                    flow.timeout(1.seconds).collect {
                        println("timeout collect: $it")
                    }
                } catch (e: TimeoutCancellationException) {
                    println("timeout collect: $e")
                }
                // 每段时间内获取的所有数据只取一个作为sample，保留最新的那一条，抛弃其他数据
                // 只在固定时间点刷新
                flow.sample(0.5.seconds).collect {
                    println("sample collect: $it")
                }
                // 防抖动
                // 在接收到一条数据时候压住这条数据，如果在时间内没有新数据到来则发送这条数据
                // 如果有新数据到来则丢弃这条数据，压住新的数据
                // 搜索提示
                flow.debounce(1.seconds).collect {
                    println("sample collect: $it")
                }
            }
        }
    }

    fun flowDropTakeTest() {
        val flowTest = flowOf(1, 2, 3, 4, 5)
        runBlocking {
            launch {
                // 过滤前几条传入参数的数据，其他数据下发
                flowTest.drop(2).collect {
                    println("drop collect: $it")
                }
                // 判断条件，抛弃符合条件的数据，一旦遇到不符合条件的数据则下发剩下的数据
                flowTest.dropWhile {
                    it < 3
                }.collect {
                    println("dropWhile collect: $it")
                }
                //
                flowTest.take(2).collect {
                    println("take collect: $it")
                }
                // 判断条件，获取符合条件的数据，一旦遇到不符合条件的数据抛弃剩下的数据
                flowTest.takeWhile {
                    it < 3
                }.collect {
                    println("takeWhile collect: $it")
                }
            }
        }
    }

    fun flowMapTest() {
        val flowTest = flow {
            emit(1)
            delay(120)
            emit(1)
            emit(2)
            emit(3)
        }
        runBlocking {
            // 提供一个算法，把上游传入的数据转化成另外一个类型的数据再传到下游
            flowTest.map {
                it + 1
            }.collect {
                println("map collect: $it")
            }
            // 先转换，再把不为null的数据传到下游
            flowTest.mapNotNull {
                if (it == 3) null else it + 1
            }.collect {
                println("mapNotNull collect: $it")
            }

            // 如果正在转化的这条数据还没有转换完成则抛弃这条数据，继续转换新的数据
            // 在mapLatest中每条数据的转化和提交是异步的
            // 具体应用场景：搜索提示功能
            flowTest.mapLatest {
                delay(100)
                it + 1
            }.collect {
                println("mapLatest collect: $it")
            }
        }
    }

    fun flowTransformTest() {
        val flowTest = flowOf(1, 2, 3, 4, 5)
        runBlocking {
            // map底层使用transform实现的
            flowTest.map {
                it + 1
            }.collect {

            }
            // 处理逻辑并手动调用emit()提交数据
            flowTest.transform {
                repeat(it) {
                    emit("$it - flaming")
                }
            }.collect {
                println("transform collect: $it")
            }

            // transform + takeWhile
            // transformWhile中如果返回true就继续如果返回false后续就全部终止
            flowTest.transformWhile {
                if (it < 3) {
                    emit(it)
                    return@transformWhile true
                }
                return@transformWhile false
            }.collect {
                println("transformWhile collect: $it")
            }

            // 如果正在转化的这条数据还没有转换完成则抛弃这条数据，继续转换新的数据
            // 在transformLatest中每条数据的转化和提交是异步的
            flowTest.transformLatest {
                delay(100)
                emit(it)
            }.collect {
                println("transformLatest collect: $it")
            }
        }
    }

    fun flowWithIndexTest() {
        val flowTest = flowOf(1, 2, 3, 4, 5)
        runBlocking {
            flowTest.withIndex().collect {
                // 0开始
                println("withIndex collect: index: ${it.index} value: ${it.value} ")
            }
            flowTest.collectIndexed { index, value ->
                println("withIndex collect: index: ${index} value: ${value} ")
            }
        }
    }

    fun flowReduceTest() {
        val flowTest = flowOf(1, 2, 3, 4, 5)

        val list = listOf(1, 2, 3, 4, 5)
        list.reduce { acc, i -> acc + i }
        // 1 3 6 10 15
        // 将目前的计算结果引用到后一个元素生成一个新的值，将这些值组成一个list返回
        list.runningReduce { acc, i -> acc + i }
        // 提供初始值的reduce 结果：25
        list.fold(10) { acc, i -> acc + i }
        list.runningFold(10) { acc, i -> acc + i }

        runBlocking {
            val sum = flowTest.reduce { acc, i ->
                // 内部调用了collect，所以他是个挂起函数，只提交最终结果
                acc + i
            }
            println("flow reduce calculate res: $sum")

            flowTest.runningReduce {
                // 返回一个新的flow
                    acc, i ->
                acc + i
            }.collect {
                println("runningReduce collect : $it")

            }

            flowTest.fold(10) { acc, i ->
                acc + i
            }
            flowTest.runningFold(10) {
                // 返回一个新的flow
                    acc, i ->
                acc + i
            }.collect {
                println("runningReduce collect : $it")
            }
        }
    }

    fun flowOnEachTest() {
        val flowTest = flowOf(1, 2, 3, 4, 5)
        runBlocking {
            // 数据拦截器，将数据处理之后提供给下游
            flowTest.onEach {
                println("onEach1: $it")
            }.onEach {
                println("onEach2: $it")
            }.collect {
                println("each collect: $it")
            }
        }
    }

    fun flowChunkedTest() {
        val flowTest = flowOf(1, 2, 3, 4, 5)
        runBlocking {
            // 将数据分块装进List中
            // chunked collect: [1, 2]
            // chunked collect: [3, 4]
            // chunked collect: [5]
            flowTest.chunked(2).collect {
                println("chunked collect: $it")
            }
        }
    }

    // flow异常管理
    fun flowException() {
        val flowTest = flow {
            // 异常透明化，如果用try catch包住emit()会吞掉collect中的产生的异常
            try {
                emit(1)
                emit(2)
                emit(3)
//                throw TimeoutException("emit过程中产生的异常")
                emit(4)
                emit(5)
            } catch (e: Exception) {
                println("在flow{ }流程中捕获异常：$e")
            }
        }
        val scope = CoroutineScope(EmptyCoroutineContext)
        runBlocking {
            // 针对整个collect流程捕获异常
            try {
                flowTest.collect {
                    //这里的异常会在flow{ }中被捕获，导致异常不可见
                    throw TimeoutException("collect单个步骤中产生的异常")
//                    try {
//                        // 数据接收流程，捕获flow collect中单个步骤中的异常
//                        throw Exception("collect单个步骤中产生的异常")
//                    } catch (e: Exception) {
//                        println("捕获collect流程中的单个步骤的异常: $e")
//                    }
                }
            } catch (e: Exception) {
                println("捕获整个flow collect中的异常: $e")
            }
        }

        val flowTest2 = flow {
            // 正确的处理：针对数据生产流程捕获异常，而emit()方法不进行异常捕获
            try {
                println("生产数据1")
            } catch (e: Exception) {
                println("生产数据1中产生异常 $e")
            }
            emit(1)
            try {
                println("生产数据2")
            } catch (e: Exception) {
                println("生产数据2中产生异常 $e")
            }
            emit(2)
        }
        runBlocking {
            try {
                flowTest2.collect {
                    // 这里的异常能够正确地抛出
                    throw Exception("在flow collect {}中的单个步骤中产生的异常")
                }
            } catch (e: Exception) {
                println("捕获整个flow collect中的异常: $e")
            }
        }

        val flowTest3 = flow {
            // 在emit()流程中都可以捕获到map中抛出的异常
            try {
                println("map 生产数据1")
                emit(1)
            } catch (e: Exception) {
                println("map 生产数据1中产生异常 $e")
            }
            try {
                println("map 生产数据2")
                emit(2)
            } catch (e: Exception) {
                println("map 生产数据2中产生异常 $e")
            }
        }.map {
            // map会返回一个flow对象
            throw Exception("在flow操作符中抛出异常")
        }
        runBlocking {
            flowTest3.collect {
                println("map flow collect： $it")
            }
        }
    }

    fun flowCatchMethod() {
        val flowTest = flow {
            // 这里的异常会被.catch{ }捕获
            throw Exception(".catch{ } flow { }中产生的异常")
            emit(1)
            emit(2)
        }.catch {
            // 使用flow { }.catch{ }相当于把flow { }中的代码块全部用try catch包住，
            // 但是不会捕获emit()中的异常，emit()中的异常就是下游异常
            // flow { }.catch{ }只捕获上游异常，不会捕获下游异常
            // 它不会捕获CancellationException，CancellationException是用来取消协程的
            println(".catch{ } 在flow{ }.catch中捕获异常：$it")
            throw Exception(".catch{ } .catch{ }中产生的异常")
        }.onEach {
            throw Exception(".catch{ } .catch{ }中产生的异常")
        }.catch {
            // 在这里只能捕获到上一个流程中抛出的异常
            println(".catch{ } 在flow{ }.catch中捕获异常：$it")
        }

        runBlocking {
            flowTest.collect {
                // 这里的异常会抛出
                throw Exception(".catch{ } collect单个步骤中产生的异常")
            }
        }
        // try catch和.catch{ }的选择 -> 能用try catch尽量用try catch
        // 如果在flow{ }内部使用try catch可以在抛异常的情况下使的flow{ }中的生产流程继续
        // 但是当我们在某些情况下无法去修改flow{ }内部代码时，可以用.catch{ }在flow{ }流程抛出异常时接管整个生产流程
    }

    fun flowRetryTest() {
        var tryTimes = 1
        val flowTest = flow {
            if (tryTimes <= 1) {
                throw Exception(".catch{ } flow { }中产生的异常")
            }
            println("重试次数：$tryTimes")
            emit(1)
            emit(2)
        }.map {
            it * 2
        }.retry(3) { throwable ->
            // 在异常的时候会重启整个上游的flow链条，最多尝试传入参数中的次数
            tryTimes++
            println("$throwable")
            return@retry true
        }
//            .retryWhen { throwable, alreadyRetryTimes ->
//                tryTimes++
//                // alreadyRetryTimes在第一次调用时为0
//                println("异常：$throwable，已经尝试的次数：$alreadyRetryTimes")
//                return@retryWhen true
//            }
        runBlocking {
            flowTest.collect {
                println("retry flow: $it")
            }
        }
    }

    fun flowStateTest() {
        val flowTest = flow {
            emit(1)
            emit(2)
        }.onStart {
            // 在调用collect之后在生产第一个数据之前被调用
            emit(0)
            println("flow state onStart: ")
        }.onCompletion {
            // 所有代码块都发送完时调用
            println("flow state onCompletion: $it")
        }.onEmpty {
            // 会在flow正常结束并且flow没有发送过任何一条数据的时候触发
        }
        runBlocking {
            flowTest.collect {
                println("flow state: $it")
            }
        }
    }

    fun flowFlowOnTest() {
        val flowTest = flow {
//            withContext(Dispatchers.IO) {
//                // 如果用withContext把emit()也包裹住会导致flow下游的线程也被切换
//                // 会报错 java.lang.IllegalStateException: Flow invariant is violated
//                emit(0)
//            }
            // 但是withContext不包裹emit()是可以的
            withContext(Dispatchers.Main) {
                // Dispatchers.Main
                println("withContext{ }中的CoroutineContext：${currentCoroutineContext()}")
            }
            // Dispatchers.IO
            println("flow{ }中的CoroutineContext：${currentCoroutineContext()}")
            emit(1)
            emit(2)
        }.map {
            // Dispatchers.IO
            println("map{ }1的CoroutineContext：${currentCoroutineContext()}")
            // flowOn只会修改上游flow中的CoroutineContext
        }.flowOn(Dispatchers.IO).map {
            // Dispatchers.Default // 两个flowOn Dispatchers.Default
            println("map{ }2的CoroutineContext：${currentCoroutineContext()}")
            // 这个flowOn会管理两个flowOn之间的map { }中的CoroutineContext
        }.flowOn(Dispatchers.Default).flowOn(Dispatchers.IO)
        // 多个flowOn对象连续调用，前一个flowOn会返回CoroutineContext对象
        // 从后往前加！！！留下前面的属性后面的先返回对象！！！
        // 后一个flowOn会修改前一个flowOn所返回的CoroutineContext中的属性

        // flowOn用来给flow定制CoroutineContext
        runBlocking {
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                flowTest.collect {
                    // Dispatchers.Default
                    println("collect{ }中的CoroutineContext：${currentCoroutineContext()}")
                }
            }
        }

//        runBlocking {
//            val scope = CoroutineScope(Dispatchers.Default)
//            val flowTest = channelFlow {
//                println("channelFlow{ }中的CoroutineContext：${currentCoroutineContext()}")
//                send(1)
//            }.flowOn(Dispatchers.IO)
//
//            scope.launch {
//                flowTest.collect {
//                    println("channelFlow collect{ }中的CoroutineContext：${currentCoroutineContext()}")
//                }
//            }
//        }
    }

    fun flowBufferTest() {
        val flowTest = flow {
            for (num in 1..5) {
                println("数据生产 emit之前 ------- num：$num")
                // 缓冲挂起是在emit执行的
                emit(num)
                println("数据生产 emit之后 +++++++ num：$num")
            }
        }
            // 给flow的生产添加一个缓冲，在数据还没有被收集消费完成时就生产下一个数据，缓存传入参数个数据
            // 通过切换线程池让数据的生产和使用分离，在上一条数据生产完毕且还没有使用之前下一条数据就已经开始生产了
            // 这是需要线程切换 + buffer共同实现 flowOn()会默认把缓冲打开
//            .flowOn(Dispatchers.IO)
            .buffer(1, BufferOverflow.SUSPEND)
            // 只缓冲一条数据的buffer
            .conflate()
            // conflate()等于buffer(CONFLATED)
            .buffer(CONFLATED)


        runBlocking {
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                flowTest.collect {
                    delay(1000)
                    println("Buffer test collect输出：$it")
                }
            }
        }
    }

    fun flowMergeTest() {
        val flow1 = flow {
            emit(1)
            delay(100)
            emit(2)
            delay(100)
            emit(3)
            delay(100)
//            emit(4)
        }
        val flow2 = flow {
            emit(5)
            emit(6)
            emit(7)
            emit(8)
        }

        // 连接flow1 flow2，在连接之后哪个数据先到先发
        val mergedFlow = merge(flow1, flow2)
        // 效果一样 连接flow1 flow2，在连接之后哪个数据先到先发
        val flowList = listOf(flow1, flow2)
        val mergedFlowList = flowList.merge()
        runBlocking {
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                mergedFlow.collect {
                    println("Merge flow test collect输出：$it")
                }
            }
        }

        // 取目前的flow1中的元素和目前flow2中的元素进行自定义操作之后生成新的Flow
        // flow1和flow2中只要其中有一个emit()的元素发生改变，新的Flow就会发生一次emit()
        val combinedFlow1 =
            flow1.combine(flow2) { flow1Element, flow2Element -> "$flow1Element, $flow2Element" }
        // 另一种写法
        val combinedFLow2 =
            combine(flow1, flow2) { flow1Element, flow2Element -> "$flow1Element, $flow2Element" }
        // 加入Transform可以自定义提交
        val combineTransformFlow1 = flow1.combineTransform(flow2) { flow1Element, flow2Element ->
            emit("$flow1Element - $flow2Element")
        }
//        val combineTransformFlow2 = combineTransform(flow1, flow2) {
//            for (i in it) {
//                emit(i)
//            }
//        }
        runBlocking {
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                combinedFlow1.collect {
                    println("combine flow test collect输出：$it")
                }
            }
        }

        // 只成对输出，取第一个flow的第一个emit() 和 第二个flow的第一个emit()
        val zippedFlow1 =
            flow1.zip(flow2) { flow1Element, flow2Element -> "$flow1Element, $flow2Element" }

        val listFlow = flowOf(flow1, flow2)
        // 连接flow1 flow2，在连接之后哪个数据先到先发
        val contactedListFlow = listFlow.flattenMerge()
        // 合并flow1 flow2，合并之后会按照先flow1再flow2的顺序依次发
        val mergedListFlow = listFlow.flattenConcat()

        val mappedFlow =
            flow1.map { from -> (1..from).asFlow().map { "$from- $it" } }
        val concatMappedFlow = mappedFlow.flattenConcat()
        val contactedMapListFlow = flow1.flatMapConcat { from -> (1..from).asFlow() }
        val mergedMapListFlow = flow1.flatMapMerge { from -> (1..from).asFlow() }
        val lastMapListFlow = flow1.flatMapLatest { from -> (1..from).asFlow() }

        runBlocking {
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                zippedFlow1.collect {
                    println("zip flow test collect输出：$it")
                }
            }
        }
    }

    fun sharedFlowTest() {
        runBlocking {
            val scope = CoroutineScope(EmptyCoroutineContext)
            scope.launch {
                val flow = flow {
                    println("开始生产") // #2
                    emit(1)
                    println("生产1完成")
                    delay(1000)
                    emit(2)
                    println("生产2完成")
                    delay(1000)
                    emit(3)
                    println("生产3完成")
                }
                // 使用shareIn创建SharedFlow 在调用shareIn的时候生产就已经开始
                println("flow.shareIn调用")
                val sharedFlow = flow.shareIn(scope, SharingStarted.Eagerly)
                println("开始等待") // #1
                delay(500)
                println("等待完成") // #3
                sharedFlow.collect {
                    println("开始收集") // #4
                    // 只能收到2 3并且程序会未响应
                    // sharedFlow只会对Flow的数据做转发，数据的生产和消费是分离的
                    println("sharedFlow collect输出：$it")
                }
                // 执行顺序
                // 开始等待 开始生产 生产1完成 等待完成 生产2完成 开始收集 sharedFlow collect输出：2
                // 生产3完成 开始收集 sharedFlow collect输出：3
            }
            // 使用场景：1.可以接受部分数据流丢失的事件流订阅模式 2.需要生产数据进行分享的事件流数据订阅
        }
    }

    fun sharedFlowApi() {
        runBlocking {
            val scope = CoroutineScope(EmptyCoroutineContext)
            scope.launch {
                val flow = flow {
                    emit(1)
                    delay(1000)
                    emit(2)
                    delay(1000)
                    emit(3)
                }
                // SharingStarted参数
                // SharingStarted.Eagerly在shareIn被调用时候立即开始生产
                // SharingStarted.Lazily在sharedFlow第一次调用collect时上游的flow才会被启动
                // SharingStarted.WhileSubscribed()在sharedFlow第一次调用collect时上游的flow才会被启动，
                // 在一次collect结束之后会关闭上游flow生产，下一次collect会再次打开生产

                // WhileSubscribed

                // replay参数 缓冲缓存区
                //  1.给SharedFlow设置缓冲区 2.对于已经被其他shareFlow实例collect过的数据也会被暂存下来
                //  3.对于fow { }生产流程的结束，缓存也是不会丢弃的，在下一次开始生产时会先发送缓存数据
                val sharedFlow = flow.shareIn(scope, SharingStarted.WhileSubscribed(), 1)
                scope.launch {
                    val outerScope = this
                    launch {
                        delay(3000)
                        outerScope.cancel()
                    }
                    delay(500)
                    // sharedFlow.collect的返回值是Nothing所以它永远不会返回，可以使用抛异常的方式来结束
                    sharedFlow.collect {
                        // 可以收到完整的数据
                        println("sharedFlow1 collect输出：$it")
                    }
                    // sharedFlow的collect如果不主动结束的，collect不结束上游flow { }的生产就不会结束
                    println("sharedFlow1生产流程结束了")
                }
                scope.launch {
                    delay(4000)
                    sharedFlow.collect {
                        println("sharedFlow2 collect输出：$it")
                    }
                }
            }
        }
    }


    fun asSharedFlowTest() {
        runBlocking {
            val scope = CoroutineScope(EmptyCoroutineContext)
            scope.launch {
                // MutableSharedFlow从外部发送事件
                val mutableSharedFlow = MutableSharedFlow<String>()
                // 如果想把mutableSharedFlow暴露给外部订阅但是不希望外部改变mutableSharedFlow的数据提交流程
                // 将其转化为SharedFlow
                val sharedFlow = mutableSharedFlow.asSharedFlow()
                scope.launch {
                    mutableSharedFlow.emit("Out send emit()")
                    mutableSharedFlow.emit("Out send emit()")
                    mutableSharedFlow.emit("Out send emit()")
                }
                scope.launch {
                    mutableSharedFlow.collect {
                        println("MutableSharedFlow collect输出：$it")
                    }
                }
            }
        }
    }

    fun testTodo() {
        val scope = CoroutineScope(EmptyCoroutineContext)
        scope.launch {
            val mutableSharedFlow = MutableSharedFlow<String>()
            mutableSharedFlow.emit("Out send emit()")
            mutableSharedFlow.emit("Out send emit()")
            mutableSharedFlow.emit("Out send emit()")
            mutableSharedFlow.collect {
                println("MutableSharedFlow collect输出：$it")
            }
        }
    }

    fun stateInTest() {
        runBlocking {
            val scope = CoroutineScope(EmptyCoroutineContext)
            scope.launch {
                val flow = flow {
                    println("开始生产") // #2
                    emit(1)
                    println("生产1完成")
                    delay(1000)
                    emit(2)
                    println("生产2完成")
                    delay(1000)
                    emit(3)
                    println("生产3完成")
                }
                // 使用shareIn创建SharedFlow 在调用shareIn的时候生产就已经开始
                println("flow.shareIn调用")
                val stateFlow = flow.stateIn(scope)
                println("开始等待") // #1
                delay(500)
                println("等待完成") // #3
                stateFlow.collect {
                    println("开始收集") // #4
                    // 只能收到2 3并且程序会未响应
                    // sharedFlow只会对Flow的数据做转发，数据的生产和消费是分离的
                    println("sharedFlow collect输出：$it")
                }
                // 执行顺序
                // 开始等待 开始生产 生产1完成 等待完成 生产2完成 开始收集 sharedFlow collect输出：2
                // 生产3完成 开始收集 sharedFlow collect输出：3
            }
            // 使用场景：1.可以接受部分数据流丢失的事件流订阅模式 2.需要生产数据进行分享的事件流数据订阅
        }
    }

    fun stateFlowApiTest() {
        // StateFlow是一个缓冲和缓存大小都是1的SharedFlow，并且可以让外部直接访问这个缓存的对象
        val mutableStateFlow = MutableStateFlow("init state")
        val scope = CoroutineScope(EmptyCoroutineContext)
        // 当想提供MutableStateFlow给外部使用，但是不希望外部改变MutableStateFlow内部状态流转过程
        mutableStateFlow.asStateFlow()
        scope.launch {
            mutableStateFlow.collect {
                println("mutableStateFlow.collect：$it")
            }
            // mutableStateFlow的collect也是不自动关闭的，下面的代码不可达
            println("Test")
        }
        scope.launch {
            delay(2000)
            mutableStateFlow.emit("change state")
        }
    }

    fun coroutineWorkTogether() {
        // 在线程中实现有序的多线程合作
        val latch = CountDownLatch(2)
        thread {
            latch.await()
            println("在countDown结束后执行业务逻辑")
        }
        thread {
            Thread.sleep(1000)
            println("执行第一块业务逻辑")
            latch.countDown()
        }
        thread {
            Thread.sleep(2000)
            println("执行第二块业务逻辑")
            latch.countDown()
        }
        // 在协程中使用join() 和 await()
    }

    fun flowSelectTest() {
        runBlocking {
            val scope = CoroutineScope(EmptyCoroutineContext)
            val job1 = scope.launch {
                delay(1000)
                println("执行第一个协程")
            }
            val job2 = scope.launch {
                delay(2000)
                println("执行第二个协程")
            }
            val deferred = scope.async {
                delay(500)
                "执行async携程"
            }

            val channel = Channel<String>()
            // 使用select谁先执行完就返回谁的结果，但是剩下的协程也会执行完毕
            scope.launch {
                val strRes = select<String> {
                    job1.onJoin {
                        "第1个协程返回的结果"
                    }
                    job2.onJoin {
                        "第2个协程返回的结果"
                    }
                    deferred.onAwait {
                        it
                    }
                    channel.onSend("OnSend返回") {
                        ""
                    }
                    channel.onReceive {
                        "OnReceive返回"
                    }
                    channel.onReceiveCatching {
                        ""
                    }
                    onTimeout(5.seconds) {
                        "Timeout"
                    }
                }
                println("执行结果：$strRes")
            }
        }
    }

    fun coroutineLockTest() {
        runBlocking {
            val mutex = Mutex()
            val lock = Object()
            val scope = CoroutineScope(EmptyCoroutineContext)
            var num = 0
            val job1 = scope.launch {
                repeat(100000) {
                    // 使用线程api也可以锁住协程，阻塞
                    // 使用协程锁，让出-挂起，性能更好
//                    synchronized(lock) {
//                        num++
//                    }
//                    mutex.lock()
//                    try {
//                        num++
//                    } finally {
//                        mutex.unlock()
//                    }
                    // 或者使用mutex.withLock，底层实现同上一样，
                    mutex.withLock {
                        num++
                    }
                }
            }
            val job2 = scope.launch {
                repeat(100000) {
                    // 使用线程api也可以锁住协程，阻塞
                    // 使用协程锁，让出-挂起，性能更好
//                    synchronized(lock) {
//                        num--
//                    }
//                    mutex.lock()
//                    try {
//                        num--
//                    } finally {
//                        mutex.unlock()
//                    }
                    // 或者使用mutex.withLock，底层实现同上一样，
                    mutex.withLock {
                        num--
                    }
                }
            }
            // num++ num--不是原子操作，需要加锁
            job1.join()
            job2.join()
            println("num 计算结果：$num")
        }
    }

    val threadLocalTestValue = ThreadLocal<String>()

    fun threadLockTest() {
        // 线程局部变量 只要没出线程其值共享
        // CoroutineContext抽象意义上就是协程的局部变量
        val threadLock = ThreadLocal<String>()
        threadLock.set("outTest")
        // out: outTest
        println("out: ${threadLock.get()}")
        thread {
            threadLock.set("flaming")
            // thread1: flaming
            println("thread1: ${threadLock.get()}")
        }
        thread {
            // thread2: null
            println("thread2: ${threadLock.get()}")
        }

        val scope = CoroutineScope(EmptyCoroutineContext)
        scope.launch {
            // 转换成协程局部变量，不管怎么切线程只要没出协程其值不变
            val coroutineLocal = threadLock.asContextElement("flaming")
        }
    }

    fun shareStateFlowHotColdTest() {
        val flow1 = flow {
            delay(1000)
            println("flow 1 1")
            emit(1)
            delay(1000)
            println("flow 1 2")
            emit(2)
            delay(1000)
            println("flow 1 3")
            emit(3)
        }
        val flow2 = flow {
            delay(1000)
            println("flow 2 1")
            emit(1)
            delay(1000)
            println("flow 2 2")
            emit(2)
            delay(1000)
            println("flow 2 3")
            emit(3)
        }
        val scope = CoroutineScope(Dispatchers.IO)
        println("flow sharedIn")
        val sharedFlow = flow1.shareIn(scope, SharingStarted.Eagerly)
        println("flow stateIn")
        val stateFlow = flow2.stateIn(scope, SharingStarted.Lazily, 0)

        // sharedFlow在没有开始collect就开始生产了，stateFlow在collect调用之后才开始生产
        // sharedFlow和stateFlow是冷流还是热流是根据shareIn stateIn传入的SharingStarted参数决定的
        // 它们即是冷流也是热流

        // flow sharedIn
        // flow stateIn
        // flow collect begin
        // flow 1 1
        // flow 1 2
        // stateFlow collected: 0
        // flow 1 3

        // flow 2 1
        // stateFlow collected: 1
        // flow 2 2
        // stateFlow collected: 2
        // flow 2 3
        // stateFlow collected: 3
        runBlocking {
            scope.launch {
                println("flow collect begin")
                delay(2000)
                stateFlow.collect {
                    println("stateFlow collected: $it")
                }
            }
        }
    }
}