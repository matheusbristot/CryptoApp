# Feature Tickers

## Responsabilidade
O módulo `:feature:tickers` implementa o fluxo de tickers da Coinpaprika. Ele encapsula as chamadas `GET tickers` e `GET tickers/{id}`, modelos de rede, mapeamento, implementações de repositories, recentes, ViewModels, sort e telas Compose. Os contratos públicos ficam em `:feature:tickers-api`.

## Organização
- `data/api/tickers`: contrato Retrofit `TickersRoutes`.
- `data/model` e `data/dto`: responses serializáveis e mapper para domínio.
- `data/datasource`: datasource remoto e bind Hilt.
- `data/repository`: implementações e binds Hilt de `TickersRepository` e do `RecentTickersRepository`, privado da feature.
- `domain`: contrato privado de tickers recentes; entidades públicas de ticker/moeda e `TickersRepository` vêm de `:feature:tickers-api`.
- `presentation/tickers`: tela principal de mercado, tile, estado, controller, sort de tickers e registro de navegação.
- `presentation/ticker`: tela de detalhe de ticker.
- `presentation/recents`: tela e seção de tickers recentes.
- `presentation/sort`: estado, controller, ViewModel e menu de sort usados pela tela de tickers.

## Integração
- O módulo reutiliza `CoinPaprikaRouteFactory`; a implementação com `Retrofit` fica encapsulada no `:network`, que é dependência direta apenas do `:app`.
- `TickersApiModule` cria `TickersRoutes` dentro da feature.
- Múltiplas quotes são serializadas no único parâmetro separado por vírgulas exigido pela Coinpaprika, por exemplo `quotes=BRL,BTC`.
- A implementação depende de `:feature:tickers-api`; consumidores que precisam somente dos dados dependem do módulo API, não desta implementação.
- A feature registra as entradas `CryptoAppDestination.Tickers`, `CryptoAppDestination.TickerDetail` e `CryptoAppDestination.RecentTickers` via Hilt `@IntoSet`.
- A tela principal resolve o header de market review por meio de `MarketOverviewHeaderRegistry` do módulo `:feature:market-review-api`.
- A feature depende de `:common` para logger, dispatchers, tema e botão flutuante compartilhado.
- A feature depende de `:navigation` para o contrato de destinos e `NavigationData`.
- O host de navegação informa se uma tab root está ativa. Os Controllers de Tickers atualizam usando `SettingsRepository.settings.value` somente ao reabrir e quando Settings mudou; tabs ocultas não fazem requests de quote.
- As telas Compose recebem Controllers estáveis e lembrados, expondo `StateFlow` e callbacks sem propagar ViewModels pela árvore de UI.

## Testes
- Unitários: `feature/tickers/src/test`.
- Compose instrumentado: `feature/tickers/src/androidTest`.
- Utilitários compartilhados vêm de `:testing`.
- Rodar unitários: `./gradlew :feature:tickers:testDebugUnitTest`.
- Compilar testes instrumentados: `./gradlew :feature:tickers:compileDebugAndroidTestKotlin`.
- Rodar instrumentados com device/emulador: `./gradlew :feature:tickers:connectedDebugAndroidTest`.
