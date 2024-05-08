package com.wentura.pkp_android.compose

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
}
