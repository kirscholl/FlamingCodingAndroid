package com.example.flamingcoding.algorithm

import java.util.LinkedList

class DataStructuresAlgorithm {

    //    146. LRU 缓存
    //    https://leetcode.cn/problems/lru-cache/description/
    class LRUCache(val capacity: Int) {
        val map = mutableMapOf<Int, Int>()
        val list = LinkedList<Int>()

        fun get(key: Int): Int {
            if (map.contains(key)) {
                list.remove(key)
                list.addLast(key)
                return map[key]!!
            }
            return -1
        }

        fun put(key: Int, value: Int) {
            if (map.contains(key)) {
                list.remove(key)
                list.addLast(key)
                map[key] = value
            } else {
                if (list.size == capacity) {
                    val r = list.removeFirst()
                    map.remove(r)
                }
                list.addLast(key)
                map[key] = value
            }
        }
    }
}