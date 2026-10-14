package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Cream background & Ink Black font design system
private val CreamBlackColorScheme = lightColorScheme(
  primary = InkBlack,
  onPrimary = CreamBackground,
  primaryContainer = CreamSurfaceVariant,
  onPrimaryContainer = InkBlack,
  secondary = ScoutIndigo,
  onSecondary = Color.White,
  secondaryContainer = CreamSurfaceSubtle,
  onSecondaryContainer = InkBlack,
  tertiary = ScoutCyan,
  onTertiary = Color.White,
  background = CreamBackground,
  onBackground = InkBlack,
  surface = CreamSurface,
  onSurface = InkBlack,
  surfaceVariant = CreamSurfaceVariant,
  onSurfaceVariant = InkMuted,
  outline = CreamBorder,
  outlineVariant = CreamBorderSubtle,
  error = RoseError,
  onError = Color.White
)

@Composable
fun OpenskillTheme(
  darkTheme: Boolean = false, // Always present the user-requested cream background & black font aesthetic
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = CreamBlackColorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
fun SkillScoutTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) = OpenskillTheme(darkTheme, dynamicColor, content)
