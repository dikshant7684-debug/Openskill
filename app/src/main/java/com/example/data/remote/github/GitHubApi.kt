package com.example.data.remote.github

import com.example.data.remote.github.dto.GitHubContentDto
import com.example.data.remote.github.dto.GitHubRepoDto
import com.example.data.remote.github.dto.GitHubSearchResponseDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface GitHubApi {

  @GET("search/repositories")
  suspend fun searchRepositories(
    @Query("q") query: String,
    @Query("sort") sort: String? = null,
    @Query("order") order: String? = null,
    @Query("per_page") perPage: Int = 30
  ): Response<GitHubSearchResponseDto>

  @GET("repos/{owner}/{repo}")
  suspend fun getRepository(
    @Path("owner") owner: String,
    @Path("repo") repo: String
  ): Response<GitHubRepoDto>

  @GET("repos/{owner}/{repo}/contents/{path}")
  suspend fun getContents(
    @Path("owner") owner: String,
    @Path("repo") repo: String,
    @Path(value = "path", encoded = true) path: String = ""
  ): Response<List<GitHubContentDto>>

  @GET
  suspend fun getRawContent(@Url url: String): Response<ResponseBody>
}
