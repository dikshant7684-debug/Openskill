package com.example.domain.model

data class RepositoryFile(
  val name: String,
  val path: String,
  val isDirectory: Boolean,
  val size: Long = 0,
  val downloadUrl: String? = null,
  val isSkillRelated: Boolean = false
)
