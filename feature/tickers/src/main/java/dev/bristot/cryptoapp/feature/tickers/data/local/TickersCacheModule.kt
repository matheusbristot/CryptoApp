package dev.bristot.cryptoapp.feature.tickers.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TickersCacheModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TickersCacheDatabase =
        Room.databaseBuilder(
            context,
            TickersCacheDatabase::class.java,
            "tickers-cache.db",
        ).build()

    @Provides
    fun provideLocalDataSource(database: TickersCacheDatabase): TickersLocalDataSource =
        RoomTickersLocalDataSource(
            database = database,
            json = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                allowSpecialFloatingPointValues = true
            },
        )
}
