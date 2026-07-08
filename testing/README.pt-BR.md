# Testing Module

## Responsabilidade
O módulo `:testing` centraliza utilitários compartilhados de testes. Ele deve ser usado apenas via `testImplementation(project(":testing"))`, evitando duplicação entre `:app` e features.

## APIs públicas
- `MainDispatcherRule`: configura `Dispatchers.Main` com `StandardTestDispatcher`.
- `clearForTest`: helper por reflexão para acionar `onCleared` em ViewModels durante testes unitários.

## Consumidores
- `:app`
- `:feature:market-review`

## Regras
- Não colocar código de produção neste módulo.
- Não usar `implementation(project(":testing"))`; o consumo deve ser restrito a testes.
