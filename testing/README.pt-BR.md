# Testing Module

## Responsabilidade
O módulo `:testing` centraliza utilitários e dependências compartilhadas de testes. Ele deve ser consumido apenas por configurações de teste, via `testImplementation(project(":testing"))` ou `androidTestImplementation(project(":testing"))`, evitando duplicação entre `:app` e features.

## APIs públicas
- `MainDispatcherRule`: configura `Dispatchers.Main` com `StandardTestDispatcher`.
- `clearForTest`: helper por reflexão para acionar `onCleared` em ViewModels durante testes unitários.
- Combot 1.0.7: API compartilhada para robots de testes instrumentados do Compose.

## Consumidores
- `:app`
- `:common`
- `:feature:coins`
- `:feature:favorites`
- `:feature:market-review`
- `:feature:settings`
- `:feature:tickers`

## Regras
- Não colocar código de produção neste módulo.
- Não usar `implementation(project(":testing"))`; o consumo deve permanecer restrito a `testImplementation` e `androidTestImplementation`.
