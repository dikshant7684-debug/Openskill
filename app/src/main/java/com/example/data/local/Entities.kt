package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
  @PrimaryKey val id: String, // "repo:owner/name" or "skill:owner/name/skillId"
  val itemType: String, // "REPO" or "SKILL"
  val title: String,
  val subtitle: String,
  val platform: String,
  val description: String,
  val language: String?,
  val stars: Int,
  val url: String,
  val collectionName: String = "Default",
  val userNote: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "collections")
data class CollectionEntity(
  @PrimaryKey val id: String,
  val name: String,
  val description: String,
  val icon: String = "folder",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
  @PrimaryKey val query: String,
  val timestamp: Long = System.currentTimeMillis()
)
