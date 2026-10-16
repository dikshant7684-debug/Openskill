package com.example.di

import android.content.Context

class AppContainer(context: Context) {
  val databaseModule = DatabaseModule(context)
  val repositoryModule = RepositoryModule(databaseModule)
}
