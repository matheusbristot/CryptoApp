package dev.bristot.cryptoapp.feature.settings.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

internal val Context.settingsDataStore by preferencesDataStore(name = "app_settings")
