package com.wentura.pkp_android.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wentura.pkp_android.R
import com.wentura.pkp_android.ui.theme.PKPAndroidTheme

@Composable
fun NavigationDrawer() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {
            Text(
                stringResource(R.string.app_name),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(16.dp))

            NavigationDrawerItem(label = { Text(text = stringResource(R.string.connection_search)) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Search, contentDescription = null
                    )
                },
                selected = true,
                onClick = {})

            HorizontalDivider(modifier = Modifier.padding(16.dp))

            NavigationDrawerItem(label = { Text(text = stringResource(R.string.login)) }, icon = {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle, contentDescription = null
                )
            }, selected = false, onClick = {})

            NavigationDrawerItem(label = { Text(text = stringResource(R.string.my_tickets)) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_confirmation_number_24),
                        contentDescription = null
                    )
                },
                selected = false,
                onClick = {})

            NavigationDrawerItem(label = { Text(text = stringResource(R.string.passengers)) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_groups_24),
                        contentDescription = null
                    )
                },
                selected = false,
                onClick = {})
        }
    }) {
        // Screen content
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationDrawerPreview() {
    PKPAndroidTheme {
        NavigationDrawer()
    }
}
