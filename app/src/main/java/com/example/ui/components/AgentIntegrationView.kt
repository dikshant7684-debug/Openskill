package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AgentCodingSystem(
  val displayName: String,
  val subtitle: String,
  val configPath: String
) {
  CLAUDE_CODE("Claude Code", "Anthropic Claude Code & OpenClaude CLI", "~/.claude.json or claude mcp add"),
  HERMES("Hermes Agent", "Nous Research Hermes & Hermes Function Calling", "~/.hermes/tools.json or hermes.json"),
  GROK_BOT("Grok Bot", "xAI Grok & LLM Function Calling Agents", "System Prompt / Function Calling Schema"),
  CURSOR_CLINE("Cursor / Cline", "Cursor IDE, Cline, Roo Code & Windsurf", ".cursor/mcp.json or claude_desktop_config.json"),
  CLI_DIRECT("Direct CLI", "Node.js, Python, or Git Terminal Shell", "Terminal / Shell Command")
}

@Composable
fun AgentIntegrationView(
  title: String,
  repoFullName: String,
  repoUrl: String,
  description: String,
  isMcp: Boolean,
  installCommand: String,
  promptInstructions: String,
  tools: List<String>,
  modifier: Modifier = Modifier
) {
  val clipboardManager = LocalClipboardManager.current
  val coroutineScope = rememberCoroutineScope()
  var selectedSystem by remember { mutableStateOf(AgentCodingSystem.CLAUDE_CODE) }
  var copiedKey by remember { mutableStateOf<String?>(null) }

  fun copyText(key: String, text: String) {
    clipboardManager.setText(AnnotatedString(text))
    copiedKey = key
    coroutineScope.launch {
      delay(2000)
      if (copiedKey == key) copiedKey = null
    }
  }

  val safeName = title.lowercase().replace("[^a-z0-9_-]".toRegex(), "-").take(24).ifEmpty { "custom-tool" }
  val cleanInstallCmd = if (installCommand.isNotBlank()) {
    installCommand
  } else if (isMcp) {
    "npx -y @modelcontextprotocol/server-$safeName"
  } else {
    "git clone $repoUrl"
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Section Header
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CreamSurfaceVariant),
      border = BorderStroke(1.dp, CreamBorder)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(InkBlack),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.SmartToy,
              contentDescription = null,
              tint = CreamBackground,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Add to Agent Coding Systems",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = InkBlack
            )
            Text(
              text = "Deploy this skill/MCP tool directly into your coding agents.",
              style = MaterialTheme.typography.bodySmall,
              color = InkMuted
            )
          }
        }
      }
    }

    // Quick One-Line Installation Card
    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = InkBlack),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Outlined.Terminal,
              contentDescription = null,
              tint = ScoutCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "QUICK INSTALLATION COMMAND",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.sp
              ),
              color = ScoutCyan
            )
          }

          IconButton(
            onClick = { copyText("quick_install", cleanInstallCmd) },
            modifier = Modifier.size(28.dp).testTag("btn_copy_quick_install")
          ) {
            Icon(
              imageVector = if (copiedKey == "quick_install") Icons.Outlined.Check else Icons.Outlined.ContentCopy,
              contentDescription = "Copy command",
              tint = if (copiedKey == "quick_install") EmeraldGreen else CreamBackground,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF1E293B),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp)
              .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "$ ",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = ScoutCyan,
              fontSize = 12.sp
            )
            Text(
              text = cleanInstallCmd,
              fontFamily = FontFamily.Monospace,
              color = CreamBackground,
              fontSize = 12.sp
            )
          }
        }
      }
    }

    // Target Agent System Switcher Tabs
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "SELECT YOUR CODING AGENT SYSTEM",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
        color = InkBlack
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AgentCodingSystem.values().forEach { system ->
          val isSelected = selectedSystem == system
          FilterChip(
            selected = isSelected,
            onClick = { selectedSystem = system },
            label = {
              Text(
                text = system.displayName,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = InkBlack,
              selectedLabelColor = CreamBackground,
              containerColor = CreamSurface,
              labelColor = InkBlack
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = CreamBorder,
              selectedBorderColor = InkBlack
            )
          )
        }
      }
    }

    // System-specific instructions and integration config box
    val (instructions, snippet, configLabel, cliCommand) = generateAgentConfig(
      system = selectedSystem,
      title = title,
      safeName = safeName,
      repoFullName = repoFullName,
      repoUrl = repoUrl,
      isMcp = isMcp,
      cleanInstallCmd = cleanInstallCmd,
      promptInstructions = promptInstructions,
      tools = tools
    )

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = CreamSurface),
      border = BorderStroke(1.dp, CreamBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // System Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = selectedSystem.displayName,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
              color = InkBlack
            )
            Text(
              text = selectedSystem.subtitle,
              style = MaterialTheme.typography.bodySmall,
              color = InkMuted
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CreamSurfaceVariant,
            border = BorderStroke(0.5.dp, CreamBorder)
          ) {
            Text(
              text = selectedSystem.configPath,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.SemiBold,
              color = InkBlack,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }

        // How to add step-by-step
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "HOW TO CONFIGURE",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
            color = InkBlack
          )
          instructions.forEachIndexed { index, step ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = InkBlack,
                modifier = Modifier.size(18.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = "${index + 1}",
                    color = CreamBackground,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
              Text(
                text = step,
                style = MaterialTheme.typography.bodySmall,
                color = InkBlack,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }

        // Dedicated CLI Command if available
        if (!cliCommand.isNullOrBlank()) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "CLI QUICK COMMAND",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                color = InkMuted
              )

              IconButton(
                onClick = { copyText("cli_cmd", cliCommand) },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = if (copiedKey == "cli_cmd") Icons.Outlined.Check else Icons.Outlined.ContentCopy,
                  contentDescription = "Copy CLI command",
                  tint = if (copiedKey == "cli_cmd") EmeraldGreen else InkBlack,
                  modifier = Modifier.size(14.dp)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF0F172A),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp)
                  .horizontalScroll(rememberScrollState())
              ) {
                Text(
                  text = cliCommand,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  color = CreamBackground
                )
              }
            }
          }
        }

        // Configuration Code Block
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = configLabel,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
              color = InkMuted
            )

            Button(
              onClick = { copyText("agent_config", snippet) },
              colors = ButtonDefaults.buttonColors(containerColor = InkBlack, contentColor = CreamBackground),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(30.dp)
            ) {
              Icon(
                imageVector = if (copiedKey == "agent_config") Icons.Outlined.Check else Icons.Outlined.ContentCopy,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = CreamBackground
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (copiedKey == "agent_config") "Copied!" else "Copy Config",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF090D16),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .horizontalScroll(rememberScrollState())
            ) {
              Text(
                text = snippet,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color(0xFFE2E8F0),
                lineHeight = 16.sp
              )
            }
          }
        }
      }
    }
  }
}

private data class AgentConfigResult(
  val instructions: List<String>,
  val snippet: String,
  val configLabel: String,
  val cliCommand: String?
)

private fun generateAgentConfig(
  system: AgentCodingSystem,
  title: String,
  safeName: String,
  repoFullName: String,
  repoUrl: String,
  isMcp: Boolean,
  cleanInstallCmd: String,
  promptInstructions: String,
  tools: List<String>
): AgentConfigResult {
  return when (system) {
    AgentCodingSystem.CLAUDE_CODE -> {
      val cli = "claude mcp add $safeName -- $cleanInstallCmd"
      val json = """
      {
        "mcpServers": {
          "$safeName": {
            "command": "npx",
            "args": [
              "-y",
              "$cleanInstallCmd"
            ],
            "env": {
              "GITHUB_PERSONAL_ACCESS_TOKEN": "<YOUR_TOKEN_IF_NEEDED>"
            }
          }
        }
      }
      """.trimIndent()
      AgentConfigResult(
        instructions = listOf(
          "Run the 'claude mcp add' command in your terminal inside your project directory.",
          "Alternatively, open your global Claude Code configuration file (~/.claude.json) or Claude Desktop config.",
          "Paste the MCP server declaration snippet below into the 'mcpServers' object.",
          "Restart Claude Code with 'claude' — the tools will automatically be indexed in your session."
        ),
        snippet = json,
        configLabel = "CLAUDE CONFIG (~/.claude.json / claude_desktop_config.json)",
        cliCommand = cli
      )
    }

    AgentCodingSystem.HERMES -> {
      val cli = "hermes tools add $safeName --command \"$cleanInstallCmd\""
      val json = """
      {
        "agent": {
          "name": "hermes-coder",
          "skills": [
            {
              "name": "$safeName",
              "source": "$repoUrl",
              "type": "${if (isMcp) "mcp" else "skill"}",
              "command": "$cleanInstallCmd",
              "capabilities": ${if (tools.isNotEmpty()) tools.joinToString(prefix = "[\"", postfix = "\"]", separator = "\", \"") else "[\"code_execution\", \"tool_calling\"]"}
            }
          ]
        }
      }
      """.trimIndent()
      AgentConfigResult(
        instructions = listOf(
          "In your Hermes Agent project, add the skill to your hermes.json or agents.json file.",
          "Define the tool capability handlers or pass the repository URL as an external skill module.",
          "Execute 'hermes run' — Hermes will automatically invoke function calls matching this repository's tools."
        ),
        snippet = json,
        configLabel = "HERMES AGENT CONFIG (hermes.json / agents.json)",
        cliCommand = cli
      )
    }

    AgentCodingSystem.GROK_BOT -> {
      val toolsJson = if (tools.isNotEmpty()) {
        tools.joinToString(",\n") { tool ->
          """
          {
            "type": "function",
            "function": {
              "name": "${tool.lowercase().replace("[^a-z0-9_]".toRegex(), "_")}",
              "description": "Execute $tool from $repoFullName",
              "parameters": {
                "type": "object",
                "properties": {
                  "input": { "type": "string", "description": "Command or query input" }
                },
                "required": ["input"]
              }
            }
          }
          """.trimIndent()
        }
      } else {
        """
        {
          "type": "function",
          "function": {
            "name": "${safeName.replace("-", "_")}",
            "description": "Tool functionality from $repoFullName: $cleanInstallCmd",
            "parameters": {
              "type": "object",
              "properties": {
                "action": { "type": "string", "description": "Action name" }
              }
            }
          }
        }
        """.trimIndent()
      }

      val prompt = if (promptInstructions.isNotBlank()) promptInstructions.take(200) else "You have access to tools from $repoFullName ($cleanInstallCmd)."
      val snippet = """
      // Grok / xAI Function Calling Definition
      {
        "tools": [
      $toolsJson
        ],
        "system_prompt": "You are a specialized AI assistant integrated with $title.\nInstructions: $prompt"
      }
      """.trimIndent()

      AgentConfigResult(
        instructions = listOf(
          "Pass the functions array below into your Grok API request under the 'tools' parameter.",
          "Inject the prompt instructions into the system prompt of your Grok bot agent loop.",
          "When Grok issues a tool_call, dispatch the execution to the $cleanInstallCmd command."
        ),
        snippet = snippet,
        configLabel = "GROK BOT / xAI FUNCTION CALLING SCHEMA",
        cliCommand = "curl -X POST https://api.x.ai/v1/chat/completions -d '{\"model\":\"grok-beta\"}'"
      )
    }

    AgentCodingSystem.CURSOR_CLINE -> {
      val json = """
      {
        "mcpServers": {
          "$safeName": {
            "command": "npx",
            "args": [
              "-y",
              "$cleanInstallCmd"
            ]
          }
        }
      }
      """.trimIndent()
      AgentConfigResult(
        instructions = listOf(
          "In Cursor: Open Settings > Features > MCP > Add New MCP Server.",
          "In Cline / Roo Code / Windsurf: Open Settings > MCP Servers or edit .cursor/mcp.json.",
          "Paste the JSON snippet below into your configuration file.",
          "Click Save/Enable — the agent will immediately show the new tools in the chat composer."
        ),
        snippet = json,
        configLabel = "CURSOR / CLINE MCP CONFIG (.cursor/mcp.json)",
        cliCommand = null
      )
    }

    AgentCodingSystem.CLI_DIRECT -> {
      val snippet = """
      # 1. Clone or Install Repository
      $cleanInstallCmd

      # 2. Inspect Available Tools
      cd ${repoFullName.substringAfterLast("/")}
      cat SKILL.md 2>/dev/null || cat mcp.json 2>/dev/null || cat package.json 2>/dev/null

      # 3. Test Direct Execution
      ${if (cleanInstallCmd.startsWith("npx")) cleanInstallCmd else "npm start || python3 main.py"}
      """.trimIndent()
      AgentConfigResult(
        instructions = listOf(
          "Open your terminal in your desired workspace folder.",
          "Run the installation command to fetch the repository or package.",
          "Follow the repository's direct execution commands."
        ),
        snippet = snippet,
        configLabel = "TERMINAL BASH / ZSH SHELL EXECUTION",
        cliCommand = cleanInstallCmd
      )
    }
  }
}
