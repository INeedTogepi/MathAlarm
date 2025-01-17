package com.timilehinaregbesola.mathalarm.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.timilehinaregbesola.mathalarm.framework.database.AlarmEntity
import com.timilehinaregbesola.mathalarm.presentation.alarmlist.components.ListDisplayScreen
import com.timilehinaregbesola.mathalarm.presentation.alarmmath.components.MathScreen
import com.timilehinaregbesola.mathalarm.presentation.alarmsettings.components.AlarmBottomSheet
import com.timilehinaregbesola.mathalarm.presentation.appsettings.AlarmPreferencesImpl
import com.timilehinaregbesola.mathalarm.presentation.appsettings.components.AppSettingsScreen
import com.timilehinaregbesola.mathalarm.presentation.appsettings.shouldUseDarkColors
import com.timilehinaregbesola.mathalarm.utils.Navigation
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalMaterialNavigationApi
@ExperimentalAnimationApi
@InternalCoroutinesApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun NavGraph(preferences: AlarmPreferencesImpl) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    Surface(color = MaterialTheme.colors.background) {
        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
            sheetShape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        ) {
            AnimatedNavHost(navController = navController, startDestination = Navigation.NAV_ALARM_LIST) {
                composable(
                    route = Navigation.NAV_ALARM_LIST,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    }
                ) {
                    ListDisplayScreen(
                        navController = navController,
                        darkTheme = preferences.shouldUseDarkColors(),
                    )
                }
                composable(
                    route = Navigation.NAV_ALARM_MATH,
                    deepLinks = listOf(navDeepLink { uriPattern = Navigation.NAV_ALARM_MATH_URI }),
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    }
                ) {
                    val alarmJson = it.arguments?.getString(Navigation.NAV_ALARM_MATH_ARGUMENT)
                    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                    val jsonAdapter = moshi.adapter(AlarmEntity::class.java).lenient()
                    val alarmObject = alarmJson?.let { it1 -> jsonAdapter.fromJson(it1) }
                    MathScreen(
                        navController = navController,
                        alarm = alarmObject!!,
                        darkTheme = preferences.shouldUseDarkColors()
                    )
                }
                composable(
                    route = Navigation.NAV_APP_SETTINGS,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    }
                ) {
                    AppSettingsScreen(
                        onBackPress = { navController.popBackStack() },
                        pref = preferences
                    )
                }
                bottomSheet(
                    route = Navigation.NAV_SETTINGS_SHEET
                ) { backStackEntry ->
                    val alarmJson = backStackEntry.arguments?.getString(Navigation.NAV_SETTINGS_SHEET_ARGUMENT)
                    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                    val jsonAdapter = moshi.adapter(AlarmEntity::class.java).lenient()
                    val alarmObject = alarmJson?.let { jsonAdapter.fromJson(it) }
                    AlarmBottomSheet(
                        navController = navController,
                        darkTheme = preferences.shouldUseDarkColors(),
                        alarm = alarmObject!!
                    )
                }
            }
        }
    }
}
