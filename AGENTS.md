# AGENTS.md

## Project
Bobby: a crowdsourced knowledge base where people contribute skills and answers. Questions are answered from verified entries first. Unanswered questions go to experts, and their answers are saved for next time. Later: voice input/output and YouTube transcript import.

## Tech Stack
- Android app: Kotlin, Jetpack Compose, minSdk 24, targetSdk latest stable
- Networking: Retrofit + kotlinx.serialization
- Local storage: DataStore (settings only, no local copy of the bank)
- Backend: Kotlin, Ktor server, kotlinx.serialization
- Database: PostgreSQL (full-text search via tsvector, pg_trgm for fuzzy match)
- DB access: Exposed (Kotlin SQL library), migrations with Flyway
- Tests: JUnit 5 + MockK (unit), Testcontainers for PostgreSQL (integration), Compose UI tests (screens)
- Lint: ktlint and detekt

## Repo Layout

knowledgebank/
app/ # Android app (Kotlin + Compose)
src/main/java/.../
ui/ # Composables, screens, theme
viewmodel/ # ViewModels, UI state only
data/ # Retrofit API, DTOs, repository
domain/ # Pure Kotlin models and rules
server/ # Ktor backend
src/main/kotlin/.../
routes/ # HTTP routes only, no business logic
services/ # Search, verification, routing, gap logging
db/ # Exposed tables, repositories
src/main/resources/db/migration/ # Flyway SQL files
src/test/kotlin/.../
spec.md # Product spec, read before building features
AGENTS.md


## Commands
- Run server: `cd server && ./gradlew run`
- Server tests: `cd server && ./gradlew test`
- Run Android unit tests: `./gradlew :app:testDebugUnitTest`
- Run Android UI tests: `./gradlew :app:connectedDebugAndroidTest`
- Build debug APK: `./gradlew :app:assembleDebug`
- Lint: `./gradlew ktlintCheck detekt`
- Migrations run on server start (Flyway)

## Coding Rules
- One feature per PR. Keep diffs small.
- Route handlers stay thin. Business logic lives in `services/` (server) and `domain/` (app).
- ViewModels expose immutable UI state with StateFlow. No Android context in domain code.
- Every new endpoint needs an integration test. Every service rule needs a unit test.
- Never show an entry as trusted unless `status == VERIFIED`.
- No LLM calls in the core search path. Any LLM feature sits behind a feature flag.
- Don't add a library without a reason in the PR description. Prefer the Kotlin/Android standard library and Jetpack over third-party code.
- Keep the app light: no image or video loading in v1, paginate every list (page size 10), no full bank cache on the device.
-

## Low-Memory Target (1GB RAM phones)
- Cold start under 3 seconds on a 1GB Android device
- No background services in v1
- No bitmap-heavy UI
- Use `LazyColumn` with paging, never load all entries at once
- Test on a real low-end device before each beta release

## Data and Privacy Rules
- Never commit `local.properties`, keystores, `.env` files, or API keys.
- Store contributor identity separately from answer content.
- Voice recording (when added) requires explicit opt-in consent and a visible indicator.
- Do not download or store YouTube video files. Store transcripts and links only.
- No personal data in server logs.

## Done Means
- Tests pass, ktlint and detekt are clean.
- The change matches the relevant section of `spec.md`.
- If UI changed, a screenshot is in the PR description.
- New behavior is described in the PR description.