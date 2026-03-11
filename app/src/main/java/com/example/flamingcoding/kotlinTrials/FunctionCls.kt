package com.example.flamingcoding.kotlinTrials

import android.util.Log
import kotlin.concurrent.thread

class FunctionCls {

    companion object {
        const val TAG = "FunCls"
    }

    fun funClsTest() {
        val testFun = { x: Int, y: String ->
            {
                Log.d(TAG, x.toString())
                Log.d(TAG, y)
            }
        }
    }

    fun paramFunc(intParam: Int, strParam: String): String {
        return "asd"
    }

    //高阶函数： 参数或者函数为返回值的函数
    fun highLevelFunTest1(strParam: String, funcParam: (Int, String) -> String) {

    }

    fun highLevelFunTest2(funcParam: (Int) -> String) {

    }

    fun test() {
        // ################################### lambda表达式 匿名函数 ##################################
        // 使用lambda表达式
        // lambda表达式不使用return返回而是使用最后一行返回
        // 因为lambda是一个代码块它总能根据最后一行的返回推断返回值所以lambda表达式不支持显示声明返回值
        // lambda表达式本质上是一个函数类型的对象
        val paramFun1 = { x: Int, y: String ->
            Log.d(TAG, x.toString())
            Log.d(TAG, y)
            x.toString() + y
        }
        highLevelFunTest1("asd", paramFun1)
        highLevelFunTest1(
            "qwe",
            { x: Int, y: String ->
                Log.d(TAG, x.toString())
                Log.d(TAG, y)
                x.toString() + y
            })
        // 如果lambda是函数传参中的最后一个参数则可以把lambda表达式写在括号的右边
        highLevelFunTest1("qwe") { x: Int, y: String ->
            Log.d(TAG, x.toString())
            Log.d(TAG, y)
            x.toString() + y
        }
        highLevelFunTest2(fun(intParam: Int): String {
            return intParam.toString()
        })
        highLevelFunTest2({ intParam -> intParam.toString() })
        // 如果lambda是函数传参中的最后一个参数则可以把lambda表达式写在括号的右边
        highLevelFunTest2() { intParam -> intParam.toString() }
        // 括号可以省略
        highLevelFunTest2 { intParam -> intParam.toString() }
        // lambda表达式单参数如果不使用可以省略，如果使用也可以省略用it代替
        highLevelFunTest2 { it.toString() }


        // 使用匿名函数声明变量
        // 匿名函数是一个函数类型的对象
        val paramFun2 = fun(x: Int, y: String): String {
            Log.d(TAG, x.toString())
            Log.d(TAG, y)
            return x.toString() + y
        }
        highLevelFunTest1("asd", paramFun2)
        highLevelFunTest1("asd", fun(x: Int, y: String): String {
            Log.d(TAG, x.toString())
            Log.d(TAG, y)
            return x.toString() + y
        })

        // 使用函数引用
        val refFunc: (Int, String) -> String = ::paramFunc  // 将函数引用赋值给变量
        refFunc(3, "test") // 函数变量() 实际是运算符重载最终调用的是该变量的invoke()
        refFunc.invoke(3, "test")
        highLevelFunTest1("qwe", refFunc)
    }

    // ######################################### infix关键字 ########################################
    // 用infix关键字标记的函数即为中缀函数，他可以使用中缀方式调用(省略点号和小括号的调用方式)
    // ·只有一个参数
    // ·在方法前必须加infix关键字
    // ·必须是成员方法或者扩展方法
    // ·不接受可变数量的参数，并且不能有默认值
    public infix fun String.and(that: String): String = "$this $that"
    val str = "abc" and "def" and "ghk"

    // ######################################## inline 内联函数 #######################################
    inline fun inlineTestFun(str: String) {
        Log.d("inlineTestFun", str)
    }

    fun testRun() {
        inlineTestFun("Test")
    }
    // 调用的函数在编译时会变成代码内嵌的形式
//    fun testRun() {
//        Log.d("inlineTestFun", str)
//    }

    // 编译时常量
//    companion object {
//        const val TAG = "FunCls"
//    }
//    fun main() {
//        val testString: String = TAG
//        // 实际编译的代码（大致）
//        // testString = "FunCls"
//    }
    // 使用内联函数会使函数调用栈变少，但是这种优化应该是可以被忽略的
    // 谨慎使用内联函数，因为错误使用内联函数，会导致函数被多处编译拷贝，导致编译产生的字节码变大很多
    fun normalFun(postAction: () -> Unit) {
        println("test")
        postAction()
    }

    fun normalTestFun() {
        normalFun {
            println("normal")
        }
    }
    // 实际编译的代码（大致）
    // 在实际编译时，编译器会创建一个临时的变量去承接函数参数变量，如果这种方法被频繁调用（循环，Update()，等），则对性能会产生损耗
//    fun normalTestFun() {
//        val post = object : Function0<Unit> {
//            override fun invoke() {
//                return println("normal")
//            }
//        }
//    }

    // inline 关键字不止可以内联自己的内部代码，还可以内联自己内部的内部的代码
    inline fun inlineRightFun(postAction: () -> Unit) {
        println("test")
        postAction()
    }

    fun inlineTestFun() {
        inlineRightFun {
            println("inline")
        }
    }
    // inline关键字平铺展开函数避免了临时变量的创建
//    fun inlineTestFun() {
//        println("test")
//        println("inline")
//    }

    // ########################################## noinline #########################################
    // inline优化会导致函数中的函数类型的参数无法被当做对象使用。noinline是用来局部地、指向性地关掉函数的内联优化的。
    inline fun noinlineFunTest(preAction: () -> Unit, noinline postAction: () -> Unit): () -> Unit {
        preAction()
        println("noinlineTest")
        postAction()
        return postAction
    }

    // ######################################## crossinline ########################################
    // lambda 表达式里不允许使用 return，除非这个lambda是内联函数的参数
    // 1.lambda里的 return，结束的不是直接的外层函数，而是外层再外层的函数；
    // 2.但只有内联函数的lambda参数可以使用 return。

    fun lambdaReturnTest(lb: () -> Unit) {
        lb()
    }

    inline fun lambdaInlineReturnTest(lb: () -> Unit) {
        // 调用lambda
        lb()
    }

    fun lambdaReturnTestRun() {
        lambdaReturnTest {
            // 任何 Lambda：都可以使用带标签的 return（局部返回）
            println("普通lambda函数内部输出")
            return@lambdaReturnTest
            // 报错 'return' is prohibited here.
//            return
        }
        lambdaInlineReturnTest {
            println("内联lambda函数内部输出")
            // 不报错 但是返回的是外层lambdaReturnTestRun
            // 因为lambda函数在这里展开了！！！
            return
        }
        // 代码不可达
        println("内联函数的lambda返回之后外层输出")
    }

    inline fun crossFunTest(preAction: () -> Unit, crossinline postAction: () -> Unit) {
        preAction()
        println("noinlineTest")
        // 如果在其他线程中调用postAction则需要crossinline关键字禁止在lambda表达式中使用return
        thread {
            // 报错 需要加crossinline
            postAction()
        }
    }

    // ############################################ Unit ###########################################
    // Unit和Java的void真正的区别在于，void是真的表示什么都不返回，而kotlin的Unit却是一个真实存在的类型
    // Java的泛型中没有Function<void>这种存在，于是经常被动地返回一个null，但是kotlin中的Unit却可以直接承接泛型

    // ########################################### Nothing #########################################
    // Nothing这个类既没有、也不会有任何的实例对象
    // 就是Nothing存在的意义：它找不到任何可用的值，所以，以它为返回值类型的一定是个不会返回的函数，比如——它可以总是抛异常
    fun nothingTest(): Nothing {
        throw RuntimeException("Nothing!")
    }
    // Nothing这个返回值类型能够给使用它的开发者一个明确的提示：这是个永远不会返回的函数

    // Nothing 是所有类型的子类型
    // val emptyList: List<Nothing> = listOf()
    // var apples: List<Apple> = emptyList
    // var users: List<User> = emptyList
    // var phones: List<Phone> = emptyList
    // var images: List<Image> = emptyList

    // Nothing 的「是所有类型的子类型」这个特点，还帮助了 Kotlin 语法的完整化
    var _name: String? = "null"
    val name: String = _name ?: throw NullPointerException("_name 在运行时不能为空！")

    // throw 的返回值是 Nothing，我们就可以把它写在等号的右边，在语法层面假装成一个值来使用，但其实目的是在例外情况时抛异常
    // 除了 throw 之外，return 也是被规定为返回 Nothing 的一个关键字
    fun sayMyName(first: String, second: String) {
        if (first == "Walter" && second == "White") println("Heisenberg")
    }

    // ####################################### kotlin标准函数 ########################################

    // let是一个函数。这个函数提供了函数式API的编程接口，并将原始调用对象作为参数传递到Lambda表达式中
    fun letTest() {
        val testString = "testString"
        // 作用1：使用it替代object对象去访问其公有的属性 & 方法
        var letReturn = testString.let {
            val test1 = it.first()
            val test2 = it.length
            // let 的返回值为最后一行
            "letReturn"
        }
        println(".let{ }中的返回值：$letReturn")
        // 作用2：判断object为null的操作
        testString?.let {
            //表示object不为null的条件下，才会去执行let函数体
//            testString.postAction()
        }
        // java 中
        // if( mVar != null ){
        //    mVar.function1();
        //
        //    mVar.function2();
        //
        //    mVar.function3();
        //}
        // 如果不使用let 每次都要判断一次是否为空
        // mVar?.function1()
        // mVar?.function2()
        // mVar?.function3()
        // 使用let只需要判断一次是否为空
        // mVar?.let {
        //     mVar.function1()
        //     mVar.function2()
        //     mVar.function3()
        // }
    }

    // let函数：返回值 = 最后一行 / return的表达式
    // also函数：返回值 = 传入的对象的本身
    fun alsoTest() {
        val testString = "testString"
        val alsoReturn = testString.also {
            // 直接持有testString语义
            val test1 = it.first()
            val test2 = it.length
            it + "Also"
            "testAlsoReturn"
            // 返回自身
        }
        // testString
        println(".also{ }中的返回值：$alsoReturn")
    }

    // 调用同一个对象的多个方法 / 属性时，可以省去对象名重复，直接调用方法名 / 属性即可
    fun withTest() {
        val testString = "testString"
        val withReturn = with(testString) {
            // 直接持有testString语义
            val test1 = first()
            val test2 = length
            " "
        }
        // 空
        println(".with{ }中的返回值：$withReturn")
    }

    // 调用同一个对象的多个方法 / 属性时，可以省去对象名重复，直接调用方法名 / 属性即可
    // 返回值 = 函数块的最后一行 / return表达式
    fun runTest() {
        val testString = "testString"
        val runReturn = testString.run {
            val test1 = first()
            val test2 = length
            "runReturn"
        }
        println(".run{ }的返回值：$runReturn")
    }

    // 与run函数类似，但区别在于返回值：
    // run函数返回最后一行的值 / 表达式
    // apply函数返回传入的对象的本身
    fun applyTest() {
        val testString = "testString"
        val applyReturn = testString.apply {
            // 直接持有testString语义
            val test1 = first()
            val test2 = length
            // 返回自身
            "applyReturn"
        }
        println(".apply{ }的返回值：$applyReturn")
    }
}