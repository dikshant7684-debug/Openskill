package com.example.data.remote.gitlab

import com.example.data.remote.gitlab.dto.GitLabProjectDto
import com.example.data.remote.gitlab.dto.GitLabTreeItemDto
import com.example.lib.network.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GitLabDataSource(private val api: GitLabApi) {

  suspend fun searchProjects(
    search: String,
    orderBy: String = "stars",
    sort: String = "desc",
    perPage: Int = 25
  ): NetworkResult<List<GitLabProjectDto>> = withContext(Dispatchers.IO) {
    try {
      val response = api.searchProjects(search, orderBy, sort, perPage)
      if (response.isSuccessful) {
        NetworkResult.Success(response.body() ?: emptyList())
      } else {
        NetworkResult.Error("GitLab search failed: HTTP ${response.code()}", response.code())
      }
    } catch (e: Exception) {
      NetworkResult.Error(e.message ?: "Network error connecting to GitLab", cause = e)
    }
  }

  suspend fun getProjectTree(projectId: Long, perPage: Int = 50): NetworkResult<List<GitLabTreeItemDto>> = withContext(Dispatchers.IO) {
    try {
      val response = api.getProjectTree(projectId, perPage)
      if (response.isSuccessful) {
        NetworkResult.Success(response.body() ?: emptyList())
      } else {
        NetworkResult.Error("Failed to fetch GitLab tree: HTTP ${response.code()}", response.code())
      }
    } catch (e: Exception) {
      NetworkResult.Error(e.message ?: "Failed to fetch GitLab tree", cause = e)
    }
  }

  suspend fun getRawFile(projectId: Long, filePath: String, ref: String = "main"): String? = withContext(Dispatchers.IO) {
    try {
      val response = api.getRawFile(projectId, filePath, ref)
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
