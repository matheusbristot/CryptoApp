# Navigation Module

Read the documentation in:

- [Português (Brasil)](README.pt-BR.md)
- [English](README.en.md)

## Purpose
This Android Library module centralizes the application's navigation infrastructure.

It exists to:

- keep the destination contract in a single place;
- expose the navigation host used by the `app`;
- let future features register routes without depending directly on the application module;
- reduce coupling between navigation and each feature's UI implementation.

## Architecture
The module follows a simple structure focused on contract and navigation host.

### `CryptoAppDestination`
Defines the possible app routes with a `sealed interface` and `NavKey`.

The current contract includes:
- `Tickers`
- `TickerDetail`

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

### `NavigationEntryProviders`
This is an injectable wrapper that lives in the `navigation` module and collects the `EntryProviderInstaller` set.

It exists to:

- keep the multibinding `Set` away from the Compose boundary;
- expose a single stable lambda to the host;
- preserve Hilt/`IntoSet` usage in the features without leaking that detail into `MainActivity`.

### `NavigationCryptoAppHilt`
This is the Compose host that builds `NavDisplay`, applies transitions, and connects the `entry providers`.

It receives:
- `modifier`
- `navigationData`
- `entryProviderBlock`

## Flow
1. The `app` creates and injects a `NavigationData` instance.
2. Features contribute `EntryProviderInstaller` bindings through Hilt using `IntoSet`.
3. `NavigationEntryProviders` groups the providers and exposes a stable lambda for the host.
4. `NavigationCryptoAppHilt` receives the composed provider block and builds the `NavDisplay`.
5. When a feature calls `navigationData.forward(...)`, the `backStack` is updated.
6. `NavDisplay` renders the new route using the entry registered by the corresponding feature.

## Dependency rules
- The `navigation` module does not know internal feature details.
- Features depend on the navigation contract, but not on the `app`.
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
