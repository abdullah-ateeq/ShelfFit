package com.example.shelffit.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val displayFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
    private val shortFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    fun formatTimestamp(timestamp: Long): String {
        return displayFormat.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        return shortFormat.format(Date(timestamp))
    }
}
