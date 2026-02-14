package com.example.flamingcoding.androidTrials

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(version = 1, entities = [RoomPracticeData::class])
abstract class PracticeRoomDatabase : RoomDatabase() {

    abstract fun roomPracticeDao(): RoomPracticeDao

    companion object {

        private var instance: PracticeRoomDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): PracticeRoomDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                PracticeRoomDatabase::class.java, "app_database"
            ).addMigrations(MIGRATION_1_2).build().apply {
                instance = this
            }
        }

        val MIGRATION_1_2 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("alter table RoomPracticeData add column browserDayTime Int not null default 0")
            }
        }
    }
}