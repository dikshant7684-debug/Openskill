# Skill Discovery & Classification Engine

## Overview
OpenSkill discovers and analyzes actual agent skills and MCP servers inside developer repositories without assuming or fabricating content.

## Verified Skill Artifacts
OpenSkill searches repository file trees for:
1. **`SKILL.md`**: The emerging open standard for agent instruction files.
2. **`skills/` & `.skills/`**: Subdirectories containing modular skill manifests.
3. **`mcp.json`**: Model Context Protocol tool and server descriptors.

## Parsing Architecture
- **`SkillFileParser`**:
  - Extracts YAML frontmatter (`name`, `description`, `author`, `version`, `tools`, `compatibility`, `tags`).
  - Identifies prompt instructions, tool requirements, and installation shell commands from Markdown AST.
  - Links directly to the original file path and remote blob URL.
- **`SkillMetadataParser`**:
  - Parses JSON configuration schemas from `mcp.json`.
  - Extracts exposed tools, parameters, and protocol version.
- **`SkillCompatibilityDetector`**:
  - Evaluates code evidence (manifests, dependencies, READMEs).
  - Categorizes resources into:
    - `MCP_SERVERS`: Model Context Protocol server implementations.
    - `AI_SKILLS`: Standardized SKILL.md agent definitions.
    - `AI_AGENTS`: Autonomous agent systems (AutoGen, CrewAI, LangChain).
    - `CODING_TOOLS`: Command line tools, linters, compilers.
    - `LIBRARIES`: Reusable modules and packages.
  - Distinguishes verification levels: `VERIFIED_FROM_SOURCE`, `DOCUMENTED_IN_REPOSITORY`, `INFERRED_FROM_TOPICS`, and `UNVERIFIED`.
