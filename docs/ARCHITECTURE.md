# OpenSkill Android Architecture

## Overview
OpenSkill is a native Android application built with Kotlin and Jetpack Compose for discovering real developer resources, AI agent skills, SKILL.md definitions, Model Context Protocol (MCP) servers, coding tools, and open-source repositories from GitHub and GitLab.

## Clean Architecture Layers

```
UI Layer (Jetpack Compose, M3, ViewModel)
                 ↓
Domain Layer (Use Cases, Domain Models, Repository Interfaces)
                 ↓
Data Layer (GitHub / GitLab Data Sources, Mappers, Room Database)
                 ↓
Core & Lib (Design System, Networking, Security, Markdown & Skill Parsers)
```

### 1. Presentation / Feature Layer (`feature/`, `ui/`)
- Pure Jetpack Compose with Material Design 3.
- Cream & Black signature aesthetic (`CreamBackground = #FBF8F2`, `InkBlack = #000000`).
- Screen state powered by `ViewModel` and `StateFlow`.
- Zero business logic inside composables.

### 2. Domain Layer (`domain/`)
- **Models**: `Repository`, `Skill`, `SkillMetadata`, `RepositoryFile`, `SearchFilters`, `Bookmark`, `Collection`, `Recommendation`.
- **Repository Interfaces**: `CodeRepository`, `SkillRepository`, `BookmarkRepository`.
- **Use Cases**: `SearchRepositoriesUseCase`, `DiscoverSkillsUseCase`, `GetRepositoryDetailsUseCase`, `ManageBookmarksUseCase`.
- Completely decoupled from Android framework APIs.

### 3. Data Layer (`data/`)
- **Remote Providers**:
  - `GitHubDataSource`: Official GitHub REST API (`/search/repositories`, `/repos/{owner}/{repo}`, `/contents/{path}`, raw files).
  - `GitLabDataSource`: Official GitLab API (`/api/v4/projects`, `/repository/tree`, `/repository/files/{path}/raw`).
- **Local Persistence**:
  - Room SQLite Database (`OpenSkillDatabase`) storing user bookmarks, custom collections, and recent search history.
  - User Preferences (`UserPreferences`) for platform, sort, and offline preferences.
- **Mappers**:
  - `RepositoryMapper`, `SkillMapper`, `BookmarkMapper` converting provider DTOs into immutable domain models.

### 4. Skill Discovery Engine (`skills/`)
- `SkillDiscoveryEngine`: Inspects actual repository file trees for `SKILL.md`, `.skills/`, and `mcp.json`.
- `SkillFileParser`: Parses real YAML frontmatter and markdown sections (overview, instructions, tools, examples).
- `SkillMetadataParser`: Extracts schemas and tool declarations from `mcp.json`.
- `SkillCompatibilityDetector`: Verifies compatibility with Claude, Cursor, OpenAI, MCP, and autonomous agent frameworks based on tangible code evidence.

### 5. Shared Infrastructure (`core/`, `lib/`)
- `core/network/`: Interceptors, User-Agent (`Openskill-Android-Discovery/1.0`), rate-limit telemetry tracking.
- `core/security/`: In-memory Personal Access Token management for GitHub and GitLab.
- `lib/markdown/`: Structured markdown AST node parser for Compose rendering.
- `lib/parsing/`: YAML frontmatter extractor.
- `lib/extensions/`: Human-readable number formatting and relative timestamps.
