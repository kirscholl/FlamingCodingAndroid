package com.example.flamingcoding.kotlinTrials

object ObjectTestSingleton {
    init {
        println("ObjectTestSingleton init")
    }

    // 编译时期常量，并不会出发类的初始化！！！
    const val TEST_STRING = "ObjectTestSingleton"

    // 静态变量，会触发类的初始化！！！
    val TEST_STRING2 = "ObjectTestSingleton"

    fun test() {

    }
}