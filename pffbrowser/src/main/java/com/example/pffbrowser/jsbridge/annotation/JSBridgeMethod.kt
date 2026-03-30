package com.example.pffbrowser.jsbridge.annotation

/**
 * JSBridge方法注解
 * 用于标记可以被H5调用的方法
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class JSBridgeMethod(
    // 方法名称，默认使用函数名
    val name: String = "",
    // 是否异步执行
    val async: Boolean = true,
    // 是否需要权限
    val needPermission: Boolean = false
)
