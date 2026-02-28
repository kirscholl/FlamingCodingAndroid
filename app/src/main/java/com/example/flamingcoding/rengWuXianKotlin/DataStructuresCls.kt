package com.example.flamingcoding.rengWuXianKotlin

import android.util.Log

class DataStructuresCls {

    companion object {
        const val TAG = "DataStructuresCls"
    }

    fun rangeTest() {
        // 闭区间 [0, 1000]
        val range: IntRange = 0..1000
        // 半开区间 [0, 1000)
        val rangeOpen: IntRange = 0 until 1000

        // range用来遍历
        for (i in range) {
            Log.d(TAG, i.toString())
        }

        // range用来固定步长遍历
        for (i in range step 2) {
            Log.d(TAG, i.toString())
        }

        // 递减区间遍历
        for (i in 4 downTo 2) {
            Log.d(TAG, i.toString())
        }
    }

    fun sequenceTest() {
        // 序列 Sequence 又被称为「惰性集合操作」
        val sequence = sequenceOf(1, 2, 3, 4)
        val result: Sequence<Int> = sequence
            // 遍历每个元素并执行给定表达式，最终形成新的集合
            .map { i ->
                println("Map $i")
                i * 2
            }
            .filter { i ->
                println("Filter $i")
                i % 3 == 0
            }
        // 惰性指当出现满足条件的第一个元素的时候，Sequence 就不会执行后面的元素遍历了，即跳过了 4 的遍历
        println(result.first()) // 👈 只取集合的第一个元素
    }

    fun arrayTest() {
        val kotlinArray = arrayOf(1, 2, 3)
        val intArray = intArrayOf(1, 2, 3)  // 基本类型数组，避免装箱
        kotlinArray[0] = 10
        println(kotlinArray.size)
    }

    fun listTest() {
        // Kotlin的List/MutableList在JVM上对应于Java的List接口，具体实现类（如ArrayList）完全复用Java标准库。
        // listOf()返回的是java.util.Arrays.ArrayList（固定长度）或kotlin.collections.EmptyList；mutableListOf()默认返回java.util.ArrayList
        val readOnly: List<String> = listOf("a", "b")  // 不可变
        val mutable: MutableList<String> = mutableListOf("a", "b")
        mutable.add("c")
    }
}