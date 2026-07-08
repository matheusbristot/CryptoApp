package dev.bristot.cryptoapp.logger

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

private const val DEFAULT_LOGGER_TAG = "CryptLogger"

@Module
@InstallIn(SingletonComponent::class)
internal object LoggerModule {

    @Provides
    fun providesLogger(): CryptoLogger = DefaultCryptoLogger(
        tag = DEFAULT_LOGGER_TAG,
        logWriter = AndroidLogWriter,
    )
}
