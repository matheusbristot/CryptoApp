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
- `:feature:market-review-api`: public contract for integrating the market overview header into the tickers screen without exposing the feature implementation.
- `:feature:market-review`: extracted feature for Coinpaprika global market overview (`GET global`), with its own API route, DTO/model, datasource, repository, domain contract, ViewModel, state, and Compose UI.
- `:feature:tickers`: extracted feature for the tickers list, detail, recents, sorting, domain, datasource, repository, and Compose UI.
- `:feature:coins`: extracted feature for the base Coinpaprika coins list (`GET coins`), with its own API route, DTO/model, datasource, repository, domain contract, ViewModel, state, and Compose UI. There is no visual/navigation consumption in `:app` yet.
- `common`: Android Library module for shared logger and coroutine dispatcher contracts, shared theme/colors, reusable floating button, and internal Hilt bindings.
- `navigation`: Android Library module with the shared navigation contract, host, and the injectable `NavigationEntryProviders` wrapper.
- `:testing`: Android Library module used only through `testImplementation` for shared test utilities such as `MainDispatcherRule` and `clearForTest`.
- Main dependency injection remains in `app`, with Hilt and `@HiltAndroidApp`; shared and feature modules also contribute their own Hilt bindings.

## Coins
- The former `data/api/coins`, `data/datasource/coins`, `data/repository/coins`, `domain/repository/CoinRepository`, `Coin` entity, and `presentation/coin_list` flow was moved out of `:app`.
- The `CoinsRoutes` provider now lives in `:feature:coins`, reusing `CoinPaprikaRouteFactory` provided by `:network`.
- The feature keeps its Compose UI and `CoinListViewModel` encapsulated for future use.
- `:app` does not depend on `:feature:coins` yet and does not register a Coins navigation destination in this step.

## Market Review
- The former `data/global`, `datasource/market_review`, `repository/market_review`, `domain/repository/MarketReviewRepository`, and `presentation/market_review` flow was moved out of `:app`.
- The `GlobalRoutes` provider now lives in `:feature:market-review`, reusing `CoinPaprikaRouteFactory` provided by `:network`.
- The tickers screen renders market review inside `:feature:tickers` through `MarketOverviewHeaderRegistry`, which resolves the renderer registered by the feature with the `MarketOverviewRendererIds.MARKET_REVIEW` key.
- `:app` depends only on the `:feature:market-review-api` contract; `MarketReviewViewModel`, `MarketViewState`, `MarketStats`, and `MarketReviewComponent` remain encapsulated in `:feature:market-review`.
- The feature implementation registers `MarketReviewHeaderRenderer` in Hilt using `@IntoMap` and `@MarketOverviewRendererKey`.

## Tickers
- The former `data/api/tickers`, `data/datasource/tickers`, `data/repository/tickers`, `data/repository/recents`, ticker domain, and `presentation/tickers|ticker|recents` flow was moved out of `:app`.
- The `TickersRoutes` provider now lives in `:feature:tickers`, reusing `CoinPaprikaRouteFactory` provided by `:network`.
- `:feature:tickers` registers the `Tickers`, `TickerDetail`, and `RecentTickers` navigation entries through Hilt `@IntoSet`.
- The shared floating button moved to `:common`; ticker-specific sorting remains encapsulated in the feature.

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
- Coins feature unit tests: `feature/coins/src/test`.
- Tickers feature unit tests: `feature/tickers/src/test`.
- Market review feature unit tests: `feature/market-review/src/test`.
- Coins feature Compose instrumented tests: `feature/coins/src/androidTest`.
- Tickers feature Compose instrumented tests: `feature/tickers/src/androidTest`.
- Market review feature Compose instrumented tests: `feature/market-review/src/androidTest`.
- Feature integration contracts: `feature/market-review-api/src/main`.
- Shared test utilities: `testing/src/main`.
- Run common module unit tests: `./gradlew :common:testDebugUnitTest`.
- Run coins feature unit tests: `./gradlew :feature:coins:testDebugUnitTest`.
- Run tickers feature unit tests: `./gradlew :feature:tickers:testDebugUnitTest`.
- Run market review feature unit tests: `./gradlew :feature:market-review:testDebugUnitTest`.
- Instrumented tests: `app/src/androidTest`.
- Run unit tests: `./gradlew :app:testDebugUnitTest`.
- Run instrumented tests: `./gradlew :app:connectedDebugAndroidTest`.
- Validate coins: `./gradlew :feature:coins:testDebugUnitTest :feature:coins:compileDebugKotlin :feature:coins:compileDebugAndroidTestKotlin`.
- Validate tickers and app: `./gradlew :common:testDebugUnitTest :common:compileDebugAndroidTestKotlin :feature:tickers:testDebugUnitTest :feature:tickers:compileDebugAndroidTestKotlin :app:testDebugUnitTest :app:compileDebugKotlin :navigation:compileDebugKotlin`.
- Validate the market review migration coverage: `./gradlew :common:testDebugUnitTest :feature:market-review:testDebugUnitTest :app:testDebugUnitTest`.
- When touching UI, also validate the Compose tests under `androidTest`.
