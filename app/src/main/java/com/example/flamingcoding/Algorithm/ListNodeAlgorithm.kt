package com.example.flamingcoding.Algorithm

class ListNode(var value: Int) {
    var next: ListNode? = null
}

class ListNodeAlgorithm {
    fun mergeTwoLists(list1: ListNode?, list2: ListNode?): ListNode? {
        val prehead = ListNode(-1)
        var prev = prehead
        var l1 = list1
        var l2 = list2
        while (l1 != null && l2 != null) {
            if (l1.value <= l2.value) {
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
}

