package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
  @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
  fun getAllBookmarks(): Flow<List<BookmarkEntity>>

  @Query("SELECT * FROM bookmarks WHERE itemType = :type ORDER BY createdAt DESC")
  fun getBookmarksByType(type: String): Flow<List<BookmarkEntity>>

  @Query("SELECT * FROM bookmarks WHERE collectionName = :collection ORDER BY createdAt DESC")
  fun getBookmarksByCollection(collection: String): Flow<List<BookmarkEntity>>

  @Query("SELECT * FROM bookmarks WHERE id = :id LIMIT 1")
  suspend fun getBookmarkById(id: String): BookmarkEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmark(bookmark: BookmarkEntity)

  @Query("DELETE FROM bookmarks WHERE id = :id")
  suspend fun deleteBookmarkById(id: String)

  @Query("UPDATE bookmarks SET userNote = :note WHERE id = :id")
  suspend fun updateNote(id: String, note: String)

  @Query("UPDATE bookmarks SET collectionName = :collection WHERE id = :id")
  suspend fun updateCollection(id: String, collection: String)
}

@Dao
interface CollectionDao {
  @Query("SELECT * FROM collections ORDER BY createdAt ASC")
  fun getAllCollections(): Flow<List<CollectionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCollection(collection: CollectionEntity)

  @Delete
  suspend fun deleteCollection(collection: CollectionEntity)

  @Query("DELETE FROM collections WHERE id = :id")
  suspend fun deleteCollectionById(id: String)
}

@Dao
interface SearchHistoryDao {
  @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
  fun getRecentSearches(): Flow<List<SearchHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSearch(search: SearchHistoryEntity)

  @Query("DELETE FROM search_history WHERE query = :query")
  suspend fun deleteSearch(query: String)

  @Query("DELETE FROM search_history")
  suspend fun clearAll()
}
