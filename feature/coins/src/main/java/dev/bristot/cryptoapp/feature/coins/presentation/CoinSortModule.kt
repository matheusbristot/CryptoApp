package dev.bristot.cryptoapp.feature.coins.presentation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.bristot.cryptoapp.feature.coins.domain.entity.Coin
import dev.bristot.cryptoapp.ui.sort.SortTemplate

@Module
@InstallIn(ViewModelComponent::class)
abstract class CoinSortModule {

    @Binds
    abstract fun bindCoinSortTemplate(
        implementation: CoinSortTemplate,
    ): SortTemplate<Coin>
}
