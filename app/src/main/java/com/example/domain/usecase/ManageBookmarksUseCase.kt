package com.example.domain.usecase

import com.example.domain.model.Bookmark
import com.example.domain.model.Collection
import com.example.domain.model.Repository
import com.example.domain.model.Skill
import com.example.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow

class ManageBookmarksUseCase(
  private val bookmarkRepository: BookmarkRepository
) {
  val bookmarks: Flow<List<Bookmark>> = bookmarkRepository.getAllBookmarks()
  val collections: Flow<List<Collection>> = bookmarkRepository.getAllCollections()
  val recentSearches: Flow<List<String>> = bookmarkRepository.getRecentSearches()

  suspend fun toggleRepositoryBookmark(repository: Repository, collectionName: String = "Default") {
    val id = "repo:${repository.fullName}"
    if (repository.isBookmarked) {
      bookmarkRepository.removeBookmark(id)
    } else {
      bookmarkRepository.bookmarkRepository(repository, collectionName)
    }
  }

  suspend fun toggleSkillBookmark(skill: Skill, collectionName: String = "Default") {
    val id = "skill:${skill.repoFullName}/${skill.id}"
    if (skill.isBookmarked) {
      bookmarkRepository.removeBookmark(id)
    } else {
      bookmarkRepository.bookmarkSkill(skill, collectionName)
    }
  }

  suspend fun updateNote(id: String, note: String) {
    bookmarkRepository.updateBookmarkNote(id, note)
  }

  suspend fun updateCollection(id: String, collectionName: String) {
    bookmarkRepository.updateBookmarkCollection(id, collectionName)
  }

  suspend fun createCollection(name: String, description: String, icon: String) {
    bookmarkRepository.createCollection(name, description, icon)
  }

  suspend fun deleteCollection(id: String) {
    bookmarkRepository.deleteCollection(id)
  }

  suspend fun deleteSearch(query: String) {
    bookmarkRepository.removeSearch(query)
  }

  suspend fun clearHistory() {
    bookmarkRepository.clearSearchHistory()
  }
}
