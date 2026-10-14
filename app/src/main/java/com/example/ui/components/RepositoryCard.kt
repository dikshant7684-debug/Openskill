package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.outlined.CallSplit
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.RepositoryItem
import com.example.ui.theme.*

@Composable
fun RepositoryCard(
  repo: RepositoryItem,
  isBookmarked: Boolean,
  onBookmarkClick: () -> Unit,
  onCardClick: () -> Unit,
  onExploreSkillsClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("repo_card_${repo.name}")
      .clickable { onCardClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(
      1.dp,
      MaterialTheme.colorScheme.outline
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header row: Avatar + Name + Platform + Bookmark
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (repo.ownerAvatarUrl.isNotBlank()) {
          AsyncImage(
            model = repo.ownerAvatarUrl,
            contentDescription = "${repo.owner} avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
          )
        } else {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = repo.owner.take(1).uppercase(),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = repo.name,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              ),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(6.dp))
            PlatformBadge(repo.platform)
          }
          Text(
            text = repo.owner,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
          )
        }

        IconButton(
          onClick = onBookmarkClick,
          modifier = Modifier
            .size(36.dp)
            .testTag("bookmark_btn_${repo.name}")
        ) {
          Icon(
            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = "Bookmark repository",
            tint = if (isBookmarked) InkBlack else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Description
      Text(
        text = repo.description,
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 19.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      // Skill highlight banner if available
      if (repo.hasSkills) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CreamSurfaceVariant)
            .border(0.5.dp, CreamBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          SkillDetectedBadge(if (repo.skillCount > 0) repo.skillCount else 3)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Contains reusable agent instructions & tools",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Topics pills
      if (repo.topics.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          repo.topics.take(3).forEach { topic ->
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
              Text(
                text = "#$topic",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Metadata Bar: Language, Stars, Forks, Updated
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          LanguageBadge(language = repo.language, color = repo.languageColor)
          StarCountBadge(stars = repo.stars)

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.CallSplit,
              contentDescription = "Forks",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = if (repo.forks >= 1000) String.format("%.1fk", repo.forks / 1000f) else repo.forks.toString(),
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Text(
          text = repo.updatedAt,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action row: "View Repo" and "Explore Skills"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onCardClick,
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("btn_view_details_${repo.name}"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = InkBlack),
          border = BorderStroke(1.dp, CreamBorder),
          contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Code,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Details", style = MaterialTheme.typography.labelMedium)
        }

        if (repo.hasSkills) {
          Button(
            onClick = onExploreSkillsClick,
            modifier = Modifier
              .weight(1.2f)
              .height(38.dp)
              .testTag("btn_explore_skills_${repo.name}"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = InkBlack,
              contentColor = CreamBackground
            ),
            contentPadding = PaddingValues(horizontal = 10.dp)
          ) {
            Text(
              text = "Explore Skills",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }
  }
}
