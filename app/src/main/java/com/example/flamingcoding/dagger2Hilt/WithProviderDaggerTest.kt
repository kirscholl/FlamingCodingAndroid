package com.example.flamingcoding.dagger2Hilt


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


class WithProviderDaggerTest {
    fun getDaggerValue(): String {
        return "WithProviderDaggerValue"
    }
}

// 在需要提供并非自己编写的类时，无法直接用@Inject标记构造函数
// 使用@Module并编写@Provides方法桥接，把要提供的类提供出去
@Module
@InstallIn(SingletonComponent::class)
object WithProviderDaggerTestModule {
    @Provides
    @Singleton
    fun providerDaggerTest(): WithProviderDaggerTest {
        return WithProviderDaggerTest()
    }

    // TODO provider带参数， 参数由其他地方注入
}