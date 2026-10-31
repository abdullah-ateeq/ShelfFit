package com.example.shelffit.data.repository

import com.example.shelffit.data.local.ShelfPreferences
import com.example.shelffit.settings.ThemeMode
import com.example.shelffit.utils.LengthUnit
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val preferences: ShelfPreferences) {

    val themeMode: Flow<ThemeMode> = preferences.themeModeFlow
    val defaultUnit: Flow<LengthUnit> = preferences.defaultUnitFlow
    val hapticsEnabled: Flow<Boolean> = preferences.hapticsEnabledFlow

    suspend fun setThemeMode(themeMode: ThemeMode) {
        preferences.setThemeMode(themeMode)
    }

    suspend fun setDefaultUnit(unit: LengthUnit) {
        preferences.setDefaultUnit(unit)
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        preferences.setHapticsEnabled(enabled)
    }

    suspend fun clearHistory() {
        preferences.clearHistory()
    }
}
