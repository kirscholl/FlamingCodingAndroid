package com.example.flamingcoding.Algorithm

import java.util.PriorityQueue

class OtherAlgorithm {

    // 两数之和
    fun twoSum(nums: IntArray, target: Int): IntArray {
        val res = IntArray(2)
        var tempTarget = 0
        val assMap = mutableMapOf<Int, Int>()

        for (index in 0 until nums.size) {
            tempTarget = target - nums[index]
            if (assMap.containsKey(tempTarget)) {
                res[0] = assMap[tempTarget]!!
                res[1] = index
            } else {
                assMap[nums[index]] = index
            }
        }
        return res
    }

    // 盛水最多的容器
    fun maxArea(height: IntArray): Int {
        var leftIndex = 0
        var rightIndex = height.size - 1
        var res = 0
        var temp: Int
        var minHeight: Int

        while (leftIndex != rightIndex) {
            minHeight = Math.min(height[leftIndex], height[rightIndex])
            temp = (rightIndex - leftIndex) * minHeight
            res = Math.max(res, temp)
            if (height[leftIndex] <= height[rightIndex]) {
                leftIndex++
            } else {
                rightIndex--
            }
        }
        return res
    }

    // 三数之和
    fun threeSum(nums: IntArray): List<List<Int>> {
        val result = ArrayList<List<Int>>()
        if (nums.size < 3) return result
        nums.sort()

        for (i in 0 until nums.size - 2) {
            // 核心剪枝：如果当前元素大于0，后面的元素加上它绝不可能等于0，直接退出循环
            if (nums[i] > 0) break

            // 跳过重复的固定元素
            if (i > 0 && nums[i] == nums[i - 1]) continue

            var left = i + 1
            var right = nums.lastIndex

            while (left < right) {
                val sum = nums[i] + nums[left] + nums[right]
                when {
                    sum < 0 -> left++
                    sum > 0 -> right--
                    else -> {
                        result.add(listOf(nums[i], nums[left], nums[right]))
                        left++
                        right--
                        // 跳过重复的左指针元素
                        while (left < right && nums[left] == nums[left - 1]) left++
                        // 跳过重复的右指针元素
                        while (left < right && nums[right] == nums[right + 1]) right--
                    }
                }
            }
        }
        return result
    }

    fun isValidCompose(s: String): Boolean {
        var res = true
        val map = mutableMapOf<Char, Char>()
        map['('] = ')'
        map['['] = ']'
        map['{'] = '}'
        val deque = ArrayDeque<Char>()

        for (i in s.length - 1 downTo 0) {
            if (!map.containsKey(s[i])) {
                deque.addFirst(s[i])
            } else {
                if (deque.isEmpty()) {
                    res = false
                    break
                }
                val tempChar = deque.removeFirst()
                if (map[s[i]] != tempChar) {
                    res = false
                    break
                }
            }
        }

        if (deque.isNotEmpty()) {
            res = false
        }

        return res
    }

    fun trap(height: IntArray): Int {
        // 左右指针分别指向数组的两端
        var left = 0
        var right = height.size - 1

        // 记录从左侧扫描和右侧扫描过程中遇到的最大高度
        var leftMax = 0
        var rightMax = 0

        // 累计的雨水量
        var water = 0

        // 当左右指针未相遇时，持续计算
        while (left < right) {
            // 如果左边柱子高度小于右边柱子高度，
            // 则当前 left 位置的蓄水上限由 leftMax 决定（因为右侧有更高的墙）
            if (height[left] < height[right]) {
                // 更新左侧最大高度
                if (height[left] >= leftMax) {
                    leftMax = height[left]
                } else {
                    // 左侧最大高度大于当前柱子高度，说明可以蓄水
                    water += leftMax - height[left]
                }
                // 左指针右移，继续处理下一个位置
                left++
            } else {
                // 同理，当右边柱子高度小于等于左边柱子高度时，
                // 当前 right 位置的蓄水上限由 rightMax 决定
                if (height[right] >= rightMax) {
                    rightMax = height[right]
                } else {
                    water += rightMax - height[right]
                }
                // 右指针左移
                right--
            }
        }

        return water
    }

    // 买卖股票的最佳时机
    fun maxProfit(prices: IntArray): Int {
        if (prices.isEmpty()) return 0

        var minPrice = prices[0]
        var maxProfit = 0

        for (price in prices) {
            // 更新历史最低价
            if (price < minPrice) {
                minPrice = price
            } else {
                // 计算当前利润并更新最大利润
                val profit = price - minPrice
                if (profit > maxProfit) {
                    maxProfit = profit
                }
            }
        }
        return maxProfit
    }

    fun findKthLargest(nums: IntArray, k: Int): Int {
        val n = nums.size
        // 第 k 大 = 第 (n-k) 小（升序排列中的索引）
        return quickSelect(nums, 0, n - 1, n - k)
    }

    private fun quickSelect(nums: IntArray, left: Int, right: Int, targetIndex: Int): Int {
        if (left == right) return nums[left]

        val pivotIndex = partition(nums, left, right)

        return when {
            targetIndex == pivotIndex -> nums[targetIndex]
            targetIndex < pivotIndex -> quickSelect(nums, left, pivotIndex - 1, targetIndex)
            else -> quickSelect(nums, pivotIndex + 1, right, targetIndex)
        }
    }

    private fun partition(nums: IntArray, left: Int, right: Int): Int {
        val pivot = nums[right]
        var i = left

        for (j in left until right) {
            if (nums[j] <= pivot) {
                nums.swap(i, j)
                i++
            }
        }
        nums.swap(i, right)
        return i
    }

    fun findKthLargest2(nums: IntArray, k: Int): Int {
        // Kotlin 的 PriorityQueue 默认是最小堆
        val minHeap = PriorityQueue<Int>(k)

        for (num in nums) {
            minHeap.offer(num)
            // 保持堆的大小为 k，堆顶即为第 k 大的候选
            if (minHeap.size > k) {
                minHeap.poll() // 移除最小的
            }
        }

        return minHeap.peek()
    }

    // 快速排序
    fun quickSort(array: IntArray) {
        val left = 0
        val right = array.size - 1

        quickSortImpl(array, left, right)
    }

    fun quickSortImpl(array: IntArray, left: Int, right: Int) {
        if (left >= right) {
            return
        }
        val index = getIndex(array, left, right)
        quickSortImpl(array, left, index - 1)
        quickSortImpl(array, index + 1, right)
    }

    fun getIndex(array: IntArray, left: Int, right: Int): Int {
        // 优化选取随机的index而不是总是选left
//        val randomIndex = (left..right).random()
//        array.swap(left, randomIndex)
        val indexValue = array[left]
        var leftIndex = left
        var rightIndex = right
        var swapFlag = true
        while (leftIndex != rightIndex) {
            if (swapFlag) {
                if (array[rightIndex] >= indexValue) {
                    rightIndex--
                } else {
                    array.swap(rightIndex, leftIndex)
                    leftIndex++
                    swapFlag = false
                }
            } else {
                if (array[leftIndex] <= indexValue) {
                    leftIndex++
                } else {
                    array.swap(rightIndex, leftIndex)
                    rightIndex--
                    swapFlag = true
                }
            }
        }
        return rightIndex
    }
}

fun IntArray.swap(index1: Int, index2: Int) {
    val tempValue = this[index2]
    this[index2] = this[index1]
    this[index1] = tempValue
}