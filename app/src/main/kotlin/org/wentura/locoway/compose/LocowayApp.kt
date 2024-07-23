package org.wentura.locoway.compose

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
import kotlinx.coroutines.launch
import org.wentura.locoway.compose.authentication.AuthenticationScreen
import org.wentura.locoway.compose.connectiondetails.ConnectionsDetailsScreen
import org.wentura.locoway.compose.connections.ConnectionsScreen
import org.wentura.locoway.compose.home.HomeScreen
import org.wentura.locoway.compose.myaccount.MyAccountScreen
import org.wentura.locoway.compose.myticket.MyTicketScreen
import org.wentura.locoway.compose.mytickets.MyTicketsScreen
import org.wentura.locoway.compose.passengers.PassengersScreen

@Composable
fun LocowayApp() {
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
                    goToAuthenticationScreen = { navController.navigate(Screen.Login.route) },
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
