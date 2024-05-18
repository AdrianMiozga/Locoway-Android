package com.wentura.pkp_android.compose.authentication

import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.wentura.pkp_android.R
import com.wentura.pkp_android.ui.PKPAndroidTheme
import com.wentura.pkp_android.viewmodels.AuthenticationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class LoginPage(@StringRes val titleResId: Int) {
    LOGIN(R.string.login), REGISTER(R.string.register)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AuthenticationScreen(
    onUpClick: () -> Unit = {},
    pages: Array<LoginPage> = LoginPage.entries.toTypedArray(),
    onSignUp: () -> Unit = {},
    onSignIn: () -> Unit = {},
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    Scaffold(topBar = { AuthenticationTopAppBar(onUpClick = onUpClick) }, snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            val uiState by authenticationViewModel.uiState.collectAsStateWithLifecycle()

            if (uiState.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }

            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                pages.forEachIndexed { index, page ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        unselectedContentColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.height(49.dp)
                    ) {
                        Text(stringResource(page.titleResId))
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.Top
            ) { index ->
                when (pages[index]) {
                    LoginPage.LOGIN -> {
                        Login(
                            onSignIn = onSignIn, authenticationViewModel = authenticationViewModel
                        )
                    }

                    LoginPage.REGISTER -> {
                        Register(
                            onSignUp = onSignUp, authenticationViewModel = authenticationViewModel
                        )
                    }
                }
            }

            uiState.userMessage?.let { message ->
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(context.getString(message))
                    authenticationViewModel.onMessageShown()
                }
            }
        }
    }
}

fun signInWithGoogle(
    context: Context,
    activity: Activity,
    coroutineScope: CoroutineScope,
    authenticationViewModel: AuthenticationViewModel,
) {
    // TODO: Add nonce
    //  https://developer.android.com/training/sign-in/credential-manager#set-nonce
    val signInWithGoogle =
        GetSignInWithGoogleOption.Builder(context.getString(R.string.firebase_web_client_id))
            .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogle)
        .build()

    val credentialManager = CredentialManager.create(activity)

    coroutineScope.launch {
        try {
            val result = credentialManager.getCredential(
                context = activity,
                request = request,
            )

            authenticationViewModel.handleGoogleSignIn(result)
        } catch (_: GetCredentialCancellationException) {
        } catch (exception: GetCredentialException) {
            authenticationViewModel.signInFailed(exception)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AuthenticationTopAppBar(onUpClick: () -> Unit) {
    CenterAlignedTopAppBar(title = {
        Text(stringResource(R.string.app_name))
    }, navigationIcon = {
        IconButton(onClick = onUpClick) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
        }
    })
}

@Composable
@Preview(showBackground = true)
private fun AuthenticationPreview() {
    PKPAndroidTheme {
        AuthenticationScreen()
    }
}
