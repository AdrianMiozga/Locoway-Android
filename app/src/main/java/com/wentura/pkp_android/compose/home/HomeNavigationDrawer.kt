package com.wentura.pkp_android.compose.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wentura.pkp_android.R
import com.wentura.pkp_android.ui.PKPAndroidTheme
import com.wentura.pkp_android.viewmodels.HomeUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun HomeNavigationDrawer(
    uiState: StateFlow<HomeUiState>,
    drawerState: DrawerState,
    scope: CoroutineScope = rememberCoroutineScope(),
    onLoginClick: () -> Unit = {},
    onMyAccountClick: () -> Unit = {},
    onMyTicketsClick: () -> Unit = {},
    onPassengersClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(252.dp)) {
                Text(
                    stringResource(R.string.app_name),
                    modifier = Modifier.padding(start = 28.dp, end = 28.dp, top = 16.dp))

                HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp))

                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.connection_search)) },
                    icon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(horizontal = 12.dp))

                HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp))

                val state by uiState.collectAsStateWithLifecycle()

                if (state.isSignedIn) {
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.my_account)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = null)
                        },
                        selected = false,
                        onClick = onMyAccountClick,
                        modifier = Modifier.padding(horizontal = 12.dp))

                    NavigationDrawerItem(
                        label = { Text(text = stringResource(R.string.my_tickets)) },
                        icon = {
                            Icon(
                                painter =
                                    painterResource(R.drawable.outline_confirmation_number_24),
                                contentDescription = null)
                        },
                        selected = false,
                        onClick = onMyTicketsClick,
                        modifier = Modifier.padding(horizontal = 12.dp))

                    NavigationDrawerItem(
                        label = { Text(text = stringResource(R.string.passengers)) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.outline_groups_24),
                                contentDescription = null)
                        },
                        selected = false,
                        onClick = onPassengersClick,
                        modifier = Modifier.padding(horizontal = 12.dp))
                } else {
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(R.string.login)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = null)
                        },
                        selected = false,
                        onClick = onLoginClick,
                        modifier = Modifier.padding(horizontal = 12.dp))
                }
            }
        }) {
            content()
        }
}

@Preview(showBackground = true)
@Composable
fun HomeNavigationDrawerPreview() {
    PKPAndroidTheme {
        HomeNavigationDrawer(
            uiState = MutableStateFlow(HomeUiState(isSignedIn = false)),
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open))
    }
}

@Preview(showBackground = true)
@Composable
fun SignedInHomeNavigationDrawerPreview() {
    PKPAndroidTheme {
        HomeNavigationDrawer(
            uiState = MutableStateFlow(HomeUiState(isSignedIn = true)),
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open))
    }
}
