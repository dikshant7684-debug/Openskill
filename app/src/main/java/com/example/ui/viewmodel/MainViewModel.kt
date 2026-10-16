package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookmarkEntity
import com.example.data.local.CollectionEntity
import com.example.data.local.SearchHistoryEntity
import com.example.data.local.SkillScoutDatabase
import com.example.data.model.*
import com.example.data.repository.SkillScoutRepository
import com.example.ui.components.NavigationDestination
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val database = SkillScoutDatabase.getDatabase(application)
  private val repository = SkillScoutRepository(
    bookmarkDao = database.bookmarkDao(),
    collectionDao = database.collectionDao(),
    searchHistoryDao = database.searchHistoryDao()
  )

  // Navigation state
  private val _currentDestination = MutableStateFlow(NavigationDestination.HOME)
  val currentDestination: StateFlow<NavigationDestination> = _currentDestination.asStateFlow()

  // Selected Detail views
  private val _selectedRepo = MutableStateFlow<RepositoryItem?>(null)
  val selectedRepo: StateFlow<RepositoryItem?> = _selectedRepo.asStateFlow()

  private val _selectedSkill = MutableStateFlow<SkillItem?>(null)
  val selectedSkill: StateFlow<SkillItem?> = _selectedSkill.asStateFlow()

  // Filter & Search state
  private val _activeFilter = MutableStateFlow(FilterOptions())
  val activeFilter: StateFlow<FilterOptions> = _activeFilter.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _showFilterSheet = MutableStateFlow(false)
  val showFilterSheet: StateFlow<Boolean> = _showFilterSheet.asStateFlow()

  // Repositories & Skills data
  private val _repositories = MutableStateFlow<List<RepositoryItem>>(emptyList())
  val repositories: StateFlow<List<RepositoryItem>> = _repositories.asStateFlow()

  private val _featuredCollections = MutableStateFlow<List<FeaturedCollection>>(emptyList())
  val featuredCollections: StateFlow<List<FeaturedCollection>> = _featuredCollections.asStateFlow()

  private val _skills = MutableStateFlow<List<SkillItem>>(emptyList())
  val skills: StateFlow<List<SkillItem>> = _skills.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  // AI Recommendation State
  private val _showAiDialog = MutableStateFlow(false)
  val showAiDialog: StateFlow<Boolean> = _showAiDialog.asStateFlow()

  private val _aiRecommendations = MutableStateFlow<List<AiRecommendation>>(emptyList())
  val aiRecommendations: StateFlow<List<AiRecommendation>> = _aiRecommendations.asStateFlow()

  private val _isAiLoading = MutableStateFlow(false)
  val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

  // Feedback Snackbar
  private val _snackBarMessage = MutableStateFlow<String?>(null)
  val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

  // Room flows
  val bookmarks: StateFlow<List<BookmarkEntity>> = repository.bookmarks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val collections: StateFlow<List<CollectionEntity>> = repository.collections
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val recentSearches: StateFlow<List<SearchHistoryEntity>> = repository.recentSearches
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private var searchJob: Job? = null

  init {
    loadInitialData()
    seedDefaultCollections()
  }

  private fun loadInitialData() {
    refreshData()
  }

  fun refreshData() {
    viewModelScope.launch {
      _isLoading.value = true
      _featuredCollections.value = repository.getFeaturedCollections()
      _skills.value = repository.getSampleSkills()
      executeSearch(_activeFilter.value)
      _isLoading.value = false
    }
  }

  private fun seedDefaultCollections() {
    viewModelScope.launch {
      collections.firstOrNull()?.let { current ->
        if (current.isEmpty()) {
          repository.createCollection("Essential AI Skills", "Core agent instructions and skill toolkits", "sparkles")
          repository.createCollection("Python Automation", "Playwright, Scrapy, and headless tools", "code")
          repository.createCollection("MCP Servers", "Custom integrations and database servers", "cpu")
        }
      }
    }
  }

  fun navigateTo(destination: NavigationDestination) {
    _selectedRepo.value = null
    _selectedSkill.value = null
    _currentDestination.value = destination
  }

  fun openRepoDetails(repo: RepositoryItem) {
    _selectedRepo.value = repo
    viewModelScope.launch {
      // Enrich with live files or details
      val updated = repository.getRepositoryDetails(repo.fullName, repo.platform)
      if (updated != null) {
        _selectedRepo.value = updated
      }
    }
  }

  fun closeRepoDetails() {
    _selectedRepo.value = null
  }

  fun openSkillDetails(skill: SkillItem) {
    _selectedSkill.value = skill
  }

  fun closeSkillDetails() {
    _selectedSkill.value = null
  }

  fun updateSearchQuery(query: String) {
    _searchQuery.value = query
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
      delay(400) // Debounce
      _activeFilter.value = _activeFilter.value.copy(query = query)
      executeSearch(_activeFilter.value)
    }
  }

  fun selectCategory(category: ResourceCategory) {
    _activeFilter.value = _activeFilter.value.copy(category = category)
    executeSearch(_activeFilter.value)
  }

  fun selectPlatform(platform: Platform) {
    _activeFilter.value = _activeFilter.value.copy(platform = platform)
    executeSearch(_activeFilter.value)
  }

  fun applyFilters(filter: FilterOptions) {
    _activeFilter.value = filter
    executeSearch(filter)
  }

  fun setShowFilterSheet(show: Boolean) {
    _showFilterSheet.value = show
  }

  fun setShowAiDialog(show: Boolean) {
    _showAiDialog.value = show
  }

  fun executeSearch(filter: FilterOptions) {
    viewModelScope.launch {
      _isLoading.value = true
      val results = repository.searchRepositories(filter)
      _repositories.value = results
      _isLoading.value = false
    }
  }

  fun toggleBookmark(repo: RepositoryItem) {
    viewModelScope.launch {
      repository.toggleBookmarkRepo(repo)
      val isNowSaved = !isRepoBookmarked(repo.fullName)
      _snackBarMessage.value = if (isNowSaved) "Saved ${repo.name} to Library" else "Removed from Library"
    }
  }

  fun toggleBookmarkSkill(skill: SkillItem) {
    viewModelScope.launch {
      repository.toggleBookmarkSkill(skill)
      _snackBarMessage.value = "Updated skill bookmark"
    }
  }

  fun isRepoBookmarked(fullName: String): Boolean {
    val id = "repo:$fullName"
    return bookmarks.value.any { it.id == id }
  }

  fun isSkillBookmarked(repoFullName: String, skillName: String): Boolean {
    val id = "skill:$repoFullName/$skillName"
    return bookmarks.value.any { it.id == id }
  }

  fun createCollection(name: String, description: String) {
    viewModelScope.launch {
      repository.createCollection(name, description)
      _snackBarMessage.value = "Created collection: $name"
    }
  }

  fun deleteCollection(id: String) {
    viewModelScope.launch {
      repository.deleteCollection(id)
      _snackBarMessage.value = "Collection deleted"
    }
  }

  fun updateBookmarkNote(id: String, note: String) {
    viewModelScope.launch {
      repository.updateBookmarkNote(id, note)
      _snackBarMessage.value = "Note saved"
    }
  }

  fun clearRecentSearches() {
    viewModelScope.launch {
      repository.clearSearchHistory()
    }
  }

  fun askAiGoal(goalQuery: String) {
    viewModelScope.launch {
      _isAiLoading.value = true
      val recs = repository.getAiRecommendations(goalQuery)
      _aiRecommendations.value = recs
      _isAiLoading.value = false
    }
  }

  fun dismissSnackBar() {
    _snackBarMessage.value = null
  }

  val apiTelemetry: StateFlow<com.example.data.remote.ApiTelemetry> = com.example.data.remote.ApiClient.telemetry

  private val _githubToken = MutableStateFlow("")
  val githubToken: StateFlow<String> = _githubToken.asStateFlow()

  private val _gitlabToken = MutableStateFlow("")
  val gitlabToken: StateFlow<String> = _gitlabToken.asStateFlow()

  private val _isTestingConnection = MutableStateFlow(false)
  val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

  fun saveGitHubToken(token: String) {
    _githubToken.value = token
    repository.setGitHubToken(token)
    _snackBarMessage.value = if (token.isNotBlank()) "GitHub Token applied (5,000 req/hr enabled)" else "Switched to Public API mode"
  }

  fun saveGitLabToken(token: String) {
    _gitlabToken.value = token
    com.example.data.remote.ApiClient.gitlabToken = token.trim()
    _snackBarMessage.value = if (token.isNotBlank()) "GitLab Access Token configured" else "GitLab set to public mode"
  }

  fun testLiveApi() {
    viewModelScope.launch {
      _isTestingConnection.value = true
      repository.testLiveConnection()
      _isTestingConnection.value = false
      _snackBarMessage.value = "Live APIs tested: real HTTP status measured"
    }
  }

  fun clearCache() {
    viewModelScope.launch {
      repository.clearSearchHistory()
      _snackBarMessage.value = "Local cache & temporary index flushed"
    }
  }

  fun exportSavedDataAsJson(): String {
    val items = bookmarks.value
    val json = buildString {
      append("{\n")
      append("  \"exportedAt\": \"${java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date())}\",\n")
      append("  \"totalBookmarks\": ${items.size},\n")
      append("  \"bookmarks\": [\n")
      items.forEachIndexed { index, b ->
        append("    {\n")
        append("      \"id\": \"${b.id.replace("\"", "\\\"")}\",\n")
        append("      \"title\": \"${b.title.replace("\"", "\\\"")}\",\n")
        append("      \"platform\": \"${b.platform}\",\n")
        append("      \"url\": \"${b.url}\",\n")
        append("      \"note\": \"${b.userNote.replace("\"", "\\\"")}\"\n")
        append("    }${if (index < items.size - 1) "," else ""}\n")
      }
      append("  ]\n")
      append("}")
    }
    return json
  }

  fun exportSavedDataAsMarkdown(): String {
    val b = StringBuilder()
    b.append("# Openskill Saved Resources\n\n")
    b.append("Exported on ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())}\n\n")
    b.append("## Bookmarked Repositories & Skills\n")
    for (item in bookmarks.value) {
      b.append("- **${item.title}** (${item.platform}) - [${item.url}](${item.url})\n")
      b.append("  ${item.description}\n")
      if (item.userNote.isNotBlank()) {
        b.append("  *Note: ${item.userNote}*\n")
      }
    }
    return b.toString()
  }
}
