package com.wentura.pkp_android.compose

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wentura.pkp_android.compose.home.HomeScreen
import com.wentura.pkp_android.compose.login.PagerScreen
import com.wentura.pkp_android.compose.search.SearchScreen
import com.wentura.pkp_android.ui.PKPAndroidTheme
import com.wentura.pkp_android.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun PKPApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val loginViewModel: LoginViewModel = viewModel()

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
            HomeScreen(drawerState = drawerState, onSearchClick = {
                navController.navigate(Screen.Search.route)
                scope.launch {
                    drawerState.close()
                }
            }, onLoginClick = {
                navController.navigate(Screen.Login.route)
                scope.launch {
                    drawerState.close()
                }
            }, loginViewModel = loginViewModel
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(onUpClick = { navController.navigateUp() })
        }

        composable(Screen.Login.route) {
            PagerScreen(
                onUpClick = { navController.navigateUp() },
                onSignUp = { navController.navigateUp() },
                loginViewModel = loginViewModel
            )
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
