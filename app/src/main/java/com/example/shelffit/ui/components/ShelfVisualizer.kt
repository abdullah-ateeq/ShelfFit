package com.example.shelffit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shelffit.domain.model.ShelfCalculationResult
import com.example.shelffit.ui.theme.ClearanceColor
import com.example.shelffit.ui.theme.RemainingSpaceColor
import com.example.shelffit.ui.theme.ShelfWoodLight
import com.example.shelffit.utils.formatMeasurement

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShelfVisualizer(
    result: ShelfCalculationResult,
    modifier: Modifier = Modifier
) {
    var isExpandedScale by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with title and scale toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Visual Shelf Layout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Proportional arrangement preview",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AssistChip(
                    onClick = { isExpandedScale = !isExpandedScale },
                    label = { Text(if (isExpandedScale) "Fit to Screen" else "Scroll Detailed") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isExpandedScale) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Diagram Canvas / Layout
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                val scrollState = rememberScrollState()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (isExpandedScale) Modifier.horizontalScroll(scrollState) else Modifier)
                        .padding(horizontal = 12.dp, vertical = 20.dp)
                ) {
                    if (result.capacity == 0 && result.visualObjects.isEmpty()) {
                        // Empty / zero state diagram
                        ZeroCapacityDiagram(result)
                    } else {
                        // Proportional items diagram
                        ProportionalShelfDiagram(
                            result = result,
                            isExpandedScale = isExpandedScale
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LegendItem(
                    color = MaterialTheme.colorScheme.primary,
                    label = "Object (${result.getObjectWidthInDisplayUnit().formatMeasurement(1)} ${result.displayUnit.symbol})"
                )
                if (result.leftClearanceBaseMm > 0 || result.rightClearanceBaseMm > 0 || result.sideSpacingBaseMm > 0) {
                    LegendItem(
                        color = ClearanceColor,
                        label = "Clearance / Spacing"
                    )
                }
                if (result.remainingWidthBaseMm > 0) {
                    LegendItem(
                        color = RemainingSpaceColor,
                        label = "Free Space (${result.getRemainingWidthInDisplayUnit().formatMeasurement(1)} ${result.displayUnit.symbol})"
                    )
                }
                if (result.excessRequested != null && result.excessRequested > 0) {
                    LegendItem(
                        color = MaterialTheme.colorScheme.error,
                        label = "Excess (+${result.excessRequested})"
                    )
                }
            }
        }
    }
}

@Composable
private fun ZeroCapacityDiagram(result: ShelfCalculationResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Shelf plank
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ShelfWoodLight)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Shelf cannot fit any objects with current dimensions.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Shelf: ${result.getShelfWidthInDisplayUnit().formatMeasurement(1)} ${result.displayUnit.symbol} • Object: ${result.getObjectWidthInDisplayUnit().formatMeasurement(1)} ${result.displayUnit.symbol}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProportionalShelfDiagram(
    result: ShelfCalculationResult,
    isExpandedScale: Boolean
) {
    val totalShelfMm = maxOf(1.0, result.shelfWidthBaseMm)
    val displayUnit = result.displayUnit

    // For fitted layout, we use weight-based sizing or minimum proportional Dp for scrollable
    val minDiagramWidth = if (isExpandedScale) {
        val count = maxOf(result.capacity, result.visualObjects.size, 1)
        (count * 90).dp.coerceAtLeast(360.dp)
    } else {
        Modifier.fillMaxWidth()
    }

    Column(
        modifier = if (isExpandedScale) Modifier.width(minDiagramWidth as androidx.compose.ui.unit.Dp) else Modifier.fillMaxWidth()
    ) {
        // Top object arrangement row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Start
        ) {
            // Left Clearance
            if (result.leftClearanceBaseMm > 0) {
                val leftRatio = (result.leftClearanceBaseMm / totalShelfMm).toFloat().coerceIn(0.04f, 0.35f)
                ClearanceBlock(
                    label = "Left\n${result.getLeftClearanceInDisplayUnit().formatMeasurement(1)}",
                    modifier = Modifier.weight(leftRatio)
                )
            }

            // Left Side Spacing
            if (result.sideSpacingBaseMm > 0) {
                val sideRatio = (result.sideSpacingBaseMm / totalShelfMm).toFloat().coerceIn(0.03f, 0.2f)
                ClearanceBlock(
                    label = "Side\n${result.getSideSpacingInDisplayUnit().formatMeasurement(1)}",
                    modifier = Modifier.weight(sideRatio)
                )
            }

            // Placed Objects & Gaps
            result.visualObjects.forEachIndexed { index, visualObj ->
                val objRatio = (result.objectWidthBaseMm / totalShelfMm).toFloat().coerceIn(0.06f, 0.8f)

                // Object Box
                ObjectBlock(
                    obj = visualObj,
                    modifier = Modifier.weight(objRatio)
                )

                // Gap after object (if not last item and gap > 0)
                if (visualObj.gapAfterMm > 0 && index < result.visualObjects.size - 1) {
                    val gapRatio = (result.gapBaseMm / totalShelfMm).toFloat().coerceIn(0.02f, 0.2f)
                    GapBlock(
                        label = "${result.getGapInDisplayUnit().formatMeasurement(1)}",
                        modifier = Modifier.weight(gapRatio)
                    )
                }
            }

            // Right Side Spacing
            if (result.sideSpacingBaseMm > 0) {
                val sideRatio = (result.sideSpacingBaseMm / totalShelfMm).toFloat().coerceIn(0.03f, 0.2f)
                ClearanceBlock(
                    label = "Side\n${result.getSideSpacingInDisplayUnit().formatMeasurement(1)}",
                    modifier = Modifier.weight(sideRatio)
                )
            }

            // Remaining Space on shelf
            if (result.remainingWidthBaseMm > 0) {
                val remRatio = (result.remainingWidthBaseMm / totalShelfMm).toFloat().coerceIn(0.05f, 0.6f)
                RemainingBlock(
                    label = "Free\n${result.getRemainingWidthInDisplayUnit().formatMeasurement(1)} ${displayUnit.symbol}",
                    modifier = Modifier.weight(remRatio)
                )
            }

            // Right Clearance
            if (result.rightClearanceBaseMm > 0) {
                val rightRatio = (result.rightClearanceBaseMm / totalShelfMm).toFloat().coerceIn(0.04f, 0.35f)
                ClearanceBlock(
                    label = "Right\n${result.getRightClearanceInDisplayUnit().formatMeasurement(1)}",
                    modifier = Modifier.weight(rightRatio)
                )
            }
        }

        // Shelf Plank (Wooden Surface Base)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(ShelfWoodLight)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Shelf Dimension measurement line
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Total Shelf: ${result.getShelfWidthInDisplayUnit().formatMeasurement(1)} ${displayUnit.symbol} (Usable: ${result.getAvailableWidthInDisplayUnit().formatMeasurement(1)} ${displayUnit.symbol})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${result.getShelfWidthInDisplayUnit().formatMeasurement(1)} ${displayUnit.symbol}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ObjectBlock(
    obj: com.example.shelffit.domain.model.VisualObject,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        obj.isExcess -> MaterialTheme.colorScheme.errorContainer
        obj.isRequested -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val contentColor = when {
        obj.isExcess -> MaterialTheme.colorScheme.onErrorContainer
        obj.isRequested -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    val borderColor = if (obj.isExcess) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .height(85.dp)
            .padding(horizontal = 1.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(2.dp)
        ) {
            Text(
                text = if (obj.isExcess) "!#${obj.index}" else "#${obj.index}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                maxLines = 1
            )
            if (obj.isExcess) {
                Text(
                    text = "Excess",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = contentColor,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun GapBlock(
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(60.dp)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun ClearanceBlock(
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(85.dp)
            .padding(horizontal = 1.dp)
            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            .background(ClearanceColor.copy(alpha = 0.20f))
            .border(1.dp, ClearanceColor.copy(alpha = 0.5f), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = ClearanceColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
            maxLines = 2
        )
    }
}

@Composable
private fun RemainingBlock(
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(85.dp)
            .padding(horizontal = 1.dp)
            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
            .background(RemainingSpaceColor.copy(alpha = 0.15f))
            .border(1.dp, RemainingSpaceColor.copy(alpha = 0.6f), RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = RemainingSpaceColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
            maxLines = 2
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
