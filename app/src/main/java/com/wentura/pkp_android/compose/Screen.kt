package com.wentura.pkp_android.compose

sealed class Screen(val route: String) {
    data object Home : Screen("home")

    data object Search : Screen("search")

    data object Login : Screen("login")

    data object MyAccount : Screen("my_account")

    data object MyTickets : Screen("my_tickets")

    data object Passengers : Screen("passengers")
}
