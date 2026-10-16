package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionEntity(
  @PrimaryKey val id: String,
  val name: String,
  val description: String,
  val icon: String = "folder",
  val createdAt: Long = System.currentTimeMillis()
)
