package com.example.shelffit.data.model

import com.example.shelffit.domain.model.ShelfCalculationInput
import com.example.shelffit.domain.model.ShelfCalculationResult
import com.example.shelffit.utils.DateUtils
import com.example.shelffit.utils.LengthUnit
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class HistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val shelfWidth: Double,
    val shelfUnit: LengthUnit,
    val leftClearance: Double = 0.0,
    val rightClearance: Double = 0.0,
    val sideSpacing: Double = 0.0,
    val objectWidth: Double,
    val objectUnit: LengthUnit,
    val gap: Double = 0.0,
    val gapUnit: LengthUnit = objectUnit,
    val requestedQuantity: Int? = null,
    val capacity: Int,
    val totalOccupiedWidth: Double,
    val remainingWidth: Double,
    val resultUnit: LengthUnit
) {
    val formattedDate: String
        get() = DateUtils.formatTimestamp(timestamp)

    fun toInput(): ShelfCalculationInput {
        return ShelfCalculationInput(
            shelfWidth = shelfWidth,
            shelfUnit = shelfUnit,
            leftClearance = leftClearance,
            rightClearance = rightClearance,
            sideSpacing = sideSpacing,
            objectWidth = objectWidth,
            objectUnit = objectUnit,
            gap = gap,
            gapUnit = gapUnit,
            requestedQuantity = requestedQuantity,
            resultUnit = resultUnit
        )
    }

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("timestamp", timestamp)
            put("shelfWidth", shelfWidth)
            put("shelfUnit", shelfUnit.name)
            put("leftClearance", leftClearance)
            put("rightClearance", rightClearance)
            put("sideSpacing", sideSpacing)
            put("objectWidth", objectWidth)
            put("objectUnit", objectUnit.name)
            put("gap", gap)
            put("gapUnit", gapUnit.name)
            if (requestedQuantity != null) {
                put("requestedQuantity", requestedQuantity)
            }
            put("capacity", capacity)
            put("totalOccupiedWidth", totalOccupiedWidth)
            put("remainingWidth", remainingWidth)
            put("resultUnit", resultUnit.name)
        }
    }

    companion object {
        fun fromResult(result: ShelfCalculationResult): HistoryEntry {
            val input = result.input
            return HistoryEntry(
                shelfWidth = input.shelfWidth,
                shelfUnit = input.shelfUnit,
                leftClearance = input.leftClearance,
                rightClearance = input.rightClearance,
                sideSpacing = input.sideSpacing,
                objectWidth = input.objectWidth,
                objectUnit = input.objectUnit,
                gap = input.gap,
                gapUnit = input.gapUnit,
                requestedQuantity = input.requestedQuantity,
                capacity = result.capacity,
                totalOccupiedWidth = result.getOccupiedWidthInDisplayUnit(),
                remainingWidth = result.getRemainingWidthInDisplayUnit(),
                resultUnit = result.displayUnit
            )
        }

        fun fromJson(json: JSONObject): HistoryEntry {
            return HistoryEntry(
                id = json.optString("id", UUID.randomUUID().toString()),
                timestamp = json.optLong("timestamp", System.currentTimeMillis()),
                shelfWidth = json.optDouble("shelfWidth", 0.0),
                shelfUnit = LengthUnit.valueOf(json.optString("shelfUnit", LengthUnit.CM.name)),
                leftClearance = json.optDouble("leftClearance", 0.0),
                rightClearance = json.optDouble("rightClearance", 0.0),
                sideSpacing = json.optDouble("sideSpacing", 0.0),
                objectWidth = json.optDouble("objectWidth", 0.0),
                objectUnit = LengthUnit.valueOf(json.optString("objectUnit", LengthUnit.CM.name)),
                gap = json.optDouble("gap", 0.0),
                gapUnit = LengthUnit.valueOf(json.optString("gapUnit", LengthUnit.CM.name)),
                requestedQuantity = if (json.has("requestedQuantity")) json.optInt("requestedQuantity") else null,
                capacity = json.optInt("capacity", 0),
                totalOccupiedWidth = json.optDouble("totalOccupiedWidth", 0.0),
                remainingWidth = json.optDouble("remainingWidth", 0.0),
                resultUnit = LengthUnit.valueOf(json.optString("resultUnit", LengthUnit.CM.name))
            )
        }

        fun listToJsonString(list: List<HistoryEntry>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }

        fun listFromJsonString(jsonString: String): List<HistoryEntry> {
            if (jsonString.isBlank()) return emptyList()
            return try {
                val array = JSONArray(jsonString)
                val list = mutableListOf<HistoryEntry>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(fromJson(obj))
                }
                list
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}
