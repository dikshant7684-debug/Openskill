package com.example.domain.model

enum class SortOption(val displayName: String) {
  RELEVANCE("Best Match"),
  STARS("Most Stars"),
  UPDATED("Recently Updated"),
  FORKS("Most Forked")
}

data class SearchQuery(
  val rawQuery: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class SearchFilters(
  val query: String = "",
  val platform: Platform = Platform.ALL,
  val category: ResourceCategory = ResourceCategory.ALL,
  val language: String? = null,
  val minStars: Int = 0,
  val sortBy: SortOption = SortOption.RELEVANCE,
  val activelyMaintainedOnly: Boolean = false,
  val hasSkillsOnly: Boolean = false
)
