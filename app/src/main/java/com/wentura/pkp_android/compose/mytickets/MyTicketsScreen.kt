package com.wentura.pkp_android.compose.mytickets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wentura.pkp_android.R
import com.wentura.pkp_android.compose.common.TrainBrandCircle
import com.wentura.pkp_android.data.Ticket
import com.wentura.pkp_android.data.TrainBrand
import com.wentura.pkp_android.ui.PKPAndroidTheme
import com.wentura.pkp_android.util.travelTime
import com.wentura.pkp_android.viewmodels.MyTicketsUiState
import com.wentura.pkp_android.viewmodels.MyTicketsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MyTicketsScreen(
    onUpClick: () -> Unit = {},
    myTicketsViewModel: MyTicketsViewModel = hiltViewModel(),
) {
    MyTicketsScreen(
        onUpClick = onUpClick,
        uiState = myTicketsViewModel.uiState,
        onPullToRefresh = myTicketsViewModel::getTickets,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketsScreen(
    onUpClick: () -> Unit = {},
    uiState: StateFlow<MyTicketsUiState>,
    onPullToRefresh: suspend () -> Unit = {},
) {
    val state by uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { MyTicketsTopAppBar(onUpClick) }) { innerPadding ->
        val pullToRefreshState = rememberPullToRefreshState()

        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(true) {
                onPullToRefresh()
                pullToRefreshState.endRefresh()
            }
        }

        Box(
            modifier =
                Modifier.padding(innerPadding)
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        if (state.isLoading) {
                            LinearProgressIndicator(Modifier.fillMaxWidth())
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    if (state.tickets.isNotEmpty() || state.isLoading) {
                        items(state.tickets.size) { index ->
                            MyTicketListItem(state.tickets[index])

                            if (index != state.tickets.size - 1) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                            }
                        }
                    } else {
                        item {
                            Column(
                                modifier = Modifier.fillParentMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(stringResource(R.string.no_tickets))
                                }
                        }
                    }
                }

                PullToRefreshContainer(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = pullToRefreshState,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
    }
}

@Composable
fun MyTicketListItem(ticket: Ticket) {
    Box(modifier = Modifier.clickable {}) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TrainBrandCircle(
                trainBrand = TrainBrand.valueOf(ticket.trainBrand),
                modifier = Modifier.padding(end = 16.dp),
            )

            Column {
                Text(
                    text = "${ticket.departureStation} - ${ticket.arrivalStation}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                Row {
                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM")

                    val departureDateTime = LocalDateTime.parse(ticket.departureDate)
                    val arrivalDateTime = LocalDateTime.parse(ticket.arrivalDate)

                    Column {
                        Text(
                            text = departureDateTime.format(timeFormatter),
                            style = MaterialTheme.typography.bodyLarge,
                        )

                        Text(
                            text = departureDateTime.format(dateFormatter),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text =
                                stringResource(
                                    R.string.duration_time,
                                    travelTime(
                                        departureDateTime,
                                        arrivalDateTime,
                                    ),
                                ),
                            style = MaterialTheme.typography.labelSmall,
                        )

                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Column {
                        Text(
                            text = arrivalDateTime.format(timeFormatter),
                            style = MaterialTheme.typography.bodyLarge,
                        )

                        Text(
                            text = arrivalDateTime.format(dateFormatter),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyTicketsTopAppBar(onUpClick: () -> Unit) {
    TopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.my_tickets))
            }
        },
        navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
            }
        })
}

@Preview(showBackground = true)
@Composable
fun MyTicketsPreview() {
    val tickets =
        listOf(
            Ticket(
                uid = "",
                carrier = "REG",
                trainNumber = 1L,
                trainBrand = "REG",
                trainClass = 1,
                seat = 1,
                departureStation = "Strzelce Opolskie",
                departureDate = "2021-12-12T12:00:00",
                arrivalStation = "Opole Główne",
                arrivalDate = "2021-12-12T12:30:00",
            ),
            Ticket(
                uid = "",
                carrier = "IC",
                trainNumber = 1L,
                trainBrand = "IC",
                trainClass = 1,
                seat = 1,
                departureStation = "Gliwice",
                departureDate = "2021-12-12T12:00:00",
                arrivalStation = "Strzelce Opolskie",
                arrivalDate = "2021-12-12T12:30:00",
            ),
        )

    PKPAndroidTheme {
        MyTicketsScreen(
            uiState = MutableStateFlow(MyTicketsUiState(tickets = tickets, isLoading = false)))
    }
}
