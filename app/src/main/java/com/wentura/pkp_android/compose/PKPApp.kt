package com.wentura.pkp_android.compose

import Search
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wentura.pkp_android.compose.home.Home
import com.wentura.pkp_android.ui.PKPAndroidTheme

@Composable
fun PKPApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route, enterTransition = {
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
        composable(Screen.Home.route) {
            Home(
                onSearchClick = { navController.navigate(Screen.Search.route) },
                drawerValue = DrawerValue.Closed
            )
        }

        composable(Screen.Search.route) {
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
