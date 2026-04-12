package com.example.flamingcoding.coroutines;

import org.jetbrains.annotations.NotNull;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;

public class SuspendJavaTest {


    public void callSuspendTestForJava() {
        SuspendTest test = new SuspendTest();
        test.suspendTestForJava("123");
    }

    public void callSuspendTestForJavaAsync() {
        SuspendTest test = new SuspendTest();
        // 返回 CompletableFuture，完全符合 Java 开发者的直觉
        test.suspendTestForJavaAsync("123").whenComplete((result, exception) -> {
            if (exception != null) {
                System.err.println("发生错误: " + exception.getMessage());
            } else {
                System.out.println("异步获取到testString: " + result);
            }
        });
    }

    public void callSuspend() {
        SuspendTest test = new SuspendTest();
        // 构造一个 Continuation 接收回调
        Continuation<String> continuation = new Continuation<String>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                // 通常传入 EmptyCoroutineContext 即可
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NotNull Object result) {
                // 注意：这里的 result 是 kotlin.Result 类型在 JVM 层的擦除
                // 成功或失败都会走到这里
                System.out.println("Callback 收到结果或异常: " + result);
            }
        };

        try {
            // Kotlin 编译器将 suspend fun getUserName(Int) 变成了 getUserName(Int, Continuation)
            Object returnValue = test.suspendTest("123", continuation);

            // 【关键判断】：检查函数是否被挂起
            if (returnValue == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                System.out.println("函数已挂起，请等待 Continuation 的 resumeWith 回调...");
            } else {
                // 如果没有挂起，说明结果直接同步返回了
                System.out.println("函数未挂起，直接返回了结果: " + returnValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
