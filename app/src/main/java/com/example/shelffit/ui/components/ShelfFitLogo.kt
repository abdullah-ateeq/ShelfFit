package com.example.shelffit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shelffit.ui.theme.DarkPrimary
import com.example.shelffit.ui.theme.LightPrimary
import com.example.shelffit.ui.theme.ShelfWoodLight

@Composable
fun ShelfFitLogo(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    shelfColor: Color = ShelfWoodLight,
    accentColor: Color = MaterialTheme.colorScheme.tertiary
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Shelf plank at bottom
        val shelfHeight = h * 0.12f
        val shelfY = h * 0.76f
        drawRoundRect(
            color = shelfColor,
            topLeft = Offset(w * 0.05f, shelfY),
            size = Size(w * 0.90f, shelfHeight),
            cornerRadius = CornerRadius(shelfHeight * 0.35f, shelfHeight * 0.35f)
        )

        // Wall brackets / supports under shelf
        drawRoundRect(
            color = shelfColor.copy(alpha = 0.75f),
            topLeft = Offset(w * 0.20f, shelfY + shelfHeight),
            size = Size(w * 0.08f, h * 0.12f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = shelfColor.copy(alpha = 0.75f),
            topLeft = Offset(w * 0.72f, shelfY + shelfHeight),
            size = Size(w * 0.08f, h * 0.12f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // 3 Books / Boxes resting neatly on shelf with small gaps
        // Box 1 (tall teal)
        val box1Width = w * 0.22f
        val box1Height = h * 0.46f
        drawRoundRect(
            color = primaryColor,
            topLeft = Offset(w * 0.12f, shelfY - box1Height),
            size = Size(box1Width, box1Height),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
        )

        // Box 2 (medium accent/amber)
        val box2Width = w * 0.22f
        val box2Height = h * 0.36f
        drawRoundRect(
            color = accentColor,
            topLeft = Offset(w * 0.39f, shelfY - box2Height),
            size = Size(box2Width, box2Height),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
        )

        // Box 3 (tall teal with tint)
        val box3Width = w * 0.22f
        val box3Height = h * 0.52f
        drawRoundRect(
            color = primaryColor.copy(alpha = 0.85f),
            topLeft = Offset(w * 0.66f, shelfY - box3Height),
            size = Size(box3Width, box3Height),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
        )

        // Subtle ruler tick on the shelf
        val tickY = shelfY + shelfHeight * 0.5f
        val tickColor = Color.White.copy(alpha = 0.6f)
        for (i in 1..7) {
            val tickX = w * (0.15f + i * 0.09f)
            drawLine(
                color = tickColor,
                start = Offset(tickX, tickY - (if (i % 2 == 0) 4f else 2.5f)),
                end = Offset(tickX, tickY + (if (i % 2 == 0) 4f else 2.5f)),
                strokeWidth = 2f
            )
        }
    }
}
