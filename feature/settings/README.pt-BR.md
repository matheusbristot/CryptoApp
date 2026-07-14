# Feature Settings

## Responsabilidade
O módulo `:feature:settings` implementa as preferências persistentes do aplicativo e a tela Compose de Settings. Ele armazena as quotes solicitadas e a quote selecionada para exibição com AndroidX Preferences DataStore.

## Organização
- `data/SettingsDataStore`: delegate do DataStore no escopo da aplicação.
- `data/DataStoreSettingsRepository`: implementação de `SettingsRepository` baseada em Preferences DataStore e exposta como `StateFlow<AppSettings>`.
- `data/SettingsRepositoryModule`: binding Hilt do contrato público.
- `presentation/SettingsViewModel`: ações assíncronas de configuração.
- `presentation/SettingsController`: holder estável para estado e callbacks da UI.
- `presentation/SettingsComponent`: UI de seleção de quotes.
- `presentation/SettingsModule`: installer de navegação e criação lembrada do Controller.

## Comportamento
- BRL é a quote padrão.
- Entre uma e três quotes permanecem habilitadas.
- A quote selecionada sempre pertence ao conjunto solicitado.
- Selecionar uma quote desabilitada também a habilita quando o limite permite.
- Nomes armazenados inválidos ou obsoletos são ignorados com segurança.
- Settings é consumido por meio de `:feature:settings-api`; consumidores não dependem deste módulo de implementação.

## Estabilidade Compose
`SettingsComponent` recebe um `@Stable SettingsController` lembrado em vez do ViewModel. O Controller expõe somente `StateFlow<AppSettings>` e callbacks, mantendo aquisição de lifecycle e criação das referências no boundary da navegação.

## Testes
- Testes unitários: `feature/settings/src/test`.
- Executar: `./gradlew :feature:settings:testDebugUnitTest`.
