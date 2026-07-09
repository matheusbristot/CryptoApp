# Feature Coins

## Responsabilidade
O módulo `:feature:coins` concentra a listagem base de moedas da Coinpaprika. Ele encapsula a chamada `GET coins`, o mapeamento para domínio, o repository, o ViewModel, o estado e os componentes Compose preservados para uso futuro.

## Organização
- `data/api`: contrato Retrofit `CoinsRoutes`.
- `data/model` e `data/dto`: response serializável e mapper para domínio.
- `data/datasource`: datasource remoto e bind Hilt.
- `data/repository`: implementação e bind Hilt de `CoinRepository`.
- `domain`: entidade `Coin` e contrato de repository.
- `presentation`: tela Compose, widgets, estado, ViewModel e sort da lista de coins.

## Integração
- O módulo reutiliza `CoinPaprikaRouteFactory`; a implementação com `Retrofit` fica encapsulada no `:network`, que é dependência direta apenas do `:app`.
- `CoinsApiModule` cria `CoinsRoutes` dentro da feature.
- A feature depende de `:common` para logger, dispatchers, tema e botão flutuante compartilhado.
- O `:app` ainda não depende de `:feature:coins`, não registra destino de navegação e não renderiza a UI de Coins nesta etapa.

## Testes
- Unitários: `feature/coins/src/test`.
- Compose instrumentado: `feature/coins/src/androidTest`.
- Utilitários compartilhados vêm de `:testing`.
- Rodar unitários: `./gradlew :feature:coins:testDebugUnitTest`.
- Compilar testes instrumentados: `./gradlew :feature:coins:compileDebugAndroidTestKotlin`.
