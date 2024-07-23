package org.wentura.locoway.compose.myaccount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.wentura.locoway.R
import org.wentura.locoway.ui.LocowayTheme
import org.wentura.locoway.viewmodels.MyAccountUiState
import org.wentura.locoway.viewmodels.MyAccountViewModel

@Composable
fun MyAccountScreen(
    myAccountViewModel: MyAccountViewModel = hiltViewModel(),
    onUpClick: () -> Unit = {},
) {
    MyAccountScreen(
        myAccountViewModel.uiState,
        onUpClick,
        myAccountViewModel::deleteAccount,
        myAccountViewModel::signOut,
    )
}

@Composable
fun MyAccountScreen(
    uiState: StateFlow<MyAccountUiState>,
    onUpClick: () -> Unit = {},
    onAccountDelete: () -> Unit = {},
    onSignOut: () -> Unit = {},
) {
    val openAlertDialog = rememberSaveable { mutableStateOf(false) }

    val state by uiState.collectAsStateWithLifecycle()

    if (!state.isSignedIn) {
        onUpClick()
    }

    Scaffold(topBar = { MyAccountTopAppBar(onUpClick) }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (openAlertDialog.value) {
                AccountDeletionDialog(
                    onDismissRequest = { openAlertDialog.value = false },
                    onConfirmation = {
                        openAlertDialog.value = false
                        onAccountDelete()
                    },
                )
            }

            Text(
                stringResource(R.string.email_address),
                style = MaterialTheme.typography.titleMedium,
            )

            Text(state.email)

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = { openAlertDialog.value = true }) {
                    Text(
                        stringResource(R.string.delete_account),
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                OutlinedButton(onClick = { onSignOut() }) { Text(stringResource(R.string.logout)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAccountTopAppBar(onUpClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.my_account)) },
        navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun MyAccountPreview() {
    LocowayTheme {
        MyAccountScreen(
            MutableStateFlow(MyAccountUiState(isSignedIn = true, email = "user@email.com")),
        )
    }
}
