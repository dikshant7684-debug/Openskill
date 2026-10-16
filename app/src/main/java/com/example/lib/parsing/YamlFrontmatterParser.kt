package com.example.lib.parsing

data class SkillFrontmatter(
  val name: String? = null,
  val description: String? = null,
  val author: String? = null,
  val version: String? = null,
  val tags: List<String> = emptyList(),
  val tools: List<String> = emptyList(),
  val compatibility: List<String> = emptyList(),
  val rawAttributes: Map<String, String> = emptyMap()
)

object YamlFrontmatterParser {

  fun parse(content: String): Pair<SkillFrontmatter?, String> {
    val trimmed = content.trimStart()
    if (!trimmed.startsWith("---")) {
      return Pair(null, content)
    }

    val secondDelimiterIndex = trimmed.indexOf("---", startIndex = 3)
    if (secondDelimiterIndex == -1) {
      return Pair(null, content)
    }

    val frontmatterBlock = trimmed.substring(3, secondDelimiterIndex).trim()
    val bodyContent = trimmed.substring(secondDelimiterIndex + 3).trimStart()

    var name: String? = null
    var description: String? = null
    var author: String? = null
    var version: String? = null
    val tags = mutableListOf<String>()
    val tools = mutableListOf<String>()
    val compatibility = mutableListOf<String>()
    val rawAttrs = mutableMapOf<String, String>()

    var activeListKey: String? = null

    for (line in frontmatterBlock.lines()) {
      val lineTrimmed = line.trim()
      if (lineTrimmed.isEmpty() || lineTrimmed.startsWith("#")) continue

      if (lineTrimmed.startsWith("- ") && activeListKey != null) {
        val item = lineTrimmed.removePrefix("- ").trim().trim('"', '\'')
        when (activeListKey) {
          "tags" -> tags.add(item)
          "tools" -> tools.add(item)
          "compatibility" -> compatibility.add(item)
        }
        continue
      }

      val colonIndex = lineTrimmed.indexOf(':')
      if (colonIndex != -1) {
        val key = lineTrimmed.substring(0, colonIndex).trim().lowercase()
        val value = lineTrimmed.substring(colonIndex + 1).trim().trim('"', '\'')
        rawAttrs[key] = value

        when (key) {
          "name" -> name = value
          "description" -> description = value
          "author" -> author = value
          "version" -> version = value
          "tags" -> {
            activeListKey = "tags"
            if (value.isNotEmpty() && !value.startsWith("[")) tags.add(value)
            if (value.startsWith("[") && value.endsWith("]")) {
              tags.addAll(value.removeSurrounding("[", "]").split(",").map { it.trim().trim('"', '\'') })
            }
          }
          "tools" -> {
            activeListKey = "tools"
            if (value.isNotEmpty() && !value.startsWith("[")) tools.add(value)
            if (value.startsWith("[") && value.endsWith("]")) {
              tools.addAll(value.removeSurrounding("[", "]").split(",").map { it.trim().trim('"', '\'') })
            }
          }
          "compatibility" -> {
            activeListKey = "compatibility"
            if (value.isNotEmpty() && !value.startsWith("[")) compatibility.add(value)
            if (value.startsWith("[") && value.endsWith("]")) {
              compatibility.addAll(value.removeSurrounding("[", "]").split(",").map { it.trim().trim('"', '\'') })
            }
          }
          else -> activeListKey = null
        }
      }
    }

    return Pair(
      SkillFrontmatter(
        name = name,
        description = description,
        author = author,
        version = version,
        tags = tags,
        tools = tools,
        compatibility = compatibility,
        rawAttributes = rawAttrs
      ),
      bodyContent
    )
  }
}
