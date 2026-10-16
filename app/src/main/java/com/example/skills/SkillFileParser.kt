package com.example.skills

import com.example.domain.model.Platform
import com.example.domain.model.ResourceCategory
import com.example.domain.model.Skill
import com.example.domain.model.SkillMetadata
import com.example.lib.parsing.YamlFrontmatterParser

object SkillFileParser {

  /**
   * Parses the raw content of an actual SKILL.md or prompt instruction file.
   * Extracts frontmatter metadata, structured instructions, and tool definitions.
   * Strictly preserves real data without hallucinating missing fields.
   */
  fun parse(
    rawContent: String,
    repoFullName: String,
    repoOwner: String,
    repoPlatform: Platform,
    filePath: String = "SKILL.md",
    repoStars: Int = 0
  ): Skill? {
    if (rawContent.isBlank()) return null

    val (frontmatter, body) = YamlFrontmatterParser.parse(rawContent)

    // Name: prefer YAML frontmatter name; fallback to first H1 in markdown; fallback to filename
    val extractedName = frontmatter?.name?.takeIf { it.isNotBlank() }
      ?: extractFirstHeading(body)
      ?: filePath.substringAfterLast("/").removeSuffix(".md")

    // Description: prefer YAML frontmatter description; fallback to first paragraph
    val extractedDescription = frontmatter?.description?.takeIf { it.isNotBlank() }
      ?: extractFirstParagraph(body)
      ?: "Skill definition located at $filePath in $repoFullName"

    // Supported tools & requirements from frontmatter or bullet points under Requirements/Tools
    val tools = frontmatter?.tools?.ifEmpty { null }
      ?: extractSectionBullets(body, listOf("tools", "supported tools", "required tools", "capabilities"))

    val requirements = frontmatter?.rawAttributes?.get("requirements")?.let { listOf(it) }
      ?: extractSectionBullets(body, listOf("requirements", "prerequisites", "dependencies"))

    // Compatibility note based on explicit frontmatter or body mentions
    val compatibility = frontmatter?.compatibility?.joinToString(", ")?.takeIf { it.isNotBlank() }
      ?: detectCompatibilityKeywords(rawContent)

    val metadata = SkillMetadata(
      name = extractedName,
      description = extractedDescription,
      author = frontmatter?.author ?: repoOwner,
      version = frontmatter?.version,
      license = frontmatter?.rawAttributes?.get("license"),
      compatibilityList = frontmatter?.compatibility ?: emptyList(),
      requiredTools = tools,
      triggers = frontmatter?.rawAttributes?.get("triggers")?.split(",")?.map { it.trim() } ?: emptyList(),
      formatVersion = frontmatter?.rawAttributes?.get("format") ?: "1.0",
      isSupported = true
    )

    val cleanId = filePath.replace("/", "_").removeSuffix(".md")

    return Skill(
      id = "${repoFullName}_$cleanId",
      name = extractedName,
      repoFullName = repoFullName,
      repoOwner = repoOwner,
      repoPlatform = repoPlatform,
      filePath = filePath,
      description = extractedDescription,
      category = ResourceCategory.AI_SKILLS,
      supportedTools = tools,
      requirements = requirements,
      installCommand = extractInstallCommand(body),
      promptInstructions = body.trim(),
      compatibility = compatibility,
      stars = repoStars,
      isBookmarked = false,
      metadata = metadata
    )
  }

  private fun extractFirstHeading(content: String): String? {
    for (line in content.lines()) {
      val trimmed = line.trim()
      if (trimmed.startsWith("# ") || trimmed.startsWith("## ")) {
        return trimmed.removePrefix("#").removePrefix("#").trim()
      }
    }
    return null
  }

  private fun extractFirstParagraph(content: String): String? {
    val lines = content.lines()
    for (line in lines) {
      val trimmed = line.trim()
      if (trimmed.isNotBlank() && !trimmed.startsWith("#") && !trimmed.startsWith("---") && !trimmed.startsWith("```")) {
        return trimmed
      }
    }
    return null
  }

  private fun extractSectionBullets(content: String, targetHeadings: List<String>): List<String> {
    val items = mutableListOf<String>()
    var inTargetSection = false

    for (line in content.lines()) {
      val trimmed = line.trim()
      if (trimmed.startsWith("#")) {
        val headingText = trimmed.removePrefix("#").trim().lowercase()
        inTargetSection = targetHeadings.any { headingText.contains(it) }
        continue
      }

      if (inTargetSection) {
        if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
          items.add(trimmed.substring(2).trim())
        } else if (trimmed.startsWith("#")) {
          break
        }
      }
    }
    return items
  }

  private fun detectCompatibilityKeywords(content: String): String {
    val lower = content.lowercase()
    val matched = mutableListOf<String>()
    if (lower.contains("claude")) matched.add("Claude")
    if (lower.contains("openai") || lower.contains("gpt-4") || lower.contains("chatgpt")) matched.add("OpenAI")
    if (lower.contains("gemini")) matched.add("Gemini")
    if (lower.contains("cursor")) matched.add("Cursor")
    if (lower.contains("windsurf")) matched.add("Windsurf")
    if (lower.contains("mcp") || lower.contains("model context protocol")) matched.add("MCP")

    return if (matched.isNotEmpty()) matched.joinToString(", ") else "Generic AI Agent"
  }

  private fun extractInstallCommand(content: String): String? {
    val lines = content.lines()
    var inCode = false
    for (line in lines) {
      val trimmed = line.trim()
      if (trimmed.startsWith("```")) {
        inCode = !inCode
        continue
      }
      if (inCode) {
        if (trimmed.startsWith("npm install") ||
          trimmed.startsWith("pip install") ||
          trimmed.startsWith("cargo install") ||
          trimmed.startsWith("uv tool") ||
          trimmed.startsWith("npx ")
        ) {
          return trimmed
        }
      }
    }
    return null
  }
}
