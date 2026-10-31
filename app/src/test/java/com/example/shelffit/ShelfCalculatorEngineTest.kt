package com.example.shelffit

import com.example.shelffit.domain.calculator.ShelfCalculatorEngine
import com.example.shelffit.domain.model.ShelfCalculationInput
import com.example.shelffit.utils.LengthUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShelfCalculatorEngineTest {

    @Test
    fun testPromptExample_120cm_shelf_25cm_object_2cm_gap() {
        val input = ShelfCalculationInput(
            shelfWidth = 120.0,
            shelfUnit = LengthUnit.CM,
            leftClearance = 0.0,
            rightClearance = 0.0,
            sideSpacing = 0.0,
            objectWidth = 25.0,
            objectUnit = LengthUnit.CM,
            gap = 2.0,
            gapUnit = LengthUnit.CM
        )

        val result = ShelfCalculatorEngine.calculate(input)

        // 4 objects require 25*4 + 2*3 = 106 cm
        // 5 objects require 25*5 + 2*4 = 133 cm > 120
        // Expected capacity: 4 objects
        // Remaining width: 14 cm
        assertEquals(4, result.capacity)
        assertEquals(106.0, result.getOccupiedWidthInDisplayUnit(), 0.001)
        assertEquals(14.0, result.getRemainingWidthInDisplayUnit(), 0.001)
    }

    @Test
    fun testIndependentUnits_MetersAndInches() {
        // Shelf: 2 meters (= 200 cm = 2000 mm)
        // Object: 10 inches (= 254 mm)
        // Gap: 1 inch (= 25.4 mm)
        // (2000 + 25.4) / (254 + 25.4) = 2025.4 / 279.4 = 7.249 -> 7 objects
        val input = ShelfCalculationInput(
            shelfWidth = 2.0,
            shelfUnit = LengthUnit.M,
            leftClearance = 0.0,
            rightClearance = 0.0,
            objectWidth = 10.0,
            objectUnit = LengthUnit.INCH,
            gap = 1.0,
            gapUnit = LengthUnit.INCH,
            resultUnit = LengthUnit.INCH
        )

        val result = ShelfCalculatorEngine.calculate(input)
        assertEquals(7, result.capacity)
    }

    @Test
    fun testWithClearancesAndSideSpacing() {
        // Shelf: 100 cm
        // Left clearance: 10 cm, Right clearance: 10 cm, Side spacing: 5 cm (5 on each side = 10 cm)
        // Usable width: 100 - 10 - 10 - (2 * 5) = 70 cm
        // Object: 20 cm, Gap: 5 cm
        // (70 + 5) / (20 + 5) = 75 / 25 = 3 objects
        val input = ShelfCalculationInput(
            shelfWidth = 100.0,
            shelfUnit = LengthUnit.CM,
            leftClearance = 10.0,
            rightClearance = 10.0,
            sideSpacing = 5.0,
            objectWidth = 20.0,
            objectUnit = LengthUnit.CM,
            gap = 5.0,
            gapUnit = LengthUnit.CM
        )

        val result = ShelfCalculatorEngine.calculate(input)
        assertEquals(3, result.capacity)
        assertEquals(70.0, result.getAvailableWidthInDisplayUnit(), 0.001)
        assertEquals(70.0, result.getOccupiedWidthInDisplayUnit(), 0.001)
        assertEquals(0.0, result.getRemainingWidthInDisplayUnit(), 0.001)
    }

    @Test
    fun testRequestedQuantity_FitsAndExceeds() {
        val inputFits = ShelfCalculationInput(
            shelfWidth = 120.0,
            shelfUnit = LengthUnit.CM,
            objectWidth = 25.0,
            objectUnit = LengthUnit.CM,
            gap = 2.0,
            gapUnit = LengthUnit.CM,
            requestedQuantity = 3
        )

        val resultFits = ShelfCalculatorEngine.calculate(inputFits)
        assertEquals(4, resultFits.capacity)
        assertEquals(true, resultFits.requestedFits)
        assertEquals(1, resultFits.additionalCanFit)

        val inputExceeds = inputFits.copy(requestedQuantity = 6)
        val resultExceeds = ShelfCalculatorEngine.calculate(inputExceeds)
        assertEquals(4, resultExceeds.capacity)
        assertEquals(false, resultExceeds.requestedFits)
        assertEquals(2, resultExceeds.excessRequested)
        assertNotNull(resultExceeds.excessWidthNeededBaseMm)
    }

    @Test
    fun testEdgeCases_ZeroWidth_ObjectLargerThanShelf() {
        val inputZeroShelf = ShelfCalculationInput(
            shelfWidth = 0.0,
            shelfUnit = LengthUnit.CM,
            objectWidth = 10.0,
            objectUnit = LengthUnit.CM
        )
        val resultZero = ShelfCalculatorEngine.calculate(inputZeroShelf)
        assertEquals(0, resultZero.capacity)

        val inputObjTooBig = ShelfCalculationInput(
            shelfWidth = 50.0,
            shelfUnit = LengthUnit.CM,
            objectWidth = 60.0,
            objectUnit = LengthUnit.CM
        )
        val resultTooBig = ShelfCalculatorEngine.calculate(inputObjTooBig)
        assertEquals(0, resultTooBig.capacity)
    }
}
