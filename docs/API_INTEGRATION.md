# API Integration Guide

OpenSkill connects directly to the official GitHub REST API and GitLab API to fetch live, verifiable developer repositories, skills, and documentation.

## 1. GitHub Integration

### Base URL
`https://api.github.com/`

### Endpoints
- **Search Repositories**: `GET /search/repositories?q={query}&sort={sort}&order={order}&per_page={count}`
- **Get Repository**: `GET /repos/{owner}/{repo}`
- **Get Repository Contents**: `GET /repos/{owner}/{repo}/contents/{path}`
- **Get Raw Content**: `GET https://raw.githubusercontent.com/{owner}/{repo}/{branch}/{file}`

### Authentication & Rate Limits
- **Unauthenticated**: 60 requests per hour per IP.
- **Authenticated (Personal Access Token)**: 5,000 requests per hour.
- Users can enter a personal GitHub PAT in the app's Settings screen (`ProfileScreen`), which is attached via the `Authorization: Bearer <token>` header on all GitHub requests.

## 2. GitLab Integration

### Base URL
`https://gitlab.com/`

### Endpoints
- **Search Projects**: `GET /api/v4/projects?search={query}&order_by={order}&sort={sort}&per_page={count}`
- **Get Repository Tree**: `GET /api/v4/projects/{id}/repository/tree?per_page={count}`
- **Get Raw File**: `GET /api/v4/projects/{id}/repository/files/{filePath}/raw?ref={branch}`

### Authentication
- Users can enter an optional GitLab Private Token in Settings, which is attached via the `PRIVATE-TOKEN: <token>` header.

## 3. Telemetry & Monitoring
- OpenSkill includes a live HTTP Telemetry logger in `core/network/NetworkConfig.kt`.
- Latency (ms), HTTP status codes, rate-limit headers (`x-ratelimit-remaining`, `x-ratelimit-limit`), and endpoint logs are visible in the Settings dashboard.
- Zero mock data or synthetic fallbacks are used when endpoints fail; genuine HTTP codes are reported to the user.
