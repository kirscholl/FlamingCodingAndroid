package com.example.flamingcoding.kotlin;

import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataStructuresJavaCls {
    void arrayTest() {
        // 数组
        // JVM在堆内存中为数组分配一段连续的空间，每个数组元素在内存中占据一定的连续空间。这使得数组的随机访问变得很快，因为可以根据索引直接计算出元素在内存中的位置
        int[] javaArray = new int[5];
        javaArray[0] = 1;
        int length = javaArray.length;
    }

    void listTest() {
        // ArrayList 动态数组，底层其实是普通数组，当创建一个 ArrayList 对象时，实际上是创建了一个 Object 类型的数组，初始容量为 10
        // 自动扩容机制
        // ArrayList 内部维护了一个 Object 类型的数组 elementData，这个数组用于存储 ArrayList 中的元素。
        // 在添加元素时，ArrayList 会先判断数组是否已满，如果已满，就会调用 ensureCapacityInternal 方法进行扩容。
        // 这个方法会计算出新的容量大小，并创建一个新的数组 newElementData，然后将原数组中的元素复制到新数组中，最后将新数组赋值给 elementData
        List<String> list = new ArrayList<>();
        // add()
        // add 方法将新元素插入到数组的末尾，并将列表的size属性加1。最后， add 方法返回一个布尔值，表示元素是否成功添加到列表中。
        list.add("a");
        String str1 = list.get(0);
        // addAll()
        // addAll方法 addAll 方法首先将要添加的元素转换为数组 在进行容量检查和扩容操作之后，
        // addAll方法使用 System.arraycopy 方法将新数组中的元素插入到ArrayList的内部数组中。
        // 这里需要注意的是， System.arraycopy 方法是一个本地方法，它能够快速地将一个数组中的元素复制到另一个数组中。
        // 在将新元素插入到ArrayList的内部数组中之后， addAll 方法会更新列表的size属性，并返回一个布尔值，表示元素是否成功添加到列表中。
        List<String> listAddAll = new ArrayList<>();
        listAddAll.add("b");
        listAddAll.add("c");
        list.addAll(listAddAll);

        // set()
        // set 方法首先会检查指定的索引是否越界，如果越界则会抛出 IndexOutOfBoundsException 异常
        // 如果索引没有越界，则 set 方法会将指定位置的元素替换为新元素，并返回原有元素的值。
        list.set(1, "index1");

        // remove()
        // remove 方法首先会检查指定的索引是否越界 在将指定位置上的元素从ArrayList中移除之前，remove方法会先获取该位置上原有的元素，并将其保存到一个临时变量中。
        // 这是因为，remove方法需要返回原有元素的值。在移除元素之后，remove方法会将列表的modCount属性加1，表示对列表的修改次数增加了1。
        // 此外，remove方法还会调用arraycopy将被移除元素后面的所有元素向前移动一位，以便填补被移除元素的位置
        list.remove("b");
        list.remove(1);
    }

    void linkedListTest() {
        // LinkedList基于双向链表
        List<String> linkedList = new LinkedList<>();
        linkedList.add("a");
        linkedList.add(1, "1");
        linkedList.add("a");
        linkedList.add(1, "1");
        linkedList.add("a");
        linkedList.add(1, "1");

        String str1 = linkedList.get(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            String str2 = linkedList.getFirst();
            String str3 = linkedList.getLast();
        }

        // 删除指定索引处的元素，并返回该元素的值。
        String res1 = linkedList.remove(1);
        // 删除链表中首次出现的指定元素，如果不存在该元素则返回 false
        boolean res2 = linkedList.remove("1");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            linkedList.removeFirst();
            linkedList.removeLast();
        }
        linkedList.clear();
    }

    void setTest() {
        // 集合（Sets）用于存储不重复的元素，常见的实现有 HashSet 和 TreeSet。
        // HashSet:
        //特点： 无序集合，基于HashMap实现。
        //优点： 高效的查找和插入操作。
        //缺点： 不保证顺序。
        //TreeSet:
        //特点：TreeSet 是有序集合，底层基于红黑树实现，不允许重复元素。
        //优点： 提供自动排序功能，适用于需要按顺序存储元素的场景。
        //缺点： 性能相对较差，不允许插入 null 元素。
        Set<String> hashSet = new HashSet<String>();
        // HashSet基于哈希表实现，它使用了一个称为“hash表”的数组来存储元素。当向HashSet中添加元素时，首先会对元素进行哈希，
        // 并通过哈希值来确定元素在数组中的位置。如果该位置已经有元素了，就会通过equals方法来判断是否重复，如果重复则不添加，
        // 如果不重复则添加到该位置。当然，由于哈希表中可能会存在多个元素都哈希到同一个位置的情况，因此这些元素会被存储在同一个位置上，
        // 形成一个链表。在查找元素时，先通过哈希值定位到链表的头部，然后在链表中进行搜索，直到找到匹配的元素或到达链表的末尾。
        // HashSet的添加、删除、查找操作时间复杂度为O(1)
        boolean addRes = hashSet.add("1");
        boolean containRes = hashSet.contains("1");
        hashSet.remove("1");

        // 利用HashSet去重
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 2, 1));
        Set<Integer> set = new HashSet<>(list);
        list = new ArrayList<>(set);
        System.out.println(list); // output: [1, 2, 3]

        // TreeSet基于红黑树实现，它是一种自平衡的二叉查找树。每个节点都有一个额外的颜色属性，只能是红色或黑色。
        // 红黑树的基本操作包括插入、删除和查找。当我们向TreeSet中添加元素时，它会根据元素的大小来将元素添加到树中的合适位置。
        // 对于每个节点，其左子树的所有元素都比该节点的元素小，右子树的所有元素都比该节点的元素大。在删除时，如果要删除的节点有两个子节点，
        // 会先在右子树中找到最小元素，然后将该节点的元素替换为最小元素。删除最小元素就是从根节点开始，一直找到最左侧的节点即可
        Set<Integer> treeSet = new TreeSet<>();
    }

    void mapTest() {
        // 映射（Maps）用于存储键值对，常见的实现有 HashMap 和 TreeMap。
        // HashMap:
        //特点： 基于哈希表实现的键值对存储结构。
        //优点： 高效的查找、插入和删除操作。
        //缺点： 无序，不保证顺序。
        //TreeMap:
        //特点： 基于红黑树实现的有序键值对存储结构。
        //优点： 有序，支持按照键的顺序遍历。
        //缺点： 插入和删除相对较慢。
        Map<String, Integer> hashMap = new HashMap<>();
        Map<String, Integer> treeMap = new TreeMap<>();
    }

    void stackQueueHeapTest() {
        // 栈（Stack）是一种线性数据结构，它按照后进先出（Last In, First Out，LIFO）的原则管理元素。
        // 在栈中，新元素被添加到栈的顶部，而只能从栈的顶部移除元素。这就意味着最后添加的元素是第一个被移除的。
        Stack<Integer> stack = new Stack<>();
        // 队列（Queue）遵循先进先出（FIFO）原则，常见的实现有 LinkedList 和 PriorityQueue。
        Queue<String> queue = new LinkedList<>();
        // 堆（Heap）优先队列的基础，可以实现最大堆和最小堆
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    }
}
