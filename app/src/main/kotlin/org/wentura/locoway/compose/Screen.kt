package org.wentura.locoway.compose

sealed class Screen(val route: String) {
    data object Home : Screen("home")

    data object Search : Screen("search")

    data object ConnectionDetails : Screen("connection_details")

    data object Login : Screen("login")

    data object MyAccount : Screen("my_account")

    data object MyTickets : Screen("my_tickets")

    data object MyTicket : Screen("my_ticket")

    data object Passengers : Screen("passengers")
}
