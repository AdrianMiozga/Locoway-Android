package com.wentura.pkp_android.compose.authentication

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.wentura.pkp_android.R
import com.wentura.pkp_android.ui.PKPAndroidTheme
import com.wentura.pkp_android.util.findActivity
import com.wentura.pkp_android.viewmodels.AuthenticationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun RegisterPage(
    uiState: StateFlow<AuthenticationUiState>,
    modifier: Modifier = Modifier,
    onSignUp: () -> Unit = {},
    passwordSignUp: (String, String, String) -> Unit = { _, _, _ -> },
    handleGoogleSignIn: (GetCredentialResponse) -> Unit = {},
    signInFailed: (GetCredentialException) -> Unit = {},
) {
    val state by uiState.collectAsStateWithLifecycle()

    val emailText = rememberSaveable { mutableStateOf("") }
    val isEmailWrong = state.isEmailWrong

    val passwordText = rememberSaveable { mutableStateOf("") }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val isPasswordWrong = state.isPasswordWrong

    val passwordConfirmationText = rememberSaveable { mutableStateOf("") }
    val passwordConfirmationVisible = rememberSaveable { mutableStateOf(false) }
    val isConfirmationPasswordWrong = state.isConfirmationPasswordWrong

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    if (state.isSignedIn) {
        onSignUp()
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = emailText.value,
                onValueChange = { emailText.value = it },
                supportingText = {
                    if (isEmailWrong) Text(stringResource(R.string.invalid_email)) else Text("")
                },
                trailingIcon = {
                    if (isEmailWrong) {
                        Icon(
                            painter = painterResource(R.drawable.outline_error_24),
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = null)
                    }
                },
                keyboardOptions =
                    KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                isError = isEmailWrong,
                singleLine = true,
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 20.dp))

            OutlinedTextField(
                value = passwordText.value,
                visualTransformation =
                    if (passwordVisible.value) VisualTransformation.None
                    else PasswordVisualTransformation(),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                isError = state.isPasswordWrong,
                supportingText = {
                    if (isPasswordWrong) Text(stringResource(R.string.password_too_short))
                    else Text("")
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                        val resource =
                            if (passwordVisible.value) R.drawable.outline_visibility_off_24
                            else R.drawable.outline_visibility_24

                        val description =
                            if (passwordVisible.value) stringResource(R.string.hide_password)
                            else stringResource(R.string.show_password)

                        Icon(painter = painterResource(resource), contentDescription = description)
                    }
                },
                onValueChange = { passwordText.value = it },
                singleLine = true,
                label = { Text(stringResource(R.string.password)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))

            OutlinedTextField(
                value = passwordConfirmationText.value,
                visualTransformation =
                    if (passwordConfirmationVisible.value) VisualTransformation.None
                    else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = isConfirmationPasswordWrong,
                supportingText = {
                    if (isConfirmationPasswordWrong)
                        Text(stringResource(R.string.passwords_not_the_same))
                    else Text("")
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordConfirmationVisible.value = !passwordConfirmationVisible.value
                        }) {
                            val resource =
                                if (passwordConfirmationVisible.value)
                                    R.drawable.outline_visibility_off_24
                                else R.drawable.outline_visibility_24

                            val description =
                                if (passwordVisible.value) stringResource(R.string.hide_password)
                                else stringResource(R.string.show_password)

                            Icon(
                                painter = painterResource(resource),
                                contentDescription = description)
                        }
                },
                onValueChange = { passwordConfirmationText.value = it },
                singleLine = true,
                label = { Text(stringResource(R.string.confirm_password)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))

            Button(
                onClick = {
                    passwordSignUp(
                        emailText.value, passwordText.value, passwordConfirmationText.value)
                },
                modifier = Modifier.padding(bottom = 10.dp)) {
                    Text(stringResource(R.string.register))
                }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp, horizontal = 26.dp))

            OutlinedButton(
                onClick = {
                    val activity = context.findActivity()

                    signInWithGoogle(
                        context,
                        activity,
                        coroutineScope,
                        handleGoogleSignIn,
                        signInFailed,
                    )
                },
                modifier = Modifier.padding(10.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.google_g_logo),
                        tint = Color.Unspecified,
                        contentDescription = null)

                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))

                    Text(stringResource(R.string.continue_with_google))
                }
        }
}

@Composable
@Preview(showBackground = true)
private fun RegisterPagePreview() {
    PKPAndroidTheme {
        RegisterPage(
            uiState = MutableStateFlow(AuthenticationUiState()),
            modifier = Modifier.fillMaxHeight().fillMaxWidth(),
        )
    }
}
