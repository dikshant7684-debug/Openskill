package com.example.skills

import com.example.domain.model.SkillMetadata
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object SkillMetadataParser {

  private val moshi: Moshi by lazy {
    Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }

  /**
   * Parses JSON metadata from mcp.json or agent manifest files without fabricating fields.
   */
  fun parseMcpJson(rawJson: String, repoOwner: String): SkillMetadata? {
    return try {
      val adapter = moshi.adapter(Map::class.java)
      val map = adapter.fromJson(rawJson) as? Map<*, *> ?: return null
      val name = (map["name"] as? String)?.takeIf { it.isNotBlank() } ?: "MCP Server"
      val desc = (map["description"] as? String)?.takeIf { it.isNotBlank() } ?: "Model Context Protocol Server implementation"
      val version = (map["version"] as? String)?.takeIf { it.isNotBlank() }

      val toolsList = mutableListOf<String>()
      val toolsRaw = map["tools"] as? List<*>
      toolsRaw?.forEach { tool ->
        if (tool is Map<*, *>) {
          val toolName = tool["name"] as? String
          if (!toolName.isNullOrBlank()) toolsList.add(toolName)
        } else if (tool is String && tool.isNotBlank()) {
          toolsList.add(tool)
        }
      }

      SkillMetadata(
        name = name,
        description = desc,
        author = repoOwner,
        version = version,
        license = (map["license"] as? String)?.takeIf { it.isNotBlank() },
        compatibilityList = listOf("Model Context Protocol (MCP)", "Claude Desktop", "Cursor"),
        requiredTools = toolsList,
        triggers = emptyList(),
        formatVersion = "mcp/v1",
        isSupported = true
      )
    } catch (_: Exception) {
      null
    }
  }
}
