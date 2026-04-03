package com.example.flamingcoding.javatest

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class CoroutineCallJava {

    suspend fun CoroutineCallJavaApiService.fetchDataSuspending(): String =
        suspendCancellableCoroutine { continuation ->
            // 1. 调用原始的 Java 回调方法
            fetchData(object : CoroutineCallJavaCallback {
                override fun onSuccess(result: String) {
                    // 2. 成功时：恢复协程并返回结果
                    continuation.resume(result) { throwable ->
                        throwable.printStackTrace()
                    }
                }

                override fun onError(e: Exception) {
                    // 3. 失败时：抛出异常给协程
                    continuation.resumeWithException(e)
                }
            })

            // 4. (可选) 处理协程取消逻辑
            continuation.invokeOnCancellation {
                // 如果你的 Java 接口支持取消，可以在这里调用
                // apiService.cancel()
            }
        }
}