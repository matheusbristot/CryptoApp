# Sync API

Public, reusable synchronization boundary for feature modules.

- `FeatureSyncTask`: a synchronization unit with a stable key, target type, cadence, and suspending operation.
- `SyncTargetProvider`: a source of tracked IDs, such as the future Favorites feature.
- `SyncTargetRegistry`: a consolidated, deduplicated view of targets.
- `SyncScheduler`: suspends while reconciling active tasks or cancels one task without exposing WorkManager.
- `SyncResult`: success, retry, or permanent failure.

A new feature implements `FeatureSyncTask`, registers it with Hilt `@IntoSet`, and keeps network, cache, and freshness rules inside its own module. A target-owning feature calls `scheduleAll()` from its coroutine after persisted targets change so jobs are created or removed immediately.
