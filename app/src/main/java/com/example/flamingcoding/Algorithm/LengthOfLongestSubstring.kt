package com.example.flamingcoding.Algorithm

import kotlin.math.max

// 3. 无重复字符的最长子串
//给定一个字符串 s ，请你找出其中不含有重复字符的 最长 子串 的长度。

//示例 1:
//输入: s = "abcabcbb"
//输出: 3
//解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。注意 "bca" 和 "cab" 也是正确答案。

//示例 2:
//输入: s = "bbbbb"
//输出: 1
//解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。

//示例 3:
//输入: s = "pwwkew"
//输出: 3
//解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
//请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。

class LengthOfLongestSubstring {

    fun mapSolution(s: String): Int {
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

    fun setSolution(s: String): Int {
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
