# CryptoApp

Read the documentation in:

- [Português (Brasil)](README.pt-BR.md)
- [English](README.en.md)

## About the app
- Android app written in Kotlin that consumes the Coinpaprika API to display cryptocurrency data.

## Architecture
The project follows MVVM with organization in `data`, `domain`, and `presentation`.
- `data`: API integration, DTOs, data sources, repositories, and mappers.
- `domain`: entities and repository contracts.
- `presentation`: ViewModels, states, controllers, and Compose components.
- `ui`: theme, colors, and reusable widgets.
- `navigation`: Android Library module with the shared navigation contract, host, and the injectable `NavigationEntryProviders` wrapper.
- Main dependency injection remains in `app`, with Hilt, `@HiltAndroidApp`, and `*Module` modules, while the `navigation` module only groups navigation providers.

## Libraries
### Google / AndroidX
- Jetpack Compose: `activity-compose`, `compose-ui`, `material3`, `ui-tooling`, `ui-tooling-preview`, `material-icons-extended`.
- Lifecycle and ViewModel: `lifecycle-runtime-ktx`, `lifecycle-viewmodel-compose`, `lifecycle-viewmodel-navigation3`.
- Navigation3: `navigation3-runtime`, `navigation3-ui`.
- Hilt: `hilt-android`, `hilt-lifecycle-viewmodel-compose`, `hilt-android-compiler`.
- AndroidX base and test libraries: `core-ktx`, `androidx.test.ext:junit`, `espresso-core`, `compose-ui-test-junit4`, `ui-test-manifest`.

### Other
- Retrofit 3 with a Kotlinx Serialization converter.
- Kotlinx Serialization.
- kotlinx.coroutines-test.
- JUnit 4.
- Compose stability analyzer: `compose.stability.analyzer`.

## Tests
- Unit tests: `app/src/test`.
- Instrumented tests: `app/src/androidTest`.
- Run unit tests: `./gradlew :app:testDebugUnitTest`.
- Run instrumented tests: `./gradlew :app:connectedDebugAndroidTest`.
- When touching UI, also validate the Compose tests under `androidTest`.
