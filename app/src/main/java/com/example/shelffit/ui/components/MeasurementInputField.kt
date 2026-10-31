package com.example.shelffit.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.shelffit.utils.LengthUnit

@Composable
fun MeasurementInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    hint: String? = null,
    error: String? = null,
    unit: LengthUnit? = null,
    onUnitChange: ((LengthUnit) -> Unit)? = null,
    isIntegerOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            // Filter to only valid number characters
            val filtered = if (isIntegerOnly) {
                input.filter { it.isDigit() }
            } else {
                val cleaned = input.replace(',', '.')
                // Allow only one decimal point
                if (cleaned.count { it == '.' } <= 1 && cleaned.all { it.isDigit() || it == '.' }) {
                    cleaned
                } else {
                    value
                }
            }
            onValueChange(filtered)
        },
        label = { Text(label) },
        supportingText = {
            if (error != null) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            } else if (hint != null) {
                Text(text = hint)
            }
        },
        isError = error != null,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isIntegerOnly) KeyboardType.Number else KeyboardType.Decimal
        ),
        leadingIcon = leadingIcon,
        trailingIcon = {
            if (unit != null && onUnitChange != null) {
                UnitSelectorPill(
                    selectedUnit = unit,
                    onUnitSelected = onUnitChange
                )
            } else if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear input",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = modifier.fillMaxWidth()
    )
}
