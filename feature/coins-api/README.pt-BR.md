# Feature Coins API

Boundary público de Coins. Contém as entidades `Coin`/`CoinQuote` e o contrato `CoinRepository`, incluindo observação do cache e refresh do detalhe por ID.

Consumers como a futura Favorites dependem deste módulo e nunca de `:feature:coins`, que mantém Retrofit, Room, mapeadores e a tarefa de sync encapsulados.

## Contrato de estabilidade Compose

`Coin` e `CoinQuote` são tratados como estáveis pelos módulos Compose consumidores por meio de [`config/compose-stability.conf`](../../config/compose-stability.conf). A decisão permanece no consumidor para que este módulo de domínio não dependa de Compose nem use `@Stable`/`@Immutable`.

Ao editar essas entidades:

- preserve a imutabilidade transitiva, usando propriedades `val` e objetos aninhados imutáveis;
- não exponha coleções mutáveis nem altere coleções depois da construção;
- se o contrato deixar de ser verdadeiro, remova ou restrinja o override e prefira um modelo de apresentação imutável no módulo consumidor;
- rode `./gradlew :feature:coins:debugStabilityCheck` e confira o baseline versionado do analyzer.
