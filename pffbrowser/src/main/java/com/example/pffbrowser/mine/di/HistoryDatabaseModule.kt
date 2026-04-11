package com.example.pffbrowser.mine.di

import android.content.Context
import androidx.room.Room
import com.example.pffbrowser.mine.data.BrowserHistoryDao
import com.example.pffbrowser.mine.data.BrowserHistoryDatabase
import com.example.pffbrowser.mine.data.BrowserHistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HistoryDatabaseModule {

    @Provides
    @Singleton
    fun provideHistoryDatabase(@ApplicationContext context: Context): BrowserHistoryDatabase {
        return Room.databaseBuilder(
            context,
            BrowserHistoryDatabase::class.java,
            BrowserHistoryDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideHistoryDao(db: BrowserHistoryDatabase): BrowserHistoryDao = db.browserHistoryDao()
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HistoryRepositoryEntryPoint {
    fun historyRepository(): BrowserHistoryRepository
}
