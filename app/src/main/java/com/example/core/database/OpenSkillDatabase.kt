package com.example.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.BookmarkDao
import com.example.data.local.dao.CollectionDao
import com.example.data.local.dao.SearchHistoryDao
import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.CollectionEntity
import com.example.data.local.entity.SearchHistoryEntity

@Database(
  entities = [BookmarkEntity::class, CollectionEntity::class, SearchHistoryEntity::class],
  version = 1,
  exportSchema = false
)
abstract class OpenSkillDatabase : RoomDatabase() {
  abstract fun bookmarkDao(): BookmarkDao
  abstract fun collectionDao(): CollectionDao
  abstract fun searchHistoryDao(): SearchHistoryDao

  companion object {
    @Volatile
    private var INSTANCE: OpenSkillDatabase? = null

    fun getDatabase(context: Context): OpenSkillDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          OpenSkillDatabase::class.java,
          "skillscout_db" // Maintain existing database file name to preserve saved user bookmarks
        )
          .fallbackToDestructiveMigration(false)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
