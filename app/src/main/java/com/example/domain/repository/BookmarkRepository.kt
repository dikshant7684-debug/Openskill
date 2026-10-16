package com.example.domain.repository

import com.example.domain.model.Bookmark
import com.example.domain.model.Collection
import com.example.domain.model.Repository
import com.example.domain.model.Skill
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
  fun getAllBookmarks(): Flow<List<Bookmark>>
  fun getAllCollections(): Flow<List<Collection>>
  fun getRecentSearches(): Flow<List<String>>

  suspend fun bookmarkRepository(repository: Repository, collectionName: String = "Default")
  suspend fun bookmarkSkill(skill: Skill, collectionName: String = "Default")
  suspend fun removeBookmark(id: String)
  suspend fun updateBookmarkNote(id: String, note: String)
  suspend fun updateBookmarkCollection(id: String, collectionName: String)

  suspend fun createCollection(name: String, description: String, icon: String)
  suspend fun deleteCollection(id: String)

  suspend fun recordSearch(query: String)
  suspend fun removeSearch(query: String)
  suspend fun clearSearchHistory()
}
