package com.example.shelffit

import com.example.shelffit.data.model.HistoryEntry
import com.example.shelffit.utils.LengthUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class HistoryEntryTest {

    @Test
    fun testJsonSerializationAndDeserialization() {
        val entry = HistoryEntry(
            shelfWidth = 150.0,
            shelfUnit = LengthUnit.CM,
            leftClearance = 5.0,
            rightClearance = 5.0,
            sideSpacing = 2.0,
            objectWidth = 20.0,
            objectUnit = LengthUnit.CM,
            gap = 1.0,
            gapUnit = LengthUnit.CM,
            requestedQuantity = 5,
            capacity = 6,
            totalOccupiedWidth = 125.0,
            remainingWidth = 11.0,
            resultUnit = LengthUnit.CM
        )

        val json = entry.toJson()
        val restored = HistoryEntry.fromJson(json)

        assertEquals(entry.id, restored.id)
        assertEquals(entry.shelfWidth, restored.shelfWidth, 0.001)
        assertEquals(entry.shelfUnit, restored.shelfUnit)
        assertEquals(entry.leftClearance, restored.leftClearance, 0.001)
        assertEquals(entry.rightClearance, restored.rightClearance, 0.001)
        assertEquals(entry.sideSpacing, restored.sideSpacing, 0.001)
        assertEquals(entry.objectWidth, restored.objectWidth, 0.001)
        assertEquals(entry.gap, restored.gap, 0.001)
        assertEquals(entry.requestedQuantity, restored.requestedQuantity)
        assertEquals(entry.capacity, restored.capacity)
        assertEquals(entry.totalOccupiedWidth, restored.totalOccupiedWidth, 0.001)
        assertEquals(entry.remainingWidth, restored.remainingWidth, 0.001)
    }

    @Test
    fun testListJsonConversion() {
        val list = listOf(
            HistoryEntry(
                shelfWidth = 100.0,
                shelfUnit = LengthUnit.CM,
                objectWidth = 10.0,
                objectUnit = LengthUnit.CM,
                capacity = 10,
                totalOccupiedWidth = 100.0,
                remainingWidth = 0.0,
                resultUnit = LengthUnit.CM
            ),
            HistoryEntry(
                shelfWidth = 3.0,
                shelfUnit = LengthUnit.M,
                objectWidth = 25.0,
                objectUnit = LengthUnit.CM,
                capacity = 12,
                totalOccupiedWidth = 300.0,
                remainingWidth = 0.0,
                resultUnit = LengthUnit.M
            )
        )

        val jsonString = HistoryEntry.listToJsonString(list)
        assertNotNull(jsonString)
        val restoredList = HistoryEntry.listFromJsonString(jsonString)

        assertEquals(2, restoredList.size)
        assertEquals(100.0, restoredList[0].shelfWidth, 0.001)
        assertEquals(3.0, restoredList[1].shelfWidth, 0.001)
    }
}
