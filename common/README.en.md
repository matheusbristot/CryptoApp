# Common Module

## Responsibility
The `common` module centralizes contracts, infrastructure, and theme primitives shared by other Android modules.

## Public APIs
- `CryptoLogger`: contract for emitting logs.
- `DispatcherProvider`: contract for accessing the `main`, `io`, and `default` dispatchers.

## Shared UI
- Theme primitives, color tokens, typography, and text color helpers used by `:app` and extracted features.
- Floating scroll-to-top button (`ui/widgets/floating_button`) used by the app and by features that render long lists.

## Implementations
Default implementations (`DefaultCryptoLogger`, `AndroidLogWriter`, and `DefaultDispatchers`) are internal to the module. External consumers should depend on the public contracts and the Hilt bindings defined internally.

## Consumers
- `:app` uses `common` for logging, dispatchers, and shared theme primitives.
- `:feature:market-review` uses `common` for `DispatcherProvider`, `CryptoLogger`, and `CryptoTheme`, without depending on the application module.
- `:feature:tickers` uses `common` for dispatchers, logging, theme primitives, and the shared floating button.

## Tests
- Unit tests: `common/src/test`.
- Floating button Compose instrumented tests: `common/src/androidTest`.
- Run tests: `./gradlew :common:testDebugUnitTest`.
- Compile instrumented tests: `./gradlew :common:compileDebugAndroidTestKotlin`.
