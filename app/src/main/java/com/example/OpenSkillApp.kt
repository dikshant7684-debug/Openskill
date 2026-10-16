package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.designsystem.colors.CreamBackground
import com.example.core.designsystem.theme.OpenSkillTheme
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.NavigationDestination
import com.example.ui.components.SkillScoutBottomBar
import com.example.ui.screens.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun OpenSkillApp(viewModel: MainViewModel = viewModel()) {
  val currentDestination by viewModel.currentDestination.collectAsState()
  val selectedRepo by viewModel.selectedRepo.collectAsState()
  val selectedSkill by viewModel.selectedSkill.collectAsState()
  val showFilterSheet by viewModel.showFilterSheet.collectAsState()
  val showAiDialog by viewModel.showAiDialog.collectAsState()
  val activeFilter by viewModel.activeFilter.collectAsState()
  val snackBarMessage by viewModel.snackBarMessage.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(snackBarMessage) {
    snackBarMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.dismissSnackBar()
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = CreamBackground,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    bottomBar = {
      if (selectedRepo == null) {
        SkillScoutBottomBar(
          currentDestination = currentDestination,
          onNavigate = { viewModel.navigateTo(it) }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (selectedRepo != null) {
        RepoDetailScreen(
          repo = selectedRepo!!,
          viewModel = viewModel,
          onBack = { viewModel.closeRepoDetails() }
        )
      } else {
        AnimatedContent(
          targetState = currentDestination,
          transitionSpec = {
            fadeIn() togetherWith fadeOut()
          },
          label = "ScreenTransition"
        ) { destination ->
          when (destination) {
            NavigationDestination.HOME -> HomeScreen(viewModel = viewModel)
            NavigationDestination.EXPLORE -> ExploreScreen(viewModel = viewModel)
            NavigationDestination.SKILLS -> SkillsScreen(viewModel = viewModel)
            NavigationDestination.SAVED -> SavedScreen(viewModel = viewModel)
          }
        }
      }

      // Modals / Bottom Sheets
      if (selectedSkill != null) {
        SkillDetailModal(
          skill = selectedSkill!!,
          viewModel = viewModel,
          onDismiss = { viewModel.closeSkillDetails() }
        )
      }

      if (showFilterSheet) {
        FilterBottomSheet(
          currentFilter = activeFilter,
          onDismiss = { viewModel.setShowFilterSheet(false) },
          onApply = { viewModel.applyFilters(it) }
        )
      }

      if (showAiDialog) {
        AiRecommendationDialog(
          viewModel = viewModel,
          onDismiss = { viewModel.setShowAiDialog(false) }
        )
      }
    }
  }
}

// Backward-compatible alias
@Composable
fun OpenskillApp(viewModel: MainViewModel = viewModel()) = OpenSkillApp(viewModel)
