package dev.bristot.cryptoapp.logger

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {

    @Provides
    fun providesLogger(): CryptoLogger = DefaultCryptoLogger("CryptLogger")
}
