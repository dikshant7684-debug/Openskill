package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

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
