package com.example.feature.repositories.readme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.colors.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MarkdownView(
  markdownText: String,
  modifier: Modifier = Modifier
) {
  val lines = remember(markdownText) { markdownText.lines() }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    var inCodeBlock = false
    val codeBlockBuilder = remember { StringBuilder() }

    for (line in lines) {
      val trimmed = line.trim()
      if (trimmed.startsWith("```")) {
        if (inCodeBlock) {
          val code = codeBlockBuilder.toString().trimEnd()
          CodeBlockContainer(code = code)
          codeBlockBuilder.clear()
          inCodeBlock = false
        } else {
          inCodeBlock = true
        }
        continue
      }

      if (inCodeBlock) {
        codeBlockBuilder.append(line).append("\n")
        continue
      }

      when {
        trimmed.startsWith("# ") -> {
          Text(
            text = trimmed.removePrefix("# ").trim(),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = InkBlack,
            modifier = Modifier.padding(top = 8.dp)
          )
        }
        trimmed.startsWith("## ") -> {
          Text(
            text = trimmed.removePrefix("## ").trim(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = InkBlack,
            modifier = Modifier.padding(top = 6.dp)
          )
        }
        trimmed.startsWith("### ") -> {
          Text(
            text = trimmed.removePrefix("### ").trim(),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = InkCharcoal,
            modifier = Modifier.padding(top = 4.dp)
          )
        }
        trimmed.startsWith("> ") -> {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = CreamSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, CreamBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = trimmed.removePrefix("> ").trim(),
              style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
              color = InkMuted,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
        trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
          Row(
            modifier = Modifier.padding(start = 6.dp),
            verticalAlignment = Alignment.Top
          ) {
            Text("• ", color = InkBlack, fontWeight = FontWeight.Bold)
            Text(
              text = trimmed.substring(2).trim(),
              style = MaterialTheme.typography.bodyMedium,
              color = InkCharcoal
            )
          }
        }
        trimmed == "---" -> {
          HorizontalDivider(
            color = CreamBorder,
            modifier = Modifier.padding(vertical = 4.dp)
          )
        }
        trimmed.isNotBlank() -> {
          Text(
            text = trimmed,
            style = MaterialTheme.typography.bodyMedium,
            color = InkCharcoal
          )
        }
      }
    }

    if (inCodeBlock && codeBlockBuilder.isNotEmpty()) {
      CodeBlockContainer(code = codeBlockBuilder.toString().trimEnd())
    }
  }
}

@Composable
fun CodeBlockContainer(code: String) {
  val clipboardManager = LocalClipboardManager.current
  var copied by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(CodeParchmentDark)
      .border(1.dp, InkBlack, RoundedCornerShape(8.dp))
      .padding(12.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "CODE",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = CodeTextLight.copy(alpha = 0.6f)
          )
        )

        IconButton(
          onClick = {
            clipboardManager.setText(AnnotatedString(code))
            copied = true
            scope.launch {
              delay(2000)
              copied = false
            }
          },
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = if (copied) Icons.Outlined.Check else Icons.Outlined.ContentCopy,
            contentDescription = "Copy Code",
            tint = if (copied) EmeraldGreen else CodeTextLight,
            modifier = Modifier.size(15.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = code,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        color = CodeTextLight,
        lineHeight = 17.sp,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}
