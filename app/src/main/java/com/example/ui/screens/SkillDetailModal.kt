package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillItem
import com.example.ui.components.AgentIntegrationView
import com.example.ui.components.MarkdownView
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDetailModal(
  skill: SkillItem,
  viewModel: MainViewModel,
  onDismiss: () -> Unit
) {
  BackHandler { onDismiss() }
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val coroutineScope = rememberCoroutineScope()
  var hasCopiedCommand by remember { mutableStateOf(false) }
  var hasCopiedInstructions by remember { mutableStateOf(false) }
  val isBookmarked = viewModel.isSkillBookmarked(skill.repoFullName, skill.name)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = CreamSurface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CreamSurfaceVariant,
              border = BorderStroke(0.5.dp, CreamBorder)
            ) {
              Text(
                text = "SKILL SPECIFICATION",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                color = InkBlack,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = skill.name,
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
              color = InkBlack
            )
            Text(
              text = "From: ${skill.repoFullName} (${skill.filePath})",
              style = MaterialTheme.typography.bodySmall,
              color = InkMuted
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = InkBlack)
          }
        }
      }

      // Quick Action Buttons (Bookmark, Copy Prompt, Open Parent Repo)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = { viewModel.toggleBookmarkSkill(skill) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = InkBlack),
            border = BorderStroke(1.dp, CreamBorder),
            modifier = Modifier.weight(1f).height(40.dp)
          ) {
            Icon(
              imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = null,
              tint = if (isBookmarked) InkBlack else InkMuted,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isBookmarked) "Saved" else "Bookmark", fontSize = 11.sp, color = InkBlack)
          }

          Button(
            onClick = {
              clipboardManager.setText(AnnotatedString(skill.promptInstructions))
              hasCopiedInstructions = true
              coroutineScope.launch {
                delay(2000)
                hasCopiedInstructions = false
              }
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = InkBlack, contentColor = CreamBackground),
            modifier = Modifier.weight(1.3f).height(40.dp).testTag("btn_copy_skill_prompt")
          ) {
            Icon(
              imageVector = if (hasCopiedInstructions) Icons.Outlined.Check else Icons.Outlined.ContentCopy,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = CreamBackground
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (hasCopiedInstructions) "Copied!" else "Copy Prompt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Agent Integration & Installation
      item {
        val isMcp = skill.name.contains("mcp", ignoreCase = true) ||
          skill.description.contains("mcp", ignoreCase = true) ||
          skill.supportedTools.any { it.contains("mcp", ignoreCase = true) } ||
          skill.category.name.contains("MCP")
        val repoUrl = if (skill.repoPlatform.name == "GITLAB") {
          "https://gitlab.com/${skill.repoFullName}"
        } else {
          "https://github.com/${skill.repoFullName}"
        }
        val cleanCmd = skill.installCommand?.ifBlank { null } ?: if (isMcp) {
          "npx -y @modelcontextprotocol/server-${skill.name.lowercase().replace("[^a-z0-9_-]".toRegex(), "-")}"
        } else {
          "git clone $repoUrl"
        }
        AgentIntegrationView(
          title = skill.name,
          repoFullName = skill.repoFullName,
          repoUrl = repoUrl,
          description = skill.description,
          isMcp = isMcp,
          installCommand = cleanCmd,
          promptInstructions = skill.promptInstructions.ifBlank { "You have access to ${skill.name} from ${skill.repoFullName}." },
          tools = skill.supportedTools
        )
      }

      // Supported Tools
      if (skill.supportedTools.isNotEmpty()) {
        item {
          Column {
            Text(
              text = "SUPPORTED TOOLS / FUNCTIONS",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
              color = InkBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              skill.supportedTools.forEach { tool ->
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = CreamSurfaceVariant,
                  border = BorderStroke(0.5.dp, CreamBorder)
                ) {
                  Text(
                    text = "⚙ $tool",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = InkBlack,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Prompt Instructions / SKILL.md Preview
      item {
        Column {
          Text(
            text = "SKILL INSTRUCTIONS (${skill.filePath})",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
            color = InkBlack
          )
          Spacer(modifier = Modifier.height(8.dp))

          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CreamSurface),
            border = BorderStroke(1.dp, CreamBorder)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              MarkdownView(markdownText = skill.promptInstructions)
            }
          }
        }
      }

      // Compatibility & Security Safeguards
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = CreamSurface),
          border = BorderStroke(1.dp, CreamBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Verification & Compatibility", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = InkBlack)
            Text("Compatible Clients: ${skill.compatibility}", fontSize = 11.sp, color = InkMuted)
            Text("Security Status: Sandboxed instructions only. No arbitrary code execution.", fontSize = 11.sp, color = EmeraldGreen)
          }
        }
      }
    }
  }
}
