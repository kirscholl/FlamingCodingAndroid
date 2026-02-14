package com.example.flamingcoding.androidTrials

class CodeNote {
    // 如果你需要在XML中引用一个id，就使用@id/id_name这种语法，而如果你需要在XML中定义一个id，则要使用@+id/id_name这种语法
    // 使用Kotlin编写的Android项目在app/build.gradle文件的头部默认引入了一个kotlin-android-extensions插件，这个插件会根据布局文件中定义的控件id自动生成一个具有相同名称的变量
    // android:exported="false" ???
    // 使用startActivityForResult()方法来启动SecondActivity的，在SecondActivity被销毁之后会回调上一个Activity的onActivityResult()方法
    // killProcess()方法用于杀掉一个进程，它接收一个进程id参数，我们可以通过myPid()方法来获得当前程序的进程id。

    // 顶层方法指的是那些没有定义在任何类中的方法
    // Java中没有顶层方法这个概念，所有的方法必须定义在类中。那么这个doSomething()方法被藏在了哪里呢？
    // 我们刚才创建的Kotlin文件名叫作Helper.kt，于是Kotlin编译器会自动创建一个叫作HelperKt的Java类，
    // doSomething()方法就是以静态方法的形式定义在HelperKt类里面的，因此在Java中使用HelperKt.doSomething()的写法来调用就可以了

    // android:gravity用于指定文字在控件中的对齐方式，而android:layout_gravity用于指定控件在布局中的对齐方式
    // 系统会先把LinearLayout下所有控件指定的layout_weight值相加，得到一个总值，然后每个控件所占大小的比例就是用该控件的layout_weight值除以刚才算出的总值
    // repeat函数是Kotlin中另外一个非常常用的标准函数，它允许你传入一个数值n，然后会把Lambda表达式中的内容执行n遍

    // Service并不是运行在一个独立的进程当中的，而是依赖于创建Service时所在的应用程序进程。当某个应用程序进程被杀掉时，所有依赖于该进程的Service也会停止运行
}