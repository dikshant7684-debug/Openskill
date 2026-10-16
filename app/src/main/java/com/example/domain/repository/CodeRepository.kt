package com.example.domain.repository

import com.example.domain.model.FeaturedCollection
import com.example.domain.model.Repository
import com.example.domain.model.RepositoryFile
import com.example.domain.model.SearchFilters

interface CodeRepository {
  suspend fun searchRepositories(filters: SearchFilters): List<Repository>
  suspend fun getRepositoryDetails(owner: String, name: String, platform: String): Repository?
  suspend fun getRepositoryFiles(owner: String, name: String, path: String = ""): List<RepositoryFile>
  suspend fun getRawFileContent(downloadUrl: String): String?
  suspend fun getFeaturedCollections(): List<FeaturedCollection>
}
