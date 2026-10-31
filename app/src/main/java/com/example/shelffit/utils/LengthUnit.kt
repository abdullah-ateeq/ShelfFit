package com.example.shelffit.utils

import java.util.Locale

/**
 * Units of measurement supported in ShelfFit.
 * Base internal calculation unit is millimeters (mm).
 */
enum class LengthUnit(
    val symbol: String,
    val factorToMm: Double,
    val displayName: String
) {
    MM("mm", 1.0, "Millimeters (mm)"),
    CM("cm", 10.0, "Centimeters (cm)"),
    M("m", 1000.0, "Meters (m)"),
    INCH("in", 25.4, "Inches (in)"),
    FEET("ft", 304.8, "Feet (ft)");

    fun toBaseMm(value: Double): Double = value * factorToMm

    fun fromBaseMm(mmValue: Double): Double = mmValue / factorToMm

    fun convertTo(value: Double, targetUnit: LengthUnit): Double {
        if (this == targetUnit) return value
        return targetUnit.fromBaseMm(toBaseMm(value))
    }

    companion object {
        fun fromSymbol(symbol: String): LengthUnit {
            return entries.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) } ?: CM
        }
    }
}

/**
 * Cleanly formats a double value without unnecessary trailing zeros.
 */
fun Double.formatMeasurement(decimals: Int = 2): String {
    if (this.isNaN() || this.isInfinite()) return "0"
    val formatted = String.format(Locale.US, "%.${decimals}f", this)
    return if (formatted.contains(".")) {
        formatted.trimEnd('0').trimEnd('.')
    } else {
        formatted
    }
}

/**
 * Formats a measurement double with its unit symbol.
 */
fun Double.formatWithUnit(unit: LengthUnit, decimals: Int = 2): String {
    return "${this.formatMeasurement(decimals)} ${unit.symbol}"
}
