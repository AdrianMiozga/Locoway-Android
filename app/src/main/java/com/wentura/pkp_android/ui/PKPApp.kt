package com.wentura.pkp_android.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wentura.pkp_android.R
import com.wentura.pkp_android.ui.home.Home
import com.wentura.pkp_android.ui.theme.PKPAndroidTheme
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PKPApp() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet(modifier = Modifier.width(252.dp)) {
            Text(
                stringResource(R.string.app_name),
                modifier = Modifier.padding(start = 28.dp, end = 28.dp, top = 16.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp))

            NavigationDrawerItem(
                label = { Text(text = stringResource(R.string.connection_search)) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Search, contentDescription = null
                    )
                },
                selected = true,
                onClick = {},
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp))

            NavigationDrawerItem(label = { Text(text = stringResource(R.string.login)) }, icon = {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle, contentDescription = null
                )
            }, selected = false, onClick = {}, modifier = Modifier.padding(horizontal = 12.dp))

            NavigationDrawerItem(
                label = { Text(text = stringResource(R.string.my_tickets)) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_confirmation_number_24),
                        contentDescription = null
                    )
                },
                selected = false,
                onClick = {},
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            NavigationDrawerItem(
                label = { Text(text = stringResource(R.string.passengers)) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_groups_24),
                        contentDescription = null
                    )
                },
                selected = false,
                onClick = {},
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = { Text(stringResource(R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(R.string.menu)
                            )
                        }
                    })
            }, modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Home(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PKPAppPreview() {
    PKPAndroidTheme {
        PKPApp()
    }
}
