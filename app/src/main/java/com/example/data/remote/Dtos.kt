package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubSearchResponse(
  @Json(name = "total_count") val totalCount: Int = 0,
  @Json(name = "incomplete_results") val incompleteResults: Boolean = false,
  @Json(name = "items") val items: List<GitHubRepoItemDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GitHubRepoItemDto(
  val id: Long,
  val name: String,
  @Json(name = "full_name") val fullName: String,
  val owner: GitHubOwnerDto,
  val description: String?,
  @Json(name = "html_url") val htmlUrl: String,
  @Json(name = "stargazers_count") val stargazersCount: Int = 0,
  @Json(name = "forks_count") val forksCount: Int = 0,
  @Json(name = "open_issues_count") val openIssuesCount: Int = 0,
  val language: String?,
  val topics: List<String>? = null,
  val license: GitHubLicenseDto?,
  @Json(name = "default_branch") val defaultBranch: String = "main",
  @Json(name = "updated_at") val updatedAt: String = "",
  val archived: Boolean = false
)

@JsonClass(generateAdapter = true)
data class GitHubOwnerDto(
  val login: String,
  @Json(name = "avatar_url") val avatarUrl: String?
)

@JsonClass(generateAdapter = true)
data class GitHubLicenseDto(
  val key: String?,
  val name: String?,
  @Json(name = "spdx_id") val spdxId: String?
)

@JsonClass(generateAdapter = true)
data class GitHubContentDto(
  val name: String,
  val path: String,
  val type: String, // "file" or "dir"
  val size: Long = 0,
  @Json(name = "download_url") val downloadUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class GitLabProjectDto(
  val id: Long,
  val name: String,
  @Json(name = "name_with_namespace") val nameWithNamespace: String,
  @Json(name = "path_with_namespace") val pathWithNamespace: String,
  val description: String?,
  @Json(name = "web_url") val webUrl: String,
  @Json(name = "star_count") val starCount: Int = 0,
  @Json(name = "forks_count") val forksCount: Int = 0,
  @Json(name = "last_activity_at") val lastActivityAt: String = "",
  @Json(name = "avatar_url") val avatarUrl: String? = null,
  @Json(name = "tag_list") val tagList: List<String> = emptyList(),
  val archived: Boolean = false
)

@JsonClass(generateAdapter = true)
data class GitLabTreeItemDto(
  val id: String,
  val name: String,
  val type: String, // "blob" or "tree"
  val path: String,
  val mode: String = ""
)
