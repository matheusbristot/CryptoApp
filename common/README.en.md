# Common Module

## Responsibility
The `common` module centralizes contracts and infrastructure shared by other Android modules.

## Public APIs
- `CryptoLogger`: contract for emitting logs.
- `DispatcherProvider`: contract for accessing the `main`, `io`, and `default` dispatchers.

## Implementations
Default implementations (`DefaultCryptoLogger`, `AndroidLogWriter`, and `DefaultDispatchers`) are internal to the module. External consumers should depend on the public contracts and the Hilt bindings defined internally.

## Tests
- Unit tests: `common/src/test`.
- Run tests: `./gradlew :common:testDebugUnitTest`.
