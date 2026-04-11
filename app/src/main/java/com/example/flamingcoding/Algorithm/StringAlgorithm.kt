package com.example.flamingcoding.Algorithm

class StringAlgorithm {

    // 字符串相加
    fun addStrings(num1: String, num2: String): String {
        val res = StringBuilder()
        var i = num1.length - 1
        var j = num2.length - 1
        var carry = 0 // 存储进位

        // 只要还有位没加完，或者还有进位，就继续
        while (i >= 0 || j >= 0 || carry != 0) {
            // ASCII码相减 任意数字的Char - '0' = 该数字Int
            val x = if (i >= 0) num1[i] - '0' else 0
            val y = if (j >= 0) num2[j] - '0' else 0

            val sum = x + y + carry
            res.append(sum % 10) // 写入当前位
            carry = sum / 10     // 更新进位

            i--
            j--
        }

        // 因为是从个位开始 append 的，结果是反的，需要反转
        return res.reverse().toString()
    }
}