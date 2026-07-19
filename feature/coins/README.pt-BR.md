# Feature Coins

## Responsabilidade
O módulo `:feature:coins` concentra a listagem de moedas da Coinpaprika. Ele combina metadados de `GET coins` com preços de tickers na quote selecionada em Settings.

## Organização
- `data/api`: contrato Retrofit `CoinsRoutes`.
- `data/model` e `data/dto`: response serializável e mapper para domínio.
- `data/datasource`: datasource remoto e bind Hilt.
- `data/repository`: implementação e bind Hilt de `CoinRepository`.
- `data/local`: cache Room do detalhe de Coin e fonte local observável.
- `data/sync`: tarefa de atualização registrada no `:sync-api` por multibinding Hilt.
- `domain/usecase`: `GetQuotedCoinsUseCase`, que mantém o cache dos metadados, solicita preços de ticker para uma quote e associa as fontes pelo ID da moeda.
- `presentation`: tela Compose, widgets, estado, ViewModel, `CoinListController` estável e sort da lista de coins.

## Integração
- O módulo reutiliza `CoinPaprikaRouteFactory`; a implementação com `Retrofit` fica encapsulada no `:network`, que é dependência direta apenas do `:app`.
- `CoinsApiModule` cria `CoinsRoutes` dentro da feature.
- A feature depende de `:common` para logger, dispatchers, tema e botão flutuante compartilhado.
- A feature depende de `:feature:settings-api` para a quote selecionada e de `:feature:tickers-api` para preços cotados. Ela não deve depender da implementação `:feature:tickers`.
- Entidades e `CoinRepository` públicos vivem em `:feature:coins-api`, permitindo que Favorites consuma o contrato sem depender da implementação.
- `observeCoin` lê o cache local; `refreshCoin` atualiza `GET coins/{id}` quando o valor tem mais de um minuto. Em background, `CoinSyncTask` recebe os IDs do registro compartilhado, respeita o TTL do repository nos retries e o WorkManager limita a periodicidade a pelo menos 15 minutos.
- Quando a tab Coins fica ativa, `CoinListViewModel` lê as configurações atuais e delega a orquestração ao `GetQuotedCoinsUseCase` somente quando a quote mudou. Tabs ocultas não atualizam, e não existe conversão monetária local.
- A feature declara `CoinsDestination` e contribui sua entrada e metadata raiz via Hilt `@IntoSet`; o app monta a navegação inferior sem conhecer a rota concreta.
- `CoinListModule` lembra `CoinListController` e `SortController`; `CoinListComponent` recebe esses holders estáveis em vez de ViewModels. Seu coroutine scope é um detalhe interno do runtime Compose.

## Testes
- Unitários: `feature/coins/src/test`.
- Compose instrumentado: `feature/coins/src/androidTest`.
- Utilitários compartilhados vêm de `:testing`.
- Rodar unitários: `./gradlew :feature:coins:testDebugUnitTest`.
- Compilar testes instrumentados: `./gradlew :feature:coins:compileDebugAndroidTestKotlin`.
- O comportamento do Controller é coberto por `CoinListControllerTest`.
