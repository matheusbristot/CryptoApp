# Common Module

## Responsabilidade
O módulo `common` concentra contratos, infraestrutura e tema compartilhados por outros módulos Android.

## APIs públicas
- `CryptoLogger`: contrato para emissão de logs.
- `DispatcherProvider`: contrato para acesso aos dispatchers `main`, `io` e `default`.

## UI compartilhada
- Primitivas de tema, tokens de cor, tipografia e helpers de texto usados por `:app` e features extraídas.

## Implementações
As implementações default (`DefaultCryptoLogger`, `AndroidLogWriter` e `DefaultDispatchers`) são internas ao módulo. O consumo externo deve ocorrer pelos contratos públicos e pelos bindings Hilt definidos internamente.

## Consumidores
- `:app` usa `common` para logger, dispatchers e tema compartilhado.
- `:feature:market-review` usa `common` para `DispatcherProvider`, `CryptoLogger` e `CryptoTheme`, sem depender do módulo de aplicação.

## Testes
- Testes unitários: `common/src/test`.
- Rodar testes: `./gradlew :common:testDebugUnitTest`.
