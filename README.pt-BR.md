# CryptoApp

Leia a documentação em:

- [Português (Brasil)](README.pt-BR.md)
- [English](README.en.md)

## Sobre o app
- Aplicativo Android em Kotlin que consome a API do Coinpaprika para exibir dados de criptomoedas.

## Arquitetura
O projeto adota MVVM com organização em `data`, `domain` e `presentation`.
- `data`: integração com API, DTOs, data sources, repositories e mappers.
- `domain`: entidades e contratos de repositório.
- `presentation`: ViewModels, estados, controllers e componentes Compose.
- `ui`: tema, cores e widgets reutilizáveis.
- `navigation`: módulo Android Library com o contrato, o host de navegação compartilhado e o wrapper injetável `NavigationEntryProviders`.
- A injeção de dependências principal permanece no `app`, com Hilt, `@HiltAndroidApp` e módulos `*Module`, enquanto o módulo `navigation` participa apenas do agrupamento dos providers de navegação.

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
- kotlinx.coroutines-test.
- JUnit 4.
- Analisador de estabilidade do Compose: `compose.stability.analyzer`.

## Testes
- Unitários: `app/src/test`.
- Instrumentados: `app/src/androidTest`.
- Rodar testes unitários: `./gradlew :app:testDebugUnitTest`.
- Rodar testes instrumentados: `./gradlew :app:connectedDebugAndroidTest`.
- Ao tocar em UI, validar também os testes de Compose em `androidTest`.
