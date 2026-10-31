package com.example.shelffit.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val CALCULATOR = "calculator"
    const val RESULT = "result"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

enum class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    HOME(Routes.HOME, "Home", Icons.Default.Home),
    CALCULATOR(Routes.CALCULATOR, "Calculate", Icons.Default.Calculate),
    HISTORY(Routes.HISTORY, "History", Icons.Default.History),
    SETTINGS(Routes.SETTINGS, "Settings", Icons.Default.Settings);
}
