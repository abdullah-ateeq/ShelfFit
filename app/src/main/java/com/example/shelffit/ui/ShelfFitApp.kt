package com.example.shelffit.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shelffit.ui.navigation.BottomNavItem
import com.example.shelffit.ui.navigation.Routes
import com.example.shelffit.ui.screens.AboutScreen
import com.example.shelffit.ui.screens.CalculatorScreen
import com.example.shelffit.ui.screens.HistoryScreen
import com.example.shelffit.ui.screens.HomeScreen
import com.example.shelffit.ui.screens.ResultScreen
import com.example.shelffit.ui.screens.SettingsScreen
import com.example.shelffit.ui.screens.SplashScreen
import com.example.shelffit.ui.theme.ShelfFitTheme
import com.example.shelffit.utils.HapticUtils
import com.example.shelffit.viewmodel.ShelfViewModel

@Composable
fun ShelfFitApp(viewModel: ShelfViewModel) {
    val themeMode by viewModel.themeMode.collectAsState()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()
    val hapticFeedback = LocalHapticFeedback.current

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Routes.HOME,
        Routes.CALCULATOR,
        Routes.HISTORY,
        Routes.SETTINGS
    )

    ShelfFitTheme(themeMode = themeMode) {
        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    NavigationBar {
                        BottomNavItem.entries.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                icon = {
                                    Icon(imageVector = item.icon, contentDescription = item.title)
                                },
                                label = { Text(item.title) },
                                selected = selected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        HapticUtils.performClick(hapticFeedback, hapticsEnabled)
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.SPLASH,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(Routes.SPLASH) {
                    SplashScreen(
                        onSplashFinished = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.HOME) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToCalculator = {
                            navController.navigate(Routes.CALCULATOR)
                        },
                        onNavigateToResult = {
                            navController.navigate(Routes.RESULT)
                        },
                        onNavigateToHistory = {
                            navController.navigate(Routes.HISTORY)
                        },
                        onNavigateToSettings = {
                            navController.navigate(Routes.SETTINGS)
                        },
                        onNavigateToAbout = {
                            navController.navigate(Routes.ABOUT)
                        }
                    )
                }

                composable(Routes.CALCULATOR) {
                    CalculatorScreen(
                        viewModel = viewModel,
                        onNavigateToResult = {
                            navController.navigate(Routes.RESULT)
                        }
                    )
                }

                composable(Routes.RESULT) {
                    ResultScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Routes.HISTORY) {
                    HistoryScreen(
                        viewModel = viewModel,
                        onNavigateToResult = {
                            navController.navigate(Routes.RESULT)
                        },
                        onNavigateToCalculator = {
                            navController.navigate(Routes.CALCULATOR)
                        }
                    )
                }

                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateToAbout = {
                            navController.navigate(Routes.ABOUT)
                        }
                    )
                }

                composable(Routes.ABOUT) {
                    AboutScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
