package org.wentura.locoway.compose.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.wentura.locoway.R
import org.wentura.locoway.ui.LocowayTheme
import org.wentura.locoway.util.findActivity
import org.wentura.locoway.viewmodels.AuthenticationUiState

@Composable
fun LoginPage(
    uiState: StateFlow<AuthenticationUiState>,
    modifier: Modifier = Modifier,
    onSignIn: () -> Unit = {},
    resetPassword: (String) -> Boolean = { false },
    passwordSignIn: (String, String) -> Unit = { _, _ -> },
    handleGoogleSignIn: (GetCredentialResponse) -> Unit = {},
    signInFailed: (GetCredentialException) -> Unit = {},
) {
    val state by uiState.collectAsStateWithLifecycle()

    val emailText = rememberSaveable { mutableStateOf("") }
    val isEmailWrong = state.isEmailWrong

    val passwordText = rememberSaveable { mutableStateOf("") }
    val isPasswordWrong = state.isPasswordWrong
    val passwordVisible = rememberSaveable { mutableStateOf(false) }

    val openAlertDialog = rememberSaveable { mutableStateOf(false) }

    if (openAlertDialog.value) {
        ResetPasswordDialog(
            onDismissRequest = { openAlertDialog.value = false },
            onSendClick = { email ->
                if (resetPassword(email)) {
                    openAlertDialog.value = false
                }
            },
        )
    }

    if (state.isSignedIn) {
        onSignIn()
    }

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        OutlinedTextField(
            value = emailText.value,
            onValueChange = { emailText.value = it },
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            supportingText = {
                if (isEmailWrong) Text(stringResource(R.string.invalid_email)) else Text("")
            },
            isError = isEmailWrong,
            singleLine = true,
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 20.dp),
        )

        OutlinedTextField(
            value = passwordText.value,
            visualTransformation =
                if (passwordVisible.value) VisualTransformation.None
                else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    val resource =
                        if (passwordVisible.value) R.drawable.outline_visibility_off_24
                        else R.drawable.outline_visibility_24

                    val description =
                        if (passwordVisible.value) stringResource(R.string.hide_password)
                        else stringResource(R.string.show_password)

                    Icon(
                        painter = painterResource(resource),
                        contentDescription = description,
                    )
                }
            },
            isError = isPasswordWrong,
            onValueChange = { passwordText.value = it },
            singleLine = true,
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 10.dp),
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
        ) {
            TextButton(
                onClick = { openAlertDialog.value = true },
                modifier = Modifier.padding(vertical = 10.dp),
            ) {
                Text(stringResource(R.string.forgot_password))
            }

            Button(
                onClick = { passwordSignIn(emailText.value, passwordText.value) },
                modifier = Modifier.padding(vertical = 10.dp),
            ) {
                Text(stringResource(R.string.login))
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp, horizontal = 26.dp))

        OutlinedButton(
            onClick = {
                val activity = context.findActivity()

                signInWithGoogle(
                    context, activity, coroutineScope, handleGoogleSignIn, signInFailed)
            },
            modifier = Modifier.padding(10.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.google_g_logo),
                tint = Color.Unspecified,
                contentDescription = null,
            )

            Spacer(Modifier.size(ButtonDefaults.IconSpacing))

            Text(stringResource(R.string.continue_with_google))
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun LoginPagePreview() {
    LocowayTheme {
        LoginPage(
            uiState = MutableStateFlow(AuthenticationUiState()),
            modifier = Modifier.fillMaxHeight().fillMaxWidth(),
        )
    }
}
