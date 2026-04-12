package com.example.flamingcoding.algorithm

import java.util.LinkedList

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

    // 二叉树最近公共祖先
    fun lowestCommonAncestor(root: TreeNode?, p: TreeNode?, q: TreeNode?): TreeNode? {
        // 1. 边界情况：如果节点为空，或者找到了 p 或 q，直接返回当前节点
        if (root == null || root == p || root == q) {
            return root
        }
        // 2. 递归向左子树寻找
        val left = lowestCommonAncestor(root.left, p, q)
        // 3. 递归向右子树寻找
        val right = lowestCommonAncestor(root.right, p, q)
        // 4. 结果合并逻辑
        return when {
            // 如果左右子树各找到一个，说明当前 root 就是最近公共祖先
            left != null && right != null -> root
            // 如果左边没找到，说明都在右边（或者右边也没找到，此时 right 也是 null）
            left == null -> right
            // 如果右边没找到，说明都在左边
            else -> left
        }
    }

    fun invertTree(root: TreeNode?): TreeNode? {
        // 1. 终止条件：如果是空节点，直接返回
        if (root == null) return null

        // 2. 交换左右孩子
        val temp = root.left
        root.left = root.right
        root.right = temp

        // 3. 递归处理子树
        invertTree(root.left)
        invertTree(root.right)

        return root
    }

    fun invertTreeIterative(root: TreeNode?): TreeNode? {
        if (root == null) return null

        // 使用队列进行层序遍历
        val queue = ArrayDeque<TreeNode>()
        queue.addLast(root)

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()

            // 交换当前节点的左右孩子
            val temp = node.left
            node.left = node.right
            node.right = temp

            // 将孩子节点加入队列继续处理
            node.left?.let { queue.addLast(it) }
            node.right?.let { queue.addLast(it) }
        }

        return root
    }

    fun isSubStructure(A: TreeNode?, B: TreeNode?): Boolean {
        // 题目约定：空树不是任意一个树的子结构
        if (A == null || B == null) return false

        // 满足以下任意一个条件即可：
        // 1. 以 A 为根节点的树包含 B (调用 recur)
        // 2. B 是 A 左子树的子结构
        // 3. B 是 A 右子树的子结构
        return recur(A, B) || isSubStructure(A.left, B) || isSubStructure(A.right, B)
    }

    /**
     * 辅助函数：判断 A 中是否包含 B
     */
    private fun recur(A: TreeNode?, B: TreeNode?): Boolean {
        // 如果 B 已经遍历完了，说明全部匹配成功
        if (B == null) return true

        // 如果 A 遍历完了 B 还没完，或者节点值不等，说明匹配失败
        if (A == null || A.`val` != B.`val`) return false

        // 当前节点匹配，继续递归判断左子树和右子树是否也匹配
        return recur(A.left, B.left) && recur(A.right, B.right)
    }

    fun zigzagLevelOrder(root: TreeNode?): List<List<Int>> {
        val result = ArrayList<List<Int>>()
        if (root == null) return result
        val deque = ArrayDeque<TreeNode>()
        deque.addFirst(root)
        var isLeftToRight = true
        while (deque.isNotEmpty()) {
            val size = deque.size
            // 使用 LinkedList 方便在头部插入，达到 O(1) 的翻转效果
            val currentLevel = LinkedList<Int>()
            for (i in 0 until size) {
                val node = deque.removeFirst()
                if (isLeftToRight) {
                    currentLevel.addLast(node.`val`) // 正常顺序
                } else {
                    currentLevel.addFirst(node.`val`) // 逆序插入头部
                }
                // 下一层的节点依然按照常规顺序入队
                node.left?.let { deque.addLast(it) }
                node.right?.let { deque.addLast(it) }
            }
            result.add(currentLevel)
            isLeftToRight = !isLeftToRight // 切换方向
        }
        return result
    }

    // 验证搜索二叉树
    fun isValidBST(root: TreeNode?): Boolean {
        // 初始调用时，上下界都为 null（代表无限制）
        return validate(root, null, null)
    }

    private fun validate(node: TreeNode?, min: Int?, max: Int?): Boolean {
        // 空树也是合法的二叉搜索树
        if (node == null) return true

        // 检查当前节点的值是否超出了允许的范围
        if (min != null && node.`val` <= min) return false
        if (max != null && node.`val` >= max) return false

        // 递归检查左右子树
        // 往左走，最大值被当前节点的值限制
        // 往右走，最小值被当前节点的值限制
        return validate(node.left, min, node.`val`) &&
                validate(node.right, node.`val`, max)
    }

    fun isValidBST2(root: TreeNode?): Boolean {
        val stack = ArrayDeque<TreeNode>()
        var curr = root
        var prev: TreeNode? = null // 用于记录中序遍历中的前一个节点

        while (curr != null || stack.isNotEmpty()) {
            // 1. 一直向左走，把左边界全部压入栈
            while (curr != null) {
                stack.addLast(curr)
                curr = curr.left
            }

            // 2. 弹出栈顶元素（此时走到了最左下角）
            curr = stack.removeLast()

            // 3. 核心判断逻辑：如果当前节点的值 <= 前一个节点的值，说明不是 BST
            if (prev != null && curr.`val` <= prev.`val`) {
                return false
            }

            // 更新 prev 为当前节点
            prev = curr

            // 4. 转向右子树
            curr = curr.right
        }

        return true
    }
}