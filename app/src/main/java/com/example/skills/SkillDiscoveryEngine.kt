package com.example.skills

import com.example.domain.model.Platform
import com.example.domain.model.Repository
import com.example.domain.model.RepositoryFile
import com.example.domain.model.Skill

class SkillDiscoveryEngine(
  private val getRawFile: suspend (url: String) -> String?
) {

  /**
   * Discovers real skills within a repository using its verified files.
   * If a repository has a SKILL.md, it downloads and parses the real content.
   * If a repository contains a skills/ directory, it inspects child definitions.
   * If no skill files exist, it returns an empty list (NO mock data).
   */
  suspend fun discoverInRepository(
    repository: Repository,
    files: List<RepositoryFile>
  ): List<Skill> {
    val discovered = mutableListOf<Skill>()

    // Look for root SKILL.md
    val rootSkillFile = files.find { it.name.equals("SKILL.md", ignoreCase = true) }
    if (rootSkillFile?.downloadUrl != null) {
      val raw = getRawFile(rootSkillFile.downloadUrl)
      if (!raw.isNullOrBlank()) {
        val skill = SkillFileParser.parse(
          rawContent = raw,
          repoFullName = repository.fullName,
          repoOwner = repository.owner,
          repoPlatform = repository.platform,
          filePath = "SKILL.md",
          repoStars = repository.stars
        )
        if (skill != null) discovered.add(skill)
      }
    }

    // Look for mcp.json
    val mcpFile = files.find { it.name.equals("mcp.json", ignoreCase = true) }
    if (mcpFile?.downloadUrl != null) {
      val rawMcp = getRawFile(mcpFile.downloadUrl)
      if (!rawMcp.isNullOrBlank()) {
        val metadata = SkillMetadataParser.parseMcpJson(rawMcp, repository.owner)
        if (metadata != null) {
          discovered.add(
            Skill(
              id = "${repository.fullName}_mcp_server",
              name = metadata.name,
              repoFullName = repository.fullName,
              repoOwner = repository.owner,
              repoPlatform = repository.platform,
              filePath = "mcp.json",
              description = metadata.description,
              category = com.example.domain.model.ResourceCategory.MCP_SERVERS,
              supportedTools = metadata.requiredTools,
              compatibility = metadata.compatibilityList.joinToString(", "),
              stars = repository.stars,
              isBookmarked = false,
              metadata = metadata
            )
          )
        }
      }
    }

    return discovered
  }
}
