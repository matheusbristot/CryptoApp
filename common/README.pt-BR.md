# Common Module

## Responsabilidade
O módulo `common` concentra contratos e infraestrutura compartilhada por outros módulos Android.

## APIs públicas
- `CryptoLogger`: contrato para emissão de logs.
- `DispatcherProvider`: contrato para acesso aos dispatchers `main`, `io` e `default`.

## Implementações
As implementações default (`DefaultCryptoLogger`, `AndroidLogWriter` e `DefaultDispatchers`) são internas ao módulo. O consumo externo deve ocorrer pelos contratos públicos e pelos bindings Hilt definidos internamente.

## Testes
- Testes unitários: `common/src/test`.
- Rodar testes: `./gradlew :common:testDebugUnitTest`.
