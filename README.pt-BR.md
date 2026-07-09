# CryptoApp

Leia a documentação em:

- [Português (Brasil)](README.pt-BR.md)
- [English](README.en.md)

## Sobre o app
- Aplicativo Android em Kotlin que consome a API do Coinpaprika para exibir dados de criptomoedas.

## Arquitetura
O projeto adota MVVM com organização em `data`, `domain` e `presentation`, distribuída entre o app e módulos Android Library.
- `app`: application Android, host da navegação, configuração base do Retrofit/Coinpaprika e features que ainda não foram extraídas.
- `:feature:market-review-api`: contrato público para integrar o header de market overview na tela de tickers sem expor a implementação da feature.
- `:feature:market-review`: feature extraída para o market overview global da Coinpaprika (`GET global`), com API route, DTO/model, datasource, repository, contrato de domínio, ViewModel, estado e UI Compose próprios.
- `common`: módulo Android Library para contratos comuns de logger e dispatchers de coroutines, tema/cores compartilhados e bindings Hilt internos.
- `navigation`: módulo Android Library com o contrato, o host de navegação compartilhado e o wrapper injetável `NavigationEntryProviders`.
- `:testing`: módulo Android Library usado apenas em `testImplementation` para utilitários compartilhados de teste, como `MainDispatcherRule` e `clearForTest`.
- A injeção de dependências principal permanece no `app`, com Hilt e `@HiltAndroidApp`; módulos compartilhados e de feature contribuem bindings Hilt próprios.

## Market Review
- O antigo fluxo `data/global`, `datasource/market_review`, `repository/market_review`, `domain/repository/MarketReviewRepository` e `presentation/market_review` saiu do `:app`.
- O provider de `GlobalRoutes` agora vive no módulo `:feature:market-review`, reutilizando o `Retrofit` singleton fornecido pelo `:app`.
- A tela de tickers renderiza o market review por meio de `MarketOverviewHeaderRegistry`, que resolve o renderer registrado pela feature com a chave `MarketOverviewRendererIds.MARKET_REVIEW`.
- O `:app` depende apenas do contrato `:feature:market-review-api`; `MarketReviewViewModel`, `MarketViewState`, `MarketStats` e `MarketReviewComponent` permanecem encapsulados em `:feature:market-review`.
- A implementação da feature registra `MarketReviewHeaderRenderer` em Hilt usando `@IntoMap` e `@MarketOverviewRendererKey`.

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
- kotlinx.coroutines-test.
- JUnit 4.
- Analisador de estabilidade do Compose: `compose.stability.analyzer`.

## Testes
- Unitários: `app/src/test`.
- Unitários do módulo comum: `common/src/test`.
- Unitários da feature de market review: `feature/market-review/src/test`.
- Testes instrumentados Compose da feature de market review: `feature/market-review/src/androidTest`.
- Contratos de integração da feature: `feature/market-review-api/src/main`.
- Utilitários compartilhados de teste: `testing/src/main`.
- Rodar testes unitários do módulo comum: `./gradlew :common:testDebugUnitTest`.
- Rodar testes unitários da feature de market review: `./gradlew :feature:market-review:testDebugUnitTest`.
- Instrumentados: `app/src/androidTest`.
- Rodar testes unitários: `./gradlew :app:testDebugUnitTest`.
- Rodar testes instrumentados: `./gradlew :app:connectedDebugAndroidTest`.
- Validar tudo que cobre a migração de market review: `./gradlew :common:testDebugUnitTest :feature:market-review:testDebugUnitTest :app:testDebugUnitTest`.
- Ao tocar em UI, validar também os testes de Compose em `androidTest`.
