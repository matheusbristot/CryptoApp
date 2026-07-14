# Navigation Module

Read the documentation in:

- [Português (Brasil)](README.pt-BR.md)
- [English](README.en.md)

## Purpose
This Android Library module centralizes the application's navigation infrastructure.

It exists to:

- keep only the open navigation contracts in a single place;
- expose the navigation host used by the `app`;
- let future features register routes without depending directly on the application module;
- reduce coupling between navigation and each feature's UI implementation.

## Architecture
The module follows a simple structure focused on contract and navigation host.

### `CryptoAppDestination`
This is an open contract based on `NavKey`. Concrete routes do not live in this module:
each feature declares and serializes its own destinations, without changing a central
`sealed interface` when screens are added or removed.

`RootDestination` marks destinations that take part in root navigation.

### `NavigationRegistry`
This is the only registry injected into the `app`. It collects root destinations
contributed through Hilt/`IntoSet` and internal entries. Each root carries its destination,
label, icon, order, and installer, so a root route is not registered twice.

### `LocalNavigationHostActive`
Exposes whether a retained root navigation host is currently visible. Root tabs remain composed to preserve their back stack and UI state, while feature Controllers use this signal to defer network refreshes until their tab becomes active.

### `NavigationData`
Keeps the `backStack` and the basic navigation operations:

- `forward`
- `back`
- `hasStack`

This class represents the in-memory navigation state and is used by the Compose host.

### `EntryProviderInstaller`
This is the contract used by features to register their screens in `NavDisplay`.

Each feature contributes a function with the type:

```kotlin
EntryProviderScope<CryptoAppDestination>.() -> Unit
```

The separate `EntryProviderInstaller` set is reserved for non-root routes such as details
and internal screens. The registry combines both into one stable lambda for the host.

### `NavigationCryptoAppHilt`
This is the Compose host that builds `NavDisplay`, applies transitions, and connects the `entry providers`.

It receives:
- `modifier`
- `navigationData`
- `entryProviderBlock`

## Flow
1. Each feature declares its concrete destinations.
2. Features contribute `RootNavigationDestination` for roots and `EntryProviderInstaller` only for internal routes through Hilt using `IntoSet`.
3. The `app` creates `NavigationData` with the first ordered root provided by the registry.
4. `NavigationRegistry` combines roots and internal entries and exposes a stable lambda for the host.
5. `NavigationCryptoAppHilt` receives the composed provider block and builds the `NavDisplay`.
6. When a feature calls `navigationData.forward(...)`, the `backStack` is updated.
7. `NavDisplay` renders the new route using the entry registered by the corresponding feature.

## Dependency rules
- The `navigation` module does not know internal feature details.
- Features depend only on the open navigation contracts, but not on the `app`.
- Route declarations belong to their features and remain separate from the modules that register their screens.
- `:feature:tickers` registers the `Tickers`, `RecentTickers`, and `TickerDetail` entries without exposing its UI implementation to the application module.
- `:feature:coins` and `:feature:settings` register their root entries through the same contract.
- The `app` remains the application entry point and only orchestrates the final UI tree.

## Current organization
- `src/main/java/dev/bristot/cryptoapp/navigation`
- `src/test/java/dev/bristot/cryptoapp/navigation`

## Tests
Run the module unit tests:

```bash
./gradlew :navigation:testDebugUnitTest
```

## Note
This module was created to support the evolution toward multiple features. When new modules are added, they should register their entries in the shared contract instead of coupling navigation to the `app`.
