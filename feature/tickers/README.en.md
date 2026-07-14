# Feature Tickers

## Responsibility
The `:feature:tickers` module implements the Coinpaprika tickers flow. It encapsulates the `GET tickers` and `GET tickers/{id}` calls, network models, domain mapping, repository implementations, recents, ViewModels, sorting, and Compose screens. Public ticker contracts live in `:feature:tickers-api`.

## Organization
- `data/api/tickers`: Retrofit `TickersRoutes` contract.
- `data/model` and `data/dto`: serializable responses and domain mapper.
- `data/datasource`: remote datasource and Hilt binding.
- `data/repository`: implementations and Hilt bindings for `TickersRepository` and the feature-private `RecentTickersRepository`.
- `domain`: feature-private recent-ticker contract; public ticker/currency entities and `TickersRepository` are provided by `:feature:tickers-api`.
- `presentation/tickers`: main market screen, tile, state, controller, ticker sorting, and navigation registration.
- `presentation/ticker`: ticker detail screen.
- `presentation/recents`: recent tickers screen and section.
- `presentation/sort`: sorting state, controller, ViewModel, and menu used by the tickers screen.

## Integration
- The module reuses `CoinPaprikaRouteFactory`; the `Retrofit` implementation stays encapsulated in `:network`, which is a direct dependency only of `:app`.
- `TickersApiModule` creates `TickersRoutes` inside the feature.
- Multiple requested quotes are serialized as the single comma-separated Coinpaprika parameter (for example, `quotes=BRL,BTC`).
- The implementation depends on `:feature:tickers-api`; consumers that only need ticker data depend on the API module instead of this module.
- The feature registers `CryptoAppDestination.Tickers`, `CryptoAppDestination.TickerDetail`, and `CryptoAppDestination.RecentTickers` entries through Hilt `@IntoSet`.
- The main screen resolves the market review header through `MarketOverviewHeaderRegistry` from `:feature:market-review-api`.
- The feature depends on `:common` for logging, dispatchers, theme primitives, and the shared floating button.
- The feature depends on `:navigation` for the destination contract and `NavigationData`.
- The navigation host exposes whether a root tab is active. Ticker Controllers refresh against the current `SettingsRepository.settings.value` only when reopened and settings changed; hidden tabs make no quote requests.
- Compose screens receive remembered stable Controllers that expose `StateFlow` and event callbacks without passing ViewModels through the UI tree.

## Tests
- Unit tests: `feature/tickers/src/test`.
- Compose instrumented tests: `feature/tickers/src/androidTest`.
- Shared test utilities come from `:testing`.
- Run unit tests: `./gradlew :feature:tickers:testDebugUnitTest`.
- Compile instrumented tests: `./gradlew :feature:tickers:compileDebugAndroidTestKotlin`.
- Run instrumented tests with a device/emulator: `./gradlew :feature:tickers:connectedDebugAndroidTest`.
