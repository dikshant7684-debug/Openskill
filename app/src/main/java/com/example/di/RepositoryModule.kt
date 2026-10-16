package com.example.di

import com.example.data.repository.OpenSkillRepositoryImpl
import com.example.domain.repository.BookmarkRepository
import com.example.domain.repository.CodeRepository
import com.example.domain.repository.SkillRepository
import com.example.domain.usecase.*

class RepositoryModule(private val databaseModule: DatabaseModule) {

  val repository: OpenSkillRepositoryImpl by lazy {
    OpenSkillRepositoryImpl(
      gitHubDataSource = NetworkModule.gitHubDataSource,
      gitLabDataSource = NetworkModule.gitLabDataSource,
      bookmarkDao = databaseModule.bookmarkDao,
      collectionDao = databaseModule.collectionDao,
      searchHistoryDao = databaseModule.searchHistoryDao
    )
  }

  val codeRepository: CodeRepository get() = repository
  val skillRepository: SkillRepository get() = repository
  val bookmarkRepository: BookmarkRepository get() = repository

  val searchRepositoriesUseCase by lazy {
    SearchRepositoriesUseCase(codeRepository, bookmarkRepository)
  }

  val discoverSkillsUseCase by lazy {
    DiscoverSkillsUseCase(skillRepository, bookmarkRepository)
  }

  val getRepositoryDetailsUseCase by lazy {
    GetRepositoryDetailsUseCase(codeRepository, skillRepository, bookmarkRepository)
  }

  val manageBookmarksUseCase by lazy {
    ManageBookmarksUseCase(bookmarkRepository)
  }
}
