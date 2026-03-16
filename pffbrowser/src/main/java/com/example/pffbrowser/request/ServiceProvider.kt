package com.example.pffbrowser.request

import com.example.pffbrowser.request.search.IHotSearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ServiceProvider {

    @Provides
    @Singleton
    fun provideHotSearchWordService(retrofit: Retrofit): IHotSearchService {
        return retrofit.create(IHotSearchService::class.java)
    }
}