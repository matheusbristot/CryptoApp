package dev.bristot.cryptoapp.feature.coins.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoinsCacheModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CoinsCacheDatabase =
        Room.databaseBuilder(
            context,
            CoinsCacheDatabase::class.java,
            "coins-cache.db",
        ).build()

    @Provides
    fun provideLocalDataSource(database: CoinsCacheDatabase): CoinsLocalDataSource =
        RoomCoinsLocalDataSource(database.coinCacheDao())
}
