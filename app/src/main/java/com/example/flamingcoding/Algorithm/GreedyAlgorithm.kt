package com.example.flamingcoding.Algorithm

class GreedyAlgorithm {

    // 救生艇 - 881
    fun numRescueBoats(people: IntArray, limit: Int): Int {
        // 1. 先进行排序，耗时 O(nlogn)
        people.sort()
        var i = 0               // 左指针，指向最轻的人
        var j = people.size - 1 // 右指针，指向最重的人
        var res = 0           // 记录船的数量
        while (i <= j) {
            // 如果最轻的 + 最重的没超重，最轻的才能上船
            if (people[i] + people[j] <= limit) {
                i++
            }
            // 无论是一个人坐还是两个人坐，重的那个人每一轮都会“消耗”掉
            // 也就是右指针一定要左移，且船数加 1
            j--
            res++
        }
        return res
    }
}