# Market Review API

## Responsibility
The `:feature:market-review-api` module defines the public contract used by `:app` to render the market overview header without knowing the feature implementation.

## Public APIs
- `MarketOverviewHeaderRenderer`: Compose contract implemented by features that expose a market overview header.
- `MarketOverviewRendererKey`: Hilt `@MapKey` used to register renderers by identifier.
- `MarketOverviewRendererIds`: known IDs for available renderers.
- `MarketOverviewHeaderRegistry`: injectable registry that encapsulates the Hilt-generated `Map<String, MarketOverviewHeaderRenderer>`.

## Usage
- The feature implements `MarketOverviewHeaderRenderer`.
- The feature registers the implementation with `@IntoMap` and `@MarketOverviewRendererKey`.
- `:app` injects `MarketOverviewHeaderRegistry` and resolves the required renderer with `required(id)`.

Example:

```kotlin
val renderer = marketOverviewHeaderRegistry.required(
    MarketOverviewRendererIds.MARKET_REVIEW
)
```

## Rules
- This module must not depend on feature implementations.
- This module may depend on shared contracts required for rendering, such as theme and Compose runtime.
