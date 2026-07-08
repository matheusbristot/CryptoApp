# CryptoApp

Read the documentation in:

- [Português (Brasil)](README.pt-BR.md)
- [English](README.en.md)

## About the app
- Android app written in Kotlin that consumes the Coinpaprika API to display cryptocurrency data.

## Architecture
The project follows MVVM with organization in `data`, `domain`, and `presentation`, split across the app and Android Library modules.
- `app`: Android application, navigation host, base Retrofit/Coinpaprika configuration, and features that have not been extracted yet.
- `:feature:market-review`: extracted feature for Coinpaprika global market overview (`GET global`), with its own API route, DTO/model, datasource, repository, domain contract, ViewModel, state, controller, and Compose UI.
- `common`: Android Library module for shared logger and coroutine dispatcher contracts, shared theme/colors, and internal Hilt bindings.
- `navigation`: Android Library module with the shared navigation contract, host, and the injectable `NavigationEntryProviders` wrapper.
- `:testing`: Android Library module used only through `testImplementation` for shared test utilities such as `MainDispatcherRule` and `clearForTest`.
- Main dependency injection remains in `app`, with Hilt and `@HiltAndroidApp`; shared and feature modules also contribute their own Hilt bindings.

## Market Review
- The former `data/global`, `datasource/market_review`, `repository/market_review`, `domain/repository/MarketReviewRepository`, and `presentation/market_review` flow was moved out of `:app`.
- The `GlobalRoutes` provider now lives in `:feature:market-review`, reusing the singleton `Retrofit` provided by `:app`.
- The tickers screen still renders market review as its header through `MarketContainer`, importing the feature public types (`MarketReviewController`, `MarketViewState`, `MarketStats`, and `MarketReviewComponent`).

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
- Kotlinx Coroutines.
- kotlinx.coroutines-test.
- JUnit 4.
- Compose stability analyzer: `compose.stability.analyzer`.

## Tests
- Unit tests: `app/src/test`.
- Common module unit tests: `common/src/test`.
- Market review feature unit tests: `feature/market-review/src/test`.
- Market review feature Compose instrumented tests: `feature/market-review/src/androidTest`.
- Shared test utilities: `testing/src/main`.
- Run common module unit tests: `./gradlew :common:testDebugUnitTest`.
- Run market review feature unit tests: `./gradlew :feature:market-review:testDebugUnitTest`.
- Instrumented tests: `app/src/androidTest`.
- Run unit tests: `./gradlew :app:testDebugUnitTest`.
- Run instrumented tests: `./gradlew :app:connectedDebugAndroidTest`.
- Validate the market review migration coverage: `./gradlew :common:testDebugUnitTest :feature:market-review:testDebugUnitTest :app:testDebugUnitTest`.
- When touching UI, also validate the Compose tests under `androidTest`.
