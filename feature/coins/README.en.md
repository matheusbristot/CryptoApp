# Feature Coins

## Responsibility
The `:feature:coins` module owns the base Coinpaprika coins list. It encapsulates the `GET coins` call, domain mapping, repository, ViewModel, state, and Compose components preserved for future use.

## Organization
- `data/api`: Retrofit `CoinsRoutes` contract.
- `data/model` and `data/dto`: serializable response and domain mapper.
- `data/datasource`: remote datasource and Hilt bind.
- `data/repository`: `CoinRepository` implementation and Hilt bind.
- `domain`: `Coin` entity and repository contract.
- `presentation`: Compose screen, widgets, state, ViewModel, and coins list sorting.

## Integration
- The module reuses `CoinPaprikaRouteFactory`; the `Retrofit` implementation stays encapsulated in `:network`, which is a direct dependency only of `:app`.
- `CoinsApiModule` creates `CoinsRoutes` inside the feature.
- The feature depends on `:common` for logger, dispatchers, theme, and the shared floating button.
- `:app` does not depend on `:feature:coins` yet, does not register a navigation destination, and does not render the Coins UI in this step.

## Tests
- Unit tests: `feature/coins/src/test`.
- Compose instrumented tests: `feature/coins/src/androidTest`.
- Shared test utilities come from `:testing`.
- Run unit tests: `./gradlew :feature:coins:testDebugUnitTest`.
- Compile instrumented tests: `./gradlew :feature:coins:compileDebugAndroidTestKotlin`.
