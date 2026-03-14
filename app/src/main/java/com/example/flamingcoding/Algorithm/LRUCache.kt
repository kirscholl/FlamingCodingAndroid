package com.example.flamingcoding.Algorithm

import java.util.LinkedList

class Node(val value: Int) {
    var pre: Node? = null
    var next: Node? = null
}

// TODO 实现自定义的双向链表
class CustomLinkedList {
    var head: Node? = null
    var tail: Node? = null

    fun addFirst(node: Node) {

    }

    fun addLast(node: Node) {

    }

    fun remove(value: Int) {

    }


    fun removeFirst() {

    }

    fun removeLast() {

    }
}

class LRUCache(val capacity: Int) {
    //    146. LRU 缓存
    //    https://leetcode.cn/problems/lru-cache/description/

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