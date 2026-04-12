package com.example.flamingcoding.algorithm

import kotlin.math.pow

class IndexForNumber {

    // 1 2 3 4 5 ... 9  1  0  1  1
    // 1 2 3 4 5 ... 9 10 11 12 13
    fun getNumberByIndex(index: Int): Int {
        if (index < 10) return index
        val level = getNumLevel(index)
        val lowTotal = getLowLevelTotalIndex(index, level)
        val remainIndex = index - lowTotal
        val levelNum = remainIndex / level + lowTotal
        val levelRemain = remainIndex % level
        val resNumList = getResNumList(levelNum)
        println("getNumberByIndex res: ${resNumList[levelRemain]}")
        return resNumList[levelRemain]
    }

    private fun getResNumList(num: Int): ArrayList<Int> {
        val list = ArrayList<Int>()
        val intStr = num.toString()
        for (chr in intStr) {
            list.add(chr.digitToInt())
        }
        return list
    }

    private fun getNumLevel(index: Int): Int {
        var level = 0
        var calculateRes = 0
        var tempNum = 0
        while (calculateRes >= 0) {
            level++
            tempNum += 10.0.pow(level.toDouble()).toInt()
            calculateRes = index - tempNum
        }
        return level
    }

    private fun getLowLevelTotalIndex(index: Int, level: Int): Int {
        val lowLevel = level - 1
        if (lowLevel == 0) return 0
        var lowLevelIndex = 0
        for (levelNum in lowLevel downTo 1) {
            lowLevelIndex += 10.0.pow(levelNum.toDouble()).toInt()
        }
        return lowLevelIndex
    }

    fun getLevel(num: Int): Int {
        var count = 0
        var tempNum = num
        while (tempNum != 0) {
            tempNum /= 10
            count++
        }
        return count
    }
}