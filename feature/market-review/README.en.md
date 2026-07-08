# Market Review Feature

## Responsibility
The `:feature:market-review` module owns Coinpaprika global market overview. It encapsulates the `GET global` call, domain mapping, repository, ViewModel, and Compose components used as the tickers screen header.

## Organization
- `data/api/global`: Retrofit `GlobalRoutes` contract.
- `data/model` and `data/dto`: serializable response and domain mapper.
- `data/datasource`: remote datasource and Hilt binding.
- `data/repository`: `MarketReviewRepository` implementation and Hilt binding.
- `domain`: `MarketReview` entity and repository contract.
- `presentation/market_review`: `MarketReviewViewModel`, state, controller, `MarketStats`, and Compose UI.

## Integration
- The module reuses the singleton `Retrofit` provided by `:app`.
- `MarketReviewApiModule` creates `GlobalRoutes` inside the feature.
- `:app` consumes only the public presentation types to render the header in `MarketContainer`.
- The feature depends on `:common` for logging, dispatchers, and shared theme primitives.

## Tests
- Unit tests: `feature/market-review/src/test`.
- Compose instrumented tests: `feature/market-review/src/androidTest`.
- Shared test utilities come from `:testing`.
- Run unit tests: `./gradlew :feature:market-review:testDebugUnitTest`.
- Compile instrumented tests: `./gradlew :feature:market-review:compileDebugAndroidTestKotlin`.
