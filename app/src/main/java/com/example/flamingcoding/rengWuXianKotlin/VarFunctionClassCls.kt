package com.example.flamingcoding.rengWuXianKotlin

import android.util.Log

// top Level function顶层函数 -> 以包名为作用域
fun topLevelFunction() {
    Log.d("topLevelFunction", "topLevelFunction invoke")
}

// kotlin中常量的声明,Kotlin的常量必须声明在对象（包括伴生对象）或者「top-level 顶层」中，因为常量是静态的
// Kotlin 中只有基本类型和 String 类型可以声明成常量
const val TOP_CONST_STRING = "TopConstString"

// 使用open让kotlin的类能使被继承，kotlin类默认是final的
open class VarFunctionClassCls {

    // 声明一个内部Object 其中的用于将一部分变量设置为静态的
    object VarFunctionClassInnerCls {
        val innerObejectString = "InnerObjectString"
    }

    // 声明一个伴生对象，一个类中最多只可以有一个伴生对象，所以其名字可以省略
    companion object {
        // kotlin中常量的声明,Kotlin的常量必须声明在对象（包括伴生对象）或者「top-level 顶层」中，因为常量是静态的
        // Kotlin 中只有基本类型和 String 类型可以声明成常量
        // Kotlin 中的常量指的是 「compile-time constant 编译时常量」，
        // 它的意思是「编译器在编译的时候就知道这个东西在每个调用处的实际值」，因此可以在编译时直接把这个值硬编码到代码里使用的地方
        const val TAG: String = "VarFunctionClassCls"

        init {
            Log.d(TAG, "companion object init function invoke")
        }
    }


    // kotlin空安全设计
    // 属性需要在声明的同时初始化，除非你把它声明成抽象的
//    var errorNullInt: Int = null
    var testInt: Int = 1

    // kotlin类型推断
    //
    var testString = "test"
        get() {
            Log.d(TAG, "Get testString")
            return field
        }
        set(value) {
            field = value
            Log.d(TAG, "Set testString")
        }

    fun testRun() {
        testString = "Mary"
        // 👆的写法实际上是👇这么调用的
        // setName("Mary")
        // 建议自己试试，IDE 的代码补全功能会在你打出 setn 的时候直接提示 name 而不是 setName

        println(testString)
        // 👆的写法实际上是👇这么调用的
        // print(getName())
        // IDE 的代码补全功能会在你打出 getn 的时候直接提示 name 而不是 getName

        SingleCls.singleInt

        topLevelFunction()
    }

    // val只读变量 它只能赋值一次，不能修改
    val testValInt = 1

    // kotlin可空类型
    var testNullInt: Int? = null
    var testNullString: String? = null

    // Kotlin 里，Int 是否装箱根据场合来定：
    var a: Int = 1 // unbox
    var b: Int? = 2 // box
    var list: List<Int> = listOf(1, 2) // box

    // 原生字符串，使用"""包裹起来的字符串不会发生转义
    var rawString = """/n \n xxx ddd \t \k ||||"""

    // init{ }函数的执行顺序都在次级构造器之前
    init {
        Log.d(TAG, "init function invoke")
    }

    // internal internal是一个可见性修饰符，用于标记声明（例如类、接口或函数）仅在同一模块内可见
    constructor() {
        Log.d(TAG, "constructor function invoke")
    }

    constructor(paramInt: Int, paramString: String) {
        this.testString = paramString
        this.testInt = paramInt
        Log.d(
            TAG,
            "constructor function invoke 222, testInt is $testInt, testString is $testString"
        )
    }

    open fun testFunction() {
        if (testNullString != null) {
            // kotlin中的?.调用是线程安全的
            val strLen = testNullString?.length
            Log.d(TAG, "String len is $strLen")
        }

        if (testNullInt != null) {
            val tempString = testNullInt.toString()
        }

//        Log.d(TAG, testNullInt?.toString()!!)
    }

    // 匿名类
//    val childClsInstance = object : VarFunctionClassCls() {
//        override fun testFunction() {
//
//        }
//    }
}


// 如果不使用open标记子类，子类不会继承父类的open
class ChildCls(var childParamInt: Int, var childParamString: String) : VarFunctionClassCls() {

    companion object {
        const val TAG = "ChildCls"
    }

    // 如果在主构造器声明上加上val 或者 var就等价于在类中创建了该名称的属性
    // class ChildCls(childParamInt: Int, childParamString: String) : VarFunctionClassCls()

    init {
        // init { } 先于次级构造器执行，后于主构造器执行
    }

    // 如果类中有主构造器，那么其他的次构造器都需要通过 this 关键字调用主构造器
    // 必须性：创建类的对象时，不管使用哪个构造器，都需要主构造器的参与
    // 第一性：在类的初始化过程中，首先执行的就是主构造器
    constructor(childParamInt: Int, childParamString: String, childParamFloat: Float) : this(
        childParamInt,
        childParamString
    ) {

    }

    constructor(
        testParamInt: Int,
        testParamString: String,
        testParamFloat: Float,
        testParamList: ArrayList<Int>
    ) : this(
        childParamInt = 1,
        childParamString = "123"
    ) {

    }

    fun childRun() {
        VarFunctionClassCls.VarFunctionClassInnerCls.innerObejectString
    }

    fun typeJudge() {
        val cls: VarFunctionClassCls = ChildCls(1, "test")
        // 👆activity 是无法调用 NewActivity 的 childRun 方法的
//        cls.childRun()
        if (cls is ChildCls) {
            // 👇的强转由于类型推断被省略了
            cls.childRun()
        }
        // 那么不进行类型判断，直接进行强转调用
        val cls2: VarFunctionClassCls = ChildCls(1, "test")
//        (cls2 as ChildCls).childRun()

        // 安全地进行转换调用：如果强转成功就执行之后的调用，如果强转不成功就不执行
        (cls2 as? ChildCls)?.childRun()
    }

    fun normalFunction() {
        var normalString = "normalString"
        fun funInnerFunction() {
            normalString = "innerString"
        }
        // ...
        Log.d(TAG, normalString)
        funInnerFunction()
    }


}

// 继承失败，报错
//class ChildChildCls : ChildCls() {
//
//}

// kotlin中申明一个单例类
// 用 object 修饰的对象中的变量和函数都是静态的
object SingleCls {
    val singleInt = 10
}

// ·kotlin中的可见性修饰符
// ·public：公开，可见性最大，哪里都可以引用。
// ·private：私有，可见性最小，根据声明位置不同可分为类中可见和文件中可见。
// ·protected：保护，相当于 private + 子类可见。
// ·internal：内部，仅对 module 内可见。
// ----internal 在写一个 library module 时非常有用，当需要创建一个函数仅开放给 module 内部使用，
// ----不想对 library 的使用者可见，这时就应该用 internal 可见性修饰符。

