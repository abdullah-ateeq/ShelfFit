package com.example.shelffit.domain.model

import com.example.shelffit.utils.LengthUnit

data class VisualObject(
    val index: Int,
    val isRequested: Boolean,
    val isExcess: Boolean,
    val widthMm: Double,
    val gapAfterMm: Double
)

data class ShelfCalculationResult(
    val input: ShelfCalculationInput,
    val capacity: Int,
    val shelfWidthBaseMm: Double,
    val leftClearanceBaseMm: Double,
    val rightClearanceBaseMm: Double,
    val sideSpacingBaseMm: Double,
    val availableWidthBaseMm: Double, // Shelf - Left - Right - 2*SideSpacing
    val objectWidthBaseMm: Double,
    val gapBaseMm: Double,
    val totalOccupiedWidthBaseMm: Double,
    val remainingWidthBaseMm: Double,
    val requestedQuantity: Int? = null,
    val requestedFits: Boolean? = null,
    val additionalCanFit: Int? = null,
    val excessRequested: Int? = null,
    val excessWidthNeededBaseMm: Double? = null,
    val distributedGapBaseMm: Double? = null,
    val visualObjects: List<VisualObject> = emptyList(),
    val breakdownSteps: List<String> = emptyList()
) {
    val displayUnit: LengthUnit get() = input.resultUnit

    fun getShelfWidthInDisplayUnit(): Double = displayUnit.fromBaseMm(shelfWidthBaseMm)
    fun getAvailableWidthInDisplayUnit(): Double = displayUnit.fromBaseMm(availableWidthBaseMm)
    fun getObjectWidthInDisplayUnit(): Double = displayUnit.fromBaseMm(objectWidthBaseMm)
    fun getGapInDisplayUnit(): Double = displayUnit.fromBaseMm(gapBaseMm)
    fun getOccupiedWidthInDisplayUnit(): Double = displayUnit.fromBaseMm(totalOccupiedWidthBaseMm)
    fun getRemainingWidthInDisplayUnit(): Double = displayUnit.fromBaseMm(remainingWidthBaseMm)
    fun getLeftClearanceInDisplayUnit(): Double = displayUnit.fromBaseMm(leftClearanceBaseMm)
    fun getRightClearanceInDisplayUnit(): Double = displayUnit.fromBaseMm(rightClearanceBaseMm)
    fun getSideSpacingInDisplayUnit(): Double = displayUnit.fromBaseMm(sideSpacingBaseMm)
    fun getExcessWidthNeededInDisplayUnit(): Double? = excessWidthNeededBaseMm?.let { displayUnit.fromBaseMm(it) }
    fun getDistributedGapInDisplayUnit(): Double? = distributedGapBaseMm?.let { displayUnit.fromBaseMm(it) }
}
