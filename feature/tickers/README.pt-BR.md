# Feature Tickers

## Responsabilidade
O módulo `:feature:tickers` concentra o fluxo de tickers da Coinpaprika. Ele encapsula as chamadas `GET tickers` e `GET tickers/{id}`, modelos de rede, mapeamento para domínio, repositories, recentes, ViewModels, sort e telas Compose de lista, detalhe e tickers recentes.

## Organização
- `data/api/tickers`: contrato Retrofit `TickersRoutes`.
- `data/model` e `data/dto`: responses serializáveis e mapper para domínio.
- `data/datasource`: datasource remoto e bind Hilt.
- `data/repository`: implementações e binds Hilt de `TickersRepository` e `RecentTickersRepository`.
- `domain`: entidades de ticker/moeda e contratos de repository.
- `presentation/tickers`: tela principal de mercado, tile, estado, controller, sort de tickers e registro de navegação.
- `presentation/ticker`: tela de detalhe de ticker.
- `presentation/recents`: tela e seção de tickers recentes.
- `presentation/sort`: estado, controller, ViewModel e menu de sort usados pela tela de tickers.

## Integração
- O módulo reutiliza `CoinPaprikaRouteFactory`; a implementação com `Retrofit` fica encapsulada no `:network`, que é dependência direta apenas do `:app`.
- `TickersApiModule` cria `TickersRoutes` dentro da feature.
- A feature registra as entradas `CryptoAppDestination.Tickers`, `CryptoAppDestination.TickerDetail` e `CryptoAppDestination.RecentTickers` via Hilt `@IntoSet`.
- A tela principal resolve o header de market review por meio de `MarketOverviewHeaderRegistry` do módulo `:feature:market-review-api`.
- A feature depende de `:common` para logger, dispatchers, tema e botão flutuante compartilhado.
- A feature depende de `:navigation` para o contrato de destinos e `NavigationData`.

## Testes
- Unitários: `feature/tickers/src/test`.
- Compose instrumentado: `feature/tickers/src/androidTest`.
- Utilitários compartilhados vêm de `:testing`.
- Rodar unitários: `./gradlew :feature:tickers:testDebugUnitTest`.
- Compilar testes instrumentados: `./gradlew :feature:tickers:compileDebugAndroidTestKotlin`.
- Rodar instrumentados com device/emulador: `./gradlew :feature:tickers:connectedDebugAndroidTest`.
