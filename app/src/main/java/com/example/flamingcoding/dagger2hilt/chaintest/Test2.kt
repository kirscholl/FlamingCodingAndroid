//package com.example.flamingcoding.dagger2Hilt.chaintest
//
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//
//
//@Module
//@InstallIn(SingletonComponent::class)
//class Test2 {
//
//    var testStr: String = ""
//
//    @Provides
//    fun provideTest2(test1: Test1): Test2 {
//        val test2 = Test2()
//        test2.testStr = test1.testStr
//        return test2
//    }
//}