package com.wentura.pkp_android.compose.login

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.wentura.pkp_android.R

@Composable
fun Login(onUpClick: () -> Unit = {}) {
    Scaffold(topBar = { LoginTopAppBar(onUpClick = onUpClick) }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("login")
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LoginTopAppBar(onUpClick: () -> Unit) {
    CenterAlignedTopAppBar(title = {
        Text(stringResource(R.string.app_name))
    }, navigationIcon = {
        IconButton(onClick = onUpClick) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
        }
    })
}
