package com.example.data.remote.github.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubSearchResponseDto(
  @Json(name = "total_count") val totalCount: Int = 0,
  @Json(name = "incomplete_results") val incompleteResults: Boolean = false,
  @Json(name = "items") val items: List<GitHubRepoDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GitHubRepoDto(
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
