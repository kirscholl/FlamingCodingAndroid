package com.example.flamingcoding.Algorithm

class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

// 各种树的算法题
class TreeAlgorithm {


    fun levelOrder(root: TreeNode?): List<List<Int>> {
        val res = ArrayList<ArrayList<Int>>()
        if (root == null) {
            return res
        }

        val deque = ArrayDeque<TreeNode>()
        deque.addLast(root)

        var tempNode: TreeNode
        while (deque.isNotEmpty()) {
            val levelList = ArrayList<Int>()
            repeat(deque.size) {
                tempNode = deque.removeFirst()
                levelList.add(tempNode.`val`)
                if (tempNode.left != null) {
                    deque.addLast(tempNode.left!!)
                }
                if (tempNode.right != null) {
                    deque.addLast(tempNode.right!!)
                }
            }
            res.add(levelList)
        }
        return res
    }

    // 层序遍历递归实现
    fun levelOrderRecursive(root: TreeNode?): List<List<Int>> {
        val result = ArrayList<ArrayList<Int>>()

        fun dfs(node: TreeNode?, level: Int) {
            if (node == null) return

            // 如果当前层级还没有对应的列表，则新建一个
            if (result.size == level) {
                result.add(ArrayList())
            }

            result[level].add(node.`val`)

            // 递归处理左右子树，层级 +1
            dfs(node.left, level + 1)
            dfs(node.right, level + 1)
        }

        dfs(root, 0)
        return result
    }


}