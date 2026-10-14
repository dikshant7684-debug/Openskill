package com.example.ui.components

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
import com.example.ui.theme.*
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
          // Finish code block
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
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            ),
            color = InkBlack,
            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
          )
        }
        trimmed.startsWith("## ") -> {
          Text(
            text = trimmed.removePrefix("## ").trim(),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            ),
            color = InkBlack,
            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
          )
        }
        trimmed.startsWith("### ") -> {
          Text(
            text = trimmed.removePrefix("### ").trim(),
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 15.sp
            ),
            color = InkBlack,
            modifier = Modifier.padding(top = 6.dp)
          )
        }
        trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
          Row(modifier = Modifier.fillMaxWidth().padding(start = 6.dp)) {
            Text(
              text = "• ",
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = InkBlack
              )
            )
            Text(
              text = trimmed.substring(2).trim(),
              style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
              color = InkCharcoal
            )
          }
        }
        trimmed.isNotBlank() -> {
          Text(
            text = trimmed,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
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
fun CodeBlockContainer(code: String, modifier: Modifier = Modifier) {
  val clipboardManager = LocalClipboardManager.current
  val coroutineScope = rememberCoroutineScope()
  var hasCopied by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(CreamSurfaceVariant)
      .border(1.dp, CreamBorder, RoundedCornerShape(8.dp))
      .padding(12.dp)
  ) {
    Text(
      text = code,
      style = MaterialTheme.typography.bodySmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        fontWeight = FontWeight.Medium
      ),
      color = InkBlack,
      modifier = Modifier
        .fillMaxWidth()
        .padding(end = 36.dp)
    )

    IconButton(
      onClick = {
        clipboardManager.setText(AnnotatedString(code))
        hasCopied = true
        coroutineScope.launch {
          delay(2000)
          hasCopied = false
        }
      },
      modifier = Modifier
        .align(Alignment.TopEnd)
        .size(30.dp)
    ) {
      Icon(
        imageVector = if (hasCopied) Icons.Outlined.Check else Icons.Outlined.ContentCopy,
        contentDescription = "Copy code block",
        tint = if (hasCopied) EmeraldGreen else InkMuted,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}
