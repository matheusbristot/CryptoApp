# Feature Settings

## Responsibility
The `:feature:settings` module implements persistent application preferences and the Settings Compose screen. It stores requested quote currencies and the selected display quote with AndroidX Preferences DataStore.

## Organization
- `data/SettingsDataStore`: application-scoped DataStore delegate.
- `data/DataStoreSettingsRepository`: `SettingsRepository` implementation backed by Preferences DataStore and exposed as `StateFlow<AppSettings>`.
- `data/SettingsRepositoryModule`: Hilt binding for the public repository contract.
- `presentation/SettingsViewModel`: asynchronous settings actions.
- `presentation/SettingsController`: stable holder for settings state and UI callbacks.
- `presentation/SettingsComponent`: quote selection UI.
- `presentation/SettingsModule`: navigation installer and remembered Controller creation.

## Behavior
- BRL is the default quote.
- At least one and at most three quote currencies remain enabled.
- The selected quote is always part of the requested quote set.
- Selecting a disabled quote enables it when the limit allows.
- Invalid or obsolete stored currency names are ignored safely.
- Settings are consumed through `:feature:settings-api`; consumers do not depend on this implementation module.

## Compose stability
`SettingsComponent` receives a remembered `@Stable SettingsController` instead of a ViewModel. The Controller exposes only `StateFlow<AppSettings>` and event callbacks, keeping lifecycle acquisition and callback construction at the navigation boundary.

## Tests
- Unit tests: `feature/settings/src/test`.
- Run: `./gradlew :feature:settings:testDebugUnitTest`.
