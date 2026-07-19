# Tickers API

## Responsibility
The `:feature:tickers-api` module is the public boundary for retrieving quoted ticker data without exposing the `:feature:tickers` implementation.

## Public APIs
- `Ticker`: public ticker entity with a map of quote-specific values.
- `Currency`: price, volume, market cap, percentage changes, and optional all-time-high data for one quote.
- `MarketCap`, `PercentChangeInterval`, and `AllTimeHigh`: quote value objects.
- `CurrencySymbol`: compatibility alias for `QuoteCurrency` from `:feature:settings-api`.
- `TickersRepository`: contract for list and detail requests using an explicit set of quote currencies.

## Boundaries
- This module may depend on public shared contracts such as `:feature:settings-api`.
- It must not depend on `:feature:tickers` or any feature implementation.
- `:feature:tickers` provides the repository implementation and Hilt binding.
- `:feature:coins` depends on this module to obtain prices, never on the ticker implementation module.

## Quote semantics
Repository callers explicitly request quote currencies. Returned prices are already quoted by the backend and must be read from `Ticker.prices[quoteCurrency]`; missing quotes are treated as unavailable, not as map errors or values requiring conversion.

## Compose stability contract

`Ticker` is treated as stable by its Compose consumer modules through [`config/compose-stability.conf`](../../config/compose-stability.conf). The decision remains in the consumer so this domain module does not depend on Compose or use `@Stable`/`@Immutable`.

When editing `Ticker`:

- preserve the transitive immutability of its properties and value objects;
- keep `prices` read-only and never reuse a `MutableMap` that can be changed after construction;
- if the contract is no longer true, remove or narrow the override and prefer an immutable presentation model in the consumer module;
- run `./gradlew :feature:tickers:debugStabilityCheck` and review the versioned analyzer baseline.

## Tests
- Contract/entity tests: `feature/tickers-api/src/test`.
- Run: `./gradlew :feature:tickers-api:testDebugUnitTest`.
