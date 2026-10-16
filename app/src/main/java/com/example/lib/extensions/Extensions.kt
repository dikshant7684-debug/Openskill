package com.example.lib.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Formats star and fork counts into human-readable compact strings (e.g., 1.2k, 14.8k).
 */
fun Int.formatCompactCount(): String {
  return when {
    this >= 1_000_000 -> String.format(Locale.US, "%.1fM", this / 1_000_000.0)
    this >= 1_000 -> String.format(Locale.US, "%.1fk", this / 1_000.0)
    else -> this.toString()
  }
}

/**
 * Parses ISO 8601 timestamps into human-readable relative or short dates.
 */
fun String.toFormattedDate(): String {
  if (this.isBlank()) return "Recently"
  return try {
    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
      timeZone = TimeZone.getTimeZone("UTC")
    }
    val cleanDateStr = if (this.length >= 19) this.substring(0, 19) else this
    val parsedDate: Date = isoFormat.parse(cleanDateStr) ?: return this
    val diffMillis = System.currentTimeMillis() - parsedDate.time
    val diffHours = diffMillis / (1000 * 60 * 60)
    val diffDays = diffHours / 24

    when {
      diffHours < 1 -> "Just now"
      diffHours < 24 -> "${diffHours}h ago"
      diffDays < 7 -> "${diffDays}d ago"
      diffDays < 30 -> "${diffDays / 7}w ago"
      diffDays < 365 -> "${diffDays / 30}mo ago"
      else -> "${diffDays / 365}y ago"
    }
  } catch (_: Exception) {
    this.take(10)
  }
}

/**
 * Safely truncates a string with an ellipsis if it exceeds the max length.
 */
fun String.truncateSafely(maxLength: Int): String {
  return if (this.length > maxLength) {
    this.take(maxLength - 3).trimEnd() + "..."
  } else {
    this
  }
}

/**
 * Capitalizes only the first character safely.
 */
fun String.capitalizeFirst(): String {
  return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}
