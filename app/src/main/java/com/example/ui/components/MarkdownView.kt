package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MarkdownView(
  markdownText: String,
  modifier: Modifier = Modifier
) {
  com.example.feature.repositories.readme.MarkdownView(
    markdownText = markdownText,
    modifier = modifier
  )
}
