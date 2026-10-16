package com.example.core.navigation

enum class NavigationDestination(
  val route: String,
  val label: String
) {
  HOME("home", "Discover"),
  EXPLORE("explore", "Explore"),
  SKILLS("skills", "Skills"),
  SAVED("saved", "Saved")
}
