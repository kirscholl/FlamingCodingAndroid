package com.example.pffbrowser.request.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import java.io.IOException
import java.util.concurrent.CancellationException

object BaseRequester {

    /**
     * 发起请求封装
     * 该方法将flow的执行切换至IO线程
     * @param requestBlock 请求的整体逻辑
     * @return Flow<T>
     */
    fun <T> request(requestBlock: suspend FlowCollector<T>.() -> Unit): Flow<T> {
        return flow(block = requestBlock).flowOn(Dispatchers.IO)
            .onStart {
                // 请求开始，可以放一些准备逻辑
            }
            .onEach {
                // 请求中间过程，可以做数据处理
            }
            .onCompletion {
                // 结束
            }
            .catch { e ->
                when (e) {
                    // 忽略OkHttp取消请求异常
                    is IOException if e.message?.contains(
                        "cancel",
                        ignoreCase = true
                    ) == true -> {
                        // 可选：打印日志或静默处理

                    }
                    // 忽略协程取消异常
                    is CancellationException -> {

                    }

                    else -> {
                        // 其他异常，调用回调并传递异常信息
                    }
                }
            }
    }
}