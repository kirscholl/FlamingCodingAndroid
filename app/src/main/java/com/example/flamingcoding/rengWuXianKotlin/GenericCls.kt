package com.example.flamingcoding.rengWuXianKotlin

import android.content.Context
import android.widget.Button
import android.widget.TextView

class GenericCls {

    fun inOutTest() {

        // Kotlin 中的泛型本身也是不可变的。
        //使用关键字 out 来支持协变，等同于 Java 中的上界通配符 ? extends。
        //使用关键字 in 来支持逆变，等同于 Java 中的下界通配符 ? super。
        var textViews1: List<out TextView>
//        var textViews2: List<in TextView>
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
    consumer.consume(Button(context)) // 👈 相当于 &#39;List&#39; 的 &#39;add&#39;

    val producer1: Producer<TextView> = Producer<Button>() // 👈 这里不写 out 也不会报错
    val producer2: Producer<out TextView> = Producer<Button>() // 👈 out 可以但没必要
}

