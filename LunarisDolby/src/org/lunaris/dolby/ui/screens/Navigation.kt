/*
 * Copyright (C) 2024-2025 Lunaris AOSP
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lunaris.dolby.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.lunaris.dolby.ui.viewmodel.AppProfileViewModel
import org.lunaris.dolby.ui.viewmodel.DolbyViewModel
import org.lunaris.dolby.ui.viewmodel.EqualizerViewModel

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxSize

sealed class Screen(val route: String) {
    object Settings : Screen("settings")
    object Equalizer : Screen("equalizer")
    object Advanced : Screen("advanced")
    object AppProfiles : Screen("app_profiles")
    object ImportExport : Screen("import_export")
}

val bottomBarRoutes = listOf(Screen.Settings.route, Screen.Equalizer.route, Screen.Advanced.route)

fun Modifier.horizontalSwipeNavigator(
    currentRoute: String?,
    destinations: List<String>,
    onNavigate: (Int) -> Unit
): Modifier = pointerInput(currentRoute) {
    var totalDrag = 0f
    detectHorizontalDragGestures(
        onDragStart = { totalDrag = 0f },
        onHorizontalDrag = { change, dragAmount ->
            change.consume()
            totalDrag += dragAmount
        },
        onDragEnd = {
            val threshold = 150f
            if (kotlin.math.abs(totalDrag) > threshold) {
                val currentIndex = destinations.indexOf(currentRoute)
                if (currentIndex == -1) return@detectHorizontalDragGestures

                if (totalDrag < 0) {
                    val next = (currentIndex + 1).coerceAtMost(destinations.lastIndex)
                    if (next != currentIndex) onNavigate(next)
                } else {
                    val prev = (currentIndex - 1).coerceAtLeast(0)
                    if (prev != currentIndex) onNavigate(prev)
                }
            }
        }
    )
}

@Composable
fun DolbyNavHost(
    dolbyViewModel: DolbyViewModel,
    equalizerViewModel: EqualizerViewModel
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val hostModifier = Modifier
        .fillMaxSize()
        .horizontalSwipeNavigator(
            currentRoute = currentRoute,
            destinations = bottomBarRoutes,
            onNavigate = { index ->
                navController.navigate(bottomBarRoutes[index]) {
                    popUpTo(Screen.Settings.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

    NavHost(
        navController = navController,
        startDestination = Screen.Settings.route,
        modifier = hostModifier,
        enterTransition = {
            val targetRoute = targetState.destination.route
            val initialRoute = initialState.destination.route
            val targetIndex = bottomBarRoutes.indexOf(targetRoute)
            val initialIndex = bottomBarRoutes.indexOf(initialRoute)

            when {
                targetIndex != -1 && initialIndex != -1 -> {
                    val offsetSign = if (targetIndex > initialIndex) 1 else -1
                    slideInHorizontally(initialOffsetX = { it * offsetSign }, animationSpec = tween(300))
                }
                targetRoute in bottomBarRoutes && initialRoute !in bottomBarRoutes -> {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300))
                }
                else -> slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
            }
        },
        exitTransition = {
            val targetRoute = targetState.destination.route
            val initialRoute = initialState.destination.route
            val targetIndex = bottomBarRoutes.indexOf(targetRoute)
            val initialIndex = bottomBarRoutes.indexOf(initialRoute)

            when {
                targetIndex != -1 && initialIndex != -1 -> {
                    val offsetSign = if (targetIndex > initialIndex) -1 else 1
                    slideOutHorizontally(targetOffsetX = { it * offsetSign }, animationSpec = tween(300))
                }
                initialRoute in bottomBarRoutes && targetRoute !in bottomBarRoutes -> {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
                }
                else -> slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
            }
        },
        popEnterTransition = {
            val targetRoute = targetState.destination.route
            val initialRoute = initialState.destination.route
            val targetIndex = bottomBarRoutes.indexOf(targetRoute)
            val initialIndex = bottomBarRoutes.indexOf(initialRoute)

            when {
                targetIndex != -1 && initialIndex != -1 -> {
                    val offsetSign = if (targetIndex > initialIndex) 1 else -1
                    slideInHorizontally(initialOffsetX = { it * offsetSign }, animationSpec = tween(300))
                }
                targetRoute in bottomBarRoutes -> {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300))
                }
                else -> slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300))
            }
        },
        popExitTransition = {
            val targetRoute = targetState.destination.route
            val initialRoute = initialState.destination.route
            val targetIndex = bottomBarRoutes.indexOf(targetRoute)
            val initialIndex = bottomBarRoutes.indexOf(initialRoute)

            when {
                targetIndex != -1 && initialIndex != -1 -> {
                    val offsetSign = if (targetIndex > initialIndex) -1 else 1
                    slideOutHorizontally(targetOffsetX = { it * offsetSign }, animationSpec = tween(300))
                }
                initialRoute !in bottomBarRoutes -> {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
                }
                else -> slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
            }
        }
    ) {
        composable(Screen.Settings.route) {
            ModernDolbySettingsScreen(
                viewModel = dolbyViewModel,
                navController = navController
            )
        }

        composable(Screen.Equalizer.route) {
            LaunchedEffect(Unit) {
                equalizerViewModel.loadEqualizer()
            }
            
            ModernEqualizerScreen(
                viewModel = equalizerViewModel,
                navController = navController
            )
        }

        composable(Screen.Advanced.route) {
            ModernAdvancedSettingsScreen(
                viewModel = dolbyViewModel,
                navController = navController
            )
        }
        
        composable(Screen.AppProfiles.route) {
            val context = LocalContext.current
            val appProfileViewModel: AppProfileViewModel = viewModel(
                factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
                    context.applicationContext as android.app.Application
                )
            )
            
            AppProfileScreen(
                viewModel = appProfileViewModel,
                navController = navController
            )
        }
        
        composable(Screen.ImportExport.route) {
            PresetImportExportScreen(
                viewModel = equalizerViewModel,
                navController = navController
            )
        }
    }
}
