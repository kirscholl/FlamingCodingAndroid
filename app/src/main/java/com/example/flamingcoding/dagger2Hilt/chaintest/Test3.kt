//package com.example.flamingcoding.dagger2Hilt.chaintest
//
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ActivityComponent
//
//
//@Module
//@InstallIn(ActivityComponent::class)
//class Test3 {
//
//    var testStr: String = ""
//
//    @Provides
//    fun provideTest3(test2: Test2): Test3 {
//        val test3 = Test3()
//        test3.testStr = test2.testStr
//        return test3
//    }
//}
