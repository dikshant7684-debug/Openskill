package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Platform
import com.example.ui.theme.*

@Composable
fun PlatformBadge(platform: Platform, modifier: Modifier = Modifier) {
  val (bgColor, textColor, borderClr) = when (platform) {
    Platform.GITHUB -> Triple(InkBlack, CreamBackground, InkBlack)
    Platform.GITLAB -> Triple(Color(0xFFE24329).copy(alpha = 0.12f), Color(0xFFC0392B), Color(0xFFE24329).copy(alpha = 0.35f))
    Platform.ALL -> Triple(CreamSurfaceVariant, InkBlack, CreamBorder)
  }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(6.dp),
    color = bgColor,
    border = androidx.compose.foundation.BorderStroke(1.dp, borderClr)
  ) {
    Text(
      text = platform.displayName,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold
      ),
      color = textColor,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    )
  }
}

val ScaffoldBackgroundDark = CreamSurfaceVariant

@Composable
fun LanguageBadge(language: String?, color: Color, modifier: Modifier = Modifier) {
  if (language.isNullOrBlank()) return
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(CreamSurfaceVariant)
      .border(0.5.dp, CreamBorder, RoundedCornerShape(6.dp))
      .padding(horizontal = 7.dp, vertical = 3.dp)
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(5.dp))
    Text(
      text = language,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      ),
      color = InkBlack
    )
  }
}

@Composable
fun StarCountBadge(stars: Int, modifier: Modifier = Modifier) {
  val formatted = when {
    stars >= 1_000_000 -> String.format("%.1fM", stars / 1_000_000f)
    stars >= 1_000 -> String.format("%.1fk", stars / 1_000f)
    else -> stars.toString()
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(CreamSurfaceVariant)
      .border(0.5.dp, CreamBorder, RoundedCornerShape(6.dp))
      .padding(horizontal = 7.dp, vertical = 3.dp)
  ) {
    Icon(
      imageVector = Icons.Default.Star,
      contentDescription = "Stars",
      tint = AmberWarning,
      modifier = Modifier.size(13.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = formatted,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
      ),
      color = InkBlack
    )
  }
}

@Composable
fun SkillDetectedBadge(count: Int, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(6.dp),
    color = CreamSurfaceVariant,
    border = androidx.compose.foundation.BorderStroke(1.dp, InkBlack.copy(alpha = 0.25f))
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
      Text(
        text = "⚡ $count SKILLS",
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        ),
        color = InkBlack
      )
    }
  }
}
