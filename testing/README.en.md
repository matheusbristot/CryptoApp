# Testing Module

## Responsibility
The `:testing` module centralizes shared test utilities. It should be consumed only through `testImplementation(project(":testing"))`, avoiding duplication between `:app` and features.

## Public APIs
- `MainDispatcherRule`: configures `Dispatchers.Main` with a `StandardTestDispatcher`.
- `clearForTest`: reflection helper that invokes `onCleared` on ViewModels during unit tests.

## Consumers
- `:app`
- `:feature:market-review`

## Rules
- Do not put production code in this module.
- Do not use `implementation(project(":testing"))`; consumption should stay test-only.
