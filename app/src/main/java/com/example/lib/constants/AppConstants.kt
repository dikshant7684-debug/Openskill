package com.example.lib.constants

object AppConstants {
  const val APP_NAME = "OpenSkill"
  const val USER_AGENT = "Openskill-Android-Discovery/1.0"
  const val GITHUB_BASE_URL = "https://api.github.com/"
  const val GITLAB_BASE_URL = "https://gitlab.com/"

  const val DEFAULT_PAGE_SIZE = 30
  const val MAX_CACHE_ENTRIES = 50
  const val CONNECT_TIMEOUT_SECONDS = 15L
  const val READ_TIMEOUT_SECONDS = 20L

  // Standard SKILL file conventions
  const val SKILL_FILE_NAME = "SKILL.md"
  const val SKILLS_DIRECTORY = "skills"
  const val DOT_SKILLS_DIRECTORY = ".skills"
  const val MCP_CONFIG_FILE = "mcp.json"

  // Supported Platforms
  const val PLATFORM_GITHUB = "GitHub"
  const val PLATFORM_GITLAB = "GitLab"
  const val PLATFORM_ALL = "All Platforms"

  // Shared Categories
  const val CATEGORY_AI_SKILLS = "skills"
  const val CATEGORY_MCP_SERVERS = "mcp"
  const val CATEGORY_AI_AGENTS = "agents"
  const val CATEGORY_CODING_TOOLS = "tools"
  const val CATEGORY_AUTOMATION = "automation"
  const val CATEGORY_WEB_DEV = "web"
  const val CATEGORY_LIBRARIES = "libraries"
  const val CATEGORY_TRENDING = "trending"
}
