# OpenSkill

**OpenSkill** is a native Android discovery application for software engineers and AI developers. It finds real developer resources, open-source repositories, AI agent skills, SKILL.md definitions, Model Context Protocol (MCP) servers, coding tools, and libraries from GitHub and GitLab.

## Features
- **100% Real Live Data**: Queries live GitHub and GitLab APIs. No fake stars, fabricated skills, or mock data.
- **Skill Discovery Engine**: Analyzes repository trees, extracts YAML frontmatter from `SKILL.md`, parses `mcp.json`, and verifies compatibility.
- **Cream & Black Aesthetic**: Sophisticated, high-contrast Material 3 theme (`#FBF8F2` cream background and `#000000` deep black font).
- **Offline Room Persistence**: Local SQLite database for bookmarks, custom collections, and search history.
- **Developer Telemetry**: Live HTTP latency monitoring, rate-limit indicators, and Personal Access Token support for 5,000 req/hr limits.
- **Export & Backup**: Export saved developer libraries to formatted JSON or Markdown.

## Architecture
- Clean Architecture (Feature, Domain, Data, Core, Lib, DI).
- Built with Kotlin, Jetpack Compose, Kotlin Coroutines, Flow, Retrofit, OkHttp, Moshi, and Room.

## Build
```bash
gradle assembleDebug
```
The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.
