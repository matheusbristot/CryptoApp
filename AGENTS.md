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

## Testing Guidelines

Tests use JUnit 4, `kotlinx-coroutines-test`, AndroidX Test, and Compose UI testing. Name classes after the subject with a `Test` suffix, such as `CoinListViewModelTest.kt`; use descriptive backtick names. Add JVM tests for business logic and state transitions, and `androidTest` coverage for Compose behavior. No coverage threshold is configured; behavior changes need focused regression tests. Run affected instrumentation tests for UI changes.

## Commit & Pull Request Guidelines

History favors Conventional Commits: `feat(coins): ...`, `fix(ui): ...`, `refactor(navigation): ...`, plus `test`, `docs`, `style`, and `build`. Keep commits focused and subjects imperative. Pull requests should explain user-visible and architectural impact, list validation commands, link issues, and include screenshots or recordings for UI changes. Call out new dependencies, module-boundary changes, and tests not run.
