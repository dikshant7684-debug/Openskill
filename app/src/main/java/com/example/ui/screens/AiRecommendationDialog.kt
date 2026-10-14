package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiRecommendation
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiRecommendationDialog(
  viewModel: MainViewModel,
  onDismiss: () -> Unit
) {
  var goalInput by remember {
    mutableStateOf("I am building a Python AI agent that can browse websites and automate tasks.")
  }
  val recommendations by viewModel.aiRecommendations.collectAsState()
  val isAiLoading by viewModel.isAiLoading.collectAsState()

  LaunchedEffect(Unit) {
    if (recommendations.isEmpty()) {
      viewModel.askAiGoal(goalInput)
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = CreamSurface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = InkBlack,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "AI Goal Scout",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = InkBlack
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = InkBlack)
        }
      }

      Text(
        text = "Describe your architecture or project goal. Openskill will retrieve and explain the best matching open-source skills.",
        style = MaterialTheme.typography.bodySmall,
        color = InkMuted
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Input Field
      OutlinedTextField(
        value = goalInput,
        onValueChange = { goalInput = it },
        modifier = Modifier.fillMaxWidth().testTag("ai_goal_input"),
        placeholder = { Text("What are you building? e.g. Autonomous web scraper, MCP server...", color = InkSubtle) },
        maxLines = 3,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = InkBlack,
          unfocusedBorderColor = CreamBorder,
          focusedContainerColor = CreamSurface,
          unfocusedContainerColor = CreamSurface,
          focusedTextColor = InkBlack,
          unfocusedTextColor = InkBlack
        )
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Action Button
      Button(
        onClick = { viewModel.askAiGoal(goalInput.trim()) },
        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("btn_run_ai_match"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = InkBlack, contentColor = CreamBackground)
      ) {
        if (isAiLoading) {
          CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = CreamBackground)
        } else {
          Icon(Icons.Outlined.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = CreamBackground)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Scout Matching Skills", fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Recommendations List
      LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(recommendations) { rec ->
          RecommendationCard(
            rec = rec,
            onClick = {
              onDismiss()
              viewModel.openRepoDetails(rec.repoItem)
            }
          )
        }
      }
    }
  }
}

@Composable
fun RecommendationCard(
  rec: AiRecommendation,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = CreamSurface
    ),
    border = BorderStroke(1.dp, CreamBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = rec.repoItem.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = InkBlack
          )
          Spacer(modifier = Modifier.width(8.dp))
          PlatformBadge(rec.repoItem.platform)
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = CreamSurfaceVariant,
          border = BorderStroke(0.5.dp, CreamBorder)
        ) {
          Text(
            text = "${rec.relevanceScore}% MATCH",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
            color = InkBlack,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = rec.repoItem.fullName,
        style = MaterialTheme.typography.bodySmall,
        color = InkMuted
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Why it matches explanation
      Row(verticalAlignment = Alignment.Top) {
        Icon(
          imageVector = Icons.Outlined.CheckCircle,
          contentDescription = null,
          tint = EmeraldGreen,
          modifier = Modifier.size(14.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = rec.whyItMatches,
          style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
          color = InkCharcoal
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Key Skill & Compatibility Note
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "🎯 ${rec.keySkillOrFeature}",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
          color = InkBlack
        )
        Text(
          text = "View Repo →",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
          color = InkBlack
        )
      }
    }
  }
}
