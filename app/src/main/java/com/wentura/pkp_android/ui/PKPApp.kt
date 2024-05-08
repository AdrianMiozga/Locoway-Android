package com.wentura.pkp_android.ui

import Search
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wentura.pkp_android.ui.home.Home
import com.wentura.pkp_android.ui.theme.PKPAndroidTheme

@Composable
fun PKPApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home", enterTransition = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Up, tween(700)
        )
    }, exitTransition = {
        fadeOut(animationSpec = tween(700))
    }, popEnterTransition = {
        fadeIn(animationSpec = tween(700))
    }, popExitTransition = {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Down, tween(700)
        )
    }) {
        composable("home") {
            Home(onSearchClick = { navController.navigate("search") })
        }

        composable("search") {
            Search(onUpClick = { navController.navigateUp() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PKPAppPreview() {
    PKPAndroidTheme {
        PKPApp()
    }
}
