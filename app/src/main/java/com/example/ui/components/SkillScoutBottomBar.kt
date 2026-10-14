package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class NavigationDestination(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
  EXPLORE("explore", "Explore", Icons.Filled.Explore, Icons.Outlined.Explore),
  SKILLS("skills", "Skills", Icons.Filled.Terminal, Icons.Outlined.Terminal),
  SAVED("saved", "Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
  PROFILE("profile", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun SkillScoutBottomBar(
  currentDestination: NavigationDestination,
  onNavigate: (NavigationDestination) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = CreamSurface,
    border = androidx.compose.foundation.BorderStroke(1.dp, CreamBorder)
  ) {
    NavigationBar(
      modifier = Modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.navigationBars)
        .testTag("skillscout_bottom_bar"),
      containerColor = CreamSurface,
      tonalElevation = 2.dp
    ) {
      NavigationDestination.values().forEach { destination ->
        val isSelected = currentDestination == destination

        NavigationBarItem(
          selected = isSelected,
          onClick = { onNavigate(destination) },
          icon = {
            Icon(
              imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
              contentDescription = destination.label,
              modifier = Modifier.size(24.dp)
            )
          },
          label = {
            Text(
              text = destination.label,
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            )
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CreamBackground,
            selectedTextColor = InkBlack,
            indicatorColor = InkBlack,
            unselectedIconColor = InkMuted,
            unselectedTextColor = InkMuted
          ),
          modifier = Modifier.testTag("nav_item_${destination.route}")
        )
      }
    }
  }
}
