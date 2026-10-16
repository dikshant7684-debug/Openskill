package com.example.domain.model

data class Bookmark(
  val id: String,
  val itemType: BookmarkType,
  val title: String,
  val subtitle: String,
  val platform: Platform,
  val description: String,
  val language: String?,
  val stars: Int,
  val url: String,
  val collectionName: String = "Default",
  val userNote: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

enum class BookmarkType {
  REPO,
  SKILL
}

data class Collection(
  val id: String,
  val name: String,
  val description: String,
  val icon: String = "folder",
  val itemCount: Int = 0,
  val createdAt: Long = System.currentTimeMillis()
)

data class FeaturedCollection(
  val id: String,
  val title: String,
  val description: String,
  val iconName: String,
  val query: String,
  val tag: String,
  val repoCount: Int
)

data class Recommendation(
  val id: String,
  val repository: Repository,
  val relevanceScore: Int, // 1 to 100
  val whyItMatches: String,
  val keySkillOrFeature: String,
  val compatibilityNote: String
)
