package com.example.data.remote.gitlab

import com.example.data.remote.gitlab.dto.GitLabProjectDto
import com.example.data.remote.gitlab.dto.GitLabTreeItemDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitLabApi {

  @GET("api/v4/projects")
  suspend fun searchProjects(
    @Query("search") search: String,
    @Query("order_by") orderBy: String = "stars",
    @Query("sort") sort: String = "desc",
    @Query("per_page") perPage: Int = 25
  ): Response<List<GitLabProjectDto>>

  @GET("api/v4/projects/{id}/repository/tree")
  suspend fun getProjectTree(
    @Path("id") projectId: Long,
    @Query("per_page") perPage: Int = 50
  ): Response<List<GitLabTreeItemDto>>

  @GET("api/v4/projects/{id}/repository/files/{filePath}/raw")
  suspend fun getRawFile(
    @Path("id") projectId: Long,
    @Path(value = "filePath", encoded = true) filePath: String,
    @Query("ref") ref: String = "main"
  ): Response<ResponseBody>
}
