package com.example.data.mapper

import com.example.data.remote.github.dto.GitHubContentDto
import com.example.data.remote.github.dto.GitHubRepoDto
import com.example.data.remote.gitlab.dto.GitLabProjectDto
import com.example.data.remote.gitlab.dto.GitLabTreeItemDto
import com.example.domain.model.Platform
import com.example.domain.model.Repository
import com.example.domain.model.RepositoryFile

object RepositoryMapper {

  fun fromGitHubDto(dto: GitHubRepoDto): Repository {
    return Repository(
      id = "gh_${dto.id}",
      name = dto.name,
      fullName = dto.fullName,
      owner = dto.owner.login,
      ownerAvatarUrl = dto.owner.avatarUrl ?: "",
      description = dto.description ?: "No description provided by repository author",
      platform = Platform.GITHUB,
      stars = dto.stargazersCount,
      forks = dto.forksCount,
      openIssues = dto.openIssuesCount,
      language = dto.language,
      topics = dto.topics ?: emptyList(),
      license = dto.license?.spdxId ?: dto.license?.name,
      defaultBranch = dto.defaultBranch,
      updatedAt = dto.updatedAt,
      isArchived = dto.archived,
      hasSkills = false,
      skillCount = 0,
      url = dto.htmlUrl,
      isBookmarked = false,
      readmePreview = "",
      detectedSkills = emptyList()
    )
  }

  fun fromGitLabDto(dto: GitLabProjectDto): Repository {
    val ownerName = dto.pathWithNamespace.substringBefore("/", "")
    return Repository(
      id = "gl_${dto.id}",
      name = dto.name,
      fullName = dto.pathWithNamespace,
      owner = if (ownerName.isNotBlank()) ownerName else dto.name,
      ownerAvatarUrl = dto.avatarUrl ?: "",
      description = dto.description ?: "No description provided by GitLab project author",
      platform = Platform.GITLAB,
      stars = dto.starCount,
      forks = dto.forksCount,
      openIssues = 0,
      language = null,
      topics = dto.tagList,
      license = null,
      defaultBranch = "main",
      updatedAt = dto.lastActivityAt,
      isArchived = dto.archived,
      hasSkills = false,
      skillCount = 0,
      url = dto.webUrl,
      isBookmarked = false,
      readmePreview = "",
      detectedSkills = emptyList()
    )
  }

  fun fromGitHubContent(dto: GitHubContentDto): RepositoryFile {
    val isSkill = dto.name.equals("SKILL.md", ignoreCase = true) ||
      dto.name.equals("mcp.json", ignoreCase = true) ||
      dto.path.contains("skills/")
    return RepositoryFile(
      name = dto.name,
      path = dto.path,
      isDirectory = dto.type == "dir",
      size = dto.size,
      downloadUrl = dto.downloadUrl,
      isSkillRelated = isSkill
    )
  }

  fun fromGitLabTree(dto: GitLabTreeItemDto, projectId: Long): RepositoryFile {
    val isSkill = dto.name.equals("SKILL.md", ignoreCase = true) ||
      dto.name.equals("mcp.json", ignoreCase = true) ||
      dto.path.contains("skills/")
    val rawUrl = "https://gitlab.com/api/v4/projects/$projectId/repository/files/${dto.path}/raw?ref=main"
    return RepositoryFile(
      name = dto.name,
      path = dto.path,
      isDirectory = dto.type == "tree",
      size = 0,
      downloadUrl = if (dto.type == "blob") rawUrl else null,
      isSkillRelated = isSkill
    )
  }
}
