# Sync API

Boundary público e reutilizável de sincronização para os módulos feature.

- `FeatureSyncTask`: unidade de sync com chave estável, tipo de alvo, cadência e operação suspensa.
- `SyncTargetProvider`: origem de IDs rastreados, como a futura Favorites.
- `SyncTargetRegistry`: visão consolidada e deduplicada dos alvos.
- `SyncScheduler`: suspende enquanto reconcilia tarefas ativas ou cancela uma tarefa sem expor WorkManager.
- `SyncResult`: sucesso, retry ou falha permanente.

Uma nova feature implementa `FeatureSyncTask`, registra-a com Hilt `@IntoSet` e mantém rede, cache e regras de freshness dentro do próprio módulo. A feature que persiste os alvos chama `scheduleAll()` em sua coroutine após cada alteração para criar ou remover jobs imediatamente.
