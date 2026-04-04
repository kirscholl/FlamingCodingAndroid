package com.example.flamingcoding.Algorithm

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
}

