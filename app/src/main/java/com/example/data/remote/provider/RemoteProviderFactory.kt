package com.example.data.remote.provider

import com.example.core.network.NetworkConfig
import com.example.core.security.TokenStorage
import com.example.data.remote.github.GitHubApi
import com.example.data.remote.github.GitHubDataSource
import com.example.data.remote.gitlab.GitLabApi
import com.example.data.remote.gitlab.GitLabDataSource
import com.example.lib.constants.AppConstants

class RemoteProviderFactory {

  private val headerInterceptor = NetworkConfig.createHeaderInterceptor(
    getGitHubToken = { TokenStorage.githubToken.value },
    getGitLabToken = { TokenStorage.gitlabToken.value }
  )

  private val okHttpClient = NetworkConfig.buildOkHttpClient(headerInterceptor)

  val gitHubApi: GitHubApi by lazy {
    NetworkConfig.buildRetrofit(AppConstants.GITHUB_BASE_URL, okHttpClient)
      .create(GitHubApi::class.java)
  }

  val gitLabApi: GitLabApi by lazy {
    NetworkConfig.buildRetrofit(AppConstants.GITLAB_BASE_URL, okHttpClient)
      .create(GitLabApi::class.java)
  }

  val gitHubDataSource: GitHubDataSource by lazy {
    GitHubDataSource(gitHubApi)
  }

  val gitLabDataSource: GitLabDataSource by lazy {
    GitLabDataSource(gitLabApi)
  }
}
