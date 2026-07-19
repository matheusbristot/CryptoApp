# Sync WorkManager

Implementação Android da sincronização persistente do CryptoApp.

- Agenda um `PeriodicWorkRequest` único para cada `FeatureSyncTask` ativa.
- Reconcilia trabalho persistido: tasks removidas do grafo ou sem alvos são canceladas.
- Aplica `NetworkType.CONNECTED`, backoff exponencial e intervalo mínimo de 15 minutos.
- Resolve tarefas registradas pelas features via Hilt e delega o trabalho sem conhecer repositories ou DTOs.
- Converte `SyncResult` em resultado do WorkManager e limita retries consecutivos.
- Os retries reutilizam a freshness do repository como checkpoint, evitando novo request para IDs já atualizados com sucesso.

Features devem depender de `:sync-api`, não deste módulo. O estudo completo, fluxo de extensão e decisões de cadência estão em [docs/sync/README.pt-BR.md](../docs/sync/README.pt-BR.md).
