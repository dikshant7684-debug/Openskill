package com.example.domain.usecase

import com.example.domain.model.Repository
import com.example.domain.model.Skill
import com.example.domain.repository.BookmarkRepository
import com.example.domain.repository.SkillRepository
import kotlinx.coroutines.flow.first

class DiscoverSkillsUseCase(
  private val skillRepository: SkillRepository,
  private val bookmarkRepository: BookmarkRepository
) {
  suspend fun getCuratedSkills(): List<Skill> {
    val skills = skillRepository.getCuratedSkills()
    val savedBookmarks = bookmarkRepository.getAllBookmarks().first()
    val savedIds = savedBookmarks.map { it.id }.toSet()

    return skills.map { skill ->
      skill.copy(isBookmarked = savedIds.contains("skill:${skill.repoFullName}/${skill.id}"))
    }
  }

  suspend fun discoverForRepository(repository: Repository): List<Skill> {
    return skillRepository.discoverSkillsInRepository(repository)
  }
}
