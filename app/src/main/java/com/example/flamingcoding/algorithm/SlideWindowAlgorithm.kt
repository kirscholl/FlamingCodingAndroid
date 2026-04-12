package com.example.flamingcoding.algorithm

import kotlin.math.max

class SlideWindowAlgorithm {

    // 3. 无重复字符的最长子串
    fun lengthOfLongestSubstring1(s: String): Int {
        var l = 0
        var r = 0
        var res = 0
        val map = mutableMapOf<Char, Int>()
        var tempChar: Char
        while (r < s.length) {
            tempChar = s[r]
            if (map.containsKey(tempChar)) {
                // 此时该数字出现的最后位置可能在l的左边，在左边就不应该移动l -> abba
//                l = map[tempChar]!!
                l = maxOf(l, map[tempChar]!!)
            }
            map[tempChar] = r + 1
            res = max(res, r - l + 1)
            r++
        }
        return res
    }

    fun lengthOfLongestSubstring2(s: String): Int {
        var l = 0
        var r = 0
        var res = 0
        val set = mutableSetOf<Char>()
        while (r < s.length) {
            while (set.contains(s[r]) && l != r) {
                set.remove(s[l])
                l++
            }
            set.add(s[r])
            res = Math.max(set.size, res)
            r++
        }
        return res
    }

}