# Testing Module

## Responsibility
The `:testing` module centralizes shared test utilities and dependencies. It should be consumed only by test configurations through `testImplementation(project(":testing"))` or `androidTestImplementation(project(":testing"))`, avoiding duplication between `:app` and features.

## Public APIs
- `MainDispatcherRule`: configures `Dispatchers.Main` with a `StandardTestDispatcher`.
- `clearForTest`: reflection helper that invokes `onCleared` on ViewModels during unit tests.
- Combot 1.0.7: shared API for instrumented Compose test robots.

## Consumers
- `:app`
- `:common`
- `:feature:coins`
- `:feature:favorites`
- `:feature:market-review`
- `:feature:settings`
- `:feature:tickers`

## Rules
- Do not put production code in this module.
- Do not use `implementation(project(":testing"))`; consumption must remain restricted to `testImplementation` and `androidTestImplementation`.
