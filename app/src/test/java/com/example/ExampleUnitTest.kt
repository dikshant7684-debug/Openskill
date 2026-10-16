package com.example

import com.example.domain.model.Platform
import com.example.domain.model.ResourceCategory
import com.example.lib.markdown.MarkdownNode
import com.example.lib.markdown.MarkdownParser
import com.example.lib.parsing.YamlFrontmatterParser
import com.example.lib.utils.FileFormatUtils
import com.example.skills.SkillCompatibilityDetector
import com.example.skills.SkillFileParser
import com.example.skills.SkillMetadataParser
import com.example.skills.VerificationStatus
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testSkillFileParserExtractsFrontmatterAndInstructions() {
    val sampleSkillMd = """
      ---
      name: GitHub Explorer Skill
      description: Automates code search and commit inspection using GitHub CLI.
      author: OpenSkill Team
      version: 1.2.0
      tools:
        - gh
        - git
      compatibility:
        - Claude 3.5
        - OpenAI GPT-4o
        - Cursor
      ---
      # GitHub Explorer
      
      This skill provides prompt instructions for interacting with remote repositories.
      
      ## Requirements
      - gh cli installed
      - active auth token
      
      ## Installation
      ```sh
      npm install -g @openskill/explorer
      ```
    """.trimIndent()

    val parsed = SkillFileParser.parse(
      rawContent = sampleSkillMd,
      repoFullName = "openskill/explorer-skill",
      repoOwner = "openskill",
      repoPlatform = Platform.GITHUB,
      filePath = "skills/github/SKILL.md",
      repoStars = 450
    )

    assertNotNull("Skill should parse successfully", parsed)
    assertEquals("GitHub Explorer Skill", parsed!!.name)
    assertEquals("Automates code search and commit inspection using GitHub CLI.", parsed.description)
    assertEquals(2, parsed.supportedTools.size)
    assertTrue("Tools should include gh", parsed.supportedTools.contains("gh"))
    assertTrue("Compatibility should mention Claude", parsed.compatibility.contains("Claude"))
    assertEquals(450, parsed.stars)
    assertEquals("npm install -g @openskill/explorer", parsed.installCommand)
  }

  @Test
  fun testMcpMetadataParserHandlesMcpJson() {
    val sampleMcpJson = """
      {
        "name": "sqlite-mcp-server",
        "description": "Exposes SQLite database schema and queries over Model Context Protocol",
        "version": "0.1.0",
        "tools": [
          { "name": "query_database", "description": "Executes SELECT queries" },
          { "name": "list_tables", "description": "Lists all tables" }
        ]
      }
    """.trimIndent()

    val metadata = SkillMetadataParser.parseMcpJson(sampleMcpJson, "testowner")
    assertNotNull("MCP metadata should parse successfully", metadata)
    assertEquals("sqlite-mcp-server", metadata!!.name)
    assertEquals(2, metadata.requiredTools.size)
    assertTrue("Tools should contain query_database", metadata.requiredTools.contains("query_database"))
  }

  @Test
  fun testSkillCompatibilityDetectorDetectsMcpServer() {
    val evidence = SkillCompatibilityDetector.evaluate(
      repoName = "mcp-server-git",
      description = "Model Context Protocol server for git repository management",
      topics = listOf("mcp", "claude", "git"),
      fileNames = listOf("mcp.json", "index.ts", "package.json"),
      readmeContent = "Install using @modelcontextprotocol/sdk"
    )

    assertEquals(ResourceCategory.MCP_SERVERS, evidence.detectedCategory)
    assertEquals(VerificationStatus.VERIFIED_FROM_SOURCE, evidence.verificationStatus)
    assertTrue("Evidence should include mcp.json", evidence.matchedSignals.any { it.contains("mcp.json") })
  }

  @Test
  fun testMarkdownParserCreatesStructuredNodes() {
    val markdown = """
      # OpenSkill Architecture
      
      OpenSkill discovers developer tools.
      
      ```kotlin
      val app = OpenSkillApp()
      ```
      
      - Feature 1
      - Feature 2
    """.trimIndent()

    val nodes = MarkdownParser.parse(markdown)
    assertTrue("Nodes should not be empty", nodes.isNotEmpty())

    val heading = nodes.find { it is MarkdownNode.Heading } as? MarkdownNode.Heading
    assertNotNull("Heading node should exist", heading)
    assertEquals("OpenSkill Architecture", heading!!.text)

    val codeBlock = nodes.find { it is MarkdownNode.CodeBlock } as? MarkdownNode.CodeBlock
    assertNotNull("Code block should exist", codeBlock)
    assertTrue("Code should contain val app", codeBlock!!.code.contains("val app"))
  }

  @Test
  fun testFileFormatUtils() {
    assertEquals("1.0 KB", FileFormatUtils.formatFileSize(1024))
    assertEquals("1.0 MB", FileFormatUtils.formatFileSize(1024 * 1024))
    assertTrue(FileFormatUtils.isSkillFile("skills/python/SKILL.md"))
    assertTrue(FileFormatUtils.isSkillFile("config/mcp.json"))
    assertFalse(FileFormatUtils.isSkillFile("src/main.rs"))
  }
}
