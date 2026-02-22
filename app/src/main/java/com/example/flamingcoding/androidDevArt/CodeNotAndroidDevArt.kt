package com.example.flamingcoding.androidDevArt

class CodeNotAndroidDevArt {
    // ########################################## 启动模式 ##########################################
    // standard 每次启动一个Activity都会重新创建一个新的实例
    // ABCD 启动D--> ABCDD ABCDD 启动A--> ABCDDA
    // standard模式的Activity默认会进入启动它的Activity所属的任务栈中,但是由于非Activity类型的Context(如ApplicationContext)
    // 并没有所谓的任务栈，解决这个问题的方法是为待启动Activity指定FLAG_ACTIVITY_NEW_TASK标记位,这样启动的时候就
    // 会为它创建一个新的任务栈,这个时候待启动Activity实际上是以singleTask模式启动的

    // singleTop 在这种模式下，如果新Activity已经位于任务栈的栈顶，那么此Activity不会被重新创建，同时它的onNewIntent方法会被回调
    // 1.ABCD 启动D--> ABCD 2.ABCD 启动A--> ABCDA
    // 在1中D的onCreate、onStart不会被系统调用，因为它并没有发生改变

    // singleTask 只要Activity在一个栈中存在，那么多次启动此Activity都不会重新创建实例，和singleTop一样，系统也会回调其onNewIntent()
    // S1:ABC 在S2中启动D --> S1:ABC S2:D
    // S1:ABC 在S1中启动D --> S1:ABCD
    // S1:ADBC  启动D--> ABCD 调用D的onNewIntent()
    // S1:AB S2:CD 启动D--> S1:ABCD   S1:AB S2:CD 启动C--> S1:ABC

    // singleInstance 单实例模式 此种模式的Activity只能单独地位于一个任务栈中
    // 当A启动后，系统会为它创建一个新的任务栈，然后A独自在这个新的任务栈中，由于栈内复用的特性，后续的请求均不会创建新的Activity，
    // 除非这个独特的任务栈被系统销毁了

    // 1.给Activity指定启动模式
    // 通过AndroidManifest android:launchMode="singleTask"
    // 2.通过启动代码指定启动模式
    // Intent intent = new Intent();
    // intent.setClass(MainActivity.this, SecondActivity.class);
    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // startActivity(intent);
    // 2.的优先级高于1.
    // ########################################## 启动模式 ##########################################
}