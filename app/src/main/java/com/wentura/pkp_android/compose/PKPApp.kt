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
import com.wentura.pkp_android.compose.authentication.AuthenticationScreen
import com.wentura.pkp_android.compose.home.HomeScreen
import com.wentura.pkp_android.compose.myaccount.MyAccountScreen
import com.wentura.pkp_android.compose.search.SearchScreen
import com.wentura.pkp_android.ui.PKPAndroidTheme
import com.wentura.pkp_android.viewmodels.AuthenticationViewModel
import kotlinx.coroutines.launch

@Composable
fun PKPApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val authenticationViewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)

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
            }, onMyAccountClick = {
                navController.navigate(Screen.MyAccount.route)

                scope.launch {
                    drawerState.close()
                }
            }, authenticationViewModel = authenticationViewModel
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(onUpClick = { navController.navigateUp() })
        }

        composable(Screen.Login.route) {
            AuthenticationScreen(
                onUpClick = { navController.navigateUp() },
                onSignUp = { navController.navigateUp() },
                authenticationViewModel = authenticationViewModel
            )
        }

        composable(Screen.MyAccount.route) {
            MyAccountScreen(onUpClick = { navController.navigateUp() })
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
