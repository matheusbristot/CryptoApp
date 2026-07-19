# Feature Coins API

Public Coins boundary. It contains the `Coin`/`CoinQuote` entities and the `CoinRepository` contract, including cache observation and detail refresh by ID.

Consumers such as the future Favorites feature depend on this module and never on `:feature:coins`, which keeps Retrofit, Room, mappers, and the synchronization task encapsulated.

## Compose stability contract

`Coin` and `CoinQuote` are treated as stable by their Compose consumer modules through [`config/compose-stability.conf`](../../config/compose-stability.conf). The decision remains in the consumer so this domain module does not depend on Compose or use `@Stable`/`@Immutable`.

When editing these entities:

- preserve transitive immutability with `val` properties and immutable nested objects;
- do not expose mutable collections or mutate collection instances after construction;
- if the contract is no longer true, remove or narrow the override and prefer an immutable presentation model in the consumer module;
- run `./gradlew :feature:coins:debugStabilityCheck` and review the versioned analyzer baseline.
