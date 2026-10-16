package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.BookmarkEntity
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
