# Settings API

## Responsibility
The `:feature:settings-api` module defines the public, implementation-independent contract for application quote settings.

## Public APIs
- `QuoteCurrency`: currencies supported by Coinpaprika quote requests.
- `AppSettings`: requested quote set and selected display quote, including its invariants.
- `SettingsRepository`: reactive `StateFlow<AppSettings>` plus commands to enable and select quotes.

## Rules
- At least one quote must be requested.
- No more than `MAX_REQUESTED_QUOTES` (three) may be requested.
- The selected quote must be requested.
- Consumers observe settings through `StateFlow`; persistence remains an implementation concern of `:feature:settings`.
- Backend values are already quoted. Consumers select and format the matching quote instead of performing currency conversion.

## Boundaries
This module must not depend on `:feature:settings` or any other feature implementation. Tickers and Coins depend on this public API.
