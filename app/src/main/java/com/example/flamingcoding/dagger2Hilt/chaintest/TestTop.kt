package com.example.flamingcoding.dagger2Hilt.chaintest

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class TestTop {

    val testStr: String = "TestTopStr"

    @Provides
    fun provideTestTop(): TestTop {
        return TestTop()
    }
}