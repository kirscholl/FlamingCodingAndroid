package com.example.flamingcoding.algorithm

class ListNode(var `val`: Int) {
    var next: ListNode? = null
}

class ListNodeAlgorithm {
    fun mergeTwoLists(list1: ListNode?, list2: ListNode?): ListNode? {
        val prehead = ListNode(-1)
        var prev = prehead
        var l1 = list1
        var l2 = list2
        while (l1 != null && l2 != null) {
            if (l1.`val` <= l2.`val`) {
                prev.next = l1
                l1 = l1.next
            } else {
                prev.next = l2
                l2 = l2.next
            }
            prev = prev.next!!
        }
        prev.next = l1 ?: l2
        return prehead.next
    }

    fun addTwoNumbers(l1: ListNode, l2: ListNode): ListNode {
        var index1 = ListNode(-1)
        index1.next = l1
        var index2 = ListNode(-1)
        index2.next = l2
        var tempFlag = 0

        var addRes: Int
        var tempNode: ListNode

        val resDummy = ListNode(-1)
        var resIndicator = resDummy

        while (index1.next != null && index2.next != null) {
            addRes = index1.next!!.`val` + index2.next!!.`val`
            if (tempFlag != 0) {
                addRes += tempFlag
                tempFlag = 0
            }
            if (addRes >= 10) {
                addRes %= 10
                tempFlag = 1
            }
            tempNode = ListNode(addRes)
            resIndicator.next = tempNode
            resIndicator = tempNode
            index1 = index1.next!!
            index2 = index2.next!!
        }

        if (index1.next != null) {
            while (index1.next != null) {
                addRes = index1.next!!.`val`
                if (tempFlag != 0) {
                    addRes += tempFlag
                    tempFlag = 0
                }
                if (addRes >= 10) {
                    addRes %= 10
                    tempFlag = 1
                }
                tempNode = ListNode(addRes)
                resIndicator.next = tempNode
                resIndicator = tempNode
                index1 = index1.next!!
            }
        }

        if (index2.next != null) {
            while (index2.next != null) {
                addRes = index2.next!!.`val`
                if (tempFlag != 0) {
                    addRes += tempFlag
                    tempFlag = 0
                }
                if (addRes >= 10) {
                    addRes %= 10
                    tempFlag = 1
                }
                tempNode = ListNode(addRes)
                resIndicator.next = tempNode
                resIndicator = tempNode
                index2 = index2.next!!
            }
        }

        if (tempFlag == 1) {
            val tailAddedNode = ListNode(1)
            resIndicator.next = tailAddedNode
        }

        return resDummy.next!!
    }

    fun lengthOfLongestSubstring(s: String): Int {
        val set = mutableSetOf<Char>()
        var res = 0

        for (char in s) {
            if (set.contains(char)) {

            } else {

            }
        }
        return res
    }

    fun isPalindrome(head: ListNode?): Boolean {
        if (head?.next == null) return true

        // 1. 快慢指针找中点
        var slow = head
        var fast = head
        while (fast?.next != null && fast.next?.next != null) {
            slow = slow?.next
            fast = fast.next?.next
        }

        // 2. 翻转后半部分链表
        // slow.next 是后半部分的开始
        val secondHalfStart = reverseList(slow?.next)

        // 3. 比较前半部分和翻转后的后半部分
        var p1 = head
        var p2 = secondHalfStart
        var result = true
        while (p2 != null) {
            if (p1?.`val` != p2.`val`) {
                result = false
                break
            }
            p1 = p1.next
            p2 = p2.next
        }

        // 4. (可选) 恢复链表结构
        // slow?.next = reverseList(secondHalfStart)

        return result
    }

    // 辅助函数：翻转链表
    private fun reverseList(head: ListNode?): ListNode? {
        var prev: ListNode? = null
        var curr = head
        while (curr != null) {
            val nextTemp = curr.next
            curr.next = prev
            prev = curr
            curr = nextTemp
        }
        return prev
    }

    // 链表排序
    fun sortList(head: ListNode?): ListNode? {
        // 基准情况：如果链表为空或只有一个节点，直接返回
        if (head?.next == null) return head

        // 1. 找到中点并分割链表
        val mid = getMid(head)
        val leftHead = head
        val rightHead = mid?.next
        mid?.next = null // 切断链表

        // 2. 递归排序左右两部分
        val left = sortList(leftHead)
        val right = sortList(rightHead)

        // 3. 合并两个有序链表
        return merge(left, right)
    }

    // 使用快慢指针寻找链表的中点
    private fun getMid(head: ListNode?): ListNode? {
        var slow = head
        var fast = head?.next // fast 多走一步，确保 slow 停在前半部分的末尾

        while (fast?.next != null) {
            slow = slow?.next
            fast = fast.next?.next
        }
        return slow
    }

    // 合并两个有序链表 (LeetCode 21 逻辑)
    private fun merge(l1: ListNode?, l2: ListNode?): ListNode? {
        val dummy = ListNode(0)
        var curr: ListNode? = dummy
        var p1 = l1
        var p2 = l2

        while (p1 != null && p2 != null) {
            if (p1.`val` < p2.`val`) {
                curr?.next = p1
                p1 = p1.next
            } else {
                curr?.next = p2
                p2 = p2.next
            }
            curr = curr?.next
        }

        // 拼接剩余部分
        curr?.next = p1 ?: p2

        return dummy.next
    }

    // 删除排序链表中的重复元素
    // 1122 -> 12
    // 112233 -> 123
    fun deleteDuplicates(list: ListNode?): ListNode? {
        // 如果链表为空或者只有一个节点，直接返回
        if (list == null || list.next == null) return list
        var current = list
        // 只要当前节点和下一个节点都不为空，就继续遍历
        while (current?.next != null) {
            // 因为 Kotlin 的空安全，这里使用 !! 或配合 if 确保 current.next 不为空
            if (current.`val` == current.next!!.`val`) {
                // 发现重复，跳过下一个节点
                current.next = current.next!!.next
            } else {
                // 不重复，移动到下一个节点
                current = current.next
            }
        }
        return list
    }

    fun deleteDuplicates2(list: ListNode?): ListNode? {
        // 1. 处理特殊情况
        if (list == null || list.next == null) return list

        // 2. 创建哑节点，指向 head
        val dummy = ListNode(0)
        dummy.next = list
        var current = dummy

        // 3. 开始遍历，至少要有两个后续节点才可能存在重复
        while (current.next != null && current.next?.next != null) {
            // 如果接下来的两个节点值相等
            if (current.next!!.`val` == current.next!!.next!!.`val`) {
                val x = current.next!!.`val`
                // 只要接下来的节点值还是 x，就一直跳过
                while (current.next != null && current.next!!.`val` == x) {
                    current.next = current.next!!.next
                }
                // 注意：这里跳过所有重复项后，cur 指针不移动
                // 因为新的 cur.next 可能又是一组重复的起点
            } else {
                // 如果不相等，cur 放心后移
                current = current.next!!
            }
        }

        return dummy.next
    }

    fun maxSlidingWindow(nums: IntArray, k: Int): IntArray {
        val n = nums.size
        if (n == 0) return intArrayOf()
        // 结果数组大小为 n - k + 1
        val result = IntArray(n - k + 1)
        // 存储的是下标
        val deque = ArrayDeque<Int>()
        for (i in nums.indices) {
            // 1. 维护单调递减性：如果当前值大于等于队尾值，队尾就没用了
            while (deque.isNotEmpty() && nums[deque.removeLast()] <= nums[i]) {
                deque.removeLast()
            }
            // 2. 将当前下标加入队尾
            deque.addLast(i)
            // 3. 检查队首是否已在窗口之外
            if (deque.removeFirst() == i - k) {
                deque.removeFirst()
            }
            // 4. 当窗口完整形成时，队首即为最大值
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.removeFirst()]
            }
        }
        return result
    }

    fun reverseListSolution(head: ListNode?): ListNode? {
        var prev: ListNode? = null
        var curr = head

        while (curr != null) {
            // 1. 临时保存当前节点的下一个节点，防止断链
            val nextTemp = curr.next

            // 2. 反转指向：让当前节点指向它的前驱节点
            curr.next = prev

            // 3. 指针整体向后移动一位
            // 先挪 prev 到当前位置
            prev = curr
            // 再挪 curr 到刚才保存的 nextTemp 位置
            curr = nextTemp
        }

        // 最后 prev 会指向原链表的最后一个节点，即新链表的头
        return prev
    }

    fun reverseKGroup(head: ListNode?, k: Int): ListNode? {
        if (head == null || k == 1) return head

        // 1. 设置一个 dummy 节点，方便处理头部的变化
        val dummy = ListNode(0)
        dummy.next = head

        // pre 指向待翻转小组的前一个节点
        var pre: ListNode? = dummy
        // end 指向待翻转小组的最后一个节点
        var end: ListNode? = dummy

        while (end?.next != null) {
            // 2. 找到本小组的末尾节点
            for (i in 0 until k) {
                end = end?.next
                if (end == null) break // 不足 k 个，直接跳出
            }
            if (end == null) break // 剩余不足 k 个，终止翻转

            // 3. 记录下一组的开始，并断开当前组以进行翻转
            val nextGroupStart = end.next
            val groupStart = pre?.next
            end.next = null // 断开连接，方便局部翻转

            // 4. 执行局部翻转，并重新连接
            pre?.next = reverse(groupStart)

            // 5. 此时 groupStart 变成了小组的末尾，连接上后续未处理的部分
            groupStart?.next = nextGroupStart

            // 6. 指针后移，准备处理下一组
            pre = groupStart
            end = pre
        }

        return dummy.next
    }

    // 基础的翻转链表辅助函数
    private fun reverse(head: ListNode?): ListNode? {
        var prev: ListNode? = null
        var curr = head
        while (curr != null) {
            val nextTemp = curr.next
            curr.next = prev
            prev = curr
            curr = nextTemp
        }
        return prev
    }

    fun reverseBetween(head: ListNode?, left: Int, right: Int): ListNode? {
        // 1. 设置 dummy 节点，处理 left 为 1 的特殊情况
        val dummy = ListNode(0)
        dummy.next = head

        // 2. 找到待反转区间的前驱节点 pre
        var pre: ListNode? = dummy
        for (i in 0 until left - 1) {
            pre = pre?.next
        }

        // 3. 开始局部反转 (头插法)
        // cur 是反转区间的第一个节点，反转后它会变成区间的最后一个节点
        val cur = pre?.next
        var next: ListNode?

        // 执行 right - left 次交换
        for (i in 0 until (right - left)) {
            next = cur?.next

            // 步骤分解：把 next 挪到 pre 后面
            cur?.next = next?.next     // cur 指向下一个要处理的节点
            next?.next = pre?.next     // next 指向当前区间的头部
            pre?.next = next           // pre 重新接上新的头部
        }

        return dummy.next
    }
}

