package com.example.flamingcoding.rengWuXianKotlin

import android.content.Context
import android.widget.Button
import android.widget.TextView

class GenericCls {
    fun inOutTest(context: Context) {
        //kotlin中的泛型本身也是不可变的。
        //使用关键字 out 来支持协变，等同于 Java 中的上界通配符 ? extends。
        //使用关键字 in 来支持逆变，等同于 Java 中的下界通配符 ? super。
        // ? extends TextView
        val outTextViewList: MutableList<out TextView> = ArrayList()
        val textViewList: MutableList<TextView> = ArrayList()
//        var textViews2: List<in TextView>
        val button = Button(context)
        val textView = TextView(context)
        // 报错不能添加，只能消费
        // Receiver type 'MutableList<out TextView>' contains out projection which prohibits the use of 'fun add(element: E): Boolean'.
//        textViewList1.add(textView)
        outTextViewList.get(0)
        textViewList.add(textView)
        textViewList.get(0)

        // ? super Button
        // ? super TextView
        val inButtonList: MutableList<in Button> = ArrayList()
        val buttonList: MutableList<TextView> = ArrayList()
        inButtonList.add(button)
        // Argument type mismatch: actual type is 'TextView', but 'CapturedType(in Button)' was expected.
//        inButtonList.add(textView)
        buttonList.add(button)
        buttonList.add(textView)
        // 报错：可以添加，但是消费出来的类型为Any?
//        val inGetValue: Button? = inButtonList.get(0)
        // Initializer type mismatch: expected 'TextView?', actual 'CapturedType(in TextView)'.
//        val inGetValue: TextView? = inButtonList.get(0)
//        val inGetValue: Any? = inButtonList.get(0)
        val getValue = buttonList.get(0)
    }
}

class Consumer<T> {
    fun consume(t: T) {
        // ...
    }
}

class Producer<out T> where T : TextView {
    fun produce(context: Context): T {
        // ...
        val txtView = TextView(context)
        return txtView as T
    }
}

fun genericTest(context: Context) {
    val consumer: Consumer<in Button> = Consumer<TextView>()
    consumer.consume(Button(context))

    val producer1: Producer<TextView> = Producer<Button>() // 👈 这里不写 out 也不会报错
    val producer2: Producer<out TextView> = Producer<Button>() // 👈 out 可以但没必要
}

