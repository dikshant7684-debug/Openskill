package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [BookmarkEntity::class, CollectionEntity::class, SearchHistoryEntity::class],
  version = 1,
  exportSchema = false
)
abstract class SkillScoutDatabase : RoomDatabase() {
  abstract fun bookmarkDao(): BookmarkDao
  abstract fun collectionDao(): CollectionDao
  abstract fun searchHistoryDao(): SearchHistoryDao

  companion object {
    @Volatile
    private var INSTANCE: SkillScoutDatabase? = null

    fun getDatabase(context: Context): SkillScoutDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          SkillScoutDatabase::class.java,
          "skillscout_db"
        )
          .fallbackToDestructiveMigration(false)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
