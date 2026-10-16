package com.example.domain.repository

import com.example.domain.model.Repository
import com.example.domain.model.Skill

interface SkillRepository {
  suspend fun getCuratedSkills(): List<Skill>
  suspend fun discoverSkillsInRepository(repository: Repository): List<Skill>
  suspend fun parseSkillFile(rawContent: String, repoFullName: String, repoOwner: String): Skill?
}
