# Market Review API

## Responsabilidade
O módulo `:feature:market-review-api` define o contrato público usado pelo `:app` para renderizar o header de market overview sem conhecer a implementação da feature.

## APIs públicas
- `MarketOverviewHeaderRenderer`: contrato Compose implementado por features que expõem um header de market overview.
- `MarketOverviewRendererKey`: `@MapKey` usado por Hilt para registrar renderers por identificador.
- `MarketOverviewRendererIds`: IDs conhecidos para renderers disponíveis.
- `MarketOverviewHeaderRegistry`: registry injetável que encapsula o `Map<String, MarketOverviewHeaderRenderer>` gerado por Hilt.

## Uso
- A feature implementa `MarketOverviewHeaderRenderer`.
- A feature registra a implementação com `@IntoMap` e `@MarketOverviewRendererKey`.
- O `:app` injeta `MarketOverviewHeaderRegistry` e resolve o renderer necessário com `required(id)`.

Exemplo:

```kotlin
val renderer = marketOverviewHeaderRegistry.required(
    MarketOverviewRendererIds.MARKET_REVIEW
)
```

## Regras
- Este módulo não deve depender de implementações de feature.
- Este módulo pode depender de contratos compartilhados necessários para renderização, como tema e Compose runtime.
