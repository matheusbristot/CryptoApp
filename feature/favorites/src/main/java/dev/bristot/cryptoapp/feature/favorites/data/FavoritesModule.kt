package dev.bristot.cryptoapp.feature.favorites.data

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.bristot.cryptoapp.feature.favorites.api.FavoritesRepository
import dev.bristot.cryptoapp.sync.api.SyncTargetProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object FavoritesDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FavoritesDatabase =
        Room.databaseBuilder(
            context,
            FavoritesDatabase::class.java,
            "favorites.db",
        ).build()

    @Provides
    fun provideFavoritesDao(database: FavoritesDatabase): FavoritesDao = database.favoritesDao()
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FavoritesBindingsModule {

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        implementation: RoomFavoritesRepository,
    ): FavoritesRepository

    @Binds
    @IntoSet
    abstract fun bindSyncTargetProvider(
        implementation: FavoritesSyncTargetProvider,
    ): SyncTargetProvider
}
