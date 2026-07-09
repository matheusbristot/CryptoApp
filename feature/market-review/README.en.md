# Market Review Feature

## Responsibility
The `:feature:market-review` module owns Coinpaprika global market overview. It encapsulates the `GET global` call, domain mapping, repository, ViewModel, renderer, and Compose components used as the tickers screen header.

## Organization
- `data/api/global`: Retrofit `GlobalRoutes` contract.
- `data/model` and `data/dto`: serializable response and domain mapper.
- `data/datasource`: remote datasource and Hilt binding.
- `data/repository`: `MarketReviewRepository` implementation and Hilt binding.
- `domain`: `MarketReview` entity and repository contract.
- `presentation`: `MarketReviewHeaderRenderer` and Hilt binding that registers the feature against the public contract.
- `presentation/market_review`: `MarketReviewViewModel`, state, `MarketStats`, and Compose UI.

## Integration
- The module reuses `CoinPaprikaRouteFactory`; the `Retrofit` implementation stays encapsulated in `:network`, which is a direct dependency only of `:app`.
- `MarketReviewApiModule` creates `GlobalRoutes` inside the feature.
- `MarketReviewPresentationModule` registers `MarketReviewHeaderRenderer` through `@IntoMap` and `@MarketOverviewRendererKey(MarketOverviewRendererIds.MARKET_REVIEW)`.
- `:app` does not import the feature implementation; it uses `MarketOverviewHeaderRegistry` from `:feature:market-review-api`.
- The feature depends on `:common` for logging, dispatchers, and shared theme primitives.
- The feature depends on `:feature:market-review-api` to implement the `MarketOverviewHeaderRenderer` contract.

## Tests
- Unit tests: `feature/market-review/src/test`.
- Compose instrumented tests: `feature/market-review/src/androidTest`.
- Shared test utilities come from `:testing`.
- Run unit tests: `./gradlew :feature:market-review:testDebugUnitTest`.
- Compile instrumented tests: `./gradlew :feature:market-review:compileDebugAndroidTestKotlin`.
