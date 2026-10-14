package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.OpenskillTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      OpenskillTheme {
        OpenskillApp()
      }
    }
  }
}

@Composable
fun OpenskillApp(viewModel: MainViewModel = viewModel()) {
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
    containerColor = com.example.ui.theme.CreamBackground,
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
            NavigationDestination.PROFILE -> ProfileScreen(viewModel = viewModel)
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
