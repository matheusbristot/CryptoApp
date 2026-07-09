# Common Module

## Responsabilidade
O módulo `common` concentra contratos, infraestrutura e tema compartilhados por outros módulos Android.

## APIs públicas
- `CryptoLogger`: contrato para emissão de logs.
- `DispatcherProvider`: contrato para acesso aos dispatchers `main`, `io` e `default`.

## UI compartilhada
- Primitivas de tema, tokens de cor, tipografia e helpers de texto usados por `:app` e features extraídas.
- Botão flutuante de retorno ao topo (`ui/widgets/floating_button`) usado pelo app e por features que exibem listas longas.

## Implementações
As implementações default (`DefaultCryptoLogger`, `AndroidLogWriter` e `DefaultDispatchers`) são internas ao módulo. O consumo externo deve ocorrer pelos contratos públicos e pelos bindings Hilt definidos internamente.

## Consumidores
- `:app` usa `common` para logger, dispatchers e tema compartilhado.
- `:feature:market-review` usa `common` para `DispatcherProvider`, `CryptoLogger` e `CryptoTheme`, sem depender do módulo de aplicação.
- `:feature:tickers` usa `common` para dispatchers, logger, tema e botão flutuante compartilhado.

## Testes
- Testes unitários: `common/src/test`.
- Testes instrumentados Compose do botão flutuante: `common/src/androidTest`.
- Rodar testes: `./gradlew :common:testDebugUnitTest`.
- Compilar instrumentados: `./gradlew :common:compileDebugAndroidTestKotlin`.
