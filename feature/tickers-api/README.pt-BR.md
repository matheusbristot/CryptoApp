# Tickers API

## Responsabilidade
O módulo `:feature:tickers-api` é o boundary público para consultar tickers cotados sem expor a implementação `:feature:tickers`.

## APIs públicas
- `Ticker`: entidade pública com mapa de valores específicos por quote.
- `Currency`: preço, volume, market cap, variações percentuais e dados opcionais de máxima histórica para uma quote.
- `MarketCap`, `PercentChangeInterval` e `AllTimeHigh`: value objects da cotação.
- `CurrencySymbol`: alias de compatibilidade para `QuoteCurrency`, de `:feature:settings-api`.
- `TickersRepository`: contrato para requests de lista e detalhe com conjunto explícito de quotes.

## Boundaries
- Este módulo pode depender de contratos públicos compartilhados, como `:feature:settings-api`.
- Ele não deve depender de `:feature:tickers` nem de qualquer implementação de feature.
- `:feature:tickers` fornece a implementação do repository e o binding Hilt.
- `:feature:coins` depende deste módulo para obter preços, nunca do módulo de implementação de tickers.

## Semântica de quotes
Callers solicitam as quotes explicitamente. Os preços retornados já são cotados pelo backend e devem ser lidos de `Ticker.prices[quoteCurrency]`; quotes ausentes são tratadas como indisponíveis, não como erro de mapa nem como valores que exigem conversão.

## Testes
- Testes de contratos e entidades: `feature/tickers-api/src/test`.
- Executar: `./gradlew :feature:tickers-api:testDebugUnitTest`.
