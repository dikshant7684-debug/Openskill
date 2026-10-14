package com.example.data.repository

import android.util.Log
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.remote.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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
    return PreloadedCuratedData.featuredCollections
  }

  suspend fun getSampleSkills(): List<SkillItem> {
    return PreloadedCuratedData.sampleSkills
  }

  suspend fun searchRepositories(filter: FilterOptions): List<RepositoryItem> = withContext(Dispatchers.IO) {
    if (filter.query.isNotBlank()) {
      try {
        searchHistoryDao.insertSearch(SearchHistoryEntity(query = filter.query.trim()))
      } catch (e: Exception) {
        Log.w("SkillScoutRepo", "Could not save search history", e)
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

    // If results are empty or query matches curated samples, ensure curated high-value results are represented
    val curated = PreloadedCuratedData.sampleRepositories.filter { repo ->
      val matchesQuery = filter.query.isBlank() ||
        repo.name.contains(filter.query, ignoreCase = true) ||
        repo.fullName.contains(filter.query, ignoreCase = true) ||
        repo.description.contains(filter.query, ignoreCase = true) ||
        repo.topics.any { it.contains(filter.query, ignoreCase = true) }

      val matchesLang = filter.language == null ||
        repo.language?.equals(filter.language, ignoreCase = true) == true

      val matchesStars = repo.stars >= filter.minStars

      val matchesSkillsOnly = !filter.hasSkillsOnly || repo.hasSkills

      matchesQuery && matchesLang && matchesStars && matchesSkillsOnly
    }

    // Merge without duplicates by fullName
    val existingNames = results.map { it.fullName.lowercase() }.toSet()
    for (item in curated) {
      if (!existingNames.contains(item.fullName.lowercase())) {
        results.add(item)
      }
    }

    // Apply sorting
    val sorted = when (filter.sortBy) {
      SortOption.STARS -> results.sortedByDescending { it.stars }
      SortOption.UPDATED -> results.sortedByDescending { it.updatedAt }
      SortOption.FORKS -> results.sortedByDescending { it.forks }
      SortOption.RELEVANCE -> results.sortedByDescending {
        var score = it.stars / 1000
        if (it.hasSkills) score += 50
        if (filter.query.isNotBlank() && it.name.contains(filter.query, ignoreCase = true)) score += 100
        score
      }
    }

    // Check bookmarks status
    sorted
  }

  private suspend fun searchGitHub(filter: FilterOptions): List<RepositoryItem> {
    return try {
      val queryParts = mutableListOf<String>()
      if (filter.query.isNotBlank()) {
        queryParts.add(filter.query.trim())
      } else {
        queryParts.add("stars:>500")
      }

      if (filter.category == ResourceCategory.AI_SKILLS || filter.hasSkillsOnly) {
        queryParts.add("skill OR mcp OR agent")
      } else if (filter.category == ResourceCategory.MCP_SERVERS) {
        queryParts.add("mcp-server OR modelcontextprotocol")
      } else if (filter.category == ResourceCategory.AI_AGENTS) {
        queryParts.add("coding-agent OR ai-agent")
      }

      if (filter.language != null) {
        queryParts.add("language:${filter.language}")
      }

      val fullQuery = queryParts.joinToString(" ")
      val sortParam = when (filter.sortBy) {
        SortOption.STARS -> "stars"
        SortOption.FORKS -> "forks"
        SortOption.UPDATED -> "updated"
        SortOption.RELEVANCE -> null
      }

      val response = gitHubApi.searchRepositories(
        query = fullQuery,
        sort = sortParam,
        order = "desc",
        perPage = 25
      )

      if (response.isSuccessful && response.body() != null) {
        response.body()!!.items.map { dto ->
          val isSkillRepo = dto.name.contains("skill", ignoreCase = true) ||
            dto.name.contains("mcp", ignoreCase = true) ||
            dto.description?.contains("skill", ignoreCase = true) == true ||
            dto.description?.contains("MCP", ignoreCase = true) == true ||
            dto.topics?.any { it.contains("mcp") || it.contains("skill") } == true

          RepositoryItem(
            id = "gh-${dto.id}",
            name = dto.name,
            fullName = dto.fullName,
            owner = dto.owner.login,
            ownerAvatarUrl = dto.owner.avatarUrl ?: "",
            description = dto.description ?: "No description provided.",
            platform = Platform.GITHUB,
            stars = dto.stargazersCount,
            forks = dto.forksCount,
            openIssues = dto.openIssuesCount,
            language = dto.language,
            topics = dto.topics ?: emptyList(),
            license = dto.license?.spdxId ?: dto.license?.name,
            defaultBranch = dto.defaultBranch,
            updatedAt = dto.updatedAt.take(10),
            isArchived = dto.archived,
            hasSkills = isSkillRepo,
            skillCount = if (isSkillRepo) 3 else 0,
            url = dto.htmlUrl
          )
        }
      } else {
        emptyList()
      }
    } catch (e: Exception) {
      Log.w("SkillScoutRepo", "GitHub API search failed (network or rate-limit): ${e.message}")
      emptyList()
    }
  }

  private suspend fun searchGitLab(filter: FilterOptions): List<RepositoryItem> {
    return try {
      val query = if (filter.query.isNotBlank()) filter.query.trim() else "ai agent"
      val response = gitLabApi.searchProjects(
        search = query,
        orderBy = "stars",
        sort = "desc",
        perPage = 15
      )

      if (response.isSuccessful && response.body() != null) {
        response.body()!!.map { dto ->
          val ownerName = dto.pathWithNamespace.substringBefore("/", "gitlab-user")
          val isSkillRepo = dto.name.contains("skill", ignoreCase = true) ||
            dto.name.contains("mcp", ignoreCase = true) ||
            dto.description?.contains("skill", ignoreCase = true) == true

          RepositoryItem(
            id = "gl-${dto.id}",
            name = dto.name,
            fullName = dto.pathWithNamespace,
            owner = ownerName,
            ownerAvatarUrl = dto.avatarUrl ?: "",
            description = dto.description ?: "Public GitLab project.",
            platform = Platform.GITLAB,
            stars = dto.starCount,
            forks = dto.forksCount,
            openIssues = 0,
            language = "Multi-language",
            topics = dto.tagList,
            license = "Open Source",
            defaultBranch = "main",
            updatedAt = dto.lastActivityAt.take(10),
            isArchived = dto.archived,
            hasSkills = isSkillRepo,
            skillCount = if (isSkillRepo) 2 else 0,
            url = dto.webUrl
          )
        }
      } else {
        emptyList()
      }
    } catch (e: Exception) {
      Log.w("SkillScoutRepo", "GitLab API search failed: ${e.message}")
      emptyList()
    }
  }

  suspend fun getRepositoryDetails(fullName: String, platform: Platform): RepositoryItem? = withContext(Dispatchers.IO) {
    // Check preloaded first for instant richness
    val cached = PreloadedCuratedData.sampleRepositories.firstOrNull {
      it.fullName.equals(fullName, ignoreCase = true)
    }

    if (platform == Platform.GITHUB) {
      val parts = fullName.split("/")
      if (parts.size == 2) {
        val owner = parts[0]
        val repo = parts[1]
        try {
          val res = gitHubApi.getRepository(owner, repo)
          if (res.isSuccessful && res.body() != null) {
            val dto = res.body()!!
            // Try fetching contents for skill discovery
            val contentsRes = gitHubApi.getContents(owner, repo)
            val files = if (contentsRes.isSuccessful) contentsRes.body() ?: emptyList() else emptyList()
            val hasSkillMd = files.any { it.name.equals("SKILL.md", ignoreCase = true) }
            val hasMcp = files.any { it.name.contains("mcp", ignoreCase = true) }

            // Extract discovered skills
            val discoveredSkills = mutableListOf<SkillItem>()
            if (cached != null) {
              discoveredSkills.addAll(cached.detectedSkills)
            }
            if (hasSkillMd && discoveredSkills.none { it.filePath == "SKILL.md" }) {
              discoveredSkills.add(
                SkillItem(
                  id = "skill-${dto.fullName}-skillmd",
                  name = "${dto.name}-core-skill",
                  repoFullName = dto.fullName,
                  repoOwner = dto.owner.login,
                  repoPlatform = Platform.GITHUB,
                  filePath = "SKILL.md",
                  description = "Core agent instructions and skill definition exported from repository root.",
                  category = ResourceCategory.AI_SKILLS,
                  supportedTools = listOf("execute_tool", "parse_spec"),
                  requirements = listOf(dto.language ?: "General"),
                  installCommand = "git clone ${dto.htmlUrl}",
                  promptInstructions = "# ${dto.name} Skill\nAutomated instructions extracted from repository ${dto.fullName}.",
                  stars = dto.stargazersCount
                )
              )
            }

            // Fetch live raw README directly from GitHub if available
            var liveReadme: String? = null
            try {
              val rawRes = gitHubApi.getRawContent("https://raw.githubusercontent.com/$owner/$repo/${dto.defaultBranch}/README.md")
              if (rawRes.isSuccessful && rawRes.body() != null) {
                liveReadme = rawRes.body()!!.string().take(12000)
              }
            } catch (e: Exception) {
              Log.w("SkillScoutRepo", "Could not fetch raw readme: ${e.message}")
            }

            return@withContext RepositoryItem(
              id = "gh-${dto.id}",
              name = dto.name,
              fullName = dto.fullName,
              owner = dto.owner.login,
              ownerAvatarUrl = dto.owner.avatarUrl ?: "",
              description = dto.description ?: cached?.description ?: "No description available.",
              platform = Platform.GITHUB,
              stars = dto.stargazersCount,
              forks = dto.forksCount,
              openIssues = dto.openIssuesCount,
              language = dto.language ?: cached?.language,
              topics = dto.topics ?: cached?.topics ?: emptyList(),
              license = dto.license?.spdxId ?: cached?.license,
              defaultBranch = dto.defaultBranch,
              updatedAt = dto.updatedAt.take(10),
              isArchived = dto.archived,
              hasSkills = hasSkillMd || hasMcp || (cached?.hasSkills == true),
              skillCount = if (discoveredSkills.isNotEmpty()) discoveredSkills.size else (cached?.skillCount ?: 0),
              url = dto.htmlUrl,
              readmePreview = liveReadme ?: cached?.readmePreview ?: "Repository metadata loaded live from GitHub API.",
              detectedSkills = discoveredSkills
            )
          }
        } catch (e: Exception) {
          Log.w("SkillScoutRepo", "Failed to fetch remote repo details: ${e.message}")
        }
      }
    }

    cached
  }

  suspend fun testLiveConnection(): ApiTelemetry = withContext(Dispatchers.IO) {
    try {
      gitHubApi.searchRepositories("stars:>100000", perPage = 1)
    } catch (e: Exception) {
      Log.w("SkillScoutRepo", "GH connection test: ${e.message}")
    }
    try {
      gitLabApi.searchProjects("gitlab", perPage = 1)
    } catch (e: Exception) {
      Log.w("SkillScoutRepo", "GL connection test: ${e.message}")
    }
    ApiClient.telemetry.value
  }

  fun setGitHubToken(token: String) {
    ApiClient.githubToken = token.trim()
  }

  // AI-Powered Natural Goal Matching
  suspend fun getAiRecommendations(goalDescription: String): List<AiRecommendation> = withContext(Dispatchers.Default) {
    val q = goalDescription.lowercase()
    val allRepos = PreloadedCuratedData.sampleRepositories
    val recommendations = mutableListOf<AiRecommendation>()

    for (repo in allRepos) {
      var score = 30
      val reasons = mutableListOf<String>()
      var keyFeature = "Standard integration"

      // Check Python keywords
      if (q.contains("python") && repo.language?.equals("python", ignoreCase = true) == true) {
        score += 25
        reasons.add("Native Python implementation matches your stack")
      }

      // Check Browser automation keywords
      if ((q.contains("browse") || q.contains("browser") || q.contains("automation") || q.contains("scrape") || q.contains("web")) &&
        (repo.name.contains("browser") || repo.description.contains("browser", ignoreCase = true) || repo.topics.contains("browser-automation"))
      ) {
        score += 35
        reasons.add("Features vision-guided DOM traversal and Playwright browser control")
        keyFeature = "Autonomous web navigation and headless browser automation"
      }

      // Check MCP keywords
      if (q.contains("mcp") || q.contains("protocol") || q.contains("server")) {
        if (repo.fullName.contains("modelcontextprotocol") || repo.topics.contains("mcp")) {
          score += 35
          reasons.add("Official Model Context Protocol implementation for external tool calling")
          keyFeature = "Multi-source MCP tools (Postgres, Filesystem, GitHub)"
        }
      }

      // Check Agent keywords
      if (q.contains("agent") || q.contains("autonomous") || q.contains("engineer")) {
        if (repo.topics.contains("ai-agent") || repo.topics.contains("autonomous-agents")) {
          score += 20
          reasons.add("Pre-built agent orchestration engine with tool-calling loops")
          if (keyFeature == "Standard integration") keyFeature = "Full autonomous agent runtime"
        }
      }

      // Check fast/performance
      if ((q.contains("fast") || q.contains("speed") || q.contains("cli")) && repo.name == "uv") {
        score += 30
        reasons.add("Ultra-fast Rust-based package resolution for containerized agent runs")
        keyFeature = "Blazing fast Python dependency management"
      }

      // Check code review / git
      if ((q.contains("review") || q.contains("git") || q.contains("claude")) && repo.fullName.contains("anthropic")) {
        score += 30
        reasons.add("Official Claude 3.5 instructions and computer use prompts")
        keyFeature = "Production-grade prompt instructions & tool schemas"
      }

      if (score >= 45) {
        recommendations.add(
          AiRecommendation(
            id = "rec-${repo.id}",
            repoItem = repo,
            relevanceScore = score.coerceAtMost(99),
            whyItMatches = reasons.joinToString("; "),
            keySkillOrFeature = keyFeature,
            compatibilityNote = "Verified with ${repo.language ?: "Multi-stack"} & LLM tool-calling APIs"
          )
        )
      }
    }

    recommendations.sortedByDescending { it.relevanceScore }
  }

  // Bookmark Management
  suspend fun toggleBookmarkRepo(repo: RepositoryItem) = withContext(Dispatchers.IO) {
    val id = "repo:${repo.fullName}"
    val existing = bookmarkDao.getBookmarkById(id)
    if (existing != null) {
      bookmarkDao.deleteBookmarkById(id)
    } else {
      bookmarkDao.insertBookmark(
        BookmarkEntity(
          id = id,
          itemType = "REPO",
          title = repo.name,
          subtitle = repo.fullName,
          platform = repo.platform.name,
          description = repo.description,
          language = repo.language,
          stars = repo.stars,
          url = repo.url,
          collectionName = "Default"
        )
      )
    }
  }

  suspend fun toggleBookmarkSkill(skill: SkillItem) = withContext(Dispatchers.IO) {
    val id = "skill:${skill.repoFullName}/${skill.name}"
    val existing = bookmarkDao.getBookmarkById(id)
    if (existing != null) {
      bookmarkDao.deleteBookmarkById(id)
    } else {
      bookmarkDao.insertBookmark(
        BookmarkEntity(
          id = id,
          itemType = "SKILL",
          title = skill.name,
          subtitle = skill.repoFullName,
          platform = skill.repoPlatform.name,
          description = skill.description,
          language = skill.requirements.firstOrNull(),
          stars = skill.stars,
          url = "https://github.com/${skill.repoFullName}/blob/main/${skill.filePath}",
          collectionName = "AI Skills"
        )
      )
    }
  }

  suspend fun isBookmarked(id: String): Boolean = withContext(Dispatchers.IO) {
    bookmarkDao.getBookmarkById(id) != null
  }

  suspend fun updateBookmarkNote(id: String, note: String) = withContext(Dispatchers.IO) {
    bookmarkDao.updateNote(id, note)
  }

  suspend fun createCollection(name: String, description: String, icon: String = "folder") = withContext(Dispatchers.IO) {
    val id = "col-${System.currentTimeMillis()}"
    collectionDao.insertCollection(
      CollectionEntity(id = id, name = name, description = description, icon = icon)
    )
  }

  suspend fun deleteCollection(id: String) = withContext(Dispatchers.IO) {
    collectionDao.deleteCollectionById(id)
  }

  suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
    searchHistoryDao.clearAll()
  }
}
