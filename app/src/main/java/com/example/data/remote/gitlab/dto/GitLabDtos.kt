package com.example.data.remote.gitlab.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
