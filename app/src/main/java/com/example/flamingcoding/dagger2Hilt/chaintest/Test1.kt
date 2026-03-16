package com.example.flamingcoding.dagger2Hilt.chaintest

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@Module
@InstallIn(ActivityComponent::class)
class Test1 {

    var testStr: String = ""

    @Provides
    fun provideTest1(testTop: TestTop): Test1 {
        val test1 = Test1()
        test1.testStr = testTop.testStr
        return test1
    }
}