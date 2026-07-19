# Sync WorkManager

Android implementation of CryptoApp's persistent synchronization.

- Schedules one unique `PeriodicWorkRequest` for each active `FeatureSyncTask`.
- Reconciles persisted work: tasks removed from the graph or left without targets are cancelled.
- Applies `NetworkType.CONNECTED`, exponential backoff, and a minimum 15-minute interval.
- Resolves feature-registered tasks through Hilt and delegates work without knowing about repositories or DTOs.
- Maps `SyncResult` to WorkManager results and limits consecutive retries.
- Retries reuse repository freshness as a checkpoint, avoiding another request for IDs already refreshed successfully.

Features must depend on `:sync-api`, not this module. The complete study, extension flow, and cadence decisions are documented in [docs/sync/README.en.md](../docs/sync/README.en.md).
