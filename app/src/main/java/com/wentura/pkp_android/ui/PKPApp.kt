package com.wentura.pkp_android.ui

import Home
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.wentura.pkp_android.R
import com.wentura.pkp_android.ui.theme.PKPAndroidTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PKPApp() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = {}) {
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PKPAppPreview() {
    PKPAndroidTheme {
        PKPApp()
    }
}
