# CryptoApp

Leia a documentação em:

- [Português (Brasil)](README.pt-BR.md)
- [English](README.en.md)

## Sobre o app
- Aplicativo Android em Kotlin que consome a API do Coinpaprika para exibir dados de criptomoedas.

## Arquitetura
O projeto adota MVVM com organização em `data`, `domain` e `presentation`, distribuída entre módulos Android Library.
- `app`: application Android e host da navegação.
- `:network`: configuração base de rede para conectar na Coinpaprika, mantendo o `Retrofit` encapsulado e fornecendo `CoinPaprikaRouteFactory` para o grafo Hilt do app.
- `:feature:market-review-api`: contrato público usado pelo host de tickers para renderizar o header sem importar tipos da implementação.
- `:feature:market-review`: feature extraída para o market overview global da Coinpaprika (`GET global`), com API route, DTO/model, datasource, repository, contrato de domínio, ViewModel, estado e UI Compose próprios.
- `:feature:tickers-api`: entidades públicas de ticker e contrato `TickersRepository` compartilhado com outras features.
- `:feature:tickers`: implementação de rede/dados de ticker, injeção, fluxos de lista/detalhe/recentes, sort e UI Compose.
- `:feature:coins`: lista de moedas da Coinpaprika (`GET coins`) enriquecida com preços obtidos via `:feature:tickers-api` na quote selecionada em Settings.
- `:feature:settings-api`: modelo público de configurações, quotes suportadas e contrato do repository.
- `:feature:settings`: repository com Preferences DataStore, ViewModel, Controller Compose estável, tela e registro de navegação.
- `common`: módulo Android Library para contratos comuns de logger e dispatchers de coroutines, tema/cores compartilhados, botão flutuante reutilizável e bindings Hilt internos.
- `navigation`: módulo Android Library com o contrato, o host de navegação compartilhado e o wrapper injetável `NavigationEntryProviders`.
- `:testing`: módulo Android Library usado apenas em `testImplementation` para utilitários compartilhados de teste, como `MainDispatcherRule` e `clearForTest`.
- A injeção de dependências principal permanece no `app`, com Hilt e `@HiltAndroidApp`; módulos compartilhados e de feature contribuem bindings Hilt próprios.

## Coins
- O antigo fluxo de Coins saiu do `:app` e agora vive em `:feature:coins`, com packages simplificados por já estar dentro do domínio da feature.
- O provider de `CoinsRoutes` agora vive no módulo `:feature:coins`, reutilizando `CoinPaprikaRouteFactory` fornecida pelo `:network`.
- O destino Coins está disponível na navegação inferior do app.
- `CoinListViewModel` combina os metadados das moedas com preços de tickers solicitados na quote selecionada.
- `:feature:coins` depende do boundary público `:feature:tickers-api`, nunca da implementação `:feature:tickers`.
- `CoinListComponent` recebe instâncias lembradas de `CoinListController` e `SortController`, sem receber ViewModels.

## Settings
- Settings está disponível na navegação inferior e persiste os dados com Preferences DataStore.
- `SettingsRepository.settings` expõe `StateFlow<AppSettings>` com as quotes solicitadas e a quote selecionada para exibição.
- A quote selecionada orienta requests de tickers, formatação de lista/detalhe, Market Overview e preços de Coins; os valores do backend já são cotados e não passam por conversão local.
- Tickers e Coins preservam o estado enquanto ocultos, mas adiam o refresh da quote até a tab ficar ativa novamente. Reabrir sem mudança relevante reutiliza o resultado atual.
- Deve existir entre uma e três quotes habilitadas, e a quote selecionada deve permanecer habilitada.
- `SettingsComponent` recebe um `SettingsController` estável e lembrado, sem receber o ViewModel.

## Market Review
- O antigo fluxo `data/global`, `datasource/market_review`, `repository/market_review`, `domain/repository/MarketReviewRepository` e `presentation/market_review` saiu do `:app`.
- O provider de `GlobalRoutes` agora vive no módulo `:feature:market-review`, reutilizando `CoinPaprikaRouteFactory` fornecida pelo `:network`.
- A tela de tickers renderiza o market review dentro de `:feature:tickers` por meio de `MarketOverviewHeaderRegistry`, que resolve o renderer registrado pela feature com a chave `MarketOverviewRendererIds.MARKET_REVIEW`.
- O Market Overview recebe market cap agregado e volume de 24 horas da quote selecionada por meio de `MarketOverviewQuoteData`.
- O host de tickers consome somente o contrato de `:feature:market-review-api`. O `:app` inclui o módulo de implementação para disponibilizar seu registro Hilt, mas os tipos internos permanecem encapsulados.
- A implementação da feature registra `MarketReviewHeaderRenderer` em Hilt usando `@IntoMap` e `@MarketOverviewRendererKey`.

## Tickers
- O antigo fluxo `data/api/tickers`, `data/datasource/tickers`, `data/repository/tickers`, `data/repository/recents`, domínio de ticker e `presentation/tickers|ticker|recents` saiu do `:app`.
- O provider de `TickersRoutes` agora vive em `:feature:tickers`, reutilizando `CoinPaprikaRouteFactory` fornecida pelo `:network`.
- Entidades públicas e `TickersRepository` vivem em `:feature:tickers-api`; DTOs, datasources, implementação, bindings Hilt, recentes e UI ficam em `:feature:tickers`.
- `:feature:tickers` registra as entradas de navegação `Tickers`, `TickerDetail` e `RecentTickers` via Hilt `@IntoSet`.
- O botão flutuante compartilhado foi movido para `:common`; o sort específico de tickers permanece encapsulado na feature.
- Requests e formatação monetária de tickers seguem a quote selecionada no `SettingsRepository`.

## Bibliotecas
### Google / AndroidX
- Jetpack Compose: `activity-compose`, `compose-ui`, `material3`, `ui-tooling`, `ui-tooling-preview`, `material-icons-extended`.
- Lifecycle e ViewModel: `lifecycle-runtime-ktx`, `lifecycle-viewmodel-compose`, `lifecycle-viewmodel-navigation3`.
- Navigation3: `navigation3-runtime`, `navigation3-ui`.
- Hilt: `hilt-android`, `hilt-lifecycle-viewmodel-compose`, `hilt-android-compiler`.
- Base e testes AndroidX: `core-ktx`, `androidx.test.ext:junit`, `espresso-core`, `compose-ui-test-junit4`, `ui-test-manifest`.

### Outras
- Retrofit 3 com conversor para Kotlinx Serialization.
- Kotlinx Serialization.
- Kotlinx Coroutines.
- AndroidX Preferences DataStore.
- kotlinx.coroutines-test.
- JUnit 4.
- Analisador de estabilidade do Compose: `compose.stability.analyzer`.

## Testes
- Unitários: `app/src/test`.
- Unitários do módulo comum: `common/src/test`.
- Unitários da feature de coins: `feature/coins/src/test`.
- Unitários da feature de tickers: `feature/tickers/src/test`.
- Testes de contrato da API de tickers: `feature/tickers-api/src/test`.
- Unitários da feature de market review: `feature/market-review/src/test`.
- Unitários da feature de settings: `feature/settings/src/test`.
- Testes instrumentados Compose da feature de coins: `feature/coins/src/androidTest`.
- Testes instrumentados Compose da feature de tickers: `feature/tickers/src/androidTest`.
- Testes instrumentados Compose da feature de market review: `feature/market-review/src/androidTest`.
- Contratos de integração da feature: `feature/market-review-api/src/main`.
- Utilitários compartilhados de teste: `testing/src/main`.
- Rodar testes unitários do módulo comum: `./gradlew :common:testDebugUnitTest`.
- Rodar testes unitários da feature de coins: `./gradlew :feature:coins:testDebugUnitTest`.
- Rodar testes unitários da feature de tickers: `./gradlew :feature:tickers:testDebugUnitTest`.
- Rodar testes da API de tickers: `./gradlew :feature:tickers-api:testDebugUnitTest`.
- Rodar testes da feature de settings: `./gradlew :feature:settings:testDebugUnitTest`.
- Rodar testes unitários da feature de market review: `./gradlew :feature:market-review:testDebugUnitTest`.
- Instrumentados: `app/src/androidTest`.
- Rodar testes unitários: `./gradlew :app:testDebugUnitTest`.
- Rodar testes instrumentados: `./gradlew :app:connectedDebugAndroidTest`.
- Validar coins: `./gradlew :feature:coins:testDebugUnitTest :feature:coins:compileDebugKotlin :feature:coins:compileDebugAndroidTestKotlin`.
- Validar tickers e app: `./gradlew :common:testDebugUnitTest :common:compileDebugAndroidTestKotlin :feature:tickers:testDebugUnitTest :feature:tickers:compileDebugAndroidTestKotlin :app:testDebugUnitTest :app:compileDebugKotlin :navigation:compileDebugKotlin`.
- Validar tudo que cobre a migração de market review: `./gradlew :common:testDebugUnitTest :feature:market-review:testDebugUnitTest :app:testDebugUnitTest`.
- Ao tocar em UI, validar também os testes de Compose em `androidTest`.
