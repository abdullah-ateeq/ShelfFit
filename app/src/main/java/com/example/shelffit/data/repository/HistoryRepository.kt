package com.example.shelffit.data.repository

import com.example.shelffit.data.local.ShelfPreferences
import com.example.shelffit.data.model.HistoryEntry
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val preferences: ShelfPreferences) {

    val history: Flow<List<HistoryEntry>> = preferences.historyFlow

    suspend fun saveCalculation(entry: HistoryEntry) {
        preferences.addHistoryEntry(entry)
    }

    suspend fun deleteEntry(id: String) {
        preferences.deleteHistoryEntry(id)
    }

    suspend fun clearAll() {
        preferences.clearHistory()
    }
}
