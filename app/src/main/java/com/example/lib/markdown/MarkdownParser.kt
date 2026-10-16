package com.example.lib.markdown

sealed interface MarkdownNode {
  data class Heading(val level: Int, val text: String) : MarkdownNode
  data class Paragraph(val text: String) : MarkdownNode
  data class BulletItem(val text: String) : MarkdownNode
  data class CodeBlock(val code: String, val language: String = "") : MarkdownNode
  data class QuoteBlock(val text: String) : MarkdownNode
  data object Divider : MarkdownNode
}

object MarkdownParser {

  fun parse(rawMarkdown: String): List<MarkdownNode> {
    if (rawMarkdown.isBlank()) return emptyList()

    val nodes = mutableListOf<MarkdownNode>()
    val lines = rawMarkdown.lines()
    var inCodeBlock = false
    val currentCodeLines = mutableListOf<String>()
    var currentCodeLang = ""

    for (line in lines) {
      val trimmed = line.trim()

      if (trimmed.startsWith("```")) {
        if (inCodeBlock) {
          nodes.add(MarkdownNode.CodeBlock(currentCodeLines.joinToString("\n"), currentCodeLang))
          currentCodeLines.clear()
          currentCodeLang = ""
          inCodeBlock = false
        } else {
          inCodeBlock = true
          currentCodeLang = trimmed.removePrefix("```").trim()
        }
        continue
      }

      if (inCodeBlock) {
        currentCodeLines.add(line)
        continue
      }

      when {
        trimmed.startsWith("### ") -> nodes.add(MarkdownNode.Heading(3, trimmed.removePrefix("### ").trim()))
        trimmed.startsWith("## ") -> nodes.add(MarkdownNode.Heading(2, trimmed.removePrefix("## ").trim()))
        trimmed.startsWith("# ") -> nodes.add(MarkdownNode.Heading(1, trimmed.removePrefix("# ").trim()))
        trimmed.startsWith("#### ") -> nodes.add(MarkdownNode.Heading(4, trimmed.removePrefix("#### ").trim()))
        trimmed.startsWith("> ") -> nodes.add(MarkdownNode.QuoteBlock(trimmed.removePrefix("> ").trim()))
        trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
          nodes.add(MarkdownNode.BulletItem(trimmed.substring(2).trim()))
        }
        trimmed == "---" || trimmed == "***" -> nodes.add(MarkdownNode.Divider)
        trimmed.isNotEmpty() -> nodes.add(MarkdownNode.Paragraph(trimmed))
      }
    }

    if (inCodeBlock && currentCodeLines.isNotEmpty()) {
      nodes.add(MarkdownNode.CodeBlock(currentCodeLines.joinToString("\n"), currentCodeLang))
    }

    return nodes
  }
}
