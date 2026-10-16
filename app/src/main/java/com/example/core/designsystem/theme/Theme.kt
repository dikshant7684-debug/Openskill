package com.example.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.core.designsystem.colors.*
import com.example.core.designsystem.shapes.OpenSkillShapes
import com.example.core.designsystem.typography.OpenSkillTypography

private val CreamColorScheme = lightColorScheme(
  primary = InkBlack,
  onPrimary = CreamBackground,
  primaryContainer = CreamSurfaceVariant,
  onPrimaryContainer = InkBlack,
  secondary = InkCharcoal,
  onSecondary = CreamBackground,
  secondaryContainer = CreamSurfaceSubtle,
  onSecondaryContainer = InkBlack,
  tertiary = ScoutCyan,
  onTertiary = CreamBackground,
  tertiaryContainer = CreamSurfaceVariant,
  onTertiaryContainer = InkBlack,
  background = CreamBackground,
  onBackground = InkBlack,
  surface = CreamSurface,
  onSurface = InkBlack,
  surfaceVariant = CreamSurfaceVariant,
  onSurfaceVariant = InkMuted,
  surfaceTint = InkBlack,
  inverseSurface = InkBlack,
  inverseOnSurface = CreamBackground,
  error = RoseError,
  onError = CreamBackground,
  errorContainer = CreamSurfaceVariant,
  onErrorContainer = RoseError,
  outline = CreamBorder,
  outlineVariant = CreamBorderSubtle
)

@Composable
fun OpenSkillTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = CreamColorScheme,
    typography = OpenSkillTypography,
    shapes = OpenSkillShapes,
    content = content
  )
}

// Backward-compatible aliases
@Composable
fun OpenskillTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) = OpenSkillTheme(darkTheme, dynamicColor, content)

@Composable
fun SkillScoutTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) = OpenSkillTheme(darkTheme, dynamicColor, content)
