package com.example.data.local.preferences

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("openskill_prefs", Context.MODE_PRIVATE)

  var defaultPlatform: String
    get() = prefs.getString(KEY_DEFAULT_PLATFORM, "All Platforms") ?: "All Platforms"
    set(value) = prefs.edit().putString(KEY_DEFAULT_PLATFORM, value).apply()

  var defaultSortOption: String
    get() = prefs.getString(KEY_DEFAULT_SORT, "Best Match") ?: "Best Match"
    set(value) = prefs.edit().putString(KEY_DEFAULT_SORT, value).apply()

  var isOfflineModeEnabled: Boolean
    get() = prefs.getBoolean(KEY_OFFLINE_MODE, false)
    set(value) = prefs.edit().putBoolean(KEY_OFFLINE_MODE, value).apply()

  companion object {
    private const val KEY_DEFAULT_PLATFORM = "key_default_platform"
    private const val KEY_DEFAULT_SORT = "key_default_sort"
    private const val KEY_OFFLINE_MODE = "key_offline_mode"
  }
}
