package org.wentura.locoway.compose

sealed class Screen(val route: String) {
    data object Authentication : Screen("authentication")

    data object ConnectionDetails : Screen("connection_details")

    data object Connections : Screen("connections")

    data object Home : Screen("home")

    data object MyAccount : Screen("my_account")

    data object MyTicket : Screen("my_ticket")

    data object MyTickets : Screen("my_tickets")

    data object Passengers : Screen("passengers")
}
