package com.example.shelffit.domain.calculator

import com.example.shelffit.domain.model.ShelfCalculationInput
import com.example.shelffit.domain.model.ShelfCalculationResult
import com.example.shelffit.domain.model.VisualObject
import com.example.shelffit.utils.formatMeasurement
import kotlin.math.floor
import kotlin.math.max

object ShelfCalculatorEngine {

    fun calculate(input: ShelfCalculationInput): ShelfCalculationResult {
        // Convert all measurements to internal base unit: Millimeters (mm)
        val shelfWidthMm = max(0.0, input.shelfUnit.toBaseMm(input.shelfWidth))
        val leftClearanceMm = max(0.0, input.shelfUnit.toBaseMm(input.leftClearance))
        val rightClearanceMm = max(0.0, input.shelfUnit.toBaseMm(input.rightClearance))
        val sideSpacingMm = max(0.0, input.shelfUnit.toBaseMm(input.sideSpacing))

        val objectWidthMm = max(0.0, input.objectUnit.toBaseMm(input.objectWidth))
        val gapMm = max(0.0, input.gapUnit.toBaseMm(input.gap))

        // Available width after clearances and side spacings (symmetric left + right)
        val availableWidthMm = max(0.0, shelfWidthMm - leftClearanceMm - rightClearanceMm - (2 * sideSpacingMm))

        val breakdown = mutableListOf<String>()
        val displayUnit = input.resultUnit

        fun toDisplay(mm: Double): String = "${displayUnit.fromBaseMm(mm).formatMeasurement(2)} ${displayUnit.symbol}"

        breakdown.add("1. Total Shelf Width: ${toDisplay(shelfWidthMm)}")
        if (leftClearanceMm > 0 || rightClearanceMm > 0 || sideSpacingMm > 0) {
            breakdown.add("2. Deductions: Left Clearance = ${toDisplay(leftClearanceMm)}, Right Clearance = ${toDisplay(rightClearanceMm)}" +
                    if (sideSpacingMm > 0) ", Side Spacing = 2 × ${toDisplay(sideSpacingMm)}" else "")
            breakdown.add("3. Usable Available Width: ${toDisplay(availableWidthMm)}")
        } else {
            breakdown.add("2. Clearances: None (full shelf width is available: ${toDisplay(availableWidthMm)})")
        }
        breakdown.add("4. Object Width: ${toDisplay(objectWidthMm)}, Gap between objects: ${toDisplay(gapMm)}")

        // Validate basic physical conditions
        if (shelfWidthMm <= 0.0 || objectWidthMm <= 0.0 || availableWidthMm < objectWidthMm) {
            val reason = when {
                shelfWidthMm <= 0.0 -> "Shelf width must be greater than zero."
                objectWidthMm <= 0.0 -> "Object width must be greater than zero."
                availableWidthMm <= 0.0 -> "Clearances and spacing exceed shelf width."
                else -> "Object width (${toDisplay(objectWidthMm)}) exceeds usable width (${toDisplay(availableWidthMm)})."
            }
            breakdown.add("5. Result: 0 objects fit. $reason")

            val excessWidthForReq = input.requestedQuantity?.let { reqQty ->
                if (reqQty > 0) {
                    val reqWidth = (reqQty * objectWidthMm) + ((reqQty - 1) * gapMm)
                    max(0.0, reqWidth - availableWidthMm)
                } else null
            }

            return ShelfCalculationResult(
                input = input,
                capacity = 0,
                shelfWidthBaseMm = shelfWidthMm,
                leftClearanceBaseMm = leftClearanceMm,
                rightClearanceBaseMm = rightClearanceMm,
                sideSpacingBaseMm = sideSpacingMm,
                availableWidthBaseMm = availableWidthMm,
                objectWidthBaseMm = objectWidthMm,
                gapBaseMm = gapMm,
                totalOccupiedWidthBaseMm = 0.0,
                remainingWidthBaseMm = availableWidthMm,
                requestedQuantity = input.requestedQuantity,
                requestedFits = if (input.requestedQuantity != null && input.requestedQuantity > 0) false else null,
                additionalCanFit = 0,
                excessRequested = input.requestedQuantity,
                excessWidthNeededBaseMm = excessWidthForReq,
                distributedGapBaseMm = null,
                visualObjects = emptyList(),
                breakdownSteps = breakdown
            )
        }

        // Capacity calculation using formula: floor((Available width + Gap) / (Object width + Gap))
        val capacity = floor((availableWidthMm + gapMm) / (objectWidthMm + gapMm)).toInt()

        val occupiedWidthMm = if (capacity > 0) {
            (capacity * objectWidthMm) + ((capacity - 1) * gapMm)
        } else {
            0.0
        }

        val remainingWidthMm = max(0.0, availableWidthMm - occupiedWidthMm)

        breakdown.add("5. Calculation Formula: Capacity = floor((Available Width + Gap) / (Object Width + Gap))")
        breakdown.add("   = floor((${toDisplay(availableWidthMm)} + ${toDisplay(gapMm)}) / (${toDisplay(objectWidthMm)} + ${toDisplay(gapMm)})) = $capacity objects")
        breakdown.add("6. Total Occupied Width for $capacity objects: $capacity × ${toDisplay(objectWidthMm)} + ${max(0, capacity - 1)} × ${toDisplay(gapMm)} = ${toDisplay(occupiedWidthMm)}")
        breakdown.add("7. Remaining Free Width: ${toDisplay(availableWidthMm)} − ${toDisplay(occupiedWidthMm)} = ${toDisplay(remainingWidthMm)}")

        // Distributed gap calculation (if objects were spread evenly across usable width)
        val distributedGapMm = if (capacity > 1) {
            val totalObjectWidthOnly = capacity * objectWidthMm
            (availableWidthMm - totalObjectWidthOnly) / (capacity - 1)
        } else null

        if (distributedGapMm != null) {
            breakdown.add("8. Evenly Distributed Spacing (Alternative): ${toDisplay(distributedGapMm)} between each object")
        }

        // Requested quantity analysis
        var requestedFits: Boolean? = null
        var additionalCanFit: Int? = null
        var excessRequested: Int? = null
        var excessWidthNeededMm: Double? = null

        input.requestedQuantity?.let { reqQty ->
            if (reqQty > 0) {
                if (reqQty <= capacity) {
                    requestedFits = true
                    additionalCanFit = capacity - reqQty
                    breakdown.add("9. Requested Quantity: $reqQty fits successfully! (${additionalCanFit} more can fit)")
                } else {
                    requestedFits = false
                    excessRequested = reqQty - capacity
                    val reqWidth = (reqQty * objectWidthMm) + ((reqQty - 1) * gapMm)
                    excessWidthNeededMm = max(0.0, reqWidth - availableWidthMm)
                    breakdown.add("9. Requested Quantity: $reqQty exceeds capacity by $excessRequested objects (${toDisplay(excessWidthNeededMm!!)} extra shelf width needed)")
                }
            }
        }

        // Build visual objects for shelf preview diagram
        // We render all capacity items, or if requested exceeds capacity we can also mark excess
        val visualList = mutableListOf<VisualObject>()
        val renderCount = if (input.requestedQuantity != null && input.requestedQuantity > capacity) {
            // Render up to capacity + min(excess, 5) to show overflow visually
            capacity + minOf(input.requestedQuantity - capacity, 4)
        } else {
            capacity
        }

        for (i in 1..renderCount) {
            val isRequested = input.requestedQuantity?.let { i <= it } ?: false
            val isExcess = i > capacity
            val gapAfter = if (i < renderCount) gapMm else 0.0
            visualList.add(
                VisualObject(
                    index = i,
                    isRequested = isRequested,
                    isExcess = isExcess,
                    widthMm = objectWidthMm,
                    gapAfterMm = gapAfter
                )
            )
        }

        return ShelfCalculationResult(
            input = input,
            capacity = capacity,
            shelfWidthBaseMm = shelfWidthMm,
            leftClearanceBaseMm = leftClearanceMm,
            rightClearanceBaseMm = rightClearanceMm,
            sideSpacingBaseMm = sideSpacingMm,
            availableWidthBaseMm = availableWidthMm,
            objectWidthBaseMm = objectWidthMm,
            gapBaseMm = gapMm,
            totalOccupiedWidthBaseMm = occupiedWidthMm,
            remainingWidthBaseMm = remainingWidthMm,
            requestedQuantity = input.requestedQuantity,
            requestedFits = requestedFits,
            additionalCanFit = additionalCanFit,
            excessRequested = excessRequested,
            excessWidthNeededBaseMm = excessWidthNeededMm,
            distributedGapBaseMm = distributedGapMm,
            visualObjects = visualList,
            breakdownSteps = breakdown
        )
    }
}
