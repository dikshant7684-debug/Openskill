package com.example.lib.utils

import java.util.Locale

object FileFormatUtils {

  fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    val groupIndex = digitGroups.coerceIn(0, units.size - 1)
    return String.format(Locale.US, "%.1f %s", bytes / Math.pow(1024.0, groupIndex.toDouble()), units[groupIndex])
  }

  fun isSkillFile(filePath: String): Boolean {
    val name = filePath.substringAfterLast("/").lowercase()
    return name == "skill.md" ||
      name == "prompt.md" ||
      name == "mcp.json" ||
      filePath.contains("/skills/") ||
      filePath.contains("/.skills/")
  }

  fun sanitizeForDisplay(text: String): String {
    return text.replace("\r\n", "\n").trim()
  }
}
