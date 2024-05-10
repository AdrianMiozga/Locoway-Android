package com.wentura.pkp_android.compose.login

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wentura.pkp_android.R
import com.wentura.pkp_android.ui.PKPAndroidTheme
import kotlinx.coroutines.launch

enum class LoginPage(@StringRes val titleResId: Int) {
    LOGIN(R.string.login), REGISTER(R.string.register)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Pager(onUpClick: () -> Unit = {}, pages: Array<LoginPage> = LoginPage.entries.toTypedArray()) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = { PagerTopAppBar(onUpClick = onUpClick) }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
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
                        Login()
                    }

                    LoginPage.REGISTER -> {
                        Register()
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PagerTopAppBar(onUpClick: () -> Unit) {
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
private fun PagerPreview() {
    PKPAndroidTheme {
        Pager()
    }
}
