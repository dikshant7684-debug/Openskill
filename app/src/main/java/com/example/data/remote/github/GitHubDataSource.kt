package com.example.data.remote.github

import com.example.core.error.AppError
import com.example.data.remote.github.dto.GitHubContentDto
import com.example.data.remote.github.dto.GitHubRepoDto
import com.example.lib.network.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GitHubDataSource(private val api: GitHubApi) {

  suspend fun searchRepositories(
    query: String,
    sort: String? = null,
    order: String? = null,
    perPage: Int = 30
  ): NetworkResult<List<GitHubRepoDto>> = withContext(Dispatchers.IO) {
    try {
      val response = api.searchRepositories(query, sort, order, perPage)
      if (response.isSuccessful) {
        val items = response.body()?.items ?: emptyList()
        NetworkResult.Success(items)
      } else {
        val code = response.code()
        val errorMsg = if (code == 403) {
          "GitHub API rate limit reached (60 req/hr). Add a Personal Access Token in Settings to raise limit to 5,000 req/hr."
        } else {
          "GitHub search failed: HTTP $code"
        }
        NetworkResult.Error(errorMsg, code)
      }
    } catch (e: Exception) {
      NetworkResult.Error(e.message ?: "Network error connecting to GitHub", cause = e)
    }
  }

  suspend fun getRepository(owner: String, repo: String): NetworkResult<GitHubRepoDto> = withContext(Dispatchers.IO) {
    try {
      val response = api.getRepository(owner, repo)
      if (response.isSuccessful && response.body() != null) {
        NetworkResult.Success(response.body()!!)
      } else {
        NetworkResult.Error("Repository not found: HTTP ${response.code()}", response.code())
      }
    } catch (e: Exception) {
      NetworkResult.Error(e.message ?: "Failed to fetch repository", cause = e)
    }
  }

  suspend fun getContents(owner: String, repo: String, path: String = ""): NetworkResult<List<GitHubContentDto>> = withContext(Dispatchers.IO) {
    try {
      val response = api.getContents(owner, repo, path)
      if (response.isSuccessful) {
        NetworkResult.Success(response.body() ?: emptyList())
      } else {
        NetworkResult.Error("Failed to fetch repository contents: HTTP ${response.code()}", response.code())
      }
    } catch (e: Exception) {
      NetworkResult.Error(e.message ?: "Failed to fetch contents", cause = e)
    }
  }

  suspend fun getRawContent(url: String): String? = withContext(Dispatchers.IO) {
    try {
      val response = api.getRawContent(url)
      if (response.isSuccessful) {
        response.body()?.string()
      } else {
        null
      }
    } catch (_: Exception) {
      null
    }
  }
}
