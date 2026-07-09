# Feature Tickers

## Responsibility
The `:feature:tickers` module owns the Coinpaprika tickers flow. It encapsulates the `GET tickers` and `GET tickers/{id}` calls, network models, domain mapping, repositories, recents, ViewModels, sorting, and Compose screens for the list, detail, and recent tickers.

## Organization
- `data/api/tickers`: Retrofit `TickersRoutes` contract.
- `data/model` and `data/dto`: serializable responses and domain mapper.
- `data/datasource`: remote datasource and Hilt binding.
- `data/repository`: implementations and Hilt bindings for `TickersRepository` and `RecentTickersRepository`.
- `domain`: ticker/currency entities and repository contracts.
- `presentation/tickers`: main market screen, tile, state, controller, ticker sorting, and navigation registration.
- `presentation/ticker`: ticker detail screen.
- `presentation/recents`: recent tickers screen and section.
- `presentation/sort`: sorting state, controller, ViewModel, and menu used by the tickers screen.

## Integration
- The module reuses the singleton `Retrofit` provided by `:app`.
- `TickersApiModule` creates `TickersRoutes` inside the feature.
- The feature registers `CryptoAppDestination.Tickers`, `CryptoAppDestination.TickerDetail`, and `CryptoAppDestination.RecentTickers` entries through Hilt `@IntoSet`.
- The main screen resolves the market review header through `MarketOverviewHeaderRegistry` from `:feature:market-review-api`.
- The feature depends on `:common` for logging, dispatchers, theme primitives, and the shared floating button.
- The feature depends on `:navigation` for the destination contract and `NavigationData`.

## Tests
- Unit tests: `feature/tickers/src/test`.
- Compose instrumented tests: `feature/tickers/src/androidTest`.
- Shared test utilities come from `:testing`.
- Run unit tests: `./gradlew :feature:tickers:testDebugUnitTest`.
- Compile instrumented tests: `./gradlew :feature:tickers:compileDebugAndroidTestKotlin`.
- Run instrumented tests with a device/emulator: `./gradlew :feature:tickers:connectedDebugAndroidTest`.
