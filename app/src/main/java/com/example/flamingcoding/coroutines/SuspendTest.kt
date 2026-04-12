package com.example.flamingcoding.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.future
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

class SuspendTest {
    suspend fun suspendTest(str: String): String {
        coroutineScope {
            delay(1000)
            println(str)
        }
        return "testReturn"
    }

    // 提供给 Java 调用的同步桥接方法
    fun suspendTestForJava(str: String): String {
        return runBlocking {
            println("runBlocking 调用 suspend")
            suspendTest(str)
        }
    }

    // 提供给 Java 的异步桥接方法，返回 CompletableFuture
    fun suspendTestForJavaAsync(testStr: String): CompletableFuture<String> {
        // 正式开发换合理的Scope
        return GlobalScope.future {
            suspendTest(testStr)
        }
    }
}