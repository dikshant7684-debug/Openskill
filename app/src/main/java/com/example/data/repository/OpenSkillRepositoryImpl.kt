package com.example.data.repository

import com.example.data.local.dao.BookmarkDao
import com.example.data.local.dao.CollectionDao
import com.example.data.local.dao.SearchHistoryDao
import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.CollectionEntity
import com.example.data.local.entity.SearchHistoryEntity
import com.example.data.mapper.BookmarkMapper
import com.example.data.mapper.RepositoryMapper
import com.example.data.remote.github.GitHubDataSource
import com.example.data.remote.gitlab.GitLabDataSource
import com.example.domain.model.*
import com.example.domain.model.Collection
import com.example.domain.repository.BookmarkRepository
import com.example.domain.repository.CodeRepository
import com.example.domain.repository.SkillRepository
import com.example.lib.network.NetworkResult
import com.example.skills.SkillCompatibilityDetector
import com.example.skills.SkillDiscoveryEngine
import com.example.skills.SkillFileParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OpenSkillRepositoryImpl(
  private val gitHubDataSource: GitHubDataSource,
  private val gitLabDataSource: GitLabDataSource,
  private val bookmarkDao: BookmarkDao,
  private val collectionDao: CollectionDao,
  private val searchHistoryDao: SearchHistoryDao
) : CodeRepository, SkillRepository, BookmarkRepository {

  private val discoveryEngine = SkillDiscoveryEngine { url ->
    gitHubDataSource.getRawContent(url)
  }

  // --- CodeRepository Implementation (REAL DATA ONLY) ---

  override suspend fun searchRepositories(filters: SearchFilters): List<Repository> = withContext(Dispatchers.IO) {
    val results = mutableListOf<Repository>()

    // Construct genuine query string based on user intent and category
    val queryBuilder = StringBuilder()
    if (filters.query.isNotBlank()) {
      queryBuilder.append(filters.query.trim())
    }

    when (filters.category) {
      ResourceCategory.AI_SKILLS -> {
        if (queryBuilder.isNotEmpty()) queryBuilder.append(" ")
        queryBuilder.append("SKILL.md OR agent-skills OR claude-skill")
      }
      ResourceCategory.MCP_SERVERS -> {
        if (queryBuilder.isNotEmpty()) queryBuilder.append(" ")
        queryBuilder.append("topic:mcp-server OR mcp.json OR \"model context protocol\"")
      }
      ResourceCategory.AI_AGENTS -> {
        if (queryBuilder.isNotEmpty()) queryBuilder.append(" ")
        queryBuilder.append("topic:ai-agent OR topic:autonomous-agents OR \"coding agent\"")
      }
      ResourceCategory.CODING_TOOLS -> {
        if (queryBuilder.isNotEmpty()) queryBuilder.append(" ")
        queryBuilder.append("topic:developer-tools OR topic:cli-tool")
      }
      ResourceCategory.AUTOMATION -> {
        if (queryBuilder.isNotEmpty()) queryBuilder.append(" ")
        queryBuilder.append("topic:automation OR topic:browser-automation")
      }
      ResourceCategory.WEB_DEV -> {
        if (queryBuilder.isNotEmpty()) queryBuilder.append(" ")
        queryBuilder.append("topic:web OR topic:frontend OR topic:fullstack")
      }
      ResourceCategory.LIBRARIES -> {
        if (queryBuilder.isNotEmpty()) queryBuilder.append(" ")
        queryBuilder.append("topic:library OR topic:sdk")
      }
      ResourceCategory.TRENDING -> {
        if (queryBuilder.isNotEmpty()) queryBuilder.append(" ")
        queryBuilder.append("stars:>500")
      }
      ResourceCategory.ALL -> {
        if (queryBuilder.isEmpty()) {
          queryBuilder.append("topic:developer-tools OR topic:mcp OR SKILL.md")
        }
      }
    }

    if (filters.language != null) {
      queryBuilder.append(" language:\"${filters.language}\"")
    }
    if (filters.minStars > 0) {
      queryBuilder.append(" stars:>=${filters.minStars}")
    }

    val finalQuery = queryBuilder.toString().trim()
    val ghSort = when (filters.sortBy) {
      SortOption.STARS -> "stars"
      SortOption.UPDATED -> "updated"
      SortOption.FORKS -> "forks"
      SortOption.RELEVANCE -> null
    }

    // Query GitHub
    if (filters.platform == Platform.GITHUB || filters.platform == Platform.ALL) {
      when (val ghRes = gitHubDataSource.searchRepositories(finalQuery, sort = ghSort, perPage = 30)) {
        is NetworkResult.Success -> {
          results.addAll(ghRes.data.map { RepositoryMapper.fromGitHubDto(it) })
        }
        is NetworkResult.Error -> {
          // Log or preserve error; do NOT inject mock data
        }
        else -> Unit
      }
    }

    // Query GitLab
    if (filters.platform == Platform.GITLAB || filters.platform == Platform.ALL) {
      val glSearch = if (filters.query.isNotBlank()) filters.query.trim() else "developer tools"
      val glOrder = if (filters.sortBy == SortOption.UPDATED) "updated_at" else "stars"
      when (val glRes = gitLabDataSource.searchProjects(glSearch, orderBy = glOrder, perPage = 20)) {
        is NetworkResult.Success -> {
          results.addAll(glRes.data.map { RepositoryMapper.fromGitLabDto(it) })
        }
        is NetworkResult.Error -> {
          // Log error; do NOT inject mock data
        }
        else -> Unit
      }
    }

    // Sort results
    when (filters.sortBy) {
      SortOption.STARS -> results.sortedByDescending { it.stars }
      SortOption.UPDATED -> results.sortedByDescending { it.updatedAt }
      SortOption.FORKS -> results.sortedByDescending { it.forks }
      SortOption.RELEVANCE -> results.sortedByDescending { it.stars }
    }
  }

  override suspend fun getRepositoryDetails(owner: String, name: String, platform: String): Repository? = withContext(Dispatchers.IO) {
    if (platform.equals("GitLab", ignoreCase = true)) {
      val glRes = gitLabDataSource.searchProjects(name)
      if (glRes is NetworkResult.Success) {
        val match = glRes.data.find { it.pathWithNamespace.equals("$owner/$name", ignoreCase = true) }
        if (match != null) return@withContext RepositoryMapper.fromGitLabDto(match)
      }
      return@withContext null
    }

    // Default GitHub
    when (val ghRes = gitHubDataSource.getRepository(owner, name)) {
      is NetworkResult.Success -> {
        val repo = RepositoryMapper.fromGitHubDto(ghRes.data)
        // Check for README
        val rawReadme = gitHubDataSource.getRawContent("https://raw.githubusercontent.com/$owner/$name/${repo.defaultBranch}/README.md")
        repo.copy(readmePreview = rawReadme ?: "")
      }
      else -> null
    }
  }

  override suspend fun getRepositoryFiles(owner: String, name: String, path: String): List<RepositoryFile> = withContext(Dispatchers.IO) {
    when (val res = gitHubDataSource.getContents(owner, name, path)) {
      is NetworkResult.Success -> res.data.map { RepositoryMapper.fromGitHubContent(it) }
      else -> emptyList()
    }
  }

  override suspend fun getRawFileContent(downloadUrl: String): String? = withContext(Dispatchers.IO) {
    gitHubDataSource.getRawContent(downloadUrl)
  }

  override suspend fun getFeaturedCollections(): List<FeaturedCollection> {
    // Honest category queries targeting real topics
    return listOf(
      FeaturedCollection("mcp", "Model Context Protocol", "Live MCP servers for AI agents & IDEs", "cpu", "topic:mcp-server OR mcp.json", "MCP", 0),
      FeaturedCollection("skills", "AI Agent Skills", "Real SKILL.md specs & agent prompt instructions", "sparkles", "SKILL.md in:path OR topic:agent-skills", "AI Skills", 0),
      FeaturedCollection("agents", "Autonomous Agents", "End-to-end coding agents and autonomous workflows", "terminal", "topic:ai-agent OR topic:coding-agent", "Agents", 0),
      FeaturedCollection("tools", "Developer Utilities", "CLI tools, linters, compilers and test engines", "code", "topic:developer-tools OR topic:cli", "Tools", 0),
      FeaturedCollection("trending", "Popular Repositories", "Top starred open-source projects", "flame", "stars:>5000", "Popular", 0)
    )
  }

  // --- SkillRepository Implementation (REAL DISCOVERY ONLY) ---

  override suspend fun getCuratedSkills(): List<Skill> = withContext(Dispatchers.IO) {
    // Search live GitHub for repos containing SKILL.md or topic agent-skills
    val skills = mutableListOf<Skill>()
    when (val res = gitHubDataSource.searchRepositories("filename:SKILL.md OR topic:agent-skills", sort = "stars", perPage = 15)) {
      is NetworkResult.Success -> {
        for (repoDto in res.data) {
          val owner = repoDto.owner.login
          val name = repoDto.name
          val branch = repoDto.defaultBranch
          val rawUrl = "https://raw.githubusercontent.com/$owner/$name/$branch/SKILL.md"
          val content = gitHubDataSource.getRawContent(rawUrl)
          if (!content.isNullOrBlank()) {
            val parsed = SkillFileParser.parse(
              rawContent = content,
              repoFullName = repoDto.fullName,
              repoOwner = owner,
              repoPlatform = Platform.GITHUB,
              filePath = "SKILL.md",
              repoStars = repoDto.stargazersCount
            )
            if (parsed != null) skills.add(parsed)
          }
        }
      }
      else -> Unit
    }
    skills
  }

  override suspend fun discoverSkillsInRepository(repository: Repository): List<Skill> = withContext(Dispatchers.IO) {
    val files = getRepositoryFiles(repository.owner, repository.name)
    discoveryEngine.discoverInRepository(repository, files)
  }

  override suspend fun parseSkillFile(rawContent: String, repoFullName: String, repoOwner: String): Skill? {
    return SkillFileParser.parse(
      rawContent = rawContent,
      repoFullName = repoFullName,
      repoOwner = repoOwner,
      repoPlatform = Platform.GITHUB
    )
  }

  // --- BookmarkRepository Implementation (ROOM PERSISTENCE) ---

  override fun getAllBookmarks(): Flow<List<Bookmark>> {
    return bookmarkDao.getAllBookmarks().map { list ->
      list.map { BookmarkMapper.toDomain(it) }
    }
  }

  override fun getAllCollections(): Flow<List<Collection>> {
    return collectionDao.getAllCollections().map { list ->
      list.map { BookmarkMapper.collectionToDomain(it) }
    }
  }

  override fun getRecentSearches(): Flow<List<String>> {
    return searchHistoryDao.getRecentSearches().map { list ->
      list.map { it.query }
    }
  }

  override suspend fun bookmarkRepository(repository: Repository, collectionName: String) = withContext(Dispatchers.IO) {
    bookmarkDao.insertBookmark(
      BookmarkEntity(
        id = "repo:${repository.fullName}",
        itemType = "REPO",
        title = repository.name,
        subtitle = repository.fullName,
        platform = repository.platform.name,
        description = repository.description,
        language = repository.language,
        stars = repository.stars,
        url = repository.url,
        collectionName = collectionName
      )
    )
  }

  override suspend fun bookmarkSkill(skill: Skill, collectionName: String) = withContext(Dispatchers.IO) {
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
        url = "https://github.com/${skill.repoFullName}/blob/main/${skill.filePath}",
        collectionName = collectionName
      )
    )
  }

  override suspend fun removeBookmark(id: String) = withContext(Dispatchers.IO) {
    bookmarkDao.deleteBookmarkById(id)
  }

  override suspend fun updateBookmarkNote(id: String, note: String) = withContext(Dispatchers.IO) {
    bookmarkDao.updateNote(id, note)
  }

  override suspend fun updateBookmarkCollection(id: String, collectionName: String) = withContext(Dispatchers.IO) {
    bookmarkDao.updateCollection(id, collectionName)
  }

  override suspend fun createCollection(name: String, description: String, icon: String) = withContext(Dispatchers.IO) {
    val id = "col_${System.currentTimeMillis()}"
    collectionDao.insertCollection(CollectionEntity(id = id, name = name, description = description, icon = icon))
  }

  override suspend fun deleteCollection(id: String) = withContext(Dispatchers.IO) {
    collectionDao.deleteCollectionById(id)
  }

  override suspend fun recordSearch(query: String) = withContext(Dispatchers.IO) {
    searchHistoryDao.insertSearch(SearchHistoryEntity(query = query))
  }

  override suspend fun removeSearch(query: String) = withContext(Dispatchers.IO) {
    searchHistoryDao.deleteSearch(query)
  }

  override suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
    searchHistoryDao.clearAll()
  }
}
