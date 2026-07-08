# Common Module

## Responsibility
The `common` module centralizes contracts, infrastructure, and theme primitives shared by other Android modules.

## Public APIs
- `CryptoLogger`: contract for emitting logs.
- `DispatcherProvider`: contract for accessing the `main`, `io`, and `default` dispatchers.

## Shared UI
- Theme primitives, color tokens, typography, and text color helpers used by `:app` and extracted features.

## Implementations
Default implementations (`DefaultCryptoLogger`, `AndroidLogWriter`, and `DefaultDispatchers`) are internal to the module. External consumers should depend on the public contracts and the Hilt bindings defined internally.

## Consumers
- `:app` uses `common` for logging, dispatchers, and shared theme primitives.
- `:feature:market-review` uses `common` for `DispatcherProvider`, `CryptoLogger`, and `CryptoTheme`, without depending on the application module.

## Tests
- Unit tests: `common/src/test`.
- Run tests: `./gradlew :common:testDebugUnitTest`.
