package com.example.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface GitHubApiService {
  @GET("search/repositories")
  suspend fun searchRepositories(
    @Query("q") query: String,
    @Query("sort") sort: String? = null,
    @Query("order") order: String? = null,
    @Query("per_page") perPage: Int = 30
  ): Response<GitHubSearchResponse>

  @GET("repos/{owner}/{repo}")
  suspend fun getRepository(
    @Path("owner") owner: String,
    @Path("repo") repo: String
  ): Response<GitHubRepoItemDto>

  @GET("repos/{owner}/{repo}/contents/{path}")
  suspend fun getContents(
    @Path("owner") owner: String,
    @Path("repo") repo: String,
    @Path(value = "path", encoded = true) path: String = ""
  ): Response<List<GitHubContentDto>>

  @GET
  suspend fun getRawContent(@Url url: String): Response<ResponseBody>
}

interface GitLabApiService {
  @GET("api/v4/projects")
  suspend fun searchProjects(
    @Query("search") search: String,
    @Query("order_by") orderBy: String = "last_activity_at",
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
