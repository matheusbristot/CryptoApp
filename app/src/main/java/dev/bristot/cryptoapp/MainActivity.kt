package dev.bristot.cryptoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.bristot.cryptoapp.navigation.NavigationCryptoAppHilt
import dev.bristot.cryptoapp.navigation.NavigationData
import dev.bristot.cryptoapp.navigation.NavigationEntryProviders
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navData: NavigationData

    @Inject
    lateinit var navigationEntryProviders: NavigationEntryProviders

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            NavigationCryptoAppHilt(
                modifier = Modifier.fillMaxSize(),
                navigationData = navData,
                entryProviderBlock = navigationEntryProviders.asEntryProvider(),
            )
        }
    }
}
