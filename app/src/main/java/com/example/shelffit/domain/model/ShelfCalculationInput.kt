package com.example.shelffit.domain.model

import com.example.shelffit.utils.LengthUnit

data class ShelfCalculationInput(
    val shelfWidth: Double,
    val shelfUnit: LengthUnit,
    val leftClearance: Double = 0.0,
    val rightClearance: Double = 0.0,
    val sideSpacing: Double = 0.0, // Optional side spacing between edge and first/last object
    val objectWidth: Double,
    val objectUnit: LengthUnit,
    val gap: Double = 0.0,
    val gapUnit: LengthUnit = objectUnit,
    val requestedQuantity: Int? = null,
    val resultUnit: LengthUnit = shelfUnit
)
