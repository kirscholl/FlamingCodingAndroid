package com.example.flamingcoding.Algorithm

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
}