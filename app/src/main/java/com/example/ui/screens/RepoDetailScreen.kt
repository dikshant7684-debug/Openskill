package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.RepositoryItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun RepoDetailScreen(
  repo: RepositoryItem,
  viewModel: MainViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val isBookmarked = viewModel.isRepoBookmarked(repo.fullName)
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("README", "Skills (${repo.skillCount})", "Files", "Info")

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    bottomBar = {
      // Bottom action bar
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = { viewModel.toggleBookmark(repo) },
            modifier = Modifier.weight(1f).height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = InkBlack),
            border = BorderStroke(1.dp, CreamBorder)
          ) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = null,
              tint = if (isBookmarked) InkBlack else InkMuted,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isBookmarked) "Saved" else "Bookmark", fontSize = 12.sp, color = InkBlack)
          }

          Button(
            onClick = {
              if (repo.url.isNotBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.url))
                context.startActivity(intent)
              }
            },
            modifier = Modifier.weight(1.3f).height(46.dp).testTag("btn_open_external_repo"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = InkBlack, contentColor = CreamBackground)
          ) {
            Icon(Icons.Outlined.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Open on ${repo.platform.displayName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {
      // Top AppBar / Back Navigation
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_repo_detail")) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            PlatformBadge(repo.platform)
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
              onClick = {
                val shareIntent = Intent().apply {
                  action = Intent.ACTION_SEND
                  putExtra(Intent.EXTRA_TEXT, "Check out ${repo.fullName} on ${repo.platform.displayName}: ${repo.url}")
                  type = "text/plain"
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Repository"))
              }
            ) {
              Icon(Icons.Outlined.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurface)
            }
          }
        }
      }

      // Repository Header Hero
      item {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (repo.ownerAvatarUrl.isNotBlank()) {
              AsyncImage(
                model = repo.ownerAvatarUrl,
                contentDescription = "${repo.owner} avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
              )
            } else {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = repo.owner.take(1).uppercase(),
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = repo.name,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onBackground
              )
              Text(
                text = repo.fullName,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = repo.description,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Key telemetry badges: Stars, Forks, Issues, Language, License
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            StarCountBadge(stars = repo.stars)
            LanguageBadge(language = repo.language, color = repo.languageColor)
            if (repo.license != null) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
              ) {
                Text(
                  text = "⚖ ${repo.license}",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                  modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Topics
          if (repo.topics.isNotEmpty()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              repo.topics.take(4).forEach { topic ->
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = CreamSurfaceVariant,
                  border = BorderStroke(0.5.dp, CreamBorder)
                ) {
                  Text(
                    text = "#$topic",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, color = InkBlack, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Tab Row
      item {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = CreamSurface,
          contentColor = InkBlack,
          modifier = Modifier.padding(top = 10.dp)
        ) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              selectedContentColor = InkBlack,
              unselectedContentColor = InkMuted,
              text = {
                Text(
                  text = title,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                  )
                )
              }
            )
          }
        }
      }

      // Tab Content
      when (selectedTab) {
        0 -> { // README
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                MarkdownView(markdownText = repo.readmePreview.ifBlank { "No README.md available in public branch." })
              }
            }
          }
        }
        1 -> { // Skills
          if (repo.detectedSkills.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(32.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "No direct SKILL.md specs detected in this repo.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          } else {
            items(repo.detectedSkills) { skill ->
              Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
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
        2 -> { // Files
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Directory Tree (${repo.defaultBranch})", fontWeight = FontWeight.Bold)
                FileItemRow("📁 skills/", "Agent instruction folder")
                FileItemRow("📄 SKILL.md", "Specification file (2.4 KB)")
                FileItemRow("📄 README.md", "Repository documentation (8.1 KB)")
                FileItemRow("📄 LICENSE", "${repo.license ?: "MIT"} License file")
                FileItemRow("📄 pyproject.toml", "Package configuration")
              }
            }
          }
        }
        3 -> { // Info
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Repository Telemetry", fontWeight = FontWeight.Bold)
                InfoRow("Default Branch", repo.defaultBranch)
                InfoRow("Last Updated", repo.updatedAt)
                InfoRow("Forks", repo.forks.toString())
                InfoRow("Open Issues", repo.openIssues.toString())
                InfoRow("Archived", if (repo.isArchived) "Yes (Read-only)" else "No (Active)")
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun FileItemRow(name: String, desc: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(name, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = ScoutCyan)
    Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

@Composable
private fun InfoRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
  }
}
