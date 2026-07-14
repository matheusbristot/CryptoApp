# Settings API

## Responsabilidade
O módulo `:feature:settings-api` define o contrato público e independente de implementação para configurações de quote do aplicativo.

## APIs públicas
- `QuoteCurrency`: moedas aceitas nos requests de quote da Coinpaprika.
- `AppSettings`: conjunto solicitado e quote selecionada para exibição, incluindo suas invariantes.
- `SettingsRepository`: `StateFlow<AppSettings>` reativo e comandos para habilitar e selecionar quotes.

## Regras
- Pelo menos uma quote deve ser solicitada.
- No máximo `MAX_REQUESTED_QUOTES` (três) podem ser solicitadas.
- A quote selecionada deve estar no conjunto solicitado.
- Consumidores observam Settings por `StateFlow`; persistência é responsabilidade de `:feature:settings`.
- Os valores do backend já são cotados. Consumidores selecionam e formatam a quote correspondente em vez de realizar conversão monetária.

## Boundaries
Este módulo não deve depender de `:feature:settings` nem de qualquer implementação de feature. Tickers e Coins dependem desta API pública.
