# CryptoApp

Read the documentation in:

- [Português (Brasil)](README.pt-BR.md)
- [English](README.en.md)

## About the app
- Android app written in Kotlin that consumes the Coinpaprika API to display cryptocurrency data.

## Architecture
The project follows MVVM with organization in `data`, `domain`, and `presentation`, split across Android Library modules.
- `app`: Android application and navigation host.
- `:network`: base network configuration for connecting to Coinpaprika, keeping `Retrofit` encapsulated and providing `CoinPaprikaRouteFactory` to the app Hilt graph.
- `:feature:market-review-api`: public contract used by the tickers host to render the market overview header without importing implementation types.
- `:feature:market-review`: extracted feature for Coinpaprika global market overview (`GET global`), with its own API route, DTO/model, datasource, repository, domain contract, ViewModel, state, and Compose UI.
- `:feature:tickers-api`: public ticker entities and `TickersRepository` contract shared with other features.
- `:feature:tickers`: ticker network/data implementation, dependency injection, list/detail/recents flows, sorting, and Compose UI.
- `:feature:coins`: Coinpaprika coins list (`GET coins`) enriched with prices obtained through `:feature:tickers-api` for the quote selected in Settings.
- `:feature:settings-api`: public settings model, supported quote currencies, and repository contract.
- `:feature:settings`: Preferences DataStore repository, Settings ViewModel, stable Compose controller, screen, and navigation registration.
- `common`: Android Library module for shared logger and coroutine dispatcher contracts, shared theme/colors, reusable floating button, and internal Hilt bindings.
- `navigation`: Android Library module with the shared navigation contract, host, and the injectable `NavigationEntryProviders` wrapper.
- `:testing`: Android Library module used only through `testImplementation` for shared test utilities such as `MainDispatcherRule` and `clearForTest`.
- Main dependency injection remains in `app`, with Hilt and `@HiltAndroidApp`; shared and feature modules also contribute their own Hilt bindings.

## Coins
- The former Coins flow was moved out of `:app` and now lives in `:feature:coins`, with simplified packages because the module already scopes the feature domain.
- The `CoinsRoutes` provider now lives in `:feature:coins`, reusing `CoinPaprikaRouteFactory` provided by `:network`.
- The Coins destination is available from the app bottom navigation.
- `CoinListViewModel` combines coin metadata with ticker prices requested in the selected quote currency.
- `:feature:coins` depends on the public `:feature:tickers-api` boundary and never on the `:feature:tickers` implementation.
- `CoinListComponent` receives remembered `CoinListController` and `SortController` instances instead of ViewModels.

## Settings
- Settings is available from the app bottom navigation and persists data with Preferences DataStore.
- `SettingsRepository.settings` exposes a `StateFlow<AppSettings>` with requested quote currencies and the selected display quote.
- The selected quote drives ticker requests, ticker/detail formatting, Market Overview values, and Coin prices; backend values are already quoted and are never converted locally.
- Tickers and Coins keep their tab state while hidden, but defer quote refreshes until the tab becomes active again. Reopening without a relevant settings change reuses the current result.
- At least one and at most three quote currencies can be enabled, and the selected quote must remain enabled.
- `SettingsComponent` receives a remembered stable `SettingsController` instead of a ViewModel.

## Market Review
- The former `data/global`, `datasource/market_review`, `repository/market_review`, `domain/repository/MarketReviewRepository`, and `presentation/market_review` flow was moved out of `:app`.
- The `GlobalRoutes` provider now lives in `:feature:market-review`, reusing `CoinPaprikaRouteFactory` provided by `:network`.
- The tickers screen renders market review inside `:feature:tickers` through `MarketOverviewHeaderRegistry`, which resolves the renderer registered by the feature with the `MarketOverviewRendererIds.MARKET_REVIEW` key.
- Market Overview receives aggregated market cap and 24-hour volume for the selected quote through `MarketOverviewQuoteData`.
- The tickers host consumes only the `:feature:market-review-api` contract. `:app` includes the implementation module so its Hilt renderer registration is available, while implementation types remain encapsulated.
- The feature implementation registers `MarketReviewHeaderRenderer` in Hilt using `@IntoMap` and `@MarketOverviewRendererKey`.

## Tickers
- The former `data/api/tickers`, `data/datasource/tickers`, `data/repository/tickers`, `data/repository/recents`, ticker domain, and `presentation/tickers|ticker|recents` flow was moved out of `:app`.
- The `TickersRoutes` provider now lives in `:feature:tickers`, reusing `CoinPaprikaRouteFactory` provided by `:network`.
- Public ticker entities and `TickersRepository` live in `:feature:tickers-api`; DTOs, datasources, repository implementation, Hilt bindings, recents, and UI stay in `:feature:tickers`.
- `:feature:tickers` registers the `Tickers`, `TickerDetail`, and `RecentTickers` navigation entries through Hilt `@IntoSet`.
- The shared floating button moved to `:common`; ticker-specific sorting remains encapsulated in the feature.
- Ticker requests and monetary formatting follow the selected quote currency from `SettingsRepository`.

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
- AndroidX Preferences DataStore.
- kotlinx.coroutines-test.
- JUnit 4.
- Compose stability analyzer: `compose.stability.analyzer`.

## Tests
- Unit tests: `app/src/test`.
- Common module unit tests: `common/src/test`.
- Coins feature unit tests: `feature/coins/src/test`.
- Tickers feature unit tests: `feature/tickers/src/test`.
- Tickers API contract tests: `feature/tickers-api/src/test`.
- Market review feature unit tests: `feature/market-review/src/test`.
- Settings feature unit tests: `feature/settings/src/test`.
- Coins feature Compose instrumented tests: `feature/coins/src/androidTest`.
- Tickers feature Compose instrumented tests: `feature/tickers/src/androidTest`.
- Market review feature Compose instrumented tests: `feature/market-review/src/androidTest`.
- Feature integration contracts: `feature/market-review-api/src/main`.
- Shared test utilities: `testing/src/main`.
- Run common module unit tests: `./gradlew :common:testDebugUnitTest`.
- Run coins feature unit tests: `./gradlew :feature:coins:testDebugUnitTest`.
- Run tickers feature unit tests: `./gradlew :feature:tickers:testDebugUnitTest`.
- Run tickers API tests: `./gradlew :feature:tickers-api:testDebugUnitTest`.
- Run settings feature unit tests: `./gradlew :feature:settings:testDebugUnitTest`.
- Run market review feature unit tests: `./gradlew :feature:market-review:testDebugUnitTest`.
- Instrumented tests: `app/src/androidTest`.
- Run unit tests: `./gradlew :app:testDebugUnitTest`.
- Run instrumented tests: `./gradlew :app:connectedDebugAndroidTest`.
- Validate coins: `./gradlew :feature:coins:testDebugUnitTest :feature:coins:compileDebugKotlin :feature:coins:compileDebugAndroidTestKotlin`.
- Validate tickers and app: `./gradlew :common:testDebugUnitTest :common:compileDebugAndroidTestKotlin :feature:tickers:testDebugUnitTest :feature:tickers:compileDebugAndroidTestKotlin :app:testDebugUnitTest :app:compileDebugKotlin :navigation:compileDebugKotlin`.
- Validate the market review migration coverage: `./gradlew :common:testDebugUnitTest :feature:market-review:testDebugUnitTest :app:testDebugUnitTest`.
- When touching UI, also validate the Compose tests under `androidTest`.
