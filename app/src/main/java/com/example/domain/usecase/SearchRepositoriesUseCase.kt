package com.example.domain.usecase

import com.example.domain.model.Repository
import com.example.domain.model.SearchFilters
import com.example.domain.repository.BookmarkRepository
import com.example.domain.repository.CodeRepository
import kotlinx.coroutines.flow.first

class SearchRepositoriesUseCase(
  private val codeRepository: CodeRepository,
  private val bookmarkRepository: BookmarkRepository
) {
  suspend operator fun invoke(filters: SearchFilters): List<Repository> {
    if (filters.query.isNotBlank()) {
      bookmarkRepository.recordSearch(filters.query.trim())
    }

    val results = codeRepository.searchRepositories(filters)
    val savedBookmarks = bookmarkRepository.getAllBookmarks().first()
    val savedIds = savedBookmarks.map { it.id }.toSet()

    return results.map { repo ->
      val isSaved = savedIds.contains("repo:${repo.fullName}")
      repo.copy(isBookmarked = isSaved)
    }
  }
}
