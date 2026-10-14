package com.example.data.repository

import com.example.data.model.*

object PreloadedCuratedData {

  val featuredCollections = listOf(
    FeaturedCollection(
      id = "ai-skills",
      title = "Essential AI Agent Skills",
      description = "Standardized SKILL.md instructions, Claude agent tools & Codex workflows",
      iconName = "sparkles",
      query = "skill.md OR agent-skills",
      tag = "AI Skills",
      repoCount = 42
    ),
    FeaturedCollection(
      id = "mcp-servers",
      title = "Top MCP Servers for Developers",
      description = "Model Context Protocol implementations for databases, browsers & IDEs",
      iconName = "cpu",
      query = "modelcontextprotocol OR mcp-server",
      tag = "MCP",
      repoCount = 38
    ),
    FeaturedCollection(
      id = "coding-agents",
      title = "Autonomous Coding Agents",
      description = "Open-source software engineers that plan, code, and execute tasks",
      iconName = "terminal",
      query = "coding-agent OR ai-engineer",
      tag = "Agents",
      repoCount = 29
    ),
    FeaturedCollection(
      id = "browser-automation",
      title = "Browser Automation & Web Scrapers",
      description = "Playwright, Puppeteer, and autonomous browser controllers for AI agents",
      iconName = "globe",
      query = "browser-use OR playwright-agent",
      tag = "Automation",
      repoCount = 31
    ),
    FeaturedCollection(
      id = "python-gems",
      title = "Most Useful Python Tools",
      description = "Lightning fast package managers, linters, and async backends",
      iconName = "code",
      query = "python developer tools",
      tag = "Python",
      repoCount = 55
    ),
    FeaturedCollection(
      id = "hidden-gems",
      title = "Hidden GitHub Gems",
      description = "High utility developer utilities with outstanding craft and zero bloat",
      iconName = "gem",
      query = "awesome-cli OR developer-tools",
      tag = "Tools",
      repoCount = 24
    )
  )

  val sampleSkills = listOf(
    SkillItem(
      id = "skill-playwright-browser",
      name = "browser-automation",
      repoFullName = "browser-use/browser-use",
      repoOwner = "browser-use",
      repoPlatform = Platform.GITHUB,
      filePath = "skills/browser-automation/SKILL.md",
      description = "Enables LLMs and agents to navigate websites, click elements, extract DOM structures, and fill forms safely.",
      category = ResourceCategory.AUTOMATION,
      supportedTools = listOf("click_element", "type_text", "extract_page_content", "take_screenshot", "scroll_page"),
      requirements = listOf("Python 3.11+", "Playwright", "Chromium"),
      installCommand = "pip install browser-use playwright && playwright install chromium",
      promptInstructions = """
        # Browser Automation Skill
        When the user requests browsing a website or extracting dynamic page content:
        1. Initialize headless browser session using Playwright.
        2. Navigate to target URL with wait_until='networkidle'.
        3. Use CSS selectors to target interactive elements with minimum touch/click surface.
        4. Sanitize and structure returned HTML into clear JSON or Markdown tables.
        5. Always handle cookie banners and captchas gracefully.
      """.trimIndent(),
      compatibility = "Claude 3.5, OpenAI GPT-4o, LangChain, CrewAI",
      stars = 24500
    ),
    SkillItem(
      id = "skill-postgres-mcp",
      name = "postgres-mcp-server",
      repoFullName = "modelcontextprotocol/servers",
      repoOwner = "modelcontextprotocol",
      repoPlatform = Platform.GITHUB,
      filePath = "src/postgres/SKILL.md",
      description = "Model Context Protocol server for inspecting schemas, executing read queries, and explaining SQL execution plans.",
      category = ResourceCategory.MCP_SERVERS,
      supportedTools = listOf("read_query", "list_tables", "describe_table", "explain_query"),
      requirements = listOf("Node.js 18+", "PostgreSQL 12+ connection string"),
      installCommand = "npx -y @modelcontextprotocol/server-postgres postgresql://user:pass@localhost:5432/mydb",
      promptInstructions = """
        # PostgreSQL MCP Server Skill
        Allows the AI assistant to read database schemas and run analytical queries.
        - NEVER execute destructive DDL/DML queries (DROP, DELETE, TRUNCATE, UPDATE without user confirmation).
        - Limit queries to 100 rows by default.
        - Prioritize indexed columns when joining tables.
      """.trimIndent(),
      compatibility = "Claude Desktop, Cursor, MCP Clients",
      stars = 38900
    ),
    SkillItem(
      id = "skill-claude-git-review",
      name = "git-code-reviewer",
      repoFullName = "anthropics/anthropic-quickstarts",
      repoOwner = "anthropics",
      repoPlatform = Platform.GITHUB,
      filePath = "computer-use-demo/skills/git_review/SKILL.md",
      description = "Deep semantic code review skill that inspects git diffs, catches edge cases, security regressions, and verifies API contracts.",
      category = ResourceCategory.AI_SKILLS,
      supportedTools = listOf("git_diff", "git_log", "git_status", "run_linter"),
      requirements = listOf("Git 2.30+", "Bash"),
      installCommand = "npm install -g @anthropic-ai/claude-code",
      promptInstructions = """
        # Git Code Reviewer Skill
        Review pull requests and staged git diffs:
        - Analyze architectural boundaries and error propagation.
        - Check for SQL injection, unvalidated deserialization, and missing bounds checks.
        - Highlight missing unit test coverage for new edge branches.
        - Format findings by severity: [Critical], [Warning], [Optimization].
      """.trimIndent(),
      compatibility = "Claude Code, Cursor, Aider",
      stars = 28700
    ),
    SkillItem(
      id = "skill-fastapi-backend",
      name = "fastapi-endpoint-scaffold",
      repoFullName = "tiangolo/fastapi",
      repoOwner = "tiangolo",
      repoPlatform = Platform.GITHUB,
      filePath = "docs/skills/fastapi_scaffold/SKILL.md",
      description = "Scaffolds production-ready async FastAPI routers, Pydantic v2 validation models, and OpenAPI schemas.",
      category = ResourceCategory.WEB_DEV,
      supportedTools = listOf("generate_router", "validate_schema", "test_endpoint"),
      requirements = listOf("Python 3.10+", "FastAPI", "Pydantic v2"),
      installCommand = "pip install \"fastapi[standard]\"",
      promptInstructions = """
        # FastAPI Scaffold Skill
        When authoring endpoints:
        - Use dependency injection for DB sessions (Depends(get_db)).
        - Enforce Pydantic v2 BaseModel with Field constraints.
        - Use HTTPStatus codes appropriately (201 for Created, 204 for No Content).
        - Add typed response_model for automated Swagger documentation.
      """.trimIndent(),
      compatibility = "OpenAI Codex, Claude 3.5, Cursor",
      stars = 79400
    ),
    SkillItem(
      id = "skill-filesystem-mcp",
      name = "filesystem-mcp-server",
      repoFullName = "modelcontextprotocol/servers",
      repoOwner = "modelcontextprotocol",
      repoPlatform = Platform.GITHUB,
      filePath = "src/filesystem/SKILL.md",
      description = "Secure sandboxed file system manipulation for MCP-compliant AI agents with path traversal guardrails.",
      category = ResourceCategory.MCP_SERVERS,
      supportedTools = listOf("read_file", "write_file", "list_directory", "move_file", "search_files"),
      requirements = listOf("Node.js 18+"),
      installCommand = "npx -y @modelcontextprotocol/server-filesystem /path/to/allowed/directory",
      promptInstructions = """
        # Filesystem MCP Server
        Enables agent to inspect and edit files strictly within allowed root paths.
        - Reject paths containing directory traversal '../'.
        - Create parent directories automatically when writing new files.
        - Validate UTF-8 file encodings.
      """.trimIndent(),
      compatibility = "Claude Desktop, Zed, Cursor, OpenDevin",
      stars = 38900
    ),
    SkillItem(
      id = "skill-cursorrules-react",
      name = "react-typescript-rules",
      repoFullName = "cursor-ai/rules",
      repoOwner = "cursor-ai",
      repoPlatform = Platform.GITHUB,
      filePath = ".cursorrules",
      description = "Clean React 19 + TypeScript coding standards: functional components, hooks hygiene, Tailwind CSS layout rules.",
      category = ResourceCategory.AI_SKILLS,
      supportedTools = listOf("typecheck", "eslint", "prettier"),
      requirements = listOf("Node 20+", "TypeScript 5.0+"),
      installCommand = "npm install -D typescript tailwindcss @types/react",
      promptInstructions = """
        # React & TypeScript Agent Rules
        - Favor Server Components where applicable; isolate client state with 'use client'.
        - Never use 'any'; provide precise generic interfaces.
        - Use React Query / SWR for server cache synchronization.
        - Keep composable components under 200 lines.
      """.trimIndent(),
      compatibility = "Cursor, GitHub Copilot, Claude Code",
      stars = 15300
    )
  )

  val sampleRepositories = listOf(
    RepositoryItem(
      id = "gh-modelcontextprotocol-servers",
      name = "servers",
      fullName = "modelcontextprotocol/servers",
      owner = "modelcontextprotocol",
      ownerAvatarUrl = "https://avatars.githubusercontent.com/u/182283088?v=4",
      description = "Official Model Context Protocol (MCP) server reference implementations: Postgres, SQLite, Filesystem, GitHub, Slack, Fetch, and Brave Search.",
      platform = Platform.GITHUB,
      stars = 38900,
      forks = 4200,
      openIssues = 120,
      language = "TypeScript",
      topics = listOf("mcp", "ai-agents", "llm-tools", "claude", "protocol", "developer-tools"),
      license = "MIT",
      defaultBranch = "main",
      updatedAt = "2 hours ago",
      isArchived = false,
      hasSkills = true,
      skillCount = 14,
      url = "https://github.com/modelcontextprotocol/servers",
      readmePreview = """
        # Model Context Protocol Servers
        This repository contains reference MCP servers that provide AI agents with secure access to external tools and data sources.
        
        ## Included Servers
        - **Postgres**: Query and inspect relational databases
        - **Filesystem**: Safe sandboxed local file operations
        - **GitHub**: Search issues, pull requests, and commit logs
        - **Brave Search**: Real-time web search for agent workflows
        - **Fetch**: Extract and convert web pages to markdown
      """.trimIndent(),
      detectedSkills = listOf(sampleSkills[1], sampleSkills[4])
    ),
    RepositoryItem(
      id = "gh-browser-use-browser-use",
      name = "browser-use",
      fullName = "browser-use/browser-use",
      owner = "browser-use",
      ownerAvatarUrl = "https://avatars.githubusercontent.com/u/187977413?v=4",
      description = "Make websites accessible for AI agents. Open-source framework for autonomous browser automation and workflow execution.",
      platform = Platform.GITHUB,
      stars = 24500,
      forks = 2800,
      openIssues = 88,
      language = "Python",
      topics = listOf("browser-automation", "ai-agents", "playwright", "web-scraping", "llm"),
      license = "MIT",
      defaultBranch = "main",
      updatedAt = "3 hours ago",
      isArchived = false,
      hasSkills = true,
      skillCount = 6,
      url = "https://github.com/browser-use/browser-use",
      readmePreview = """
        # Browser Use
        Browser Use is the easiest way to connect your AI agent with the web browser.
        
        ## Features
        - Vision-guided web browsing with DOM element anchoring
        - Multi-tab management and cookie session caching
        - Full compatibility with LangChain, Claude, and OpenAI agents
      """.trimIndent(),
      detectedSkills = listOf(sampleSkills[0])
    ),
    RepositoryItem(
      id = "gh-anthropics-anthropic-quickstarts",
      name = "anthropic-quickstarts",
      fullName = "anthropics/anthropic-quickstarts",
      owner = "anthropics",
      ownerAvatarUrl = "https://avatars.githubusercontent.com/u/76263028?v=4",
      description = "A collection of projects designed to help developers quickly build with the Anthropic API, Claude Code, and Computer Use.",
      platform = Platform.GITHUB,
      stars = 28700,
      forks = 3900,
      openIssues = 45,
      language = "Python",
      topics = listOf("anthropic", "claude", "agent-skills", "computer-use", "llm-tools"),
      license = "MIT",
      defaultBranch = "main",
      updatedAt = "Yesterday",
      isArchived = false,
      hasSkills = true,
      skillCount = 9,
      url = "https://github.com/anthropics/anthropic-quickstarts",
      readmePreview = """
        # Anthropic Quickstarts & Agent Skills
        Practical examples and reusable skill definitions for Claude 3.5 Sonnet:
        - Customer support agent workflows
        - Git code review and diff inspection
        - Computer use container orchestration
      """.trimIndent(),
      detectedSkills = listOf(sampleSkills[2])
    ),
    RepositoryItem(
      id = "gh-OpenDevin-OpenDevin",
      name = "OpenHands",
      fullName = "All-Hands-AI/OpenHands",
      owner = "All-Hands-AI",
      ownerAvatarUrl = "https://avatars.githubusercontent.com/u/163013825?v=4",
      description = "OpenHands (formerly OpenDevin): An open-source autonomous coding agent that plans, writes code, debugs, and runs unit tests in sandboxed environments.",
      platform = Platform.GITHUB,
      stars = 43200,
      forks = 5100,
      openIssues = 210,
      language = "Python",
      topics = listOf("autonomous-agents", "software-engineer", "coding-agent", "docker", "python"),
      license = "MIT",
      defaultBranch = "main",
      updatedAt = "Just now",
      isArchived = false,
      hasSkills = true,
      skillCount = 12,
      url = "https://github.com/All-Hands-AI/OpenHands",
      readmePreview = """
        # OpenHands: Code Less, Make More
        OpenHands is an open-source autonomous platform for software development powered by modern LLMs.
      """.trimIndent()
    ),
    RepositoryItem(
      id = "gh-astral-sh-uv",
      name = "uv",
      fullName = "astral-sh/uv",
      owner = "astral-sh",
      ownerAvatarUrl = "https://avatars.githubusercontent.com/u/115962839?v=4",
      description = "An extremely fast Python package and project manager, written in Rust. 10-100x faster than pip and pip-tools.",
      platform = Platform.GITHUB,
      stars = 42100,
      forks = 1400,
      openIssues = 160,
      language = "Rust",
      topics = listOf("python", "rust", "package-manager", "cli", "fast", "developer-tools"),
      license = "Apache-2.0",
      defaultBranch = "main",
      updatedAt = "1 hour ago",
      isArchived = false,
      hasSkills = true,
      skillCount = 3,
      url = "https://github.com/astral-sh/uv",
      readmePreview = """
        # uv: Extremely Fast Python Package Manager
        Written in Rust, uv replaces pip, pip-tools, virtualenv, and poetry with a single, blazingly fast CLI binary.
      """.trimIndent()
    ),
    RepositoryItem(
      id = "gh-cline-cline",
      name = "cline",
      fullName = "cline/cline",
      owner = "cline",
      ownerAvatarUrl = "https://avatars.githubusercontent.com/u/178523307?v=4",
      description = "Autonomous coding agent right in your IDE that creates/edits files, runs terminal commands, and uses MCP tools with human-in-the-loop control.",
      platform = Platform.GITHUB,
      stars = 26800,
      forks = 2500,
      openIssues = 190,
      language = "TypeScript",
      topics = listOf("vscode-extension", "ai-agent", "coding-assistant", "mcp", "claude"),
      license = "Apache-2.0",
      defaultBranch = "main",
      updatedAt = "Today",
      isArchived = false,
      hasSkills = true,
      skillCount = 8,
      url = "https://github.com/cline/cline",
      readmePreview = """
        # Cline: Autonomous Coding Agent for VS Code
        Cline is an AI assistant that can use your CLI, write files, inspect compiler errors, and browse the web via MCP.
      """.trimIndent()
    ),
    RepositoryItem(
      id = "gh-tiangolo-fastapi",
      name = "fastapi",
      fullName = "tiangolo/fastapi",
      owner = "tiangolo",
      ownerAvatarUrl = "https://avatars.githubusercontent.com/u/1326112?v=4",
      description = "FastAPI framework, high performance, easy to learn, fast to code, ready for production with Python 3.8+ type hints.",
      platform = Platform.GITHUB,
      stars = 79400,
      forks = 6400,
      openIssues = 480,
      language = "Python",
      topics = listOf("fastapi", "python", "pydantic", "asyncio", "api", "web-framework"),
      license = "MIT",
      defaultBranch = "master",
      updatedAt = "Yesterday",
      isArchived = false,
      hasSkills = true,
      skillCount = 5,
      url = "https://github.com/tiangolo/fastapi",
      readmePreview = """
        # FastAPI
        FastAPI is a modern, fast (high-performance), web framework for building APIs with Python 3.8+ based on standard Python type hints.
      """.trimIndent(),
      detectedSkills = listOf(sampleSkills[3])
    ),
    RepositoryItem(
      id = "gl-gitlab-org-gitlab-runner",
      name = "gitlab-runner",
      fullName = "gitlab-org/gitlab-runner",
      owner = "gitlab-org",
      ownerAvatarUrl = "https://gitlab.com/uploads/-/system/group/avatar/9970/logo-extra-whitespace.png",
      description = "GitLab Runner is the open source project that is used to run your CI/CD jobs and send the results back to GitLab.",
      platform = Platform.GITLAB,
      stars = 5800,
      forks = 2200,
      openIssues = 90,
      language = "Go",
      topics = listOf("cicd", "devops", "gitlab", "runner", "automation"),
      license = "MIT",
      defaultBranch = "main",
      updatedAt = "4 hours ago",
      isArchived = false,
      hasSkills = true,
      skillCount = 4,
      url = "https://gitlab.com/gitlab-org/gitlab-runner",
      readmePreview = """
        # GitLab Runner
        GitLab Runner works in conjunction with GitLab CI/CD to run jobs in Docker containers, Kubernetes clusters, or native binaries.
      """.trimIndent()
    ),
    RepositoryItem(
      id = "gh-Significant-Gravitas-AutoGPT",
      name = "AutoGPT",
      fullName = "Significant-Gravitas/AutoGPT",
      owner = "Significant-Gravitas",
      ownerAvatarUrl = "https://avatars.githubusercontent.com/u/131589345?v=4",
      description = "AutoGPT is the vision of accessible AI for everyone, to use and to build on. Our mission is to provide the tools, so that you can focus on what matters.",
      platform = Platform.GITHUB,
      stars = 168000,
      forks = 44000,
      openIssues = 350,
      language = "Python",
      topics = listOf("ai-agent", "autonomous", "automation", "python", "gpt-4"),
      license = "MIT",
      defaultBranch = "master",
      updatedAt = "3 days ago",
      isArchived = false,
      hasSkills = true,
      skillCount = 18,
      url = "https://github.com/Significant-Gravitas/AutoGPT",
      readmePreview = """
        # AutoGPT: The Heart of the Open-Source AI Agent Ecosystem
        AutoGPT provides the infrastructure to build, test, and deploy autonomous workflows.
      """.trimIndent()
    )
  )
}
