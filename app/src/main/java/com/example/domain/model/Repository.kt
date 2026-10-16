package com.example.domain.model

import androidx.compose.ui.graphics.Color
import com.example.core.designsystem.colors.*

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

data class Repository(
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
  val detectedSkills: List<Skill> = emptyList()
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
