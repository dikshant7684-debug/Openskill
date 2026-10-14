package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FilterOptions
import com.example.data.model.Platform
import com.example.data.model.ResourceCategory
import com.example.data.model.SortOption
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
  currentFilter: FilterOptions,
  onDismiss: () -> Unit,
  onApply: (FilterOptions) -> Unit
) {
  var selectedPlatform by remember { mutableStateOf(currentFilter.platform) }
  var selectedCategory by remember { mutableStateOf(currentFilter.category) }
  var selectedLanguage by remember { mutableStateOf(currentFilter.language) }
  var minStars by remember { mutableFloatStateOf(currentFilter.minStars.toFloat()) }
  var selectedSort by remember { mutableStateOf(currentFilter.sortBy) }
  var hasSkillsOnly by remember { mutableStateOf(currentFilter.hasSkillsOnly) }

  val languages = listOf("Any", "Python", "TypeScript", "JavaScript", "Rust", "Go", "Kotlin")

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Search Filters",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close filters")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Platform selection
      Text(
        text = "Repository Platform",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Platform.values().forEach { platform ->
          val isSelected = selectedPlatform == platform
          FilterChip(
            selected = isSelected,
            onClick = { selectedPlatform = platform },
            label = { Text(platform.displayName, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = ScoutCyan.copy(alpha = 0.2f),
              selectedLabelColor = ScoutCyan
            ),
            modifier = Modifier.weight(1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Category selection
      Text(
        text = "Resource Category",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(8.dp))
      OptInCategories(
        selected = selectedCategory,
        onSelect = { selectedCategory = it }
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Programming Language
      Text(
        text = "Programming Language",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        languages.take(4).forEach { lang ->
          val isSelected = (lang == "Any" && selectedLanguage == null) || (selectedLanguage == lang)
          FilterChip(
            selected = isSelected,
            onClick = { selectedLanguage = if (lang == "Any") null else lang },
            label = { Text(lang, fontSize = 11.sp) }
          )
        }
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        languages.drop(4).forEach { lang ->
          val isSelected = selectedLanguage == lang
          FilterChip(
            selected = isSelected,
            onClick = { selectedLanguage = lang },
            label = { Text(lang, fontSize = 11.sp) }
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Sort By
      Text(
        text = "Sort Results By",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SortOption.values().forEach { sort ->
          val isSelected = selectedSort == sort
          FilterChip(
            selected = isSelected,
            onClick = { selectedSort = sort },
            label = { Text(sort.displayName, fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Skills Only Toggle
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "AI Skills & MCP Only",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Show only repositories containing SKILL.md or MCP servers",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Switch(
          checked = hasSkillsOnly,
          onCheckedChange = { hasSkillsOnly = it }
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Action Buttons: Reset & Apply
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = {
            selectedPlatform = Platform.ALL
            selectedCategory = ResourceCategory.ALL
            selectedLanguage = null
            minStars = 0f
            selectedSort = SortOption.RELEVANCE
            hasSkillsOnly = false
          },
          modifier = Modifier.weight(1f).height(48.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = InkBlack),
          border = androidx.compose.foundation.BorderStroke(1.dp, CreamBorder)
        ) {
          Text("Reset", color = InkBlack)
        }

        Button(
          onClick = {
            onApply(
              currentFilter.copy(
                platform = selectedPlatform,
                category = selectedCategory,
                language = selectedLanguage,
                minStars = minStars.toInt(),
                sortBy = selectedSort,
                hasSkillsOnly = hasSkillsOnly
              )
            )
            onDismiss()
          },
          modifier = Modifier.weight(1f).height(48.dp).testTag("btn_apply_filters"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = InkBlack, contentColor = CreamBackground)
        ) {
          Text("Apply Filters", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun OptInCategories(
  selected: ResourceCategory,
  onSelect: (ResourceCategory) -> Unit
) {
  val cats = listOf(
    ResourceCategory.ALL,
    ResourceCategory.AI_SKILLS,
    ResourceCategory.MCP_SERVERS,
    ResourceCategory.AI_AGENTS,
    ResourceCategory.CODING_TOOLS,
    ResourceCategory.AUTOMATION
  )

  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    cats.chunked(3).forEach { rowList ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        rowList.forEach { cat ->
          val isSelected = selected == cat
          FilterChip(
            selected = isSelected,
            onClick = { onSelect(cat) },
            label = { Text(cat.title, fontSize = 11.sp, maxLines = 1) },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}
