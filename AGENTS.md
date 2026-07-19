# Repository Guidelines

## Project Structure & Module Organization

CryptoApp is a multi-module Kotlin/Compose Android app. `app/` owns the shell; `common/` holds shared UI and utilities; `network/` isolates networking; `testing/` provides helpers through `testImplementation`. Features live under `feature/`; `*-api` modules expose cross-feature contracts. Modules use `src/main`, `src/test` for JVM tests, and `src/androidTest` for device/Compose tests; resources belong in `src/main/res`.

## Navigation Best Practices

`navigation/` owns contracts, registry and Compose host; concrete destinations stay in features. Root routes are retained tabs registered as `RootNavigationDestination`. Internal/detail routes use `EntryProviderInstaller` on current stack. Limit navigation code in `app` to `NavigationRegistry`; retain Gradle feature dependencies for Hilt aggregation.

## Hilt Best Practices

Prefer constructor injection. Contribute route providers with `@IntoSet`. Use `@Provides` only for non-injectable construction and `@Binds` for interface implementations. Keep modules near implementations, scopes clear, and feature-to-feature dependencies limited to `*-api` contracts. Avoid service locators and static access.

## Build, Test, and Development Commands

Use the Gradle wrapper and JDK 17.

- `./gradlew assembleDebug` builds a debug APK.
- `./gradlew testDebugUnitTest` runs debug JVM tests.
- `./gradlew :feature:coins:testDebugUnitTest` runs one module's tests.
- `./gradlew connectedDebugAndroidTest` runs device and Compose UI tests.
- `./gradlew lintDebug` runs Android lint.
- `./gradlew build` runs the full build lifecycle.
- `./gradlew popcornParent -PerrorReportEnabled` reports architectural violations, including invalid module dependencies. Run it after creating or editing any `build.gradle.kts` file.

## Coding Style & Naming Conventions

Follow Kotlin's official style (`kotlin.code.style=official`) with four-space indentation and multiline trailing commas. Use `UpperCamelCase` for types and composables, `lowerCamelCase` for functions and properties, and lowercase packages matching existing paths (for example, `feature.market_review`). Keep data, domain, and presentation responsibilities in their packages. Prefer immutable view state and explicit coroutine dispatchers.

## Compose Stability Contracts

`Coin`, `CoinQuote`, and `Ticker` are domain contracts owned by `:feature:coins-api` and `:feature:tickers-api`. They intentionally remain free of Compose annotations and dependencies; Compose consumers declare their stability through `config/compose-stability.conf`.

When editing any of these three classes, preserve transitive immutability: keep state in `val` properties, do not expose mutable collections or mutable nested objects, and do not mutate collection instances after construction. If a change can no longer satisfy that contract, do not leave the stability override in place merely to silence the analyzer. Remove or narrow the matching configuration and introduce an immutable presentation model in the consuming feature when appropriate. After a compatible change, compile `:feature:coins` and `:feature:tickers` and inspect the Compose Stability Analyzer result. Do not move the domain classes or add Compose dependencies to their API modules solely to express UI stability.

## Testing Guidelines

Tests use JUnit 4, `kotlinx-coroutines-test`, AndroidX Test, and Compose UI testing. Name classes after the subject with a `Test` suffix, such as `CoinListViewModelTest.kt`; use descriptive backtick names. Add JVM tests for business logic and state transitions, and `androidTest` coverage for Compose behavior. No coverage threshold is configured; behavior changes need focused regression tests. Run affected instrumentation tests for UI changes.

## Commit & Pull Request Guidelines

History favors Conventional Commits: `feat(coins): ...`, `fix(ui): ...`, `refactor(navigation): ...`, plus `test`, `docs`, `style`, and `build`. Keep commits focused and subjects imperative. Pull requests should explain user-visible and architectural impact, list validation commands, link issues, and include screenshots or recordings for UI changes. Call out new dependencies, module-boundary changes, and tests not run.

## Mandatory Code Review Gate

Before Codex creates a commit, pushes a branch, marks a pull request ready, approves a pull request, or recommends that changes are safe to upload, it must run an independent review with the project-scoped `cryptoapp_code_reviewer` agent defined in `.codex/agents/cryptoapp-code-reviewer.toml`.

- Freeze the candidate scope before review. For a commit, include every staged, unstaged, untracked, and deleted file intended for that commit. For a push or pull-request approval, include every commit since the merge-base with the target branch and disclose pending local changes.
- Give the reviewer the intended base/target, the exact diff scope, the purpose of the change, and validation evidence already collected.
- The implementation agent must not substitute a self-review in the main thread for this independent gate.
- `P0`, `P1`, or `P2` findings block commit, push, readiness, approval, and any claim that the changes are safe to upload. Fix the findings and run the reviewer again against the updated diff.
- `P3` findings are advisory unless the reviewer shows that their combined impact is blocking.
- An approval is valid only for the exact reviewed content. Any subsequent source, build, test, or configuration change invalidates it and requires another review.
- If the custom reviewer cannot run or cannot inspect the complete scope, do not create the commit, push, approve, or claim readiness. Report the missing review as the blocker.
- This is a Codex workflow gate. Git actions performed outside Codex require a repository hook or CI branch-protection rule for mechanical enforcement.
