package com.example.skills

import com.example.domain.model.ResourceCategory

enum class VerificationStatus(val label: String) {
  VERIFIED_FROM_SOURCE("Verified Source Implementation"),
  DOCUMENTED_IN_REPOSITORY("Documented in Repository"),
  INFERRED_FROM_TOPICS("Inferred from Topics/Metadata"),
  UNVERIFIED("Unverified Compatibility")
}

data class CompatibilityEvidence(
  val detectedCategory: ResourceCategory,
  val verificationStatus: VerificationStatus,
  val matchedSignals: List<String>,
  val supportedEnvironments: List<String>,
  val evidenceSummary: String
)

object SkillCompatibilityDetector {

  fun evaluate(
    repoName: String,
    description: String,
    topics: List<String>,
    fileNames: List<String>,
    readmeContent: String?
  ): CompatibilityEvidence {
    val signals = mutableListOf<String>()
    val envs = mutableListOf<String>()
    val lowerDesc = description.lowercase()
    val lowerTopics = topics.map { it.lowercase() }
    val lowerReadme = readmeContent?.lowercase() ?: ""

    // 1. Check for verified MCP server
    val hasMcpFile = fileNames.any { it.equals("mcp.json", ignoreCase = true) }
    val hasMcpTopic = lowerTopics.any { it.contains("mcp") || it.contains("modelcontextprotocol") }
    val hasMcpInNameOrDesc = repoName.lowercase().contains("mcp-server") || lowerDesc.contains("model context protocol")
    val hasMcpInReadme = lowerReadme.contains("@modelcontextprotocol/sdk") || lowerReadme.contains("pip install mcp") || lowerReadme.contains("mcp server")

    if (hasMcpFile) {
      signals.add("Contains mcp.json configuration file")
      envs.addAll(listOf("Claude Desktop", "Cursor", "Windsurf", "Zed"))
      return CompatibilityEvidence(
        detectedCategory = ResourceCategory.MCP_SERVERS,
        verificationStatus = VerificationStatus.VERIFIED_FROM_SOURCE,
        matchedSignals = signals,
        supportedEnvironments = envs.distinct(),
        evidenceSummary = "Verified MCP server with explicit configuration"
      )
    }

    if (hasMcpInReadme || (hasMcpTopic && hasMcpInNameOrDesc)) {
      if (hasMcpInReadme) signals.add("Documented MCP SDK usage in README")
      if (hasMcpTopic) signals.add("Tagged with MCP topics")
      envs.addAll(listOf("Claude Desktop", "Cursor", "MCP Host"))
      return CompatibilityEvidence(
        detectedCategory = ResourceCategory.MCP_SERVERS,
        verificationStatus = if (hasMcpInReadme) VerificationStatus.DOCUMENTED_IN_REPOSITORY else VerificationStatus.INFERRED_FROM_TOPICS,
        matchedSignals = signals,
        supportedEnvironments = envs.distinct(),
        evidenceSummary = "Documented Model Context Protocol server"
      )
    }

    // 2. Check for AI Agent Skills (SKILL.md)
    val hasSkillMd = fileNames.any { it.equals("SKILL.md", ignoreCase = true) || it.contains("/SKILL.md") }
    val hasSkillsDir = fileNames.any { it.startsWith("skills/") || it.startsWith(".skills/") }
    val hasSkillTopic = lowerTopics.any { it == "agent-skills" || it == "claude-skill" || it == "ai-skills" }

    if (hasSkillMd || hasSkillsDir) {
      signals.add(if (hasSkillMd) "Verified SKILL.md file present" else "Repository contains skills directory")
      envs.addAll(listOf("Claude", "OpenAI", "Cursor Rules", "Codex"))
      return CompatibilityEvidence(
        detectedCategory = ResourceCategory.AI_SKILLS,
        verificationStatus = VerificationStatus.VERIFIED_FROM_SOURCE,
        matchedSignals = signals,
        supportedEnvironments = envs.distinct(),
        evidenceSummary = "Standardized AI agent skill specification found"
      )
    }

    // 3. Check for Autonomous AI Agents
    val isAgentTopic = lowerTopics.any { it in listOf("ai-agent", "autonomous-agents", "agentic", "ai-engineer", "coding-agent") }
    val isAgentDesc = lowerDesc.contains("autonomous agent") || lowerDesc.contains("ai software engineer") || lowerDesc.contains("multi-agent")
    val hasAgentFramework = lowerReadme.contains("autogen") || lowerReadme.contains("crewai") || lowerReadme.contains("langchain")

    if (isAgentTopic || isAgentDesc || hasAgentFramework) {
      if (isAgentTopic) signals.add("Tagged with AI agent topics")
      if (isAgentDesc) signals.add("Description specifies autonomous agent system")
      if (hasAgentFramework) signals.add("Utilizes agent orchestration frameworks")
      envs.addAll(listOf("Python / CLI", "Docker", "Node.js"))
      return CompatibilityEvidence(
        detectedCategory = ResourceCategory.AI_AGENTS,
        verificationStatus = if (hasAgentFramework) VerificationStatus.DOCUMENTED_IN_REPOSITORY else VerificationStatus.INFERRED_FROM_TOPICS,
        matchedSignals = signals,
        supportedEnvironments = envs.distinct(),
        evidenceSummary = "Autonomous agent system or agent framework"
      )
    }

    // 4. Check for Coding Tools & Developer Utilities
    val isCliOrTool = lowerTopics.any { it in listOf("cli", "developer-tools", "devtools", "linter", "compiler", "terminal") } ||
      lowerDesc.contains("command line") || lowerDesc.contains("developer tool")

    if (isCliOrTool) {
      signals.add("Developer utility or terminal tool")
      envs.add("Terminal / Shell")
      return CompatibilityEvidence(
        detectedCategory = ResourceCategory.CODING_TOOLS,
        verificationStatus = VerificationStatus.INFERRED_FROM_TOPICS,
        matchedSignals = signals,
        supportedEnvironments = envs,
        evidenceSummary = "Developer utility or CLI workflow tool"
      )
    }

    // Default fallback
    return CompatibilityEvidence(
      detectedCategory = ResourceCategory.LIBRARIES,
      verificationStatus = VerificationStatus.UNVERIFIED,
      matchedSignals = listOf("Standard open-source repository"),
      supportedEnvironments = listOf("Source repository"),
      evidenceSummary = "Open-source developer repository"
    )
  }
}
