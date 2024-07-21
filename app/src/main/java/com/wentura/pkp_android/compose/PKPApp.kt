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
import com.wentura.pkp_android.compose.connectiondetails.ConnectionsDetailsScreen
import com.wentura.pkp_android.compose.connections.ConnectionsScreen
import com.wentura.pkp_android.compose.home.HomeScreen
import com.wentura.pkp_android.compose.myaccount.MyAccountScreen
import com.wentura.pkp_android.compose.myticket.MyTicketScreen
import com.wentura.pkp_android.compose.mytickets.MyTicketsScreen
import com.wentura.pkp_android.compose.passengers.PassengersScreen
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
        }) {
            composable(Screen.Home.route) {
                HomeScreen(
                    drawerState = drawerState,
                    onSearchClick = { departureStation, arrivalStation, departureDate, departureTime
                        ->
                        navController.navigate(
                            "${Screen.Search.route}/$departureStation/$arrivalStation/$departureDate/$departureTime")

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
                    })
            }

            composable(
                "${Screen.Search.route}/{departureStation}/{arrivalStation}/{departureDate}/{departureTime}",
            ) {
                ConnectionsScreen(
                    onUpClick = { navController.navigateUp() },
                    onConnectionClick = { trainId ->
                        navController.navigate("${Screen.ConnectionDetails.route}/$trainId")
                    },
                )
            }

            composable("${Screen.ConnectionDetails.route}/{trainId}") {
                ConnectionsDetailsScreen(
                    onUpClick = { navController.navigateUp() },
                    onBuyButtonClick = { navController.popBackStack(Screen.Home.route, false) })
            }

            composable(Screen.Login.route) {
                AuthenticationScreen(
                    onUpClick = { navController.navigateUp() },
                    onSignUp = { navController.navigateUp() },
                    onSignIn = { navController.navigateUp() })
            }

            composable(Screen.MyAccount.route) {
                MyAccountScreen(onUpClick = { navController.navigateUp() })
            }

            composable(Screen.MyTickets.route) {
                MyTicketsScreen(
                    onUpClick = { navController.navigateUp() },
                    onTicketClick = { navController.navigate("${Screen.MyTicket.route}/$it") },
                )
            }

            composable("${Screen.MyTicket.route}/{ticketId}") {
                MyTicketScreen(onUpClick = { navController.navigateUp() })
            }

            composable(Screen.Passengers.route) {
                PassengersScreen(onUpClick = { navController.navigateUp() })
            }
        }
}
