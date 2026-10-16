package com.example.core.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TokenStorage {
  private val _githubToken = MutableStateFlow("")
  val githubToken: StateFlow<String> = _githubToken.asStateFlow()

  private val _gitlabToken = MutableStateFlow("")
  val gitlabToken: StateFlow<String> = _gitlabToken.asStateFlow()

  fun setGitHubToken(token: String) {
    _githubToken.value = token.trim()
  }

  fun setGitLabToken(token: String) {
    _gitlabToken.value = token.trim()
  }

  fun clearTokens() {
    _githubToken.value = ""
    _gitlabToken.value = ""
  }
}
