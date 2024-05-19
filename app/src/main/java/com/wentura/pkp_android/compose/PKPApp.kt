package com.wentura.pkp_android.compose

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wentura.pkp_android.compose.authentication.AuthenticationScreen
import com.wentura.pkp_android.compose.home.HomeScreen
import com.wentura.pkp_android.compose.myaccount.MyAccountScreen
import com.wentura.pkp_android.compose.mytickets.MyTicketsScreen
import com.wentura.pkp_android.compose.passengers.PassengersScreen
import com.wentura.pkp_android.compose.search.SearchScreen
import kotlinx.coroutines.launch

@Composable
fun PKPApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(700))
        },
        exitTransition = { fadeOut(animationSpec = tween(700)) },
        popEnterTransition = { fadeIn(animationSpec = tween(700)) },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(700))
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                drawerState = drawerState,
                onSearchClick = {
                    navController.navigate(Screen.Search.route)

                    scope.launch { drawerState.close() }
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)

                    scope.launch { drawerState.close() }
                },
                onMyAccountClick = {
                    navController.navigate(Screen.MyAccount.route)

                    scope.launch { drawerState.close() }
                },
                onMyTicketsClick = {
                    navController.navigate(Screen.MyTickets.route)

                    scope.launch { drawerState.close() }
                },
                onPassengersClick = {
                    navController.navigate(Screen.Passengers.route)

                    scope.launch { drawerState.close() }
                }
            )
        }

        composable(Screen.Search.route) { SearchScreen(onUpClick = { navController.navigateUp() }) }

        composable(Screen.Login.route) {
            AuthenticationScreen(
                onUpClick = { navController.navigateUp() },
                onSignUp = { navController.navigateUp() },
                onSignIn = { navController.navigateUp() }
            )
        }

        composable(Screen.MyAccount.route) {
            MyAccountScreen(onUpClick = { navController.navigateUp() })
        }

        composable(Screen.MyTickets.route) {
            MyTicketsScreen(onUpClick = { navController.navigateUp() })
        }

        composable(Screen.Passengers.route) {
            PassengersScreen(onUpClick = { navController.navigateUp() })
        }
    }
}
