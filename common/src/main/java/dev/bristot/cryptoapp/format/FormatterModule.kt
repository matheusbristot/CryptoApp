package dev.bristot.cryptoapp.format

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FormatterModule {
    @Binds
    @Singleton
    abstract fun bindCryptoValueFormatter(
        formatter: DefaultCryptoValueFormatter,
    ): CryptoValueFormatter
}
