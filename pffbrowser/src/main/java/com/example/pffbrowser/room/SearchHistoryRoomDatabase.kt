package com.example.pffbrowser.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [SearchHistoryEntity::class])
abstract class SearchHistoryRoomDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {

        private var instance: SearchHistoryRoomDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): SearchHistoryRoomDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                SearchHistoryRoomDatabase::class.java,
                "pb_search_history"
            ).build()
        }

//        val MIGRATION_1_2 = object : Migration(2, 3) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                db.execSQL("alter table SearchHistoryDao add column browserDayTime Int not null default 0")
//            }
//        }
    }

}