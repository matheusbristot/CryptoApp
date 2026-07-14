package dev.bristot.cryptoapp.navigation

import androidx.navigation3.runtime.NavKey
/** Open navigation contract. Each feature owns its concrete destinations. */
interface CryptoAppDestination : NavKey

/** Marker for destinations displayed in the app's root navigation. */
interface RootDestination : CryptoAppDestination
