# Navigation Module

Leia a documentação em:

- [Português (Brasil)](README.pt-BR.md)
- [English](README.en.md)

## Objetivo
Este módulo Android Library centraliza a infraestrutura de navegação do aplicativo.

Ele existe para:

- manter o contrato de destinos em um lugar único;
- expor o host de navegação usado pelo `app`;
- permitir que futuras features registrem suas rotas sem depender diretamente do módulo de aplicação;
- reduzir acoplamento entre a navegação e a implementação visual de cada feature.

## Arquitetura
O módulo segue uma estrutura simples, focada em contrato e host de navegação.

### `CryptoAppDestination`
Define as rotas possíveis do app com `sealed interface` e `NavKey`.

Hoje o contrato possui:
- `Tickers`
- `Coins`
- `Settings`
- `RecentTickers`
- `TickerDetail`

### `LocalNavigationHostActive`
Expõe se um host root retido está visível. As tabs permanecem compostas para preservar back stack e estado de UI, enquanto os Controllers usam esse sinal para adiar refreshes de rede até a tab ficar ativa.

### `NavigationData`
Mantém o `backStack` e as operações básicas de navegação:

- `forward`
- `back`
- `hasStack`

Essa classe representa o estado de navegação em memória e é usada pelo host Compose.

### `EntryProviderInstaller`
É o contrato usado pelas features para registrar suas telas no `NavDisplay`.

Cada feature contribui com uma função do tipo:

```kotlin
EntryProviderScope<CryptoAppDestination>.() -> Unit
```

### `NavigationEntryProviders`
É um wrapper injetável que vive no módulo `navigation` e reúne o conjunto de `EntryProviderInstaller`.

Ele existe para:

- manter o `Set` de multibinding fora da borda do Compose;
- entregar ao host uma única lambda estável;
- preservar o uso de Hilt/`IntoSet` nas features sem expor esse detalhe para a `MainActivity`.

### `NavigationCryptoAppHilt`
É o host Compose que monta o `NavDisplay`, aplica as transições e conecta os `entry providers`.

Ele recebe:
- `modifier`
- `navigationData`
- `entryProviderBlock`

## Fluxo
1. O `app` cria e injeta uma instância de `NavigationData`.
2. As features contribuem com `EntryProviderInstaller` via Hilt usando `IntoSet`.
3. `NavigationEntryProviders` agrupa os providers e expõe uma lambda estável para o host.
4. `NavigationCryptoAppHilt` recebe o bloco composto de providers e monta o `NavDisplay`.
5. Quando uma feature chama `navigationData.forward(...)`, o `backStack` é atualizado.
6. O `NavDisplay` renderiza a nova rota usando a entrada registrada pela feature correspondente.
7. Quando a root selecionada muda, `LocalNavigationHostActive` invalida os hosts retidos para que a nova feature ativa execute um refresh condicional.

## Regras de dependência
- O módulo `navigation` não conhece detalhes internos das features.
- As features dependem do contrato de navegação, mas não do `app`.
- `:feature:tickers` registra as entradas de `Tickers`, `RecentTickers` e `TickerDetail` sem expor sua implementação visual ao módulo de aplicação.
- `:feature:coins` e `:feature:settings` registram suas roots pelo mesmo contrato.
- O `app` continua como ponto de entrada da aplicação e apenas orquestra a montagem final da árvore de UI.

## Organização atual
- `src/main/java/dev/bristot/cryptoapp/navigation`
- `src/test/java/dev/bristot/cryptoapp/navigation`

## Testes
Executar os testes unitários do módulo:

```bash
./gradlew :navigation:testDebugUnitTest
```

## Observação
Este módulo foi criado para suportar a evolução para múltiplas features. Quando novos módulos forem adicionados, eles devem registrar suas entradas no contrato compartilhado em vez de acoplar a navegação ao `app`.
