package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ResourceCategory
import com.example.data.model.SkillItem
import com.example.ui.components.SkillCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SkillsScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val skills by viewModel.skills.collectAsState()
  var selectedCategory by remember { mutableStateOf<ResourceCategory?>(null) }
  var searchQuery by remember { mutableStateOf("") }

  val filteredSkills = remember(skills, selectedCategory, searchQuery) {
    skills.filter { skill ->
      val matchesCat = selectedCategory == null || skill.category == selectedCategory
      val matchesQuery = searchQuery.isBlank() ||
        skill.name.contains(searchQuery, ignoreCase = true) ||
        skill.description.contains(searchQuery, ignoreCase = true) ||
        skill.repoFullName.contains(searchQuery, ignoreCase = true) ||
        skill.supportedTools.any { it.contains(searchQuery, ignoreCase = true) }
      matchesCat && matchesQuery
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("skills_screen_list"),
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
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Skill Explorer",
              style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
              color = InkBlack
            )
            Text(
              text = "Direct indexing of SKILL.md specs, MCP tools & prompts.",
              style = MaterialTheme.typography.bodyMedium,
              color = InkMuted
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CreamSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, CreamBorder)
          ) {
            Text(
              text = "${skills.size} INDEXED",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              color = InkBlack,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search in skills
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier.fillMaxWidth(),
          placeholder = {
            Text("Filter by skill name, tool (e.g. playwright, sql)...", fontSize = 13.sp, color = InkSubtle)
          },
          leadingIcon = {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = InkBlack)
          },
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = InkBlack,
            unfocusedBorderColor = CreamBorder,
            focusedContainerColor = CreamSurface,
            unfocusedContainerColor = CreamSurface,
            focusedTextColor = InkBlack,
            unfocusedTextColor = InkBlack
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Category filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          item {
            FilterChip(
              selected = selectedCategory == null,
              onClick = { selectedCategory = null },
              label = { Text("All Formats", fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = InkBlack,
                selectedLabelColor = CreamBackground,
                containerColor = CreamSurface,
                labelColor = InkBlack
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedCategory == null,
                borderColor = CreamBorder,
                selectedBorderColor = InkBlack
              )
            )
          }
          val formats = listOf(
            ResourceCategory.AI_SKILLS,
            ResourceCategory.MCP_SERVERS,
            ResourceCategory.AUTOMATION,
            ResourceCategory.WEB_DEV
          )
          items(formats) { cat ->
            val isSelected = selectedCategory == cat
            FilterChip(
              selected = isSelected,
              onClick = { selectedCategory = if (isSelected) null else cat },
              label = { Text(cat.title, fontSize = 11.sp) },
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
    }

    // Results Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Discovered Skills (${filteredSkills.size})",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = InkBlack
        )
      }
    }

    // Skills List
    items(filteredSkills, key = { it.id }) { skill ->
      Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
        SkillCard(
          skill = skill,
          isBookmarked = viewModel.isSkillBookmarked(skill.repoFullName, skill.name),
          onBookmarkClick = { viewModel.toggleBookmarkSkill(skill) },
          onInspectSkillClick = { viewModel.openSkillDetails(skill) }
        )
      }
    }
  }
}
