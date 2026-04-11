package com.example.flamingcoding.Algorithm

class DpAlgorithm {

    // 最大正方形-221
    fun maximalSquare(matrix: Array<CharArray>): Int {
        if (matrix.isEmpty() || matrix[0].isEmpty()) return 0

        val rows = matrix.size
        val cols = matrix[0].size
        // dp[i][j] 表示以 (i, j) 为右下角的正方形的最大边长
        val dp = Array(rows) { IntArray(cols) }
        var maxSide = 0

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (matrix[i][j] == '1') {
                    if (i == 0 || j == 0) {
                        // 第一行或第一列的边界情况
                        dp[i][j] = 1
                    } else {
                        // 核心状态转移方程
                        dp[i][j] =
                            Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1
                    }
                    // 更新全局最大边长
                    maxSide = Math.max(maxSide, dp[i][j])
                }
            }
        }

        // 返回面积
        return maxSide * maxSide
    }

    // 打家劫舍1-198
    // 数组中不相邻元素和的最大值
    fun rob(nums: IntArray): Int {
        val n = nums.size
        if (n == 0) return 0
        if (n == 1) return nums[0]

        // dp[i] 表示前 i 个房子能偷到的最大金额
        val dp = IntArray(n)

        // 基础状态
        dp[0] = nums[0]
        dp[1] = maxOf(nums[0], nums[1])

        // 填充 dp 数组
        for (i in 2 until n) {
            dp[i] = maxOf(dp[i - 1], dp[i - 2] + nums[i])
        }

        return dp[n - 1]
    }

    // 单词拆分
    fun wordBreak(s: String, wordDict: List<String>): Boolean {
        val wordSet = wordDict.toHashSet() // 转化为 Set，O(1) 复杂度查询
        val n = s.length
        // dp[i] 表示 s 的前 i 个字符是否能拆分
        val dp = BooleanArray(n + 1)
        // 初始状态
        dp[0] = true
        // 遍历字符串的所有长度
        for (i in 1..n) {
            // 尝试每一个可能的分割点 j
            for (j in 0 until i) {
                // 如果前 j 个字符能拆分，且剩余部分 [j, i) 在字典中
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true
                    break // 只要找到一种拆分方式，dp[i] 就可以确定为 true
                }
            }
        }
        return dp[n]
    }
}