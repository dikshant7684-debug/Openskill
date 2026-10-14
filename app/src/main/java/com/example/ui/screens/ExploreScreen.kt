package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Platform
import com.example.data.model.ResourceCategory
import com.example.data.model.SortOption
import com.example.ui.components.RepositoryCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ExploreScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val activeFilter by viewModel.activeFilter.collectAsState()
  val repositories by viewModel.repositories.collectAsState()
  val isLoading by viewModel.isLoading.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("explore_screen_list"),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // Header
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 20.dp, vertical = 16.dp)
      ) {
        Text(
          text = "Explore & Index",
          style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
          color = InkBlack
        )
        Text(
          text = "Browse curated ecosystems, platforms, and open-source infrastructure.",
          style = MaterialTheme.typography.bodyMedium,
          color = InkMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Platform Switcher (All / GitHub / GitLab)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Platform.values().forEach { platform ->
            val isSelected = activeFilter.platform == platform
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.selectPlatform(platform) },
              label = {
                Text(
                  text = platform.displayName,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
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
              ),
              modifier = Modifier.weight(1f)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sort Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
          ) {
            items(SortOption.values()) { sort ->
              val isSelected = activeFilter.sortBy == sort
              FilterChip(
                selected = isSelected,
                onClick = {
                  viewModel.applyFilters(activeFilter.copy(sortBy = sort))
                },
                label = { Text(sort.displayName, fontSize = 11.sp) },
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

          IconButton(onClick = { viewModel.setShowFilterSheet(true) }) {
            Icon(Icons.Filled.Tune, contentDescription = "Open Filters", tint = InkBlack)
          }
        }
      }
    }

    // Category Grid Pills
    item {
      Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
          text = "CATEGORIES",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = InkBlack
        )
        Spacer(modifier = Modifier.height(8.dp))

        val categories = listOf(
          ResourceCategory.ALL,
          ResourceCategory.AI_SKILLS,
          ResourceCategory.MCP_SERVERS,
          ResourceCategory.AI_AGENTS,
          ResourceCategory.CODING_TOOLS,
          ResourceCategory.AUTOMATION,
          ResourceCategory.WEB_DEV
        )

        categories.chunked(2).forEach { rowList ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            rowList.forEach { cat ->
              val isSelected = activeFilter.category == cat
              Surface(
                modifier = Modifier
                  .weight(1f)
                  .padding(vertical = 4.dp)
                  .clickable { viewModel.selectCategory(cat) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) InkBlack else CreamSurface,
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isSelected) InkBlack else CreamBorder
                )
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = when (cat) {
                      ResourceCategory.AI_SKILLS -> Icons.Outlined.AutoAwesome
                      ResourceCategory.MCP_SERVERS -> Icons.Outlined.Memory
                      ResourceCategory.AI_AGENTS -> Icons.Outlined.SmartToy
                      ResourceCategory.CODING_TOOLS -> Icons.Outlined.Build
                      ResourceCategory.AUTOMATION -> Icons.Outlined.Bolt
                      else -> Icons.Outlined.Public
                    },
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isSelected) CreamBackground else InkBlack
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = cat.title,
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) CreamBackground else InkBlack,
                    maxLines = 1
                  )
                }
              }
            }
          }
        }
      }
    }

    // Results Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp)
          .padding(top = 18.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${repositories.size} Repositories Discovered",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = InkBlack
        )

        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp,
            color = InkBlack
          )
        }
      }
    }

    // Repository Cards
    items(repositories, key = { "explore-${it.id}" }) { repo ->
      Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
        RepositoryCard(
          repo = repo,
          isBookmarked = viewModel.isRepoBookmarked(repo.fullName),
          onBookmarkClick = { viewModel.toggleBookmark(repo) },
          onCardClick = { viewModel.openRepoDetails(repo) },
          onExploreSkillsClick = { viewModel.openRepoDetails(repo) }
        )
      }
    }
  }
}
