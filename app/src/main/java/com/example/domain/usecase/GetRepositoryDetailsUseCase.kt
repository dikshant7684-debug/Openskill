package com.example.domain.usecase

import com.example.domain.model.Repository
import com.example.domain.model.RepositoryFile
import com.example.domain.repository.BookmarkRepository
import com.example.domain.repository.CodeRepository
import com.example.domain.repository.SkillRepository
import kotlinx.coroutines.flow.first

class GetRepositoryDetailsUseCase(
  private val codeRepository: CodeRepository,
  private val skillRepository: SkillRepository,
  private val bookmarkRepository: BookmarkRepository
) {
  suspend fun getDetails(owner: String, name: String, platform: String): Repository? {
    val repo = codeRepository.getRepositoryDetails(owner, name, platform) ?: return null
    val savedBookmarks = bookmarkRepository.getAllBookmarks().first()
    val isSaved = savedBookmarks.any { it.id == "repo:${repo.fullName}" }

    val detected = if (repo.detectedSkills.isEmpty()) {
      skillRepository.discoverSkillsInRepository(repo)
    } else {
      repo.detectedSkills
    }

    return repo.copy(
      isBookmarked = isSaved,
      detectedSkills = detected,
      hasSkills = detected.isNotEmpty(),
      skillCount = if (detected.isNotEmpty()) detected.size else repo.skillCount
    )
  }

  suspend fun getFiles(owner: String, name: String, path: String = ""): List<RepositoryFile> {
    return codeRepository.getRepositoryFiles(owner, name, path)
  }

  suspend fun getReadme(downloadUrl: String): String? {
    return codeRepository.getRawFileContent(downloadUrl)
  }
}
