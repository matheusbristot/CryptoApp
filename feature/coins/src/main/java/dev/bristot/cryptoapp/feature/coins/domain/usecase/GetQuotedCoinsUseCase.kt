package dev.bristot.cryptoapp.feature.coins.domain.usecase

import dev.bristot.cryptoapp.coroutines.dispatcher.DispatcherProvider
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.feature.coins.domain.entity.CoinQuote
import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import dev.bristot.cryptoapp.feature.settings.api.QuoteCurrency
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class GetQuotedCoinsUseCase @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val coinRepository: CoinRepository,
    private val tickersRepository: TickersRepository,
) {

    private var cachedCoins: List<Coin>? = null

    suspend operator fun invoke(quoteCurrency: QuoteCurrency): List<Coin> =
        withContext(dispatcherProvider.default) {
            val coins = cachedCoins
                ?: coinRepository.getCoins().first().also { cachedCoins = it }
            val tickersById = tickersRepository
                .getTickers(currencies = setOf(quoteCurrency))
                .first()
                .associateBy { ticker -> ticker.id }

            coins.map { coin ->
                coin.copy(
                    quote = CoinQuote(
                        currency = quoteCurrency,
                        price = tickersById[coin.id]
                            ?.prices
                            ?.get(quoteCurrency)
                            ?.price,
                    )
                )
            }
        }
}
