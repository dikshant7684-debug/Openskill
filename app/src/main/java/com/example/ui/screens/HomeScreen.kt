package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FeaturedCollection
import com.example.data.model.RepositoryItem
import com.example.data.model.ResourceCategory
import com.example.ui.components.RepositoryCard
import com.example.ui.components.StarCountBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val searchQuery by viewModel.searchQuery.collectAsState()
  val activeFilter by viewModel.activeFilter.collectAsState()
  val repositories by viewModel.repositories.collectAsState()
  val featuredCollections by viewModel.featuredCollections.collectAsState()
  val recentSearches by viewModel.recentSearches.collectAsState()
  val isLoading by viewModel.isLoading.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("home_screen_list"),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // Top Bar & Welcome Header
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                CreamSurfaceVariant,
                MaterialTheme.colorScheme.background
              )
            )
          )
          .statusBarsPadding()
          .padding(horizontal = 20.dp, vertical = 16.dp)
      ) {
        // Brand Header Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CreamSurface)
                .border(1.dp, CreamBorder, RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Image(
                painter = painterResource(id = R.drawable.skillscout_logo),
                contentDescription = "Openskill Logo",
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Text(
                text = "Openskill",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = (-0.3).sp
                ),
                color = InkBlack
              )
              Text(
                text = "UNIVERSAL DISCOVERY",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = InkBlack
              )
            }
          }

          // AI Goal Button (Solid black button with cream text)
          Button(
            onClick = { viewModel.setShowAiDialog(true) },
            colors = ButtonDefaults.buttonColors(
              containerColor = InkBlack,
              contentColor = CreamBackground
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("btn_open_ai_scout")
          ) {
            Icon(
              imageVector = Icons.Outlined.AutoAwesome,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = CreamBackground
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Ask AI",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Headline & Subtitle
        Text(
          text = "Find your next superpower.",
          style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 34.sp
          ),
          color = InkBlack
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Discover battle-tested AI agent skills, MCP servers, and developer tools across GitHub & GitLab.",
          style = MaterialTheme.typography.bodyMedium,
          color = InkMuted
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Search Bar with Filter trigger
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.updateSearchQuery(it) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("home_search_input"),
          placeholder = {
            Text(
              "Search skills, MCP servers, Playwright, tools...",
              style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, color = InkSubtle)
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = InkBlack
            )
          },
          trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (searchQuery.isNotBlank()) {
                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = InkMuted
                  )
                }
              }
              IconButton(
                onClick = { viewModel.setShowFilterSheet(true) },
                modifier = Modifier.testTag("btn_filter_sheet")
              ) {
                Icon(
                  imageVector = Icons.Outlined.Tune,
                  contentDescription = "Filters",
                  tint = InkBlack
                )
              }
            }
          },
          shape = RoundedCornerShape(14.dp),
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

        // Recent searches chips if available
        if (recentSearches.isNotEmpty() && searchQuery.isBlank()) {
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Recent: ",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
              color = InkMuted
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              items(recentSearches.take(4)) { item ->
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = CreamSurfaceVariant,
                  border = androidx.compose.foundation.BorderStroke(1.dp, CreamBorder),
                  modifier = Modifier.clickable { viewModel.updateSearchQuery(item.query) }
                ) {
                  Text(
                    text = item.query,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                    color = InkBlack,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Discovery Categories Carousel
    item {
      Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
          text = "DISCOVERY CATEGORIES",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = InkBlack,
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
        )

        LazyRow(
          contentPadding = PaddingValues(horizontal = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(ResourceCategory.values().drop(1)) { category ->
            val isSelected = activeFilter.category == category
            FilterChip(
              selected = isSelected,
              onClick = {
                viewModel.selectCategory(if (isSelected) ResourceCategory.ALL else category)
              },
              label = {
                Text(
                  text = category.title,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  )
                )
              },
              leadingIcon = {
                Icon(
                  imageVector = when (category) {
                    ResourceCategory.AI_SKILLS -> Icons.Outlined.AutoAwesome
                    ResourceCategory.MCP_SERVERS -> Icons.Outlined.Memory
                    ResourceCategory.AI_AGENTS -> Icons.Outlined.SmartToy
                    ResourceCategory.CODING_TOOLS -> Icons.Outlined.Build
                    ResourceCategory.AUTOMATION -> Icons.Outlined.Bolt
                    ResourceCategory.WEB_DEV -> Icons.Outlined.Language
                    else -> Icons.Outlined.Folder
                  },
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = if (isSelected) CreamBackground else InkBlack
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = InkBlack,
                selectedLabelColor = CreamBackground,
                selectedLeadingIconColor = CreamBackground,
                containerColor = CreamSurface,
                labelColor = InkBlack
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isSelected,
                borderColor = CreamBorder,
                selectedBorderColor = InkBlack
              ),
              shape = RoundedCornerShape(10.dp)
            )
          }
        }
      }
    }

    // Featured Collections Carousel
    item {
      Column(modifier = Modifier.padding(top = 24.dp)) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "FEATURED COLLECTIONS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = InkBlack
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
          contentPadding = PaddingValues(horizontal = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(featuredCollections) { col ->
            FeaturedCollectionCard(
              collection = col,
              onClick = {
                viewModel.updateSearchQuery(col.query)
              }
            )
          }
        }
      }
    }

    // Trending & Discovered Repositories Section
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp)
          .padding(top = 28.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (searchQuery.isNotBlank()) "Search Results" else "Trending Repositories",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = InkBlack
          )
          Text(
            text = "${repositories.size} resources ready for inspection",
            style = MaterialTheme.typography.bodySmall,
            color = InkMuted
          )
        }

        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
            color = InkBlack
          )
        }
      }
    }

    // Repository List Items
    if (repositories.isEmpty() && !isLoading) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CreamSurface),
          border = androidx.compose.foundation.BorderStroke(1.dp, CreamBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Outlined.SearchOff,
              contentDescription = null,
              tint = InkMuted,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No developer resources found",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = InkBlack
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Try adjusting your query or resetting the platform/language filters.",
              style = MaterialTheme.typography.bodySmall,
              color = InkMuted
            )
          }
        }
      }
    } else {
      items(repositories, key = { it.id }) { repo ->
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
}

@Composable
fun FeaturedCollectionCard(
  collection: FeaturedCollection,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .width(260.dp)
      .clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = CreamSurface
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      CreamBorder
    )
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = CreamSurfaceVariant,
          border = androidx.compose.foundation.BorderStroke(0.5.dp, CreamBorder)
        ) {
          Text(
            text = collection.tag.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            ),
            color = InkBlack,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Text(
          text = "${collection.repoCount}+ items",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
          color = InkMuted
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = collection.title,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        ),
        color = InkBlack,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = collection.description,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
        color = InkMuted,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
