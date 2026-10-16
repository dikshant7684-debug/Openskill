package com.example.data.mapper

import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.CollectionEntity
import com.example.domain.model.Bookmark
import com.example.domain.model.BookmarkType
import com.example.domain.model.Collection
import com.example.domain.model.Platform

object BookmarkMapper {

  fun toDomain(entity: BookmarkEntity): Bookmark {
    val platform = when (entity.platform.uppercase()) {
      "GITHUB" -> Platform.GITHUB
      "GITLAB" -> Platform.GITLAB
      else -> Platform.GITHUB
    }
    val type = if (entity.itemType.equals("SKILL", ignoreCase = true)) BookmarkType.SKILL else BookmarkType.REPO
    return Bookmark(
      id = entity.id,
      itemType = type,
      title = entity.title,
      subtitle = entity.subtitle,
      platform = platform,
      description = entity.description,
      language = entity.language,
      stars = entity.stars,
      url = entity.url,
      collectionName = entity.collectionName,
      userNote = entity.userNote,
      createdAt = entity.createdAt
    )
  }

  fun collectionToDomain(entity: CollectionEntity, count: Int = 0): Collection {
    return Collection(
      id = entity.id,
      name = entity.name,
      description = entity.description,
      icon = entity.icon,
      itemCount = count,
      createdAt = entity.createdAt
    )
  }
}
