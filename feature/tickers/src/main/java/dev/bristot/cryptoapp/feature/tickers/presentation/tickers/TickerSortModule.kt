package dev.bristot.cryptoapp.feature.tickers.presentation.tickers

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.bristot.cryptoapp.feature.tickers.domain.entity.Ticker
import dev.bristot.cryptoapp.ui.sort.SortTemplate

@Module
@InstallIn(ViewModelComponent::class)
abstract class TickerSortModule {

    @Binds
    abstract fun bindTickerSortTemplate(
        implementation: TickerSortTemplate,
    ): SortTemplate<Ticker>
}
