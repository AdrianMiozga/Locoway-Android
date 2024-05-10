package com.wentura.pkp_android.compose.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wentura.pkp_android.R
import com.wentura.pkp_android.ui.PKPAndroidTheme

@Composable
fun Login(modifier: Modifier = Modifier) {
    val emailText = remember { mutableStateOf("") }
    val passwordText = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        OutlinedTextField(
            value = emailText.value,
            onValueChange = { emailText.value = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
            ),
            singleLine = true,
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 10.dp)
        )

        OutlinedTextField(
            value = passwordText.value,
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisible.value = !passwordVisible.value
                }) {
                    val resource =
                        if (passwordVisible.value) R.drawable.outline_visibility_off_24 else R.drawable.outline_visibility_24

                    val description =
                        if (passwordVisible.value) stringResource(R.string.hide_password) else stringResource(
                            R.string.show_password
                        )

                    Icon(
                        painter = painterResource(resource), contentDescription = description
                    )
                }
            },
            onValueChange = { passwordText.value = it },
            singleLine = true,
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            TextButton(onClick = {}, modifier = Modifier.padding(vertical = 10.dp)) {
                Text(stringResource(R.string.forgot_password))
            }

            Button(onClick = {}, modifier = Modifier.padding(vertical = 10.dp)) {
                Text(stringResource(R.string.login))
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp, horizontal = 26.dp))
    }
}

@Composable
@Preview(showBackground = true)
private fun LoginPreview() {
    PKPAndroidTheme {
        Login(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        )
    }
}
