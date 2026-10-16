package com.example.lib.storage

import org.json.JSONArray
import org.json.JSONObject

object JsonStorageHelper {

  fun formatBookmarkExport(
    bookmarks: List<Map<String, Any?>>,
    collections: List<Map<String, Any?>>
  ): String {
    val root = JSONObject()
    root.put("app", "OpenSkill")
    root.put("version", "1.0.0")
    root.put("exportedAt", System.currentTimeMillis())

    val collectionsArray = JSONArray()
    for (c in collections) {
      collectionsArray.put(JSONObject(c))
    }
    root.put("collections", collectionsArray)

    val bookmarksArray = JSONArray()
    for (b in bookmarks) {
      bookmarksArray.put(JSONObject(b))
    }
    root.put("bookmarks", bookmarksArray)

    return root.toString(2)
  }
}
