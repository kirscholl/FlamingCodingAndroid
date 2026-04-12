package com.example.flamingcoding.dagger2hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier


interface IMultiNameDaggerTest {
    fun getDaggerData(): String
}

class MultiOneDaggerTest : IMultiNameDaggerTest {
    override fun getDaggerData(): String {
        return "MultiOne"
    }
}

class MultiOtherDaggerTest : IMultiNameDaggerTest {
    override fun getDaggerData(): String {
        return "MultiOther"
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MultiOne

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MultiOther

@Module
@InstallIn(SingletonComponent::class)
object MultiDaggerTestModule {

    @MultiOne
    @Provides
    fun provideMultiOne(): MultiOneDaggerTest {
        return MultiOneDaggerTest()
    }

    @Provides
    @MultiOther
    fun provideMultiOther(): MultiOtherDaggerTest {
        return MultiOtherDaggerTest()
    }
}