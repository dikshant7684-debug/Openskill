# OpenSkill File Structure

```
OpenSkill/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── OpenSkillApp.kt
│   │   │   │   ├── MainActivity.kt
│   │   │   │   │
│   │   │   │   ├── core/
│   │   │   │   │   ├── common/
│   │   │   │   │   ├── designsystem/
│   │   │   │   │   │   ├── colors/
│   │   │   │   │   │   │   └── Color.kt
│   │   │   │   │   │   ├── typography/
│   │   │   │   │   │   │   └── Type.kt
│   │   │   │   │   │   ├── shapes/
│   │   │   │   │   │   │   └── Shapes.kt
│   │   │   │   │   │   ├── theme/
│   │   │   │   │   │   │   └── Theme.kt
│   │   │   │   │   │   └── components/
│   │   │   │   │   │       └── CommonComponents.kt
│   │   │   │   │   ├── navigation/
│   │   │   │   │   │   └── NavigationDestination.kt
│   │   │   │   │   ├── network/
│   │   │   │   │   │   └── NetworkConfig.kt
│   │   │   │   │   ├── database/
│   │   │   │   │   │   └── OpenSkillDatabase.kt
│   │   │   │   │   ├── error/
│   │   │   │   │   │   └── AppError.kt
│   │   │   │   │   └── security/
│   │   │   │   │       └── TokenStorage.kt
│   │   │   │   │
│   │   │   │   ├── lib/
│   │   │   │   │   ├── constants/
│   │   │   │   │   │   └── AppConstants.kt
│   │   │   │   │   ├── extensions/
│   │   │   │   │   │   └── Extensions.kt
│   │   │   │   │   ├── markdown/
│   │   │   │   │   │   └── MarkdownParser.kt
│   │   │   │   │   ├── parsing/
│   │   │   │   │   │   └── YamlFrontmatterParser.kt
│   │   │   │   │   ├── network/
│   │   │   │   │   │   └── NetworkResult.kt
│   │   │   │   │   ├── storage/
│   │   │   │   │   │   └── JsonStorageHelper.kt
│   │   │   │   │   └── utils/
│   │   │   │   │       └── FileFormatUtils.kt
│   │   │   │   │
│   │   │   │   ├── data/
│   │   │   │   │   ├── remote/
│   │   │   │   │   │   ├── github/
│   │   │   │   │   │   │   ├── GitHubApi.kt
│   │   │   │   │   │   │   ├── GitHubDataSource.kt
│   │   │   │   │   │   │   └── dto/
│   │   │   │   │   │   │       └── GitHubDtos.kt
│   │   │   │   │   │   ├── gitlab/
│   │   │   │   │   │   │   ├── GitLabApi.kt
│   │   │   │   │   │   │   ├── GitLabDataSource.kt
│   │   │   │   │   │   │   └── dto/
│   │   │   │   │   │   │       └── GitLabDtos.kt
│   │   │   │   │   │   └── provider/
│   │   │   │   │   │       └── RemoteProviderFactory.kt
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── BookmarkDao.kt
│   │   │   │   │   │   │   ├── CollectionDao.kt
│   │   │   │   │   │   │   └── SearchHistoryDao.kt
│   │   │   │   │   │   ├── entity/
│   │   │   │   │   │   │   ├── BookmarkEntity.kt
│   │   │   │   │   │   │   ├── CollectionEntity.kt
│   │   │   │   │   │   │   └── SearchHistoryEntity.kt
│   │   │   │   │   │   └── preferences/
│   │   │   │   │   │       └── UserPreferences.kt
│   │   │   │   │   ├── mapper/
│   │   │   │   │   │   ├── RepositoryMapper.kt
│   │   │   │   │   │   └── BookmarkMapper.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── OpenSkillRepositoryImpl.kt
│   │   │   │   │       └── SkillScoutRepository.kt
│   │   │   │   │
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Repository.kt
│   │   │   │   │   │   ├── Skill.kt
│   │   │   │   │   │   ├── RepositoryFile.kt
│   │   │   │   │   │   ├── SearchFilters.kt
│   │   │   │   │   │   └── Bookmark.kt
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── CodeRepository.kt
│   │   │   │   │   │   ├── SkillRepository.kt
│   │   │   │   │   │   └── BookmarkRepository.kt
│   │   │   │   │   └── usecase/
│   │   │   │   │       ├── SearchRepositoriesUseCase.kt
│   │   │   │   │       ├── DiscoverSkillsUseCase.kt
│   │   │   │   │       ├── GetRepositoryDetailsUseCase.kt
│   │   │   │   │       └── ManageBookmarksUseCase.kt
│   │   │   │   │
│   │   │   │   ├── skills/
│   │   │   │   │   ├── SkillDiscoveryEngine.kt
│   │   │   │   │   ├── SkillFileParser.kt
│   │   │   │   │   ├── SkillMetadataParser.kt
│   │   │   │   │   └── SkillCompatibilityDetector.kt
│   │   │   │   │
│   │   │   │   ├── feature/
│   │   │   │   │   ├── repositories/
│   │   │   │   │   │   └── readme/
│   │   │   │   │   │       └── MarkdownView.kt
│   │   │   │   │   └── ...
│   │   │   │   │
│   │   │   │   └── di/
│   │   │   │       ├── NetworkModule.kt
│   │   │   │       ├── DatabaseModule.kt
│   │   │   │       ├── RepositoryModule.kt
│   │   │   │       └── AppContainer.kt
│   │   │   │
│   │   │   └── res/
│   │   │       ├── drawable/
│   │   │       │   ├── openskill_logo.png
│   │   │       │   ├── ic_launcher_background.xml
│   │   │       │   └── ic_launcher_foreground.xml
│   │   │       └── mipmap-*/
│   │   │           ├── ic_launcher.png
│   │   │           └── ic_launcher_round.png
│   │   │
│   │   └── test/
│   │       └── java/com/example/
│   │           ├── ExampleUnitTest.kt
│   │           └── ExampleRobolectricTest.kt
│   │
│   └── build.gradle.kts
│
├── docs/
│   ├── ARCHITECTURE.md
│   ├── FILE_STRUCTURE.md
│   ├── API_INTEGRATION.md
│   └── SKILL_DISCOVERY.md
├── README.md
└── metadata.json
```
