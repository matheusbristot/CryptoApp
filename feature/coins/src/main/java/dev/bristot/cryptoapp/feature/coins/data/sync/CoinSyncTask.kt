package dev.bristot.cryptoapp.feature.coins.data.sync

import dev.bristot.cryptoapp.feature.coins.domain.repository.CoinRepository
import dev.bristot.cryptoapp.feature.settings.api.SettingsRepository
import dev.bristot.cryptoapp.feature.tickers.domain.repository.TickersRepository
import dev.bristot.cryptoapp.logger.CryptoLogger
import dev.bristot.cryptoapp.sync.api.FeatureSyncTask
import dev.bristot.cryptoapp.sync.api.SyncResult
import dev.bristot.cryptoapp.sync.api.SyncTargetRegistry
import dev.bristot.cryptoapp.sync.api.SyncTargetType
import java.io.IOException
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import retrofit2.HttpException

class CoinSyncTask @Inject constructor(
    private val repository: CoinRepository,
    private val tickersRepository: TickersRepository,
    private val settingsRepository: SettingsRepository,
    private val targetRegistry: SyncTargetRegistry,
    private val logger: CryptoLogger,
) : FeatureSyncTask {

    override val taskKey: String = "coin-details"
    override val targetType: SyncTargetType = SyncTargetType.COIN
    override val repeatInterval: Duration = 1.minutes

    override suspend fun sync(): SyncResult {
        var retryRequired = false
        var permanentFailure = false
        val currencies = settingsRepository.settings.value.requestedQuoteCurrencies
        targetRegistry.idsFor(targetType).forEach { coinId ->
            listOf<suspend () -> Unit>(
                { repository.refreshCoin(coinId = coinId, force = false) },
                {
                    tickersRepository.refreshTicker(
                        coinId = coinId,
                        currencies = currencies,
                        force = false,
                    )
                },
            ).forEach { refresh ->
                try {
                    refresh()
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: IOException) {
                    retryRequired = true
                    logger.warning(message = "Coin sync failed for $coinId", throwable = exception)
                } catch (exception: HttpException) {
                    if (exception.code() == 429 || exception.code() >= 500) {
                        retryRequired = true
                    }
                    logger.warning(
                        message = "Coin sync HTTP ${exception.code()} for $coinId",
                        throwable = exception,
                    )
                } catch (exception: Exception) {
                    permanentFailure = true
                    logger.error(
                        throwable = exception,
                        message = "Coin sync failed permanently for $coinId",
                    )
                }
            }
        }
        return when {
            retryRequired -> SyncResult.Retry
            permanentFailure -> SyncResult.Failure
            else -> SyncResult.Success
        }
    }
}
