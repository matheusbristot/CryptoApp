# Feature Coins

## Responsibility
The `:feature:coins` module owns the Coinpaprika coins list. It combines metadata from `GET coins` with ticker prices for the quote currency selected in Settings.

## Organization
- `data/api`: Retrofit `CoinsRoutes` contract.
- `data/model` and `data/dto`: serializable response and domain mapper.
- `data/datasource`: remote datasource and Hilt bind.
- `data/repository`: `CoinRepository` implementation and Hilt bind.
- `domain`: `Coin` entity and repository contract.
- `domain/usecase`: `GetQuotedCoinsUseCase`, which caches coin metadata, requests ticker prices for one quote, and joins both sources by coin ID.
- `presentation`: Compose screen, widgets, state, ViewModel, stable `CoinListController`, and coins list sorting.

## Integration
- The module reuses `CoinPaprikaRouteFactory`; the `Retrofit` implementation stays encapsulated in `:network`, which is a direct dependency only of `:app`.
- `CoinsApiModule` creates `CoinsRoutes` inside the feature.
- The feature depends on `:common` for logger, dispatchers, theme, and the shared floating button.
- The feature depends on `:feature:settings-api` for the selected quote and on `:feature:tickers-api` for quoted prices. It must not depend on the `:feature:tickers` implementation.
- When the Coins tab becomes active, `CoinListViewModel` reads the current settings and delegates data orchestration to `GetQuotedCoinsUseCase` only when the selected quote changed. Hidden tabs do not refresh, and no local currency conversion is performed.
- The feature registers `CryptoAppDestination.Coins` through Hilt `@IntoSet`, and the app exposes it in bottom navigation.
- `CoinListModule` remembers `CoinListController` and `SortController`; `CoinListComponent` receives these stable holders instead of ViewModels. Its coroutine scope is an internal Compose runtime detail.

## Tests
- Unit tests: `feature/coins/src/test`.
- Compose instrumented tests: `feature/coins/src/androidTest`.
- Shared test utilities come from `:testing`.
- Run unit tests: `./gradlew :feature:coins:testDebugUnitTest`.
- Compile instrumented tests: `./gradlew :feature:coins:compileDebugAndroidTestKotlin`.
- Controller behavior is covered by `CoinListControllerTest`.
