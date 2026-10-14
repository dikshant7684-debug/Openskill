package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.local.CollectionEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SavedScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val bookmarks by viewModel.bookmarks.collectAsState()
  val collections by viewModel.collections.collectAsState()
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  var selectedCollectionName by remember { mutableStateOf<String?>(null) }
  var showCreateCollectionDialog by remember { mutableStateOf(false) }
  var showExportDialog by remember { mutableStateOf(false) }
  var editingNoteBookmark by remember { mutableStateOf<BookmarkEntity?>(null) }

  val filteredBookmarks = remember(bookmarks, selectedCollectionName) {
    if (selectedCollectionName == null) bookmarks
    else bookmarks.filter { it.collectionName.equals(selectedCollectionName, ignoreCase = true) }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("saved_screen_list"),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // Header & Actions
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
              text = "Personal Library",
              style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
              color = InkBlack
            )
            Text(
              text = "${bookmarks.size} saved repositories & agent skills.",
              style = MaterialTheme.typography.bodyMedium,
              color = InkMuted
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
              onClick = { showExportDialog = true },
              modifier = Modifier.testTag("btn_export_library")
            ) {
              Icon(Icons.Outlined.FileDownload, contentDescription = "Export Library", tint = InkBlack)
            }
            Button(
              onClick = { showCreateCollectionDialog = true },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = InkBlack,
                contentColor = CreamBackground
              ),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier.testTag("btn_create_collection")
            ) {
              Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("New Folder", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Custom Collections Carousel
        Text(
          text = "COLLECTIONS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = InkBlack
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          item {
            FilterChip(
              selected = selectedCollectionName == null,
              onClick = { selectedCollectionName = null },
              label = { Text("All Saved (${bookmarks.size})", fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = InkBlack,
                selectedLabelColor = CreamBackground,
                containerColor = CreamSurface,
                labelColor = InkBlack
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedCollectionName == null,
                borderColor = CreamBorder,
                selectedBorderColor = InkBlack
              )
            )
          }
          items(collections) { col ->
            val isSelected = selectedCollectionName == col.name
            val count = bookmarks.count { it.collectionName.equals(col.name, ignoreCase = true) }
            FilterChip(
              selected = isSelected,
              onClick = { selectedCollectionName = if (isSelected) null else col.name },
              label = { Text("${col.name} ($count)", fontSize = 11.sp) },
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

    // Empty State if no bookmarks
    if (filteredBookmarks.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 32.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CreamSurface),
          border = BorderStroke(1.dp, CreamBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Outlined.BookmarkAdd,
              contentDescription = null,
              tint = InkBlack,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = if (selectedCollectionName != null) "Collection is empty" else "Your Library is clean",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = InkBlack
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Explore repositories or skills, tap the bookmark icon to organize them with custom notes and folders.",
              style = MaterialTheme.typography.bodySmall,
              color = InkMuted
            )
          }
        }
      }
    } else {
      items(filteredBookmarks, key = { it.id }) { item ->
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
          SavedItemCard(
            item = item,
            onDeleteClick = {
              if (item.itemType == "REPO") {
                viewModel.toggleBookmark(
                  com.example.data.model.RepositoryItem(
                    id = item.id,
                    name = item.title,
                    fullName = item.subtitle,
                    owner = item.subtitle.substringBefore("/"),
                    description = item.description,
                    stars = item.stars
                  )
                )
              } else {
                viewModel.toggleBookmarkSkill(
                  com.example.data.model.SkillItem(
                    id = item.id,
                    name = item.title,
                    repoFullName = item.subtitle,
                    repoOwner = item.subtitle.substringBefore("/"),
                    description = item.description
                  )
                )
              }
            },
            onEditNoteClick = { editingNoteBookmark = item }
          )
        }
      }
    }
  }

  // Create Collection Dialog
  if (showCreateCollectionDialog) {
    var folderName by remember { mutableStateOf("") }
    var folderDesc by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showCreateCollectionDialog = false },
      containerColor = CreamSurface,
      title = { Text("New Custom Collection", color = InkBlack, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = folderName,
            onValueChange = { folderName = it },
            label = { Text("Collection Name", color = InkMuted) },
            placeholder = { Text("e.g. Friday AI Skills, MCP Servers", color = InkSubtle) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = InkBlack,
              unfocusedBorderColor = CreamBorder,
              focusedTextColor = InkBlack,
              unfocusedTextColor = InkBlack
            ),
            modifier = Modifier.fillMaxWidth().testTag("input_collection_name")
          )
          OutlinedTextField(
            value = folderDesc,
            onValueChange = { folderDesc = it },
            label = { Text("Description (Optional)", color = InkMuted) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = InkBlack,
              unfocusedBorderColor = CreamBorder,
              focusedTextColor = InkBlack,
              unfocusedTextColor = InkBlack
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (folderName.isNotBlank()) {
              viewModel.createCollection(folderName.trim(), folderDesc.trim())
              showCreateCollectionDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = InkBlack, contentColor = CreamBackground),
          modifier = Modifier.testTag("btn_confirm_create_collection")
        ) {
          Text("Create Folder", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showCreateCollectionDialog = false }) {
          Text("Cancel", color = InkBlack)
        }
      }
    )
  }

  // Edit Note Dialog
  if (editingNoteBookmark != null) {
    val b = editingNoteBookmark!!
    var noteText by remember { mutableStateOf(b.userNote) }

    AlertDialog(
      onDismissRequest = { editingNoteBookmark = null },
      containerColor = CreamSurface,
      title = { Text("Personal Note: ${b.title}", color = InkBlack, fontWeight = FontWeight.Bold) },
      text = {
        OutlinedTextField(
          value = noteText,
          onValueChange = { noteText = it },
          label = { Text("Your Notes / Implementation Thoughts", color = InkMuted) },
          placeholder = { Text("Add instructions, key commands, or why you saved this...", color = InkSubtle) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = InkBlack,
            unfocusedBorderColor = CreamBorder,
            focusedTextColor = InkBlack,
            unfocusedTextColor = InkBlack
          ),
          modifier = Modifier.fillMaxWidth().height(140.dp)
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateBookmarkNote(b.id, noteText.trim())
            editingNoteBookmark = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = InkBlack, contentColor = CreamBackground)
        ) {
          Text("Save Note", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { editingNoteBookmark = null }) {
          Text("Cancel", color = InkBlack)
        }
      }
    )
  }

  // Export Dialog
  if (showExportDialog) {
    val markdown = remember { viewModel.exportSavedDataAsMarkdown() }

    AlertDialog(
      onDismissRequest = { showExportDialog = false },
      containerColor = CreamSurface,
      title = { Text("Export Library", color = InkBlack, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("Export all your saved repositories and skills as clean Markdown:", color = InkCharcoal)
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CreamSurfaceVariant,
            border = BorderStroke(1.dp, CreamBorder),
            modifier = Modifier.fillMaxWidth().height(160.dp).padding(4.dp)
          ) {
            Text(
              text = markdown,
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
              color = InkBlack,
              modifier = Modifier.padding(10.dp)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            clipboardManager.setText(AnnotatedString(markdown))
            val sendIntent: Intent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(Intent.EXTRA_TEXT, markdown)
              type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share Openskill Library")
            context.startActivity(shareIntent)
            showExportDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = InkBlack, contentColor = CreamBackground)
        ) {
          Text("Share / Copy Markdown", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportDialog = false }) {
          Text("Close", color = InkBlack)
        }
      }
    )
  }
}

@Composable
fun SavedItemCard(
  item: BookmarkEntity,
  onDeleteClick: () -> Unit,
  onEditNoteClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = CreamSurface),
    border = BorderStroke(1.dp, CreamBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CreamSurfaceVariant,
            border = BorderStroke(0.5.dp, CreamBorder)
          ) {
            Text(
              text = item.itemType,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
              color = InkBlack,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
            color = InkBlack,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete bookmark", tint = RoseError)
        }
      }

      Text(
        text = item.subtitle,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
        color = InkMuted
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = item.description,
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
        color = InkMuted,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      // User note if present
      if (item.userNote.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(CreamSurfaceVariant)
            .border(0.5.dp, CreamBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Outlined.EditNote, contentDescription = null, tint = InkBlack, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = item.userNote,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = InkBlack,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = CreamSurfaceVariant,
          border = BorderStroke(0.5.dp, CreamBorder)
        ) {
          Text(
            text = "📁 ${item.collectionName}",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
            color = InkBlack,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        TextButton(
          onClick = onEditNoteClick,
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
          modifier = Modifier.height(28.dp)
        ) {
          Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(12.dp), tint = InkBlack)
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (item.userNote.isNotBlank()) "Edit Note" else "Add Note", fontSize = 11.sp, color = InkBlack, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}
