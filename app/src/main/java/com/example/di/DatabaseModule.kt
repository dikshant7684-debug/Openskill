package com.example.di

import android.content.Context
import com.example.core.database.OpenSkillDatabase
import com.example.data.local.dao.BookmarkDao
import com.example.data.local.dao.CollectionDao
import com.example.data.local.dao.SearchHistoryDao
import com.example.data.local.preferences.UserPreferences

class DatabaseModule(context: Context) {

  val database: OpenSkillDatabase by lazy {
    OpenSkillDatabase.getDatabase(context)
  }

  val bookmarkDao: BookmarkDao by lazy {
    database.bookmarkDao()
  }

  val collectionDao: CollectionDao by lazy {
    database.collectionDao()
  }

  val searchHistoryDao: SearchHistoryDao by lazy {
    database.searchHistoryDao()
  }

  val userPreferences: UserPreferences by lazy {
    UserPreferences(context)
  }
}
