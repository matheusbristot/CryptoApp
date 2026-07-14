# Navigation Module

Leia a documentação em:

- [Português (Brasil)](README.pt-BR.md)
- [English](README.en.md)

## Objetivo
Este módulo Android Library centraliza a infraestrutura de navegação do aplicativo.

Ele existe para:

- manter apenas os contratos abertos de navegação em um lugar único;
- expor o host de navegação usado pelo `app`;
- permitir que futuras features registrem suas rotas sem depender diretamente do módulo de aplicação;
- reduzir acoplamento entre a navegação e a implementação visual de cada feature.

## Arquitetura
O módulo segue uma estrutura simples, focada em contrato e host de navegação.

### `CryptoAppDestination`
É um contrato aberto baseado em `NavKey`. As rotas concretas não vivem neste módulo:
cada feature declara e serializa seus próprios destinos, sem alterar uma `sealed interface`
central ao adicionar ou remover telas.

`RootDestination` identifica os destinos que participam da navegação raiz.

### `NavigationRegistry`
É o único registro injetado no `app`. Agrupa os destinos raiz contribuídos via
Hilt/`IntoSet` e as entradas internas. Cada root contém destino, label, ícone, ordem e
seu próprio instalador; assim, uma rota raiz não é registrada duas vezes.

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

O conjunto separado de `EntryProviderInstaller` é reservado para rotas não-root, como
detalhes e telas internas. O registry combina ambos em uma única lambda estável para o host.

### `NavigationCryptoAppHilt`
É o host Compose que monta o `NavDisplay`, aplica as transições e conecta os `entry providers`.

Ele recebe:
- `modifier`
- `navigationData`
- `entryProviderBlock`

## Fluxo
1. Cada feature declara seus destinos concretos.
2. As features contribuem com `RootNavigationDestination` para roots e `EntryProviderInstaller` apenas para rotas internas, via Hilt usando `IntoSet`.
3. O `app` cria `NavigationData` com o primeiro root ordenado fornecido pelo registry.
4. `NavigationRegistry` combina roots e entradas internas e expõe uma lambda estável para o host.
5. `NavigationCryptoAppHilt` recebe o bloco composto de providers e monta o `NavDisplay`.
6. Quando uma feature chama `navigationData.forward(...)`, o `backStack` é atualizado.
7. O `NavDisplay` renderiza a nova rota usando a entrada registrada pela feature correspondente.

## Regras de dependência
- O módulo `navigation` não conhece detalhes internos das features.
- As features dependem apenas dos contratos abertos de navegação, mas não do `app`.
- As declarações de rota pertencem às features e ficam separadas dos módulos que registram suas telas.
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
