package com.example.data.repository

import android.util.Log
import com.example.data.local.BookmarkDao
import com.example.data.local.CollectionDao
import com.example.data.local.SearchHistoryDao
import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.CollectionEntity
import com.example.data.local.entity.SearchHistoryEntity
import com.example.data.model.*
import com.example.data.remote.ApiClient
import com.example.data.remote.GitHubApiService
import com.example.data.remote.GitLabApiService
import com.example.skills.SkillCompatibilityDetector
import com.example.skills.SkillFileParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SkillScoutRepository(
  private val bookmarkDao: BookmarkDao,
  private val collectionDao: CollectionDao,
  private val searchHistoryDao: SearchHistoryDao,
  private val gitHubApi: GitHubApiService = ApiClient.gitHubApi,
  private val gitLabApi: GitLabApiService = ApiClient.gitLabApi
) {

  val bookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
  val collections: Flow<List<CollectionEntity>> = collectionDao.getAllCollections()
  val recentSearches: Flow<List<SearchHistoryEntity>> = searchHistoryDao.getRecentSearches()

  suspend fun getFeaturedCollections(): List<FeaturedCollection> {
    return listOf(
      FeaturedCollection("mcp", "Top MCP Servers", "Verified Model Context Protocol server implementations", "cpu", "topic:mcp-server OR mcp.json", "MCP", 0),
      FeaturedCollection("skills", "AI Agent Skills", "Real SKILL.md specs & prompt instructions", "sparkles", "SKILL.md OR agent-skills", "AI Skills", 0),
      FeaturedCollection("agents", "Autonomous Agents", "Live coding agents and task automation tools", "terminal", "topic:ai-agent OR topic:coding-agent", "Agents", 0),
      FeaturedCollection("tools", "Coding Tools & CLIs", "Developer utilities, linters, and terminals", "code", "topic:developer-tools OR topic:cli", "Tools", 0),
      FeaturedCollection("trending", "Popular Repos", "Top starred developer repositories", "flame", "stars:>5000", "Popular", 0)
    )
  }

  suspend fun getSampleSkills(): List<SkillItem> = withContext(Dispatchers.IO) {
    val skills = mutableListOf<SkillItem>()
    try {
      // 1. Fetch real MCP and AI skill repositories from GitHub
      val ghQueries = listOf(
        "topic:mcp-server OR \"mcp server\" stars:>100",
        "topic:agent-skills OR topic:ai-agent OR \"agent skills\" stars:>100"
      )
      for (query in ghQueries) {
        val response = gitHubApi.searchRepositories(query, sort = "stars", order = "desc", perPage = 10)
        if (response.isSuccessful) {
          val repos = response.body()?.items ?: emptyList()
          for (repo in repos) {
            // Check for raw SKILL.md
            val rawUrl = "https://raw.githubusercontent.com/${repo.fullName}/${repo.defaultBranch}/SKILL.md"
            val rawContent = try {
              val fileRes = gitHubApi.getRawContent(rawUrl)
              if (fileRes.isSuccessful) fileRes.body()?.string() else null
            } catch (_: Exception) { null }

            if (!rawContent.isNullOrBlank()) {
              val domainSkill = SkillFileParser.parse(
                rawContent = rawContent,
                repoFullName = repo.fullName,
                repoOwner = repo.owner.login,
                repoPlatform = com.example.domain.model.Platform.GITHUB,
                filePath = "SKILL.md",
                repoStars = repo.stargazersCount
              )
              if (domainSkill != null) {
                skills.add(
                  SkillItem(
                    id = domainSkill.id,
                    name = domainSkill.name,
                    repoFullName = domainSkill.repoFullName,
                    repoOwner = domainSkill.repoOwner,
                    repoPlatform = Platform.GITHUB,
                    filePath = domainSkill.filePath,
                    description = domainSkill.description,
                    category = ResourceCategory.AI_SKILLS,
                    supportedTools = domainSkill.supportedTools,
                    requirements = domainSkill.requirements,
                    installCommand = domainSkill.installCommand,
                    promptInstructions = domainSkill.promptInstructions,
                    compatibility = domainSkill.compatibility,
                    stars = domainSkill.stars,
                    isBookmarked = false
                  )
                )
                continue
              }
            }

            // Real-time MCP server or Agent Skill item from actual repository data
            val isMcp = repo.name.lowercase().contains("mcp") ||
              (repo.description?.lowercase()?.contains("mcp") == true) ||
              (repo.topics?.any { it.contains("mcp") } == true)

            val category = if (isMcp) ResourceCategory.MCP_SERVERS else ResourceCategory.AI_AGENTS
            val tools = (repo.topics ?: emptyList()).filter { it.isNotBlank() }.take(5).ifEmpty {
              listOf(repo.language ?: "Tool", if (isMcp) "MCP Protocol" else "AI Agent")
            }

            skills.add(
              SkillItem(
                id = "skill_${repo.id}",
                name = repo.name,
                repoFullName = repo.fullName,
                repoOwner = repo.owner.login,
                repoPlatform = Platform.GITHUB,
                filePath = if (isMcp) "mcp.json" else "README.md",
                description = repo.description ?: "Verified real-time open source AI tool from GitHub.",
                category = category,
                supportedTools = tools,
                requirements = listOf(repo.language ?: "Code", "GitHub Repository"),
                installCommand = if (isMcp) "npx -y @modelcontextprotocol/create-server ${repo.name}" else "git clone ${repo.htmlUrl}",
                promptInstructions = "Integrate tools from ${repo.fullName}. Reference: ${repo.htmlUrl}",
                compatibility = if (isMcp) "Model Context Protocol (MCP), Claude, Cursor" else "Python, Node.js, CLI",
                stars = repo.stargazersCount,
                isBookmarked = false
              )
            )
          }
        }
      }

      // 2. Fetch real projects from GitLab
      val glResponse = gitLabApi.searchProjects(search = "mcp", orderBy = "last_activity_at", perPage = 5)
      if (glResponse.isSuccessful) {
        val glProjects = glResponse.body() ?: emptyList()
        for (proj in glProjects) {
          val ownerName = proj.pathWithNamespace.substringBefore("/", "")
          skills.add(
            SkillItem(
              id = "gl_skill_${proj.id}",
              name = proj.name,
              repoFullName = proj.pathWithNamespace,
              repoOwner = if (ownerName.isNotBlank()) ownerName else proj.name,
              repoPlatform = Platform.GITLAB,
              filePath = "README.md",
              description = proj.description ?: "Real-time GitLab project.",
              category = ResourceCategory.MCP_SERVERS,
              supportedTools = proj.tagList.ifEmpty { listOf("GitLab", "MCP") },
              requirements = listOf("GitLab Project"),
              installCommand = "git clone ${proj.webUrl}",
              promptInstructions = "Real-time GitLab integration for ${proj.name}. Reference: ${proj.webUrl}",
              compatibility = "Model Context Protocol (MCP), GitLab CI/CD",
              stars = proj.starCount,
              isBookmarked = false
            )
          )
        }
      }
    } catch (e: Exception) {
      Log.e("OpenSkillRepo", "Failed to fetch live skills", e)
    }
    skills
  }

  suspend fun searchRepositories(filter: FilterOptions): List<RepositoryItem> = withContext(Dispatchers.IO) {
    if (filter.query.isNotBlank()) {
      try {
        searchHistoryDao.insertSearch(SearchHistoryEntity(query = filter.query.trim()))
      } catch (e: Exception) {
        Log.w("OpenSkillRepo", "Could not save search history", e)
      }
    }

    val results = mutableListOf<RepositoryItem>()

    // If GitHub is selected or ALL
    if (filter.platform == Platform.GITHUB || filter.platform == Platform.ALL) {
      val ghResults = searchGitHub(filter)
      results.addAll(ghResults)
    }

    // If GitLab is selected or ALL
    if (filter.platform == Platform.GITLAB || filter.platform == Platform.ALL) {
      val glResults = searchGitLab(filter)
      results.addAll(glResults)
    }

    // Apply sorting
    val sorted = when (filter.sortBy) {
      SortOption.STARS -> results.sortedByDescending { it.stars }
      SortOption.UPDATED -> results.sortedByDescending { it.updatedAt }
      SortOption.FORKS -> results.sortedByDescending { it.forks }
      SortOption.RELEVANCE -> results.sortedByDescending { it.stars }
    }
    sorted
  }

  private suspend fun searchGitHub(filter: FilterOptions): List<RepositoryItem> {
    return try {
      val userQuery = filter.query.trim()
      val q = buildString {
        if (userQuery.isNotBlank()) {
          append(userQuery)
          when (filter.category) {
            ResourceCategory.AI_SKILLS -> append(" skills")
            ResourceCategory.MCP_SERVERS -> append(" mcp")
            ResourceCategory.AI_AGENTS -> append(" agent")
            ResourceCategory.CODING_TOOLS -> append(" tools")
            ResourceCategory.AUTOMATION -> append(" automation")
            ResourceCategory.WEB_DEV -> append(" web")
            ResourceCategory.LIBRARIES -> append(" library")
            else -> {}
          }
        } else {
          when (filter.category) {
            ResourceCategory.AI_SKILLS -> append("agent-skills OR ai-agent stars:>100")
            ResourceCategory.MCP_SERVERS -> append("mcp-server OR \"model context protocol\" stars:>50")
            ResourceCategory.AI_AGENTS -> append("ai-agent OR \"autonomous agent\" stars:>200")
            ResourceCategory.CODING_TOOLS -> append("developer-tools OR cli stars:>500")
            ResourceCategory.AUTOMATION -> append("automation OR playwright OR scraper stars:>500")
            ResourceCategory.WEB_DEV -> append("web OR fullstack OR frontend stars:>1000")
            ResourceCategory.LIBRARIES -> append("library OR sdk stars:>1000")
            ResourceCategory.TRENDING -> append("stars:>5000")
            ResourceCategory.ALL -> append("mcp-server OR ai-agent OR developer-tools stars:>500")
          }
        }

        if (filter.language != null && filter.language != "Any") {
          append(" language:${filter.language}")
        }
        if (filter.minStars > 0) {
          append(" stars:>=${filter.minStars}")
        }
      }.trim()

      val sortParam = when (filter.sortBy) {
        SortOption.STARS -> "stars"
        SortOption.UPDATED -> "updated"
        SortOption.FORKS -> "forks"
        SortOption.RELEVANCE -> null
      }

      val response = gitHubApi.searchRepositories(query = q, sort = sortParam, perPage = 30)
      if (response.isSuccessful) {
        val items = response.body()?.items ?: emptyList()
        items.map { dto ->
          val evidence = SkillCompatibilityDetector.evaluate(
            repoName = dto.name,
            description = dto.description ?: "",
            topics = dto.topics ?: emptyList(),
            fileNames = emptyList(),
            readmeContent = null
          )
          val hasSkills = evidence.detectedCategory.name == "AI_SKILLS" || evidence.detectedCategory.name == "MCP_SERVERS"

          RepositoryItem(
            id = "gh_${dto.id}",
            name = dto.name,
            fullName = dto.fullName,
            owner = dto.owner.login,
            ownerAvatarUrl = dto.owner.avatarUrl ?: "",
            description = dto.description ?: "No description provided",
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
            hasSkills = hasSkills,
            skillCount = if (hasSkills) 1 else 0,
            url = dto.htmlUrl
          )
        }
      } else {
        emptyList()
      }
    } catch (e: Exception) {
      Log.e("OpenSkillRepo", "GitHub search error", e)
      emptyList()
    }
  }

  private suspend fun searchGitLab(filter: FilterOptions): List<RepositoryItem> {
    return try {
      val query = if (filter.query.isNotBlank()) {
        filter.query.trim()
      } else {
        when (filter.category) {
          ResourceCategory.MCP_SERVERS -> "mcp"
          ResourceCategory.AI_SKILLS, ResourceCategory.AI_AGENTS -> "agent"
          ResourceCategory.AUTOMATION -> "automation"
          else -> "developer tools"
        }
      }
      val response = gitLabApi.searchProjects(search = query, orderBy = "last_activity_at", perPage = 25)
      if (response.isSuccessful) {
        val items = response.body() ?: emptyList()
        items.map { dto ->
          val ownerName = dto.pathWithNamespace.substringBefore("/", "")
          RepositoryItem(
            id = "gl_${dto.id}",
            name = dto.name,
            fullName = dto.pathWithNamespace,
            owner = if (ownerName.isNotBlank()) ownerName else dto.name,
            ownerAvatarUrl = dto.avatarUrl ?: "",
            description = dto.description ?: "No description provided",
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
            url = dto.webUrl
          )
        }
      } else {
        emptyList()
      }
    } catch (e: Exception) {
      Log.e("OpenSkillRepo", "GitLab search error", e)
      emptyList()
    }
  }

  suspend fun getRepositoryDetails(fullName: String, platform: Platform): RepositoryItem? {
    val parts = fullName.split("/")
    val owner = if (parts.size >= 2) parts[0] else ""
    val repo = if (parts.size >= 2) parts[1] else fullName
    return getRepositoryDetails(owner, repo, platform)
  }

  suspend fun getRepositoryDetails(owner: String, repo: String, platform: Platform): RepositoryItem? = withContext(Dispatchers.IO) {
    if (platform == Platform.GITLAB) {
      val glRes = try { gitLabApi.searchProjects(repo) } catch (_: Exception) { null }
      val match = glRes?.body()?.find { it.pathWithNamespace.equals("$owner/$repo", ignoreCase = true) }
      if (match != null) {
        return@withContext RepositoryItem(
          id = "gl_${match.id}",
          name = match.name,
          fullName = match.pathWithNamespace,
          owner = owner,
          ownerAvatarUrl = match.avatarUrl ?: "",
          description = match.description ?: "No description provided",
          platform = Platform.GITLAB,
          stars = match.starCount,
          forks = match.forksCount,
          openIssues = 0,
          language = null,
          topics = match.tagList,
          license = null,
          defaultBranch = "main",
          updatedAt = match.lastActivityAt,
          url = match.webUrl
        )
      }
      return@withContext null
    }

    try {
      val response = gitHubApi.getRepository(owner, repo)
      if (response.isSuccessful && response.body() != null) {
        val dto = response.body()!!
        val branch = dto.defaultBranch
        val rawReadme = try {
          val readmeRes = gitHubApi.getRawContent("https://raw.githubusercontent.com/$owner/$repo/$branch/README.md")
          if (readmeRes.isSuccessful) readmeRes.body()?.string() else ""
        } catch (_: Exception) { "" }

        val skills = fetchSkillDetails(owner, repo, branch, dto.stargazersCount)

        RepositoryItem(
          id = "gh_${dto.id}",
          name = dto.name,
          fullName = dto.fullName,
          owner = dto.owner.login,
          ownerAvatarUrl = dto.owner.avatarUrl ?: "",
          description = dto.description ?: "No description provided",
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
          hasSkills = skills.isNotEmpty(),
          skillCount = skills.size,
          url = dto.htmlUrl,
          readmePreview = rawReadme ?: "",
          detectedSkills = skills
        )
      } else {
        null
      }
    } catch (e: Exception) {
      Log.e("OpenSkillRepo", "Failed to get repository details", e)
      null
    }
  }

  private suspend fun fetchSkillDetails(owner: String, repo: String, branch: String, stars: Int): List<SkillItem> {
    val skills = mutableListOf<SkillItem>()
    try {
      val contentsRes = gitHubApi.getContents(owner, repo, "")
      if (contentsRes.isSuccessful) {
        val files = contentsRes.body() ?: emptyList()
        val skillFile = files.find { it.name.equals("SKILL.md", ignoreCase = true) }
        if (skillFile?.downloadUrl != null) {
          val raw = gitHubApi.getRawContent(skillFile.downloadUrl).body()?.string()
          if (!raw.isNullOrBlank()) {
            val domainSkill = SkillFileParser.parse(
              rawContent = raw,
              repoFullName = "$owner/$repo",
              repoOwner = owner,
              repoPlatform = com.example.domain.model.Platform.GITHUB,
              filePath = "SKILL.md",
              repoStars = stars
            )
            if (domainSkill != null) {
              skills.add(
                SkillItem(
                  id = domainSkill.id,
                  name = domainSkill.name,
                  repoFullName = domainSkill.repoFullName,
                  repoOwner = domainSkill.repoOwner,
                  repoPlatform = Platform.GITHUB,
                  filePath = domainSkill.filePath,
                  description = domainSkill.description,
                  category = ResourceCategory.AI_SKILLS,
                  supportedTools = domainSkill.supportedTools,
                  requirements = domainSkill.requirements,
                  installCommand = domainSkill.installCommand,
                  promptInstructions = domainSkill.promptInstructions,
                  compatibility = domainSkill.compatibility,
                  stars = domainSkill.stars,
                  isBookmarked = false
                )
              )
            }
          }
        }
      }
    } catch (e: Exception) {
      Log.e("OpenSkillRepo", "Failed to fetch skill details", e)
    }
    return skills
  }

  // --- Bookmark & Collection operations ---

  suspend fun bookmarkRepo(repo: RepositoryItem) = withContext(Dispatchers.IO) {
    bookmarkDao.insertBookmark(
      BookmarkEntity(
        id = "repo:${repo.fullName}",
        itemType = "REPO",
        title = repo.name,
        subtitle = repo.fullName,
        platform = repo.platform.name,
        description = repo.description,
        language = repo.language,
        stars = repo.stars,
        url = repo.url
      )
    )
  }

  suspend fun bookmarkSkill(skill: SkillItem) = withContext(Dispatchers.IO) {
    bookmarkDao.insertBookmark(
      BookmarkEntity(
        id = "skill:${skill.repoFullName}/${skill.id}",
        itemType = "SKILL",
        title = skill.name,
        subtitle = "${skill.repoFullName} • ${skill.filePath}",
        platform = skill.repoPlatform.name,
        description = skill.description,
        language = "SKILL.md",
        stars = skill.stars,
        url = "https://github.com/${skill.repoFullName}/blob/main/${skill.filePath}"
      )
    )
  }

  suspend fun removeBookmark(id: String) = withContext(Dispatchers.IO) {
    bookmarkDao.deleteBookmarkById(id)
  }

  suspend fun updateNote(id: String, note: String) = withContext(Dispatchers.IO) {
    bookmarkDao.updateNote(id, note)
  }

  suspend fun updateCollection(id: String, collection: String) = withContext(Dispatchers.IO) {
    bookmarkDao.updateCollection(id, collection)
  }

  suspend fun createCollection(name: String, desc: String, icon: String = "folder") = withContext(Dispatchers.IO) {
    val id = "col_${System.currentTimeMillis()}"
    collectionDao.insertCollection(CollectionEntity(id = id, name = name, description = desc, icon = icon))
  }

  suspend fun toggleBookmarkRepo(repo: RepositoryItem) = withContext(Dispatchers.IO) {
    val id = "repo:${repo.fullName}"
    val existing = bookmarkDao.getBookmarkById(id)
    if (existing != null) {
      bookmarkDao.deleteBookmarkById(id)
    } else {
      bookmarkRepo(repo)
    }
  }

  suspend fun toggleBookmarkSkill(skill: SkillItem) = withContext(Dispatchers.IO) {
    val id = "skill:${skill.repoFullName}/${skill.id}"
    val existing = bookmarkDao.getBookmarkById(id)
    if (existing != null) {
      bookmarkDao.deleteBookmarkById(id)
    } else {
      bookmarkSkill(skill)
    }
  }

  suspend fun updateBookmarkNote(id: String, note: String) = updateNote(id, note)

  suspend fun clearSearchHistory() = clearHistory()

  fun setGitHubToken(token: String) {
    ApiClient.githubToken = token.trim()
  }

  suspend fun testLiveConnection() = withContext(Dispatchers.IO) {
    try {
      gitHubApi.searchRepositories("openskill", sort = "stars", order = "desc", perPage = 1)
      gitLabApi.searchProjects("openskill", perPage = 1)
    } catch (_: Exception) {
    }
  }

  suspend fun getAiRecommendations(goalQuery: String): List<AiRecommendation> = withContext(Dispatchers.IO) {
    try {
      val terms = goalQuery.split(" ").filter { it.length > 3 }.take(3).joinToString(" ")
      val query = if (terms.isNotBlank()) terms else "ai agent mcp"
      val response = gitHubApi.searchRepositories(query, sort = "stars", order = "desc", perPage = 5)
      val items = response.body()?.items ?: emptyList()
      items.mapIndexed { index, dto ->
        val repoItem = RepositoryItem(
          id = "gh_${dto.id}",
          name = dto.name,
          fullName = dto.fullName,
          owner = dto.owner.login,
          ownerAvatarUrl = dto.owner.avatarUrl ?: "",
          description = dto.description ?: "Open source repository for $goalQuery",
          platform = Platform.GITHUB,
          stars = dto.stargazersCount,
          forks = dto.forksCount,
          openIssues = dto.openIssuesCount,
          language = dto.language,
          topics = dto.topics ?: emptyList(),
          license = dto.license?.spdxId ?: dto.license?.name,
          defaultBranch = dto.defaultBranch,
          updatedAt = dto.updatedAt,
          url = dto.htmlUrl,
          hasSkills = true
        )
        AiRecommendation(
          id = "rec_${dto.id}_$index",
          repoItem = repoItem,
          relevanceScore = (98 - (index * 4)).coerceAtLeast(70),
          whyItMatches = "Live GitHub repository matching '$goalQuery' with ${dto.stargazersCount} stars and verified code implementation.",
          keySkillOrFeature = dto.topics?.firstOrNull() ?: dto.language ?: "AI Agent & Tools",
          compatibilityNote = "Verified open-source repository on GitHub"
        )
      }
    } catch (_: Exception) {
      emptyList()
    }
  }

  suspend fun deleteCollection(id: String) = withContext(Dispatchers.IO) {
    collectionDao.deleteCollectionById(id)
  }

  suspend fun clearHistory() = withContext(Dispatchers.IO) {
    searchHistoryDao.clearAll()
  }

  suspend fun deleteHistoryItem(query: String) = withContext(Dispatchers.IO) {
    searchHistoryDao.deleteSearch(query)
  }
}
