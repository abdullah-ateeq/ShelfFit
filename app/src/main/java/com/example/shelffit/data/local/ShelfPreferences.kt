package com.example.shelffit.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.shelffit.data.model.HistoryEntry
import com.example.shelffit.settings.ThemeMode
import com.example.shelffit.utils.LengthUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "shelffit_prefs")

class ShelfPreferences(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DEFAULT_UNIT = stringPreferencesKey("default_unit")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val HISTORY_JSON = stringPreferencesKey("history_json")
    }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val name = preferences[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(name)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = mode.name
        }
    }

    val defaultUnitFlow: Flow<LengthUnit> = context.dataStore.data.map { preferences ->
        val name = preferences[Keys.DEFAULT_UNIT] ?: LengthUnit.CM.name
        try {
            LengthUnit.valueOf(name)
        } catch (_: Exception) {
            LengthUnit.CM
        }
    }

    suspend fun setDefaultUnit(unit: LengthUnit) {
        context.dataStore.edit { preferences ->
            preferences[Keys.DEFAULT_UNIT] = unit.name
        }
    }

    val hapticsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.HAPTICS_ENABLED] ?: true
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.HAPTICS_ENABLED] = enabled
        }
    }

    val historyFlow: Flow<List<HistoryEntry>> = context.dataStore.data.map { preferences ->
        val json = preferences[Keys.HISTORY_JSON] ?: ""
        HistoryEntry.listFromJsonString(json)
    }

    suspend fun addHistoryEntry(entry: HistoryEntry) {
        context.dataStore.edit { preferences ->
            val json = preferences[Keys.HISTORY_JSON] ?: ""
            val existing = HistoryEntry.listFromJsonString(json).toMutableList()
            // Add new at the beginning
            existing.add(0, entry)
            // Keep up to 100 recent entries
            if (existing.size > 100) {
                existing.removeAt(existing.size - 1)
            }
            preferences[Keys.HISTORY_JSON] = HistoryEntry.listToJsonString(existing)
        }
    }

    suspend fun deleteHistoryEntry(id: String) {
        context.dataStore.edit { preferences ->
            val json = preferences[Keys.HISTORY_JSON] ?: ""
            val existing = HistoryEntry.listFromJsonString(json).toMutableList()
            existing.removeAll { it.id == id }
            preferences[Keys.HISTORY_JSON] = HistoryEntry.listToJsonString(existing)
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { preferences ->
            preferences[Keys.HISTORY_JSON] = "[]"
        }
    }
}
