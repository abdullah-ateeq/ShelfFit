package com.example.shelffit.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shelffit.data.local.ShelfPreferences
import com.example.shelffit.data.model.HistoryEntry
import com.example.shelffit.data.repository.HistoryRepository
import com.example.shelffit.data.repository.SettingsRepository
import com.example.shelffit.domain.calculator.ShelfCalculatorEngine
import com.example.shelffit.domain.model.ShelfCalculationInput
import com.example.shelffit.domain.model.ShelfCalculationResult
import com.example.shelffit.settings.ThemeMode
import com.example.shelffit.utils.LengthUnit
import com.example.shelffit.utils.formatMeasurement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val shelfWidthText: String = "120",
    val shelfUnit: LengthUnit = LengthUnit.CM,
    val leftClearanceText: String = "",
    val rightClearanceText: String = "",
    val sideSpacingText: String = "",
    val objectWidthText: String = "25",
    val objectUnit: LengthUnit = LengthUnit.CM,
    val gapText: String = "2",
    val gapUnit: LengthUnit = LengthUnit.CM,
    val quantityText: String = "",
    val resultUnit: LengthUnit = LengthUnit.CM,
    val shelfWidthError: String? = null,
    val objectWidthError: String? = null,
    val generalError: String? = null,
    val currentResult: ShelfCalculationResult? = null,
    val isSaved: Boolean = false
)

class ShelfViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = ShelfPreferences(application)
    private val historyRepository = HistoryRepository(preferences)
    private val settingsRepository = SettingsRepository(preferences)

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val defaultUnit: StateFlow<LengthUnit> = settingsRepository.defaultUnit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LengthUnit.CM)

    val hapticsEnabled: StateFlow<Boolean> = settingsRepository.hapticsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val historyList: StateFlow<List<HistoryEntry>> = historyRepository.history
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        // Run initial calculation with default values so user has an immediate live example
        calculate(autoSave = false)
    }

    fun onShelfWidthChange(value: String) {
        _uiState.update { it.copy(shelfWidthText = value, shelfWidthError = null, generalError = null) }
    }

    fun onShelfUnitChange(unit: LengthUnit) {
        _uiState.update { it.copy(shelfUnit = unit) }
    }

    fun onLeftClearanceChange(value: String) {
        _uiState.update { it.copy(leftClearanceText = value, generalError = null) }
    }

    fun onRightClearanceChange(value: String) {
        _uiState.update { it.copy(rightClearanceText = value, generalError = null) }
    }

    fun onSideSpacingChange(value: String) {
        _uiState.update { it.copy(sideSpacingText = value, generalError = null) }
    }

    fun onObjectWidthChange(value: String) {
        _uiState.update { it.copy(objectWidthText = value, objectWidthError = null, generalError = null) }
    }

    fun onObjectUnitChange(unit: LengthUnit) {
        _uiState.update { it.copy(objectUnit = unit, gapUnit = unit) }
    }

    fun onGapChange(value: String) {
        _uiState.update { it.copy(gapText = value) }
    }

    fun onGapUnitChange(unit: LengthUnit) {
        _uiState.update { it.copy(gapUnit = unit) }
    }

    fun onQuantityChange(value: String) {
        _uiState.update { it.copy(quantityText = value) }
    }

    fun onResultUnitChange(unit: LengthUnit) {
        _uiState.update { state ->
            val updatedState = state.copy(resultUnit = unit)
            // Recompute display result with updated unit if result exists
            state.currentResult?.let { prevResult ->
                val updatedInput = prevResult.input.copy(resultUnit = unit)
                val newResult = ShelfCalculatorEngine.calculate(updatedInput)
                updatedState.copy(currentResult = newResult)
            } ?: updatedState
        }
    }

    fun swapShelfAndObject() {
        _uiState.update { state ->
            state.copy(
                shelfWidthText = state.objectWidthText,
                shelfUnit = state.objectUnit,
                objectWidthText = state.shelfWidthText,
                objectUnit = state.shelfUnit,
                shelfWidthError = null,
                objectWidthError = null,
                generalError = null
            )
        }
    }

    fun resetInputs() {
        val defUnit = defaultUnit.value
        _uiState.update {
            CalculatorUiState(
                shelfWidthText = "",
                shelfUnit = defUnit,
                leftClearanceText = "",
                rightClearanceText = "",
                sideSpacingText = "",
                objectWidthText = "",
                objectUnit = defUnit,
                gapText = "",
                gapUnit = defUnit,
                quantityText = "",
                resultUnit = defUnit,
                shelfWidthError = null,
                objectWidthError = null,
                generalError = null,
                currentResult = null,
                isSaved = false
            )
        }
    }

    fun calculate(autoSave: Boolean = true): Boolean {
        val state = _uiState.value

        val shelfWidth = state.shelfWidthText.toDoubleOrNull()
        val objectWidth = state.objectWidthText.toDoubleOrNull()
        val leftClearance = state.leftClearanceText.toDoubleOrNull() ?: 0.0
        val rightClearance = state.rightClearanceText.toDoubleOrNull() ?: 0.0
        val sideSpacing = state.sideSpacingText.toDoubleOrNull() ?: 0.0
        val gap = state.gapText.toDoubleOrNull() ?: 0.0
        val quantity = state.quantityText.toIntOrNull()

        var hasError = false
        var shelfError: String? = null
        var objectError: String? = null
        var generalError: String? = null

        if (shelfWidth == null || shelfWidth <= 0.0) {
            shelfError = "Enter a valid shelf width > 0"
            hasError = true
        }

        if (objectWidth == null || objectWidth <= 0.0) {
            objectError = "Enter a valid object width > 0"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    shelfWidthError = shelfError,
                    objectWidthError = objectError,
                    generalError = generalError
                )
            }
            return false
        }

        val input = ShelfCalculationInput(
            shelfWidth = shelfWidth!!,
            shelfUnit = state.shelfUnit,
            leftClearance = leftClearance,
            rightClearance = rightClearance,
            sideSpacing = sideSpacing,
            objectWidth = objectWidth!!,
            objectUnit = state.objectUnit,
            gap = gap,
            gapUnit = state.gapUnit,
            requestedQuantity = quantity,
            resultUnit = state.resultUnit
        )

        val result = ShelfCalculatorEngine.calculate(input)

        _uiState.update {
            it.copy(
                shelfWidthError = null,
                objectWidthError = null,
                generalError = null,
                currentResult = result,
                isSaved = autoSave
            )
        }

        if (autoSave && result.capacity >= 0) {
            viewModelScope.launch {
                historyRepository.saveCalculation(HistoryEntry.fromResult(result))
            }
        }

        return true
    }

    fun loadFromHistory(entry: HistoryEntry) {
        _uiState.update {
            it.copy(
                shelfWidthText = entry.shelfWidth.formatMeasurement(2),
                shelfUnit = entry.shelfUnit,
                leftClearanceText = if (entry.leftClearance > 0) entry.leftClearance.formatMeasurement(2) else "",
                rightClearanceText = if (entry.rightClearance > 0) entry.rightClearance.formatMeasurement(2) else "",
                sideSpacingText = if (entry.sideSpacing > 0) entry.sideSpacing.formatMeasurement(2) else "",
                objectWidthText = entry.objectWidth.formatMeasurement(2),
                objectUnit = entry.objectUnit,
                gapText = if (entry.gap > 0) entry.gap.formatMeasurement(2) else "0",
                gapUnit = entry.gapUnit,
                quantityText = entry.requestedQuantity?.toString() ?: "",
                resultUnit = entry.resultUnit,
                shelfWidthError = null,
                objectWidthError = null,
                generalError = null
            )
        }
        val result = ShelfCalculatorEngine.calculate(entry.toInput())
        _uiState.update { it.copy(currentResult = result, isSaved = true) }
    }

    fun deleteHistoryItem(id: String) {
        viewModelScope.launch {
            historyRepository.deleteEntry(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepository.clearAll()
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setDefaultUnit(unit: LengthUnit) {
        viewModelScope.launch {
            settingsRepository.setDefaultUnit(unit)
            // If user resets or changes default unit, sync inputs if empty
            if (_uiState.value.shelfWidthText.isEmpty()) {
                _uiState.update { it.copy(shelfUnit = unit, objectUnit = unit, gapUnit = unit, resultUnit = unit) }
            }
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setHapticsEnabled(enabled)
        }
    }

    fun copySummaryToClipboard(context: Context): Boolean {
        val result = _uiState.value.currentResult ?: return false
        val displayUnit = result.displayUnit
        val summary = buildString {
            appendLine("📐 ShelfFit Calculation Summary")
            appendLine("• Shelf Width: ${result.getShelfWidthInDisplayUnit().formatMeasurement(2)} ${displayUnit.symbol}")
            if (result.leftClearanceBaseMm > 0 || result.rightClearanceBaseMm > 0) {
                appendLine("• Clearances: Left ${result.getLeftClearanceInDisplayUnit().formatMeasurement(2)}, Right ${result.getRightClearanceInDisplayUnit().formatMeasurement(2)} ${displayUnit.symbol}")
            }
            appendLine("• Usable Width: ${result.getAvailableWidthInDisplayUnit().formatMeasurement(2)} ${displayUnit.symbol}")
            appendLine("• Object Width: ${result.getObjectWidthInDisplayUnit().formatMeasurement(2)} ${displayUnit.symbol}")
            appendLine("• Gap: ${result.getGapInDisplayUnit().formatMeasurement(2)} ${displayUnit.symbol}")
            appendLine("━━━━━━━━━━━━━━━━━━━")
            appendLine("🎯 Capacity: ${result.capacity} objects")
            appendLine("• Total Occupied: ${result.getOccupiedWidthInDisplayUnit().formatMeasurement(2)} ${displayUnit.symbol}")
            appendLine("• Remaining Space: ${result.getRemainingWidthInDisplayUnit().formatMeasurement(2)} ${displayUnit.symbol}")
            result.requestedQuantity?.let { req ->
                val fits = result.requestedFits == true
                appendLine("• Requested $req: ${if (fits) "FITS ✓" else "DOES NOT FIT ✗"}")
            }
        }

        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("ShelfFit Summary", summary)
            clipboard.setPrimaryClip(clip)
            true
        } catch (_: Exception) {
            false
        }
    }
}

class ShelfViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShelfViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShelfViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
