package com.example.domain.model

data class Skill(
  val id: String,
  val name: String,
  val repoFullName: String,
  val repoOwner: String,
  val repoPlatform: Platform = Platform.GITHUB,
  val filePath: String = "SKILL.md",
  val description: String,
  val category: ResourceCategory = ResourceCategory.AI_SKILLS,
  val supportedTools: List<String> = emptyList(),
  val requirements: List<String> = emptyList(),
  val installCommand: String? = null,
  val promptInstructions: String = "",
  val compatibility: String = "Claude, OpenAI, Cursor, MCP",
  val stars: Int = 0,
  val isBookmarked: Boolean = false,
  val metadata: SkillMetadata? = null
)

data class SkillMetadata(
  val name: String,
  val description: String,
  val author: String? = null,
  val version: String? = null,
  val license: String? = null,
  val compatibilityList: List<String> = emptyList(),
  val requiredTools: List<String> = emptyList(),
  val triggers: List<String> = emptyList(),
  val formatVersion: String = "1.0",
  val isSupported: Boolean = true
)
