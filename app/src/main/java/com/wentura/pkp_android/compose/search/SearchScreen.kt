package com.wentura.pkp_android.compose.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wentura.pkp_android.ui.PKPAndroidTheme

@Composable
fun SearchScreen(onUpClick: () -> Unit = {}) {
    Scaffold(topBar = { SearchTopAppBar(onUpClick) }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {}
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchTopAppBar(onUpClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Strzelce Opolskie")
                Text("Gliwice")
            }
        },
        navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    PKPAndroidTheme { SearchScreen() }
}
