package com.example.oceanfresh.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class PrefManager(private val context: Context) {

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
    }

    // Flow that emits the current theme status
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY] ?: false // Default to light mode
    }

    // Function to update the preference
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
}