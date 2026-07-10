package dev.bristot.cryptoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dev.bristot.cryptoapp.navigation.NavigationData
import dev.bristot.cryptoapp.navigation.NavigationEntryProviders
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var initialRootNavigationData: NavigationData

    @Inject
    lateinit var navigationEntryProviders: NavigationEntryProviders

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CryptoAppContent(
                initialRootNavigationData = initialRootNavigationData,
                navigationEntryProviders = navigationEntryProviders,
            )
        }
    }
}
