# Feature Market Review

## Responsabilidade
O módulo `:feature:market-review` concentra o market overview global da Coinpaprika. Ele encapsula a chamada `GET global`, o mapeamento para domínio, o repository, o ViewModel, o renderer e os componentes Compose usados como header da tela de tickers.

## Organização
- `data/api/global`: contrato Retrofit `GlobalRoutes`.
- `data/model` e `data/dto`: response serializável e mapper para domínio.
- `data/datasource`: datasource remoto e bind Hilt.
- `data/repository`: implementação e bind Hilt de `MarketReviewRepository`.
- `domain`: entidade `MarketReview` e contrato de repository.
- `presentation`: `MarketReviewHeaderRenderer` e bind Hilt para registrar a feature no contrato público.
- `presentation/market_review`: `MarketReviewViewModel`, state, `MarketStats` e UI Compose.

## Integração
- O módulo reutiliza `CoinPaprikaRouteFactory`; a implementação com `Retrofit` fica encapsulada no `:network`, que é dependência direta apenas do `:app`.
- `MarketReviewApiModule` cria `GlobalRoutes` dentro da feature.
- `MarketReviewPresentationModule` registra `MarketReviewHeaderRenderer` via `@IntoMap` e `@MarketOverviewRendererKey(MarketOverviewRendererIds.MARKET_REVIEW)`.
- O renderer recebe `MarketOverviewQuoteData` da tela de tickers e formata market cap e volume de 24 horas na quote selecionada em Settings.
- O `:app` inclui este módulo para disponibilizar seu registro Hilt, mas o host de tickers importa somente `MarketOverviewHeaderRegistry` e outros contratos de `:feature:market-review-api`.
- A feature depende de `:common` para logger, dispatchers e tema compartilhado.
- A feature depende de `:feature:market-review-api` para implementar o contrato `MarketOverviewHeaderRenderer`.
- Os valores já cotados pelo backend são exibidos diretamente; a feature não realiza conversão monetária.

## Testes
- Unitários: `feature/market-review/src/test`.
- Compose instrumentado: `feature/market-review/src/androidTest`.
- Utilitários compartilhados vêm de `:testing`.
- Rodar unitários: `./gradlew :feature:market-review:testDebugUnitTest`.
- Compilar testes instrumentados: `./gradlew :feature:market-review:compileDebugAndroidTestKotlin`.
