package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class Platform(val displayName: String, val iconLabel: String) {
  GITHUB("GitHub", "GH"),
  GITLAB("GitLab", "GL"),
  ALL("All Platforms", "ALL")
}

enum class ResourceCategory(val id: String, val title: String, val description: String) {
  ALL("all", "All Resources", "Explore skills, servers, agents and developer tools"),
  AI_SKILLS("skills", "AI Agent Skills", "Claude Skills, Codex skills, and SKILL.md definitions"),
  MCP_SERVERS("mcp", "MCP Servers", "Model Context Protocol servers and tool integrations"),
  AI_AGENTS("agents", "AI Agents", "Autonomous coding, research, and task automation agents"),
  CODING_TOOLS("tools", "Coding Tools", "CLI tools, SDKs, developer utilities, and compilers"),
  AUTOMATION("automation", "Automation", "Browser automation, workflows, and productivity scripts"),
  WEB_DEV("web", "Web Development", "Modern fullstack, backend, frontend, and UI component systems"),
  LIBRARIES("libraries", "Developer Libraries", "High-performance modules, frameworks, and packages"),
  TRENDING("trending", "Trending Repos", "Rapidly growing and popular developer repositories")
}

data class RepositoryItem(
  val id: String,
  val name: String,
  val fullName: String,
  val owner: String,
  val ownerAvatarUrl: String = "",
  val description: String,
  val platform: Platform = Platform.GITHUB,
  val stars: Int = 0,
  val forks: Int = 0,
  val openIssues: Int = 0,
  val language: String? = null,
  val topics: List<String> = emptyList(),
  val license: String? = null,
  val defaultBranch: String = "main",
  val updatedAt: String = "recently",
  val isArchived: Boolean = false,
  val hasSkills: Boolean = false,
  val skillCount: Int = 0,
  val url: String = "",
  val isBookmarked: Boolean = false,
  val readmePreview: String = "",
  val detectedSkills: List<SkillItem> = emptyList()
) {
  val languageColor: Color
    get() = when (language?.lowercase()) {
      "python" -> LangPython
      "typescript" -> LangTypeScript
      "javascript" -> LangJavaScript
      "rust" -> LangRust
      "go" -> LangGo
      "kotlin" -> LangKotlin
      "java" -> LangJava
      "c++", "cpp" -> LangCpp
      "ruby" -> LangRuby
      "swift" -> LangSwift
      "shell", "bash" -> LangShell
      else -> LangOther
    }
}

data class SkillItem(
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
  val isBookmarked: Boolean = false
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

data class FilterOptions(
  val query: String = "",
  val platform: Platform = Platform.ALL,
  val category: ResourceCategory = ResourceCategory.ALL,
  val language: String? = null,
  val minStars: Int = 0,
  val sortBy: SortOption = SortOption.RELEVANCE,
  val activelyMaintainedOnly: Boolean = false,
  val hasSkillsOnly: Boolean = false
)

enum class SortOption(val displayName: String) {
  RELEVANCE("Best Match"),
  STARS("Most Stars"),
  UPDATED("Recently Updated"),
  FORKS("Most Forked")
}

data class AiRecommendation(
  val id: String,
  val repoItem: RepositoryItem,
  val relevanceScore: Int, // 1 to 100
  val whyItMatches: String,
  val keySkillOrFeature: String,
  val compatibilityNote: String
)

data class UserNote(
  val targetId: String, // repo fullName or skill id
  val noteText: String,
  val updatedAt: Long = System.currentTimeMillis()
)
