# Feature Market Review

## Responsabilidade
O módulo `:feature:market-review` concentra o market overview global da Coinpaprika. Ele encapsula a chamada `GET global`, o mapeamento para domínio, o repository, o ViewModel e os componentes Compose usados como header da tela de tickers.

## Organização
- `data/api/global`: contrato Retrofit `GlobalRoutes`.
- `data/model` e `data/dto`: response serializável e mapper para domínio.
- `data/datasource`: datasource remoto e bind Hilt.
- `data/repository`: implementação e bind Hilt de `MarketReviewRepository`.
- `domain`: entidade `MarketReview` e contrato de repository.
- `presentation/market_review`: `MarketReviewViewModel`, state, controller, `MarketStats` e UI Compose.

## Integração
- O módulo reutiliza o `Retrofit` singleton fornecido pelo `:app`.
- `MarketReviewApiModule` cria `GlobalRoutes` dentro da feature.
- `:app` consome somente os tipos públicos de presentation para renderizar o header em `MarketContainer`.
- A feature depende de `:common` para logger, dispatchers e tema compartilhado.

## Testes
- Unitários: `feature/market-review/src/test`.
- Compose instrumentado: `feature/market-review/src/androidTest`.
- Utilitários compartilhados vêm de `:testing`.
- Rodar unitários: `./gradlew :feature:market-review:testDebugUnitTest`.
- Compilar testes instrumentados: `./gradlew :feature:market-review:compileDebugAndroidTestKotlin`.
